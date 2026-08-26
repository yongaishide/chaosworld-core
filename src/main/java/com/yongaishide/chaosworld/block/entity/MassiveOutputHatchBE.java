package com.yongaishide.chaosworld.block.entity;

import com.yongaishide.chaosworld.api.ae.IMassiveInjector;
import com.yongaishide.chaosworld.api.multiblock.IMultiblockPart;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.compat.mekanism.MekanismChemicalStorage;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AEKey;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Block Entity for the ME Massive Output Hatch.
 * <p>
 * This is a hybrid entity: it extends AE2's {@link AENetworkedBlockEntity}
 * to gain a real grid connection (cables, channels, power), while also
 * implementing our internal API interfaces:
 * <ul>
 *   <li>{@link IMassiveInjector} — bulk item/fluid injection into the ME network</li>
 *   <li>{@link IMultiblockPart} — multiblock structure participation</li>
 * </ul>
 * <p>
 * <b>How injection works:</b>
 * <ol>
 *   <li>The Stellar Nexus Controller finishes a simulation cycle</li>
 *   <li>It locates all Output Hatches among its parts</li>
 *   <li>Calls {@link #injectIntoNetwork(AEKey, long, Level)} on each hatch</li>
 *   <li>The hatch uses {@code grid.getStorageService().getInventory().insert(...)} to push items directly into ME storage</li>
 * </ol>
 */
public class MassiveOutputHatchBE extends AENetworkedBlockEntity
        implements IMassiveInjector, IMultiblockPart, IGridTickable, MekanismChemicalStorage {
    private static final long CHEMICAL_CAPACITY = 16_000_000L;

    /** Controller position for multiblock link (null if standalone). */
    @Nullable
    private BlockPos controllerPos = null;

    /** Statistics: total items injected since last reset. */
    private long totalInjected = 0;

    /** Statistics: last injection amount (for GUI/tooltip display). */
    private long lastInjectionAmount = 0;
    @Nullable
    private net.minecraft.resources.ResourceLocation storedChemicalId = null;
    private long storedChemicalAmount = 0L;

    public MassiveOutputHatchBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        // Configure AE2 grid node: no idle power draw, connectable on all sides
        this.getMainNode()
                .setExposedOnSides(java.util.EnumSet.allOf(Direction.class))
                .setFlags()                          // No special flags
                .setIdlePowerUsage(0)                // No passive drain
                .addService(IGridTickable.class, this); // Register for tick callbacks
    }

    // ═══════════════════════════════════════════════════════════
    //  IMassiveInjector — Bulk AE2 Network Injection
    // ═══════════════════════════════════════════════════════════

    @Override
    public long injectIntoNetwork(AEKey what, long amount, Level level) {
        if (!isNetworkReady() || what == null || amount <= 0) {
            return 0;
        }

        // Access the ME grid's unified storage
        var gridNode = this.getMainNode().getNode();
        if (gridNode == null || gridNode.getGrid() == null) {
            return 0;
        }

        var grid = gridNode.getGrid();
        var storageService = grid.getStorageService();
        var inventory = storageService.getInventory();

        // Use IActionSource.ofMachine to identify ourselves as the source
        var source = IActionSource.ofMachine(this);

        // Inject directly — AE2's insert() natively handles long quantities
        long inserted = inventory.insert(what, amount, Actionable.MODULATE, source);

        // Track statistics
        if (inserted > 0) {
            this.totalInjected += inserted;
            this.lastInjectionAmount = inserted;
            this.setChanged();
        }

        return inserted;
    }

    @Override
    public boolean isNetworkReady() {
        var node = this.getMainNode().getNode();
        return node != null && node.isActive() && node.isPowered();
    }

    // ═══════════════════════════════════════════════════════════
    //  IMultiblockPart — Structure Participation
    // ═══════════════════════════════════════════════════════════

    @Override
    public void linkToController(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        refreshGridConnection();
        this.setChanged();
    }

    @Override
    public void unlinkFromController() {
        this.controllerPos = null;
        refreshGridConnection();
        this.setChanged();
    }

    @Nullable
    @Override
    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    /**
     * @return {@code true} if this hatch is currently linked to a multiblock controller.
     */
    public boolean isLinked() {
        return this.controllerPos != null;
    }

    // ═══════════════════════════════════════════════════════════
    //  IGridTickable — AE2 Tick Callbacks
    // ═══════════════════════════════════════════════════════════

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        // We don't actively do work — injection is called by the controller.
        // Sleep until woken.
        return new TickingRequest(20, 20, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        // Nothing to do autonomously; the controller drives injection.
        return TickRateModulation.SLEEP;
    }

    // ═══════════════════════════════════════════════════════════
    //  AE2 Configuration
    // ═══════════════════════════════════════════════════════════

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.DENSE_SMART;
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        // Allow AE2 cable connections from all 6 sides
        return EnumSet.allOf(Direction.class);
    }

    public void refreshGridConnection() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        this.getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
        this.onGridConnectableSidesChanged();
    }

    // ═══════════════════════════════════════════════════════════
    //  Statistics API (for future GUI / tooltips)
    // ═══════════════════════════════════════════════════════════

    public long getTotalInjected() {
        return this.totalInjected;
    }

    public long getLastInjectionAmount() {
        return this.lastInjectionAmount;
    }

    public void resetStatistics() {
        this.totalInjected = 0;
        this.lastInjectionAmount = 0;
        this.setChanged();
    }

    @Override
    public boolean supportsChemicalIO() {
        return this.getBlockState().is(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get());
    }

    @Override
    public long getChemicalCapacity() {
        return CHEMICAL_CAPACITY;
    }

    @Override
    public @Nullable net.minecraft.resources.ResourceLocation getStoredChemicalId() {
        return this.storedChemicalId;
    }

    @Override
    public long getStoredChemicalAmount() {
        return this.storedChemicalAmount;
    }

    @Override
    public void setStoredChemical(@Nullable net.minecraft.resources.ResourceLocation chemicalId, long amount) {
        long clamped = Math.max(0L, Math.min(CHEMICAL_CAPACITY, amount));
        this.storedChemicalId = clamped > 0L ? chemicalId : null;
        this.storedChemicalAmount = clamped;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  NBT Persistence (AE2 pattern: saveAdditional + loadTag)
    // ═══════════════════════════════════════════════════════════

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.controllerPos != null) {
            tag.put("controllerPos", NbtUtils.writeBlockPos(this.controllerPos));
        }
        tag.putLong("totalInjected", this.totalInjected);
        tag.putLong("lastInjection", this.lastInjectionAmount);
        if (this.storedChemicalId != null && this.storedChemicalAmount > 0L) {
            tag.putString("storedChemicalId", this.storedChemicalId.toString());
            tag.putLong("storedChemicalAmount", this.storedChemicalAmount);
        }
    }

    @Override
    public void loadTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadTag(tag, registries);
        if (tag.contains("controllerPos")) {
            NbtUtils.readBlockPos(tag.getCompound("controllerPos"), "").ifPresent(pos -> this.controllerPos = pos);
        } else {
            this.controllerPos = null;
        }
        this.totalInjected = tag.getLong("totalInjected");
        this.lastInjectionAmount = tag.getLong("lastInjection");
        this.storedChemicalId = tag.contains("storedChemicalId") ? net.minecraft.resources.ResourceLocation.parse(tag.getString("storedChemicalId")) : null;
        this.storedChemicalAmount = this.storedChemicalId == null ? 0L : tag.getLong("storedChemicalAmount");
    }
}

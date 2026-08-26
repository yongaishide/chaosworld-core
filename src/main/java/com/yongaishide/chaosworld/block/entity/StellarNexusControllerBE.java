package com.yongaishide.chaosworld.block.entity;

import com.yongaishide.chaosworld.api.multiblock.IMultiblockController;
import com.yongaishide.chaosworld.api.multiblock.IMultiblockPart;
import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import com.yongaishide.chaosworld.block.entity.pattern.StellarNexusPatternFactory;
import com.yongaishide.chaosworld.block.StellarNexusControllerBlock;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.fluid.ModFluids;
import com.yongaishide.chaosworld.recipe.StellarSimulationRecipe;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.blockentity.grid.AENetworkedBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Block Entity for the Stellar Nexus Controller.
 * <p>
 * Manages the multiblock structure, AE energy charging, fuel/coolant
 * extraction from the ME network, thermal system, and simulation processing.
 * <p>
 * <b>Terminology:</b>
 * <ul>
 *   <li><b>Energy</b> (energyBuffer) = AE power charged passively from the AE grid</li>
 *   <li><b>Fuel</b> = liquid combustible extracted from ME storage on start (e.g., Hydrogen)</li>
 *   <li><b>Coolant</b> = liquid refrigerant consumed during operation (e.g., Gelid Cryotheum)</li>
 * </ul>
 */
import com.yongaishide.chaosworld.screen.StellarNexusControllerMenu;
import com.yongaishide.chaosworld.block.MultiblockBlocks;

public class StellarNexusControllerBE extends BlockEntity implements IMultiblockController, MenuProvider {

    private boolean assembled = false;
    private boolean structureDirty = true;
    private int scanCooldown = 0;
    private final List<BlockPos> parts = new ArrayList<>();

    // Processing state
    private ResourceLocation activeRecipeId = null;
    private int progress = 0;
    private int maxProgress = 0; // Cached total time
    private boolean running = false;
    private final ContainerData data;

    // Energy buffer — AE power charged passively from AE2 network via Energy Input
    // Hatch
    private long energyBuffer = 0;
    private static final long GLOBAL_ENERGY_CAPACITY = 200_000_000_000L; // 200 Billion AE global buffer
    private long energyCapacity = GLOBAL_ENERGY_CAPACITY;

    // Thermal system
    private int heatLevel = 0; // 0-1000 (displayed as 0.0% - 100.0%)
    private static final int MAX_HEAT = 100000;
    private boolean safeMode = true; // Default ON: auto-shutdown at 100% heat
    private int cooldownTimer = 0; // Ticks remaining for 30-min cooldown after overheat
    private static final int COOLDOWN_DURATION = 36000; // 30 minutes = 36000 ticks
    private static final int COOLDOWN_SAVE_INTERVAL = 20;

    // UI Toggles
    private boolean autoStart = false;
    private boolean simulationLocked = false;
    private boolean isOverclocked = false;

    // Catastrophic explosion system
    private boolean exploding = false;
    private int explosionTick = 0;
    private int explosionRadius = 50;
    private int explosionShellRadius = 0;
    private int explosionCursorX = 0;
    private int explosionCursorY = 0;
    private int explosionCursorZ = 0;
    private static final int EXPLOSION_BLOCKS_PER_TICK = 4096;

    // Cached field tier
    private int fieldLevel = 0;

    // AE network connection state
    private boolean aeConnected = false;

    // The multiblock pattern will be initialized lazily
    private static MultiblockPattern PATTERN;

    // Energy charge rates per Field Generator tier (AE per tick)
    private static final long[] ENERGY_RATE_BY_TIER = { 0, 5_000_000, 15_000_000, 50_000_000 };

    public StellarNexusControllerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        // ContainerData layout:
        // 0 = progress
        // 1 = maxProgress
        // 2 = assembled (0 or 1)
        // 3 = fieldLevel (1-3)
        // 4 = energyPercent (0-100)
        // 5 = running (0 or 1)
        // 6 = heatPercent (0-1000, displayed as 0.0%-100.0%)
        // 7 = safeMode (0 or 1)
        // 8 = cooldownTimer (ticks remaining)
        // 9-12 = energyBuffer as 4 shorts
        // 13-16 = energyCapacity as 4 shorts
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> StellarNexusControllerBE.this.progress;
                    case 1 -> StellarNexusControllerBE.this.maxProgress;
                    case 2 -> StellarNexusControllerBE.this.assembled ? 1 : 0;
                    case 3 -> StellarNexusControllerBE.this.fieldLevel;
                    case 4 -> StellarNexusControllerBE.this.energyCapacity > 0
                            ? (int) (StellarNexusControllerBE.this.energyBuffer * 100
                                    / StellarNexusControllerBE.this.energyCapacity)
                            : 0;
                    case 5 -> StellarNexusControllerBE.this.running ? 1 : 0;
                    case 6 -> StellarNexusControllerBE.this.heatLevel;
                    case 7 -> StellarNexusControllerBE.this.safeMode ? 1 : 0;
                    case 8 -> StellarNexusControllerBE.this.cooldownTimer;
                    case 9 -> (int) (StellarNexusControllerBE.this.energyBuffer & 0xFFFF);
                    case 10 -> (int) ((StellarNexusControllerBE.this.energyBuffer >> 16) & 0xFFFF);
                    case 11 -> (int) ((StellarNexusControllerBE.this.energyBuffer >> 32) & 0xFFFF);
                    case 12 -> (int) ((StellarNexusControllerBE.this.energyBuffer >> 48) & 0xFFFF);
                    case 13 -> (int) (GLOBAL_ENERGY_CAPACITY & 0xFFFF);
                    case 14 -> (int) ((GLOBAL_ENERGY_CAPACITY >> 16) & 0xFFFF);
                    case 15 -> (int) ((GLOBAL_ENERGY_CAPACITY >> 32) & 0xFFFF);
                    case 16 -> (int) ((GLOBAL_ENERGY_CAPACITY >> 48) & 0xFFFF);
                    case 17 -> StellarNexusControllerBE.this.autoStart ? 1 : 0;
                    case 18 -> StellarNexusControllerBE.this.simulationLocked ? 1 : 0;
                    case 19 -> StellarNexusControllerBE.this.isOverclocked ? 1 : 0;
                    case 20 -> StellarNexusControllerBE.this.aeConnected ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> StellarNexusControllerBE.this.progress = pValue;
                    case 1 -> StellarNexusControllerBE.this.maxProgress = pValue;
                    case 2 -> StellarNexusControllerBE.this.assembled = pValue == 1;
                    case 3 -> StellarNexusControllerBE.this.fieldLevel = pValue;
                    case 5 -> StellarNexusControllerBE.this.running = pValue == 1;
                    case 6 -> StellarNexusControllerBE.this.heatLevel = pValue;
                    case 7 -> StellarNexusControllerBE.this.safeMode = pValue == 1;
                    case 8 -> StellarNexusControllerBE.this.cooldownTimer = pValue;
                    case 17 -> StellarNexusControllerBE.this.autoStart = pValue == 1;
                    case 18 -> StellarNexusControllerBE.this.simulationLocked = pValue == 1;
                    case 19 -> StellarNexusControllerBE.this.isOverclocked = pValue == 1;
                    case 20 -> StellarNexusControllerBE.this.aeConnected = pValue == 1;
                }
            }

            @Override
            public int getCount() {
                return 21;
            }
        };
    }

    // ──────────────────── IMultiblockController ────────────────────

    @Override
    public boolean isAssembled() {
        return this.assembled;
    }

    @Override
    public void scanStructure(Level level) {
        scanStructure(level, null);
    }

    public void scanStructure(Level level, @Nullable Player player) {
        MultiblockPattern pattern = getPattern();
        BlockState currentState = level.getBlockState(this.worldPosition);
        net.minecraft.core.Direction facing = net.minecraft.core.Direction.NORTH;
        if (currentState.hasProperty(net.minecraft.world.level.block.DirectionalBlock.FACING)) {
            facing = currentState.getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);
        }
        MultiblockPattern.MatchResult result = pattern.match(level, this.worldPosition, facing);
        List<BlockPos> expectedE = pattern.getExpectedPositions(this.worldPosition, facing, 'E');
        boolean hasUnloadedFieldPositions = false;
        int tier1 = 0, tier2 = 0, tier3 = 0;
        for (BlockPos ePos : expectedE) {
            if (!level.isLoaded(ePos)) {
                hasUnloadedFieldPositions = true;
                continue;
            }

            Block block = level.getBlockState(ePos).getBlock();
            if (block == MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get()) {
                tier1++;
            } else if (block == MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get()) {
                tier2++;
            } else if (block == MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get()) {
                tier3++;
            }
        }

        boolean waitingForChunks = result.hasUnloadedPositions() || hasUnloadedFieldPositions;
        if (waitingForChunks && player == null) {
            this.scanCooldown = 0;
            this.structureDirty = true;
            return;
        }

        boolean wasAssembled = this.assembled;
        this.structureDirty = false;
        this.scanCooldown = 0;
        this.assembled = result.isValid();

        // Global Hatch Validation — no longer requires Fuel Hatch
        if (this.assembled) {
            int itemOutputs = 0;
            int fluidOutputs = 0;
            int itemInputs = 0;
            int energyInputs = 0;

            for (BlockPos partPos : result.partPositions()) {
                Block block = level.getBlockState(partPos).getBlock();
                if (block == MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())
                    itemOutputs++;
                else if (block == MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())
                    fluidOutputs++;
                else if (block == MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())
                    itemInputs++;
                else if (block == MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get())
                    energyInputs++;
            }

            // Require exactly 1 of each hatch (no fuel hatch anymore)
            if (itemOutputs != 1 || fluidOutputs != 1 || itemInputs != 1 || energyInputs != 1) {
                this.assembled = false;
            }
        }

        int totalFound = tier1 + tier2 + tier3;
        int targetFields = expectedE.size();

        // Field Tier Validation
        if (this.assembled) {
            int typesCount = (tier1 > 0 ? 1 : 0) + (tier2 > 0 ? 1 : 0) + (tier3 > 0 ? 1 : 0);

            if (typesCount > 1 || totalFound < targetFields) {
                // Mixed tiers or missing fields. Structure is invalid.
                this.assembled = false;
                this.fieldLevel = 0;
            } else if (typesCount == 1) {
                // Structure is homogeneous and valid
                if (tier3 > 0) this.fieldLevel = 3;
                else if (tier2 > 0) this.fieldLevel = 2;
                else this.fieldLevel = 1;
            } else {
                this.fieldLevel = 0;
            }
        } else {
            this.fieldLevel = 0;
        }

        // --- Player Feedback Logic ---
        if (player != null && waitingForChunks) {
            player.displayClientMessage(Component.translatable("message.ufo.stellar.waiting_chunks"), false);
        } else if (player != null && !this.assembled) {
            if (targetFields > 0 && (totalFound < targetFields || (tier1 > 0 && tier2 > 0) || (tier2 > 0 && tier3 > 0) || (tier1 > 0 && tier3 > 0))) {
                player.displayClientMessage(Component.translatable("message.ufo.stellar.mixed_fields"), false);
                
                if (tier1 > 0 || totalFound == 0) {
                    player.displayClientMessage(Component.translatable("message.ufo.stellar.missing_tier",
                            (targetFields - tier1), "1"), false);
                }
                if (tier2 > 0) {
                    player.displayClientMessage(Component.translatable("message.ufo.stellar.missing_tier",
                            (targetFields - tier2), "2"), false);
                }
                if (tier3 > 0) {
                    player.displayClientMessage(Component.translatable("message.ufo.stellar.missing_tier",
                            (targetFields - tier3), "3"), false);
                }
            } else {
                // Fields are fine, something else failed
                if (totalFound == targetFields) {
                     player.displayClientMessage(Component.translatable("message.ufo.stellar.structure_incomplete"), false);
                } else {
                     player.displayClientMessage(Component.translatable("message.ufo.stellar.structure_match_failed"), false);
                }
            }
        }

        // Update tracked parts
        this.parts.clear();
        if (this.assembled) {
            this.parts.addAll(result.partPositions());

            // Link all parts to this controller
            for (BlockPos partPos : this.parts) {
                if (level.getBlockEntity(partPos) instanceof IMultiblockPart part) {
                    part.linkToController(this.worldPosition);
                    if (part instanceof MassiveOutputHatchBE hatch) {
                        hatch.refreshGridConnection();
                    }
                }
            }
        }

        // Update block state visual
        if (wasAssembled != this.assembled) {
            BlockState finalState = level.getBlockState(this.worldPosition);
            if (finalState.getBlock() instanceof StellarNexusControllerBlock) {
                level.setBlock(this.worldPosition,
                        finalState.setValue(StellarNexusControllerBlock.ASSEMBLED, this.assembled),
                        Block.UPDATE_CLIENTS);
            }
            this.setChanged();
        }
    }

    @Override
    public void addPart(BlockPos partPos) {
        if (!this.parts.contains(partPos)) {
            this.parts.add(partPos);
        }
    }

    @Override
    public void removePart(BlockPos partPos) {
        this.parts.remove(partPos);
        this.structureDirty = true;
    }

    @Override
    public List<BlockPos> getParts() {
        return Collections.unmodifiableList(this.parts);
    }

    @Override
    public BlockPos getControllerPos() {
        return this.worldPosition;
    }

    // ──────────────────── Server Tick ────────────────────

    public void serverTick() {
        if (this.level == null || this.level.isClientSide())
            return;

        // Handle ongoing catastrophic explosion (spread across ticks)
        if (this.exploding) {
            processExplosionTick();
            return;
        }

        // Only re-scan when dirty, throttled to every 20 ticks
        if (this.structureDirty) {
            this.scanCooldown++;
            if (this.scanCooldown >= 20) {
                scanStructure(this.level);
            }
        }

        // Cached field level is now updated inside scanStructure()

        // Update AE network connection state
        this.aeConnected = getConnectedNetworkNode() != null;

        // Handle cooldown after overheat
        if (this.cooldownTimer > 0) {
            this.cooldownTimer--;
            if (this.cooldownTimer == 0) {
                this.heatLevel = 0;
                this.setChanged();
            } else if (this.cooldownTimer % COOLDOWN_SAVE_INTERVAL == 0) {
                this.setChanged();
            }
            return;
        }

        if (this.assembled) {
            processMachineTick();

            // Auto-Restart logic
            if (!this.running && this.activeRecipeId != null && this.cooldownTimer == 0) {
                if (this.autoStart) {
                    startOperation();
                }
            }
        } else {
            this.progress = 0;
            this.running = false;
        }
    }

    // ──────────────────── Start Operation ────────────────────

    public boolean isActive() {
        return this.running;
    }

    /**
     * Validates all requirements for starting a simulation and returns a list
     * of error messages. An empty list means the simulation can start.
     */
    public List<Component> getStartErrors() {
        List<Component> errors = new ArrayList<>();

        if (!this.assembled) {
            errors.add(Component.translatable("message.ufo.stellar.err.not_assembled"));
        }
        if (this.running) {
            errors.add(Component.translatable("message.ufo.stellar.err.already_running"));
        }
        if (this.cooldownTimer > 0) {
            int secLeft = this.cooldownTimer / 20;
            errors.add(Component.translatable("message.ufo.stellar.err.cooling", secLeft));
        }
        if (this.activeRecipeId == null) {
            errors.add(Component.translatable("message.ufo.stellar.err.no_program"));
            return errors;
        }

        if (this.level == null)
            return errors;

        var recipeOpt = this.level.getRecipeManager().byKey(this.activeRecipeId);
        if (recipeOpt.isEmpty() || !(recipeOpt.get().value() instanceof StellarSimulationRecipe recipe)) {
            errors.add(Component.translatable("message.ufo.stellar.err.invalid_program"));
            return errors;
        }

        // Field tier check
        if (this.fieldLevel < recipe.getFieldTier()) {
            errors.add(Component.translatable("message.ufo.stellar.err.field_low",
                    toRoman(this.fieldLevel), toRoman(recipe.getFieldTier())));
        }

        // Compute effective costs (safe mode has no penalty; overclock costs more)
        boolean resuming = this.progress > 0 && this.maxProgress > 0;
        double multiplier = this.isOverclocked ? 10.0 : 1.0;
        long effectiveEnergyCost = (long) (recipe.getEnergyCost() * multiplier);

        double fuelMultiplier = this.isOverclocked ? 5.0 : 1.0;
        long effectiveFuelAmount = (long) (recipe.getFuelAmount() * fuelMultiplier);

        // Energy check
        if (this.energyBuffer < effectiveEnergyCost) {
            String safeNote = this.isOverclocked ? " §7(10x O.C.)" : "";
            errors.add(Component.translatable("message.ufo.stellar.err.energy",
                    formatAmount(this.energyBuffer), formatAmount(effectiveEnergyCost), safeNote));
        }

        // Fuel liquid check (from ME storage) — only for a fresh start, not for a resume
        if (!resuming && !recipe.getFuelFluid().isEmpty() && recipe.getFuelAmount() > 0) {
            AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
            if (nodeBE == null || nodeBE.getActionableNode() == null || nodeBE.getActionableNode().getGrid() == null) {
                errors.add(Component.translatable("message.ufo.stellar.err.no_me"));
            } else {
                ResourceLocation fuelRL = ResourceLocation.parse(recipe.getFuelFluid());
                Fluid fuelFluid = BuiltInRegistries.FLUID.get(fuelRL);
                if (fuelFluid == null || fuelFluid == net.minecraft.world.level.material.Fluids.EMPTY) {
                    errors.add(Component.translatable("message.ufo.stellar.err.invalid_fuel", fuelRL));
                } else {
                    AEFluidKey fuelKey = AEFluidKey.of(fuelFluid);
                    MEStorage storage = nodeBE.getActionableNode().getGrid().getStorageService().getInventory();
                    IActionSource src = IActionSource.ofMachine(nodeBE);
                    long available = storage.extract(fuelKey, effectiveFuelAmount, Actionable.SIMULATE, src);
                    if (available < effectiveFuelAmount) {
                        String fluidName = formatFluidName(fuelRL.getPath());
                        String safeNote = this.isOverclocked ? " §7(5x O.C.)" : "";
                        errors.add(Component.translatable("message.ufo.stellar.err.fuel",
                                formatAmount(available), formatAmount(effectiveFuelAmount), fluidName, safeNote));
                    }
                }
            }
        }

        // Item/Fluid inputs check — only for a fresh start, not for a resume
        if (!resuming) {
            AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
            if (nodeBE != null && nodeBE.getActionableNode() != null && nodeBE.getActionableNode().getGrid() != null) {
            MEStorage storage = nodeBE.getActionableNode().getGrid().getStorageService().getInventory();
            IActionSource src = IActionSource.ofMachine(nodeBE);

            for (var req : recipe.getItemInputs()) {
                if (!req.isEmpty()) {
                    long available = simulateExtractItem(req, storage, src);
                    if (available < req.getAmount()) {
                        // Get the display name of the first matching item
                        String itemName = "Unknown Item";
                        ItemStack[] matches = req.getIngredient().getItems();
                        if (matches.length > 0) {
                            itemName = matches[0].getHoverName().getString();
                        }
                        errors.add(Component.translatable("message.ufo.stellar.err.missing_item",
                                formatAmount(available), formatAmount(req.getAmount()), itemName));
                    }
                }
            }
            for (var req : recipe.getFluidInputs()) {
                if (!req.isEmpty()) {
                    long available = simulateExtractFluid(req, storage, src);
                    if (available < req.getAmount()) {
                        // Get the fluid name
                        String fluidName = "Unknown Fluid";
                        var fluidStacks = req.getIngredient().getStacks();
                        if (fluidStacks.length > 0) {
                            ResourceLocation fluidRL = BuiltInRegistries.FLUID.getKey(fluidStacks[0].getFluid());
                            fluidName = formatFluidName(fluidRL.getPath());
                        }
                        errors.add(Component.translatable("message.ufo.stellar.err.missing_fluid",
                                formatAmount(available), formatAmount(req.getAmount()), fluidName));
                    }
                }
            }
        }
        }

        return errors;
    }

    /**
     * Called from the network packet when the player clicks "Start Operation".
     * Returns a list of error messages (empty = success).
     */
    public List<Component> startOperation() {
        List<Component> errors = getStartErrors();
        if (!errors.isEmpty())
            return errors;

        if (this.level == null || this.level.isClientSide())
            return List.of(Component.translatable("message.ufo.stellar.err.internal"));

        var recipeOpt = this.level.getRecipeManager().byKey(this.activeRecipeId);
        if (recipeOpt.isEmpty() || !(recipeOpt.get().value() instanceof StellarSimulationRecipe recipe)) {
            return List.of(Component.translatable("message.ufo.stellar.err.invalid_recipe"));
        }

        AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
        if (nodeBE == null || nodeBE.getActionableNode() == null)
            return List.of(Component.translatable("message.ufo.stellar.err.no_network"));
        IGridNode node = nodeBE.getActionableNode();
        if (node.getGrid() == null)
            return List.of(Component.translatable("message.ufo.stellar.err.no_grid"));

        IGrid grid = node.getGrid();
        IActionSource src = IActionSource.ofMachine(nodeBE);
        MEStorage storage = grid.getStorageService().getInventory();

        // Compute effective costs (safe mode has no penalty; overclock costs more)
        boolean resuming = this.progress > 0 && this.maxProgress > 0;
        double multiplier = this.isOverclocked ? 10.0 : 1.0;
        long effectiveEnergyCost = (long) (recipe.getEnergyCost() * multiplier);

        double fuelMultiplier = this.isOverclocked ? 5.0 : 1.0;
        long effectiveFuelAmount = (long) (recipe.getFuelAmount() * fuelMultiplier);

        if (!resuming) {
            // Fresh start: reserve and extract fuel + item/fluid inputs, reset progress
            ResourceReservation reservation = reserveStartResources(recipe, storage, src, effectiveFuelAmount);
            if (reservation == null) {
                return List.of(Component.translatable("message.ufo.stellar.err.extract_failed"));
            }
            extractReservation(reservation, storage, src);
            this.maxProgress = recipe.getTime();
            this.progress = 0;
        }

        // Resume (after overheat shutdown): re-pay only the energy cost, keep progress
        this.energyBuffer -= effectiveEnergyCost;
        this.running = true;
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
        return List.of(); // Success
    }

    public void toggleSafeMode() {
        if (!this.running) {
            this.safeMode = !this.safeMode;
            this.setChanged();
        }
    }

    public void toggleAutoStart() {
        this.autoStart = !this.autoStart;
        this.setChanged();
    }

    public void toggleSimulationLock() {
        this.simulationLocked = !this.simulationLocked;
        this.setChanged();
    }

    public void toggleOverclock() {
        if (!this.running) {
            this.isOverclocked = !this.isOverclocked;
            this.setChanged();
        }
    }

    private void processMachineTick() {
        boolean changed = false;
        if (this.energyCapacity != GLOBAL_ENERGY_CAPACITY) {
            this.energyCapacity = GLOBAL_ENERGY_CAPACITY;
            changed = true;
        }

        AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
        changed |= chargeEnergyFromNetwork(nodeBE);

        if (this.activeRecipeId == null) {
            if (changed) {
                this.setChanged();
            }
            return;
        }

        var recipeOpt = this.level.getRecipeManager().byKey(this.activeRecipeId);
        if (recipeOpt.isEmpty() || !(recipeOpt.get().value() instanceof StellarSimulationRecipe recipe)) {
            if (changed) {
                this.setChanged();
            }
            return;
        }

        // Cache the time for UI
        this.maxProgress = recipe.getTime();
        if (!this.running) {
            if (changed) {
                this.setChanged();
            }
            return;
        }

        // ── Active processing ──
        if (nodeBE == null || nodeBE.getActionableNode() == null)
            return;
        IGridNode node = nodeBE.getActionableNode();
        if (node.getGrid() == null)
            return;

        IGrid grid = node.getGrid();
        IActionSource src = IActionSource.ofMachine(nodeBE);
        MEStorage storage = grid.getStorageService().getInventory();

        // Heat generation — increases based on recipe cooling requirement
        // (cooling level + 1) * 40 heat per tick; safe-mode off and overclock each multiply by 3
        int heatPerTick = (recipe.getCoolingLevel() + 1) * 40;
        if (!this.safeMode) heatPerTick *= 3;
        if (this.isOverclocked) heatPerTick *= 5;
        this.heatLevel = Math.min(MAX_HEAT, this.heatLevel + heatPerTick);

        // Coolant consumption from ME network — reduces heat (2.5x in safe mode)
        int coolingApplied = consumeCoolant(recipe, grid);
        this.heatLevel = Math.max(0, this.heatLevel - coolingApplied);

        // Overheat check
        if (this.heatLevel >= MAX_HEAT) {
            if (this.safeMode) {
                this.running = false;
                this.cooldownTimer = this.isOverclocked ? 144000 : COOLDOWN_DURATION;
                this.setChanged();
                if (this.level != null) {
                    BlockPos pos = this.worldPosition;
                    int cooldownMinutes = this.cooldownTimer / 1200;
                    this.level.players().forEach(p -> p.displayClientMessage(
                            Component.translatable("message.ufo.stellar.safe_mode",
                                    pos.toShortString(), cooldownMinutes),
                            false));

                    BlockState state = this.level.getBlockState(this.worldPosition);
                    this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
                }
                return;
            } else {
                triggerStellarExplosion();
                return;
            }
        }

        // Progress and completion (safe mode off = 3x speed, overclock = 3x, stackable to 9x)
        int speed = 1;
        if (!this.safeMode) speed *= 3;
        if (this.isOverclocked) speed *= 5;
        this.progress += speed;
        if (this.progress >= recipe.getTime()) {
            injectOutputs(recipe, storage, src);
            this.progress = 0;
            this.running = false;

            if (this.level != null) {
                BlockState state = this.level.getBlockState(this.worldPosition);
                this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            }
        }
        this.setChanged();
    }

    private boolean chargeEnergyFromNetwork(@Nullable AENetworkedBlockEntity nodeBE) {
        if (nodeBE == null || nodeBE.getActionableNode() == null || this.fieldLevel < 1 || this.fieldLevel > 3) {
            return false;
        }

        IGridNode node = nodeBE.getActionableNode();
        if (node.getGrid() == null) {
            return false;
        }

        long spaceLeft = this.energyCapacity - this.energyBuffer;
        if (spaceLeft <= 0L) {
            return false;
        }

        long toCharge = Math.min(ENERGY_RATE_BY_TIER[this.fieldLevel], spaceLeft);
        if (toCharge <= 0L) {
            return false;
        }

        IEnergyService energy = node.getGrid().getEnergyService();
        double extracted = energy.extractAEPower(toCharge, Actionable.MODULATE, PowerMultiplier.CONFIG);
        long accepted = Math.min(spaceLeft, (long) extracted);
        if (accepted <= 0L) {
            return false;
        }

        this.energyBuffer += accepted;
        return true;
    }

    private void triggerStellarExplosion() {
        if (this.level == null)
            return;
        BlockPos pos = this.worldPosition;

        this.explosionRadius = this.fieldLevel == 3 ? 100 : (this.fieldLevel == 2 ? 50 : 30);

        this.level.players().forEach(p -> p.displayClientMessage(
                Component.translatable("message.ufo.stellar.explosion", pos.toShortString()),
                false));

        // Initial core detonation kicks off the larger wave processor.
        this.level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                Math.max(12.0f, this.explosionRadius * 0.18f), Level.ExplosionInteraction.BLOCK);

        this.exploding = true;
        this.explosionTick = 0;
        this.explosionShellRadius = 0;
        resetExplosionCursor();

        this.running = false;
        this.progress = 0;
        this.heatLevel = 0;
        this.energyBuffer = 0;
        this.assembled = false;

        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }

        onControllerBroken();
    }

    private void processExplosionTick() {
        if (this.level == null) {
            this.exploding = false;
            return;
        }

        this.explosionTick++;
        int processed = 0;
        while (processed < EXPLOSION_BLOCKS_PER_TICK && this.exploding) {
            if (this.explosionShellRadius > this.explosionRadius) {
                finishExplosionWave();
                break;
            }

            int radius = this.explosionShellRadius;
            int stepResult = processExplosionCursor(radius);
            if (stepResult > 0) {
                processed++;
            } else if (stepResult == 0) {
                damageEntitiesForShell(radius);
                spawnExplosionPulse(radius);
                this.explosionShellRadius++;
                resetExplosionCursor();
            }
        }

        this.setChanged();
    }

    private int processExplosionCursor(int radius) {
        if (radius == 0) {
            processExplosionBlock(this.worldPosition, 0);
            return 0;
        }

        if (this.explosionCursorY > radius) {
            return 0;
        }

        int dx = this.explosionCursorX;
        int dy = this.explosionCursorY;
        int dz = this.explosionCursorZ;

        advanceExplosionCursor(radius);

        int distSq = dx * dx + dy * dy + dz * dz;
        int outerSq = radius * radius;
        int innerSq = (radius - 1) * (radius - 1);
        if (distSq > outerSq || distSq <= innerSq) {
            return -1;
        }

        processExplosionBlock(this.worldPosition.offset(dx, dy, dz), radius);
        return 1;
    }

    private void advanceExplosionCursor(int radius) {
        this.explosionCursorX++;
        if (this.explosionCursorX > radius) {
            this.explosionCursorX = -radius;
            this.explosionCursorZ++;
            if (this.explosionCursorZ > radius) {
                this.explosionCursorZ = -radius;
                this.explosionCursorY++;
            }
        }
    }

    private void resetExplosionCursor() {
        this.explosionCursorX = -this.explosionShellRadius;
        this.explosionCursorY = -this.explosionShellRadius;
        this.explosionCursorZ = -this.explosionShellRadius;
    }

    private void processExplosionBlock(BlockPos target, int radius) {
        if (this.level == null || !this.level.isLoaded(target) || !this.level.isInWorldBounds(target)) {
            return;
        }

        BlockState targetState = this.level.getBlockState(target);
        if (targetState.isAir() || targetState.getDestroySpeed(this.level, target) < 0) {
            return;
        }

        int lavaRadius = Math.max(3, (int) (this.explosionRadius * 0.28));
        if (radius <= lavaRadius) {
            this.level.setBlock(target, net.minecraft.world.level.block.Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }

        this.level.setBlock(target, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        BlockPos above = target.above();
        if (this.level.isLoaded(above) && this.level.getBlockState(above).isAir()) {
            this.level.setBlock(above, net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private void damageEntitiesForShell(int radius) {
        if (this.level == null || radius <= 0) {
            return;
        }

        double shell = Math.min(radius + 2.0, this.explosionRadius);
        var entities = this.level.getEntitiesOfClass(
                net.minecraft.world.entity.LivingEntity.class,
                new net.minecraft.world.phys.AABB(
                        this.worldPosition.getX() - shell, this.worldPosition.getY() - shell, this.worldPosition.getZ() - shell,
                        this.worldPosition.getX() + shell, this.worldPosition.getY() + shell, this.worldPosition.getZ() + shell));

        for (var entity : entities) {
            double dist = Math.sqrt(entity.distanceToSqr(this.worldPosition.getCenter()));
            if (dist > shell) {
                continue;
            }

            float damage = (float) Math.max(6.0, (this.explosionRadius - dist) * 1.8);
            entity.hurt(this.level.damageSources().explosion(null), damage);
            entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks(), 200));
        }
    }

    private void spawnExplosionPulse(int radius) {
        if (this.level == null || radius <= 0) {
            return;
        }

        if (radius == 1 || radius == this.explosionRadius || radius % 4 == 0) {
            var random = this.level.getRandom();
            double offsetScale = Math.max(2.0, radius * 0.35);
            double ox = this.worldPosition.getX() + 0.5 + (random.nextDouble() - 0.5) * offsetScale;
            double oy = this.worldPosition.getY() + 0.5 + (random.nextDouble() - 0.5) * offsetScale;
            double oz = this.worldPosition.getZ() + 0.5 + (random.nextDouble() - 0.5) * offsetScale;
            float power = Math.min(18.0f, 4.0f + radius * 0.12f);
            this.level.explode(null, ox, oy, oz, power, Level.ExplosionInteraction.BLOCK);
        }
    }

    private void finishExplosionWave() {
        this.exploding = false;
        this.explosionTick = 0;
        this.explosionShellRadius = 0;
        resetExplosionCursor();
        removeControllerBlockAfterExplosion();
    }

    private void removeControllerBlockAfterExplosion() {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return;
        }

        this.level.removeBlock(this.worldPosition, false);
    }

    private AENetworkedBlockEntity getConnectedNetworkNode() {
        if (this.level == null)
            return null;
        AENetworkedBlockEntity fallback = null;

        for (BlockPos p : this.parts) {
            if (!(this.level.getBlockEntity(p) instanceof AENetworkedBlockEntity nodeBE)) {
                continue;
            }
            if (nodeBE.getActionableNode() == null
                    || nodeBE.getActionableNode().getGrid() == null
                    || !nodeBE.getActionableNode().isActive()) {
                continue;
            }

            BlockState state = this.level.getBlockState(p);
            if (state.is(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())) {
                return nodeBE;
            }
            if (fallback == null && state.is(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())) {
                fallback = nodeBE;
                continue;
            }
            if (fallback == null && state.is(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get())) {
                fallback = nodeBE;
                continue;
            }
            if (fallback == null && state.is(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())) {
                fallback = nodeBE;
            }
        }

        return fallback;
    }



    // ──────────────────── Coolant System ────────────────────

    /** Amount of coolant fluid (mB) consumed per tick during active operation. */
    private static final long COOLANT_CONSUMPTION_PER_TICK = 100;

    /**
     * Tries to extract the recipe's specific coolant fluid from the ME network.
     * Returns the cooling power applied this tick (heat units to subtract).
     * <p>
     * Prioritizes the intended coolant ladder without falling back to water.
     * <p>
     * Coolant effectiveness per tier (per 100 mB per tick):
     * <ul>
     * <li>Gelid Cryotheum (T1): 40 cooling</li>
     * <li>Stable Coolant (T2): 80 cooling</li>
     * <li>Temporal Fluid (T3): 160 cooling</li>
     * </ul>
     * Same-tier coolant exactly offsets the recipe's heat generation,
     * one tier lower lets heat creep up (about 2 minutes to overheat).
     */
    private int consumeCoolant(StellarSimulationRecipe recipe, IGrid grid) {
        AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
        if (nodeBE == null)
            return 0;

        IActionSource src = IActionSource.ofMachine(nodeBE);
        MEStorage storage = grid.getStorageService().getInventory();

        // Try getting fluids based on tier
        AEFluidKey t3 = AEFluidKey.of(BuiltInRegistries.FLUID.get(ResourceLocation.parse("chaosworld_core:source_temporal_fluid")));
        AEFluidKey t2 = AEFluidKey.of(BuiltInRegistries.FLUID.get(ResourceLocation.parse("chaosworld_core:source_stable_coolant")));
        AEFluidKey t1 = AEFluidKey.of(BuiltInRegistries.FLUID.get(ResourceLocation.parse("chaosworld_core:source_gelid_cryotheum")));
        AEFluidKey[] toTry;
        if (this.fieldLevel == 3) {
            toTry = new AEFluidKey[]{t3, t2, t1};
        } else if (this.fieldLevel == 2) {
            toTry = new AEFluidKey[]{t2, t3, t1};
        } else if (this.fieldLevel == 1) {
            toTry = new AEFluidKey[]{t1, t2, t3};
        } else {
            toTry = new AEFluidKey[0];
        }

        // Safe mode off and overclock each consume 3x more coolant per tick
        double multiplier = 1.0;
        if (!this.safeMode) multiplier *= 3.0;
        if (this.isOverclocked) multiplier *= 5.0;
        long effectiveCoolantPerTick = (long) (COOLANT_CONSUMPTION_PER_TICK * multiplier);

        for (AEFluidKey coolantKey : toTry) {
            if (coolantKey == null || coolantKey.getFluid() == net.minecraft.world.level.material.Fluids.EMPTY) continue;
            long extracted = storage.extract(coolantKey, effectiveCoolantPerTick, Actionable.MODULATE, src);
            if (extracted > 0) {
                int efficiency = getCoolantEfficiency(coolantKey.getFluid());

                // Cooling scales with actual consumption: more coolant pumped per tick = more cooling
                return (int) (extracted * efficiency / COOLANT_CONSUMPTION_PER_TICK);
            }
        }

        return 0; // No coolant available — heat will continue to rise!
    }

    private int getCoolantEfficiency(Fluid fluid) {
        if (fluid == ModFluids.SOURCE_TEMPORAL_FLUID.get() || fluid == ModFluids.FLOWING_TEMPORAL_FLUID.get()) {
            return 160;
        }
        if (fluid == ModFluids.SOURCE_STABLE_COOLANT.get() || fluid == ModFluids.FLOWING_STABLE_COOLANT.get()) {
            return 120;
        }
        return 80;
    }

    public ResourceLocation getActiveRecipeId() {
        return this.activeRecipeId;
    }

    public void setActiveRecipe(ResourceLocation activeRecipeId) {
        this.activeRecipeId = activeRecipeId;
        this.progress = 0;
        this.setChanged();

        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.getBlockPos());
            this.level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
        }
    }

    // ──────────────────── Inventory Operations ────────────────────

    private boolean extractInputs(StellarSimulationRecipe recipe, MEStorage storage, IActionSource src) {
        // SIMULATE pass guarantees all inputs exist
        for (var req : recipe.getItemInputs()) {
            if (!req.isEmpty() && simulateExtractItem(req, storage, src) < req.getAmount())
                return false;
        }
        for (var req : recipe.getFluidInputs()) {
            if (!req.isEmpty() && simulateExtractFluid(req, storage, src) < req.getAmount())
                return false;
        }

        // MODULATE pass actually consumes them
        for (var req : recipe.getItemInputs()) {
            if (!req.isEmpty())
                modulateExtractItem(req, storage, src);
        }
        for (var req : recipe.getFluidInputs()) {
            if (!req.isEmpty())
                modulateExtractFluid(req, storage, src);
        }
        return true;
    }

    @Nullable
    private ResourceReservation reserveStartResources(StellarSimulationRecipe recipe, MEStorage storage, IActionSource src, long effectiveFuelAmount) {
        Map<AEItemKey, Long> itemReservations = new HashMap<>();
        Map<AEFluidKey, Long> fluidReservations = new HashMap<>();

        if (!recipe.getFuelFluid().isEmpty() && recipe.getFuelAmount() > 0) {
            ResourceLocation fuelRL = ResourceLocation.parse(recipe.getFuelFluid());
            Fluid fuelFluid = BuiltInRegistries.FLUID.get(fuelRL);
            if (fuelFluid == null || fuelFluid == net.minecraft.world.level.material.Fluids.EMPTY) {
                return null;
            }
            if (!reserveFluid(AEFluidKey.of(fuelFluid), effectiveFuelAmount, fluidReservations, storage, src)) {
                return null;
            }
        }

        for (var req : recipe.getItemInputs()) {
            if (!req.isEmpty() && !reserveItem(req, itemReservations, storage, src)) {
                return null;
            }
        }
        for (var req : recipe.getFluidInputs()) {
            if (!req.isEmpty() && !reserveFluid(req, fluidReservations, storage, src)) {
                return null;
            }
        }

        return new ResourceReservation(itemReservations, fluidReservations);
    }

    private boolean reserveItem(IngredientStack.Item req, Map<AEItemKey, Long> reservations, MEStorage storage, IActionSource src) {
        long amount = req.getAmount();
        for (ItemStack match : req.getIngredient().getItems()) {
            AEItemKey key = AEItemKey.of(match);
            long reserved = reservations.getOrDefault(key, 0L);
            long neededWithReservation = saturatedAdd(reserved, amount);
            long available = storage.extract(key, neededWithReservation, Actionable.SIMULATE, src);
            if (available >= neededWithReservation) {
                reservations.put(key, neededWithReservation);
                return true;
            }
        }
        return false;
    }

    private boolean reserveFluid(IngredientStack.Fluid req, Map<AEFluidKey, Long> reservations, MEStorage storage, IActionSource src) {
        long amount = req.getAmount();
        for (FluidStack match : req.getIngredient().getStacks()) {
            if (reserveFluid(AEFluidKey.of(match.getFluid()), amount, reservations, storage, src)) {
                return true;
            }
        }
        return false;
    }

    private boolean reserveFluid(AEFluidKey key, long amount, Map<AEFluidKey, Long> reservations, MEStorage storage, IActionSource src) {
        if (amount <= 0L) {
            return true;
        }
        long reserved = reservations.getOrDefault(key, 0L);
        long neededWithReservation = saturatedAdd(reserved, amount);
        long available = storage.extract(key, neededWithReservation, Actionable.SIMULATE, src);
        if (available < neededWithReservation) {
            return false;
        }
        reservations.put(key, neededWithReservation);
        return true;
    }

    private void extractReservation(ResourceReservation reservation, MEStorage storage, IActionSource src) {
        for (var entry : reservation.itemReservations().entrySet()) {
            storage.extract(entry.getKey(), entry.getValue(), Actionable.MODULATE, src);
        }
        for (var entry : reservation.fluidReservations().entrySet()) {
            storage.extract(entry.getKey(), entry.getValue(), Actionable.MODULATE, src);
        }
    }

    private long saturatedAdd(long a, long b) {
        if (b > 0L && a > Long.MAX_VALUE - b) {
            return Long.MAX_VALUE;
        }
        return a + b;
    }

    private long simulateExtractItem(IngredientStack.Item req, MEStorage storage, IActionSource src) {
        long extracted = 0;
        long needed = req.getAmount();
        for (ItemStack match : req.getIngredient().getItems()) {
            long ext = storage.extract(AEItemKey.of(match), needed, Actionable.SIMULATE, src);
            extracted += ext;
            needed -= ext;
            if (needed <= 0)
                break;
        }
        return extracted;
    }

    private void modulateExtractItem(IngredientStack.Item req, MEStorage storage, IActionSource src) {
        long needed = req.getAmount();
        for (ItemStack match : req.getIngredient().getItems()) {
            long ext = storage.extract(AEItemKey.of(match), needed, Actionable.MODULATE, src);
            needed -= ext;
            if (needed <= 0)
                break;
        }
    }

    private long simulateExtractFluid(IngredientStack.Fluid req, MEStorage storage, IActionSource src) {
        long extracted = 0;
        long needed = req.getAmount();
        for (FluidStack match : req.getIngredient().getStacks()) {
            long ext = storage.extract(AEFluidKey.of(match.getFluid()), needed, Actionable.SIMULATE, src);
            extracted += ext;
            needed -= ext;
            if (needed <= 0)
                break;
        }
        return extracted;
    }

    private void modulateExtractFluid(IngredientStack.Fluid req, MEStorage storage, IActionSource src) {
        long needed = req.getAmount();
        for (FluidStack match : req.getIngredient().getStacks()) {
            long ext = storage.extract(AEFluidKey.of(match.getFluid()), needed, Actionable.MODULATE, src);
            needed -= ext;
            if (needed <= 0)
                break;
        }
    }

    private void injectOutputs(StellarSimulationRecipe recipe, MEStorage storage, IActionSource src) {
        for (GenericStack out : recipe.getItemOutputs()) {
            storage.insert(out.what(), out.amount(), Actionable.MODULATE, src);
        }
        for (GenericStack out : recipe.getFluidOutputs()) {
            storage.insert(out.what(), out.amount(), Actionable.MODULATE, src);
        }
    }

    private record ResourceReservation(Map<AEItemKey, Long> itemReservations, Map<AEFluidKey, Long> fluidReservations) {
    }

    public void markStructureDirty() {
        this.structureDirty = true;
        this.scanCooldown = 0;
    }

    public void onControllerBroken() {
        if (this.level == null)
            return;
        for (BlockPos partPos : this.parts) {
            if (this.level.getBlockEntity(partPos) instanceof IMultiblockPart part) {
                part.unlinkFromController();
            }
        }
        this.parts.clear();
        this.assembled = false;
    }

    // ──────────────────── Utility ────────────────────

    private static String formatAmount(long amount) {
        if (amount >= 1_000_000_000)
            return String.format("%.1fB", amount / 1_000_000_000.0);
        if (amount >= 1_000_000)
            return String.format("%.1fM", amount / 1_000_000.0);
        if (amount >= 1_000)
            return String.format("%.1fK", amount / 1_000.0);
        return String.valueOf(amount);
    }

    private static String toRoman(int tier) {
        return switch (tier) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> String.valueOf(tier);
        };
    }

    /**
     * Converts a fluid registry path to a human-readable name.
     * e.g., "source_gelid_cryotheum" → "Gelid Cryotheum"
     */
    private static String formatFluidName(String path) {
        if (path.startsWith("source_"))
            path = path.substring(7);
        if (path.startsWith("flowing_"))
            path = path.substring(8);
        String[] words = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    // ──────────────────── Pattern Definition ────────────────────

    private static MultiblockPattern getPattern() {
        if (PATTERN == null) {
            PATTERN = StellarNexusPatternFactory.getPattern();
        }
        return PATTERN;
    }

    // ──────────────────── Menu Provider ────────────────────

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.chaosworld_core.stellar_nexus_controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new StellarNexusControllerMenu(id, playerInventory, this, this.data);
    }

    // ──────────────────── NBT Persistence ────────────────────

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("assembled", this.assembled);
        tag.putBoolean("running", this.running);
        tag.putBoolean("safeMode", this.safeMode);

        tag.putInt("progress", this.progress);
        tag.putInt("heatLevel", this.heatLevel);
        tag.putInt("cooldownTimer", this.cooldownTimer);
        tag.putLong("energyBuffer", this.energyBuffer);
        tag.putLong("energyCapacity", this.energyCapacity);
        
        tag.putBoolean("autoStart", this.autoStart);
        tag.putBoolean("simulationLocked", this.simulationLocked);

        if (this.activeRecipeId != null) {
            tag.putString("activeRecipeId", this.activeRecipeId.toString());
        }

        ListTag partsList = new ListTag();
        for (BlockPos pos : this.parts) {
            partsList.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("parts", partsList);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.assembled = tag.getBoolean("assembled");
        this.running = tag.getBoolean("running");
        this.safeMode = tag.getBoolean("safeMode");

        this.progress = tag.getInt("progress");
        this.heatLevel = tag.getInt("heatLevel");
        this.cooldownTimer = tag.getInt("cooldownTimer");
        
        this.autoStart = tag.getBoolean("autoStart");
        this.simulationLocked = tag.getBoolean("simulationLocked");

        // Backward compat: read old "fuelBuffer"/"fuelCapacity" tags as energy
        if (tag.contains("energyBuffer")) {
            this.energyBuffer = tag.getLong("energyBuffer");
        } else if (tag.contains("fuelBuffer")) {
            this.energyBuffer = tag.getLong("fuelBuffer");
        }
        if (tag.contains("energyCapacity")) {
            this.energyCapacity = tag.getLong("energyCapacity");
        } else if (tag.contains("fuelCapacity")) {
            this.energyCapacity = tag.getLong("fuelCapacity");
        }

        if (tag.contains("activeRecipeId", Tag.TAG_STRING)) {
            this.activeRecipeId = ResourceLocation.parse(tag.getString("activeRecipeId"));
        } else {
            this.activeRecipeId = null;
        }

        this.parts.clear();
        if (tag.contains("parts", Tag.TAG_LIST)) {
            ListTag partsList = tag.getList("parts", Tag.TAG_COMPOUND);
            for (int i = 0; i < partsList.size(); i++) {
                NbtUtils.readBlockPos(partsList.getCompound(i), "").ifPresent(this.parts::add);
            }
        }

        this.structureDirty = true;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
}

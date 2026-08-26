package com.yongaishide.chaosworld.item.custom.cell;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.util.ConfigInventory;
import cn.dancingsnow.neoecoae.api.storage.ECOStorageCells;
import cn.dancingsnow.neoecoae.api.storage.IECOCellHandler;
import cn.dancingsnow.neoecoae.api.storage.IECOStorageCell;
import com.wintercogs.ae2omnicells.common.init.OCDataComponents;
import com.wintercogs.ae2omnicells.common.me.AEUniversalCellData;
import com.wintercogs.ae2omnicells.common.me.AEUniversalCellInventory;
import com.wintercogs.ae2omnicells.common.me.IAEUniversalCell;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Registers the Quantum Omni storage matrices into the NeoECOAE ECO storage subsystem
 * (ECO Drive). Uses the OmniCells universal storage backend directly (the item itself
 * is deliberately not an IAEUniversalCell, so it can only enter the ECO drive).
 * Includes the UUID ownership guard against duplicated storage being loaded by a second host.
 */
public final class QuantumOmniCellHandler implements IECOCellHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantumOmniCellHandler.class);
    private static final String UUID_TAG = "ae_universal_cell_uuid";

    public static final QuantumOmniCellHandler INSTANCE = new QuantumOmniCellHandler();

    private final Map<UUID, ISaveProvider> owners = new HashMap<>();
    private final Map<ISaveProvider, UUID> ownerIds = new IdentityHashMap<>();

    private QuantumOmniCellHandler() {
    }

    public static void register() {
        ECOStorageCells.register(INSTANCE);
    }

    @Override
    public boolean isCell(ItemStack stack) {
        return stack.getItem() instanceof QuantumOmniStorageCellItem && stack.getCount() == 1;
    }

    @Override
    public @Nullable IECOStorageCell getCellInventory(ItemStack stack, @Nullable ISaveProvider host) {
        if (ServerLifecycleHooks.getCurrentServer() == null) return null;
        Item item = stack.getItem();
        if (!(item instanceof QuantumOmniStorageCellItem cellItem)) return null;
        if (stack.getCount() != 1) return null;

        boolean hadCellId = stack.has(OCDataComponents.CELL_UUID.get());
        AEUniversalCellData cellData = AEUniversalCellData.computeIfAbsentCellDataForItemStack(stack);
        if (cellData == null) return null;
        if (!hadCellId && host != null) {
            host.saveChanges();
        }

        StorageCell delegate = new AEUniversalCellInventory(cellData, stack, new UniversalCellAdapter(cellItem), host);
        return new QuantumOmniCell(delegate, stack, cellItem);
    }

    @Override
    public synchronized void releaseCellInventory(@Nullable ItemStack stack, @Nullable ISaveProvider host) {
        if (host == null) {
            return;
        }
        UUID id = this.ownerIds.get(host);
        if (id == null) {
            id = getStorageId(stack);
        }
        if (id != null && this.owners.get(id) == host) {
            this.owners.remove(id);
            this.ownerIds.remove(host);
        }
    }

    @Override
    public synchronized void clearRuntimeState() {
        this.owners.clear();
        this.ownerIds.clear();
    }

    private synchronized boolean claimUniqueStorage(ItemStack stack, ISaveProvider host) {
        UUID currentId = getStorageId(stack);
        if (currentId == null) {
            return false;
        }
        ISaveProvider owner = this.owners.get(currentId);
        if (owner == null || owner == host) {
            this.claim(currentId, host);
            return false;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String originalId = tag.getString(UUID_TAG);
        tag.remove(UUID_TAG);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        AEUniversalCellData replacement = AEUniversalCellData.computeIfAbsentCellDataForItemStack(stack);
        UUID replacementId = getStorageId(stack);
        if (replacement == null || replacementId == null || replacementId.equals(currentId)) {
            tag.putString(UUID_TAG, originalId);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            throw new IllegalStateException("Unable to detach duplicated Omni storage UUID " + currentId);
        }
        replacement.getOriginalStorage().clear();
        replacement.setDirty();
        this.claim(replacementId, host);
        LOGGER.warn("Detached duplicated Omni storage UUID {} -> {} for a second ECO drive host; replacement starts empty", currentId, replacementId);
        return true;
    }

    private void claim(UUID id, ISaveProvider host) {
        UUID previousId = this.ownerIds.put(host, id);
        if (previousId != null && !previousId.equals(id) && this.owners.get(previousId) == host) {
            this.owners.remove(previousId);
        }
        this.owners.put(id, host);
    }

    private static @Nullable UUID getStorageId(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(UUID_TAG)) {
            return null;
        }
        try {
            return UUID.fromString(tag.getString(UUID_TAG));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * Bridges the standalone item to the OmniCells inventory's IAEUniversalCell view.
     * Total bytes/types are reported as unlimited (-1); the real capacity is enforced
     * by the QuantumOmniCell wrapper.
     */
    private static final class UniversalCellAdapter implements IAEUniversalCell, ICellWorkbenchItem {

        private final QuantumOmniStorageCellItem item;

        private UniversalCellAdapter(QuantumOmniStorageCellItem item) {
            this.item = item;
        }

        @Override
        public int getTotalBytes() {
            return -1;
        }

        @Override
        public int getTotalTypes() {
            return -1;
        }

        @Override
        public double getIdleDrain() {
            return item.getIdleDrain();
        }

        @Override
        public IUpgradeInventory getUpgrades(ItemStack is) {
            return item.getUpgrades(is);
        }

        @Override
        public Item asItem() {
            return item;
        }

        @Override
        public ConfigInventory getConfigInventory(ItemStack is) {
            return item.getConfigInventory(is);
        }

        @Override
        public FuzzyMode getFuzzyMode(ItemStack is) {
            return item.getFuzzyMode(is);
        }

        @Override
        public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
            item.setFuzzyMode(is, fzMode);
        }
    }
}

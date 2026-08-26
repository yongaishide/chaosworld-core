package com.yongaishide.chaosworld.item.custom.cell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import cn.dancingsnow.neoecoae.api.IECOTier;
import cn.dancingsnow.neoecoae.api.storage.ECOCellType;
import cn.dancingsnow.neoecoae.api.storage.IECOStorageCell;
import com.wintercogs.ae2omnicells.common.me.IAEUniversalCell;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Adapter exposing the OmniCells universal storage to the NeoECOAE ECO storage subsystem.
 * Faithful port of NeoECOAE's ECOUniversalStorageCell.
 */
public final class QuantumOmniCell implements IECOStorageCell {

    private final StorageCell delegate;
    private final ItemStack stack;
    private final QuantumOmniStorageCellItem item;

    public QuantumOmniCell(StorageCell delegate, ItemStack stack, QuantumOmniStorageCellItem item) {
        this.delegate = delegate;
        this.stack = stack;
        this.item = item;
    }

    @Override
    public IECOTier getTier() {
        return this.item.getTier();
    }

    @Override
    public ECOCellType getCellType() {
        return this.item.getCellType();
    }

    @Override
    public long getStoredItemTypes() {
        return IAEUniversalCell.getUsedTypes(this.stack);
    }

    @Override
    public long getTotalItemTypes() {
        return 0;
    }

    @Override
    public boolean hasInfiniteTypeCapacity() {
        return true;
    }

    @Override
    public long getUsedBytes() {
        return IAEUniversalCell.getUsedBytes(this.stack);
    }

    @Override
    public long getTotalBytes() {
        return this.item.getECOStorageTotalBytes();
    }

    @Override
    public CellState getStatus() {
        if (this.item.isExternallyUnlimited() && this.getUsedBytes() >= this.getTotalBytes()) {
            return CellState.FULL;
        }
        return this.delegate.getStatus();
    }

    @Override
    public double getIdleDrain() {
        return this.delegate.getIdleDrain();
    }

    @Override
    public boolean canFitInsideCell() {
        return this.delegate.canFitInsideCell();
    }

    @Override
    public void persist() {
        this.delegate.persist();
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return this.delegate.isPreferredStorageFor(what, source);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!this.item.isExternallyUnlimited()) {
            return this.delegate.insert(what, amount, mode, source);
        }
        long amountPerByte = Math.max(1L, what.getType().getAmountPerByte());
        long freeBytes = Math.max(0L, this.getTotalBytes() - this.getUsedBytes());
        long capacityBound = saturatingMultiply(freeBytes, amountPerByte);
        return this.delegate.insert(what, Math.min(amount, capacityBound), mode, source);
    }

    private static long saturatingMultiply(long left, long right) {
        if (left == 0L || right == 0L) {
            return 0L;
        }
        return left > Long.MAX_VALUE / right ? Long.MAX_VALUE : left * right;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        return this.delegate.extract(what, amount, mode, source);
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        this.delegate.getAvailableStacks(out);
    }

    @Override
    public Component getDescription() {
        return this.delegate.getDescription();
    }
}

package com.yongaishide.chaosworld.patch.contents;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.raishxn.ufo.item.custom.cell.AEBigIntegerCellData;
import com.raishxn.ufo.item.custom.cell.IAEBigIntegerCell;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChaosUniversalBigCellInventory implements StorageCell {

    private final @NotNull AEBigIntegerCellData cellData;
    private final @NotNull Object2ObjectMap<AEKey, BigInteger> storage;
    private final @NotNull ItemStack itemStack;
    private final @NotNull IAEBigIntegerCell cellType;
    private final @Nullable ISaveProvider saveContainer;
    private final Long2ObjectOpenHashMap<BigInteger> bucketSums = new Long2ObjectOpenHashMap<>();
    private BigInteger usedBytesCached = BigInteger.ZERO;
    private boolean isPersisted = false;

    public ChaosUniversalBigCellInventory(@NotNull AEBigIntegerCellData cellData, @NotNull ItemStack itemStack,
            @NotNull IAEBigIntegerCell cellType, @Nullable ISaveProvider saveProvider) {
        this.cellData = cellData;
        this.storage = cellData.getOriginalStorage();
        this.itemStack = itemStack;
        this.cellType = cellType;
        this.saveContainer = saveProvider;

        this.bucketSums.defaultReturnValue(BigInteger.ZERO);
        ObjectIterator<Object2ObjectMap.Entry<AEKey, BigInteger>> iterator = this.storage.object2ObjectEntrySet()
                .iterator();
        while (iterator.hasNext()) {
            Object2ObjectMap.Entry<AEKey, BigInteger> entry = iterator.next();
            BigInteger value = nonNegative(entry.getValue());
            if (value.signum() <= 0) {
                continue;
            }
            long amountPerByte = Math.max(1L, entry.getKey().getType().getAmountPerByte());
            this.bucketSums.put(amountPerByte, this.bucketSums.get(amountPerByte).add(value));
        }
        this.recalculateUsedBytes();
        this.updateItemTooltipState();
    }

    @Override
    public CellState getStatus() {
        return this.storage.isEmpty() ? CellState.EMPTY : CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return this.cellType.getIdleDrain();
    }

    @Override
    public boolean canFitInsideCell() {
        return true;
    }

    @Override
    public void persist() {
        if (this.isPersisted) {
            return;
        }
        this.updateItemTooltipState();
        this.isPersisted = true;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount <= 0L) {
            return 0L;
        }
        if (this.cellType.getKeyType() != null && what.getType() != this.cellType.getKeyType()) {
            return 0L;
        }
        if (this.cellType.isBlackListed(this.itemStack, what)) {
            return 0L;
        }
        if (!this.matchesPartitionAndUpgrades(what)) {
            return 0L;
        }
        if (!this.canNestStorageCells(what)) {
            return 0L;
        }

        long amountPerByte = Math.max(1L, what.getType().getAmountPerByte());
        BigInteger current = nonNegative(this.storage.get(what));

        long maxBytesCap = this.cellType.getMaxBytes(this.itemStack);
        int maxTypesCap = this.cellType.getMaxTypes(this.itemStack);
        int overhead = Math.max(0, this.cellType.getBytesPerType(this.itemStack));

        if (maxBytesCap != Long.MAX_VALUE) {
            long usedBytes = clampToLong(this.usedBytesCached);
            int typesUsed = this.storage.size();
            long freeBytes = maxBytesCap - usedBytes;
            if (current.signum() == 0) {
                if (typesUsed >= maxTypesCap) {
                    return 0L;
                }
                freeBytes -= overhead;
            }
            if (freeBytes <= 0L) {
                return 0L;
            }
            long maxItemsFit = freeBytes > Long.MAX_VALUE / amountPerByte ? Long.MAX_VALUE
                    : freeBytes * amountPerByte;
            if (amount > maxItemsFit) {
                amount = maxItemsFit;
            }
        } else if (current.signum() == 0 && this.storage.size() >= maxTypesCap) {
            return 0L;
        }

        if (amount <= 0L) {
            return 0L;
        }

        if (mode == Actionable.MODULATE) {
            BigInteger bucket = this.bucketSums.get(amountPerByte);
            BigInteger newBucket = bucket.add(BigInteger.valueOf(amount));
            this.usedBytesCached = this.usedBytesCached
                    .add(ceilDiv(newBucket, BigInteger.valueOf(amountPerByte))
                            .subtract(ceilDiv(bucket, BigInteger.valueOf(amountPerByte))));
            this.bucketSums.put(amountPerByte, newBucket);
            this.storage.put(what, current.add(BigInteger.valueOf(amount)));
            this.markChanged();
        }
        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount <= 0L) {
            return 0L;
        }
        BigInteger current = nonNegative(this.storage.get(what));
        if (current.signum() <= 0) {
            return 0L;
        }

        long taken = Math.min(amount, clampToLong(current));
        if (taken <= 0L) {
            return 0L;
        }

        if (mode == Actionable.MODULATE) {
            long amountPerByte = Math.max(1L, what.getType().getAmountPerByte());
            BigInteger bucket = this.bucketSums.get(amountPerByte);
            BigInteger newBucket = bucket.subtract(BigInteger.valueOf(taken));
            if (newBucket.signum() < 0) {
                newBucket = BigInteger.ZERO;
            }
            this.usedBytesCached = this.usedBytesCached
                    .add(ceilDiv(newBucket, BigInteger.valueOf(amountPerByte))
                            .subtract(ceilDiv(bucket, BigInteger.valueOf(amountPerByte))));
            if (newBucket.signum() > 0) {
                this.bucketSums.put(amountPerByte, newBucket);
            } else {
                this.bucketSums.remove(amountPerByte);
            }

            BigInteger next = current.subtract(BigInteger.valueOf(taken));
            if (next.signum() > 0) {
                this.storage.put(what, next);
            } else {
                this.storage.remove(what);
            }
            this.markChanged();
        }

        return taken;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        ObjectIterator<Object2ObjectMap.Entry<AEKey, BigInteger>> iterator = this.storage.object2ObjectEntrySet()
                .iterator();
        while (iterator.hasNext()) {
            Object2ObjectMap.Entry<AEKey, BigInteger> entry = iterator.next();
            BigInteger value = nonNegative(entry.getValue());
            if (value.signum() <= 0) {
                continue;
            }
            long existing = out.get(entry.getKey());
            long headroom = existing <= 0L ? Long.MAX_VALUE : Long.MAX_VALUE - existing;
            if (headroom <= 0L) {
                continue;
            }
            long add = clampToLong(value);
            if (add > headroom) {
                add = headroom;
            }
            if (add > 0L) {
                out.add(entry.getKey(), add);
            }
        }
    }

    @Override
    public Component getDescription() {
        return this.itemStack.getHoverName();
    }

    private boolean canNestStorageCells(AEKey what) {
        if (what instanceof AEItemKey itemKey) {
            ItemStack nestedStack = itemKey.toStack();
            StorageCell nested = StorageCells.getCellInventory(nestedStack, null);
            return nested == null || nested.canFitInsideCell();
        }
        return true;
    }

    private boolean matchesPartitionAndUpgrades(AEKey what) {
        IUpgradeInventory upgrades = this.cellType.getUpgrades(this.itemStack);
        boolean hasInverter = upgrades.isInstalled(AEItems.INVERTER_CARD);
        boolean hasFuzzy = upgrades.isInstalled(AEItems.FUZZY_CARD);

        ConfigInventory config = null;
        FuzzyMode fuzzyMode = FuzzyMode.IGNORE_ALL;
        if (this.cellType instanceof ICellWorkbenchItem cellWorkbenchItem) {
            config = cellWorkbenchItem.getConfigInventory(this.itemStack);
            if (hasFuzzy) {
                fuzzyMode = cellWorkbenchItem.getFuzzyMode(this.itemStack);
            }
        }

        ConfigInventory resolvedConfig = config;
        IPartitionList.Builder partitionBuilder = IPartitionList.builder();
        if (hasFuzzy) {
            partitionBuilder.fuzzyMode(fuzzyMode);
        }
        if (resolvedConfig != null) {
            partitionBuilder.addAll(resolvedConfig.keySet());
        }

        IncludeExclude mode = hasInverter ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST;
        return partitionBuilder.build().matchesFilter(what, mode);
    }

    private void markChanged() {
        this.cellData.setDirty();
        this.isPersisted = false;
        if (this.saveContainer != null) {
            this.saveContainer.saveChanges();
        } else {
            this.persist();
        }
    }

    private void updateItemTooltipState() {
        BigInteger used = this.usedBytesCached.signum() > 0 ? this.usedBytesCached : BigInteger.ZERO;
        IAEBigIntegerCell.setUsedBytes(this.itemStack, used);
        IAEBigIntegerCell.setUsedTypes(this.itemStack, this.storage.size());
        IAEBigIntegerCell.setCellState(this.itemStack, this.getStatus());

        List<GenericStack> show = new ArrayList<>(5);
        int count = 0;
        ObjectIterator<Object2ObjectMap.Entry<AEKey, BigInteger>> iterator = this.storage.object2ObjectEntrySet()
                .iterator();
        while (iterator.hasNext()) {
            Object2ObjectMap.Entry<AEKey, BigInteger> entry = iterator.next();
            BigInteger value = nonNegative(entry.getValue());
            if (value.signum() <= 0) {
                continue;
            }
            show.add(new GenericStack(entry.getKey(), clampToLong(value)));
            if (++count >= 5) {
                break;
            }
        }
        IAEBigIntegerCell.setTooltipShowStacks(this.itemStack, show);
    }

    private void recalculateUsedBytes() {
        BigInteger total = BigInteger.ZERO;
        ObjectIterator<it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<BigInteger>> iterator = this.bucketSums
                .long2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<BigInteger> entry = iterator.next();
            total = total.add(ceilDiv(entry.getValue(), BigInteger.valueOf(entry.getLongKey())));
        }
        this.usedBytesCached = total;
    }

    private static BigInteger ceilDiv(BigInteger value, BigInteger divisor) {
        if (divisor.signum() <= 0) {
            throw new IllegalArgumentException("div by non-positive");
        }
        if (value.signum() <= 0) {
            return BigInteger.ZERO;
        }
        return value.add(divisor.subtract(BigInteger.ONE)).divide(divisor);
    }

    private static long clampToLong(BigInteger value) {
        if (value.signum() <= 0) {
            return 0L;
        }
        if (value.bitLength() > 63) {
            return Long.MAX_VALUE;
        }
        long result = value.longValue();
        return result < 0 ? Long.MAX_VALUE : result;
    }

    private static BigInteger nonNegative(BigInteger value) {
        return value == null || value.signum() <= 0 ? BigInteger.ZERO : value;
    }
}

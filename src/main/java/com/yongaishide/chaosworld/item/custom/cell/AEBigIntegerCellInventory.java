package com.yongaishide.chaosworld.item.custom.cell;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.*;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigInventory;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * BigInteger 版本的AEUniversalCellInventory内部存储。
 * <p>
 * 由于AE2以及绝大部分正常模组的api都不会使用BigInteger。故，此仓库计划仅用于创造元件，有关容量检查以及与容量相关升级卡的部分均被移除。
 *
 * @author Frostbite
 */
public class AEBigIntegerCellInventory implements StorageCell
{

    private final @NotNull AEBigIntegerCellData cellData;
    private final @NotNull Object2ObjectMap<AEKey, BigInteger> storage;
    private final @NotNull ItemStack itemStack;
    private final @NotNull IAEBigIntegerCell cellType;
    private final @Nullable ISaveProvider saveContainer;
    private BigInteger usedBytesCached;
    private boolean isPersisted = false;
    private final Long2ObjectOpenHashMap<BigInteger> bucketSums = new Long2ObjectOpenHashMap<>();

    public AEBigIntegerCellInventory(@NotNull AEBigIntegerCellData cellData,
                                     @NotNull ItemStack itemStack,
                                     @NotNull IAEBigIntegerCell cellType,
                                     @Nullable ISaveProvider saveProvider)
    {
        this.cellData = cellData;
        this.storage = cellData.getOriginalStorage();
        this.itemStack = itemStack;
        this.cellType = cellType;
        this.saveContainer = saveProvider;

        this.bucketSums.defaultReturnValue(BigInteger.ZERO);
        for (Object2ObjectMap.Entry<AEKey, BigInteger> e : storage.object2ObjectEntrySet())
        {
            BigInteger v = nonNegative(e.getValue());
            if (v.signum() <= 0) continue;
            long apb = Math.max(1, e.getKey().getType().getAmountPerByte());
            bucketSums.put(apb, bucketSums.get(apb).add(v));
        }

        BigInteger bytesForValues = BigInteger.ZERO;
        for (var e : bucketSums.long2ObjectEntrySet())
        {
            bytesForValues = bytesForValues.add(ceilDiv(e.getValue(), BigInteger.valueOf(e.getLongKey())));
        }
        this.usedBytesCached = bytesForValues;
        updateItemTooltipState();
    }
    @Override
    public CellState getStatus()
    {
        if (storage.isEmpty()) return CellState.EMPTY;
        else return CellState.NOT_EMPTY;
    }
    @Override
    public double getIdleDrain()
    {
        return cellType.getIdleDrain();
    }
    @Override
    public boolean canFitInsideCell()
    {
        return true;
    }
    @Override
    public void persist()
    {
        if (isPersisted) return;

        updateItemTooltipState();
        isPersisted = true;
    }
    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source)
    {
        if (amount <= 0) return 0;
        if (cellType.getKeyType() != null && what.getType() != cellType.getKeyType()) return 0;
        if (cellType.isBlackListed(itemStack, what)) return 0;
        if (!matchesPartitionAndUpgrades(what)) return 0;
        if (!canNestStorageCells(what)) return 0;

        final long apb = Math.max(1, what.getType().getAmountPerByte());
        final BigInteger current = nonNegative(storage.get(what));

        long maxBytesCap = cellType.getMaxBytes(itemStack);
        int maxTypesCap = cellType.getMaxTypes(itemStack);
        int overhead = cellType.getBytesPerType(itemStack);

        if (maxBytesCap != Long.MAX_VALUE) {
            long usedBytes = clampToLong(usedBytesCached);
            int typesUsed = storage.size();
            long freeBytes = maxBytesCap - usedBytes;

            if (current.signum() == 0) {
                if (typesUsed >= maxTypesCap) return 0;
                freeBytes -= overhead;
            }

            if (freeBytes <= 0) return 0;

            long maxItemsFit = freeBytes > Long.MAX_VALUE / apb ? Long.MAX_VALUE : freeBytes * apb;
            if (amount > maxItemsFit) {
                amount = maxItemsFit;
            }
        }

        if (amount <= 0) return 0;

        if (mode == Actionable.MODULATE)
        {
            BigInteger bucket = bucketSums.get(apb);
            BigInteger newBucket = bucket.add(BigInteger.valueOf(amount));
            usedBytesCached = usedBytesCached.add(
                    ceilDiv(newBucket, BigInteger.valueOf(apb)).subtract(ceilDiv(bucket, BigInteger.valueOf(apb))));
            bucketSums.put(apb, newBucket);
            storage.put(what, current.add(BigInteger.valueOf(amount)));
            markChanged();
        }
        return amount;
    }
    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source)
    {
        if (amount <= 0) return 0;

        final BigInteger current = nonNegative(storage.get(what));
        if (current.signum() <= 0) return 0;

        final long currentAsLongCap = clampToLong(current);
        final long taken = Math.min(amount, currentAsLongCap);
        if (taken <= 0) return 0;

        if (mode == Actionable.MODULATE)
        {
            final long apb = Math.max(1, what.getType().getAmountPerByte());
            BigInteger bucket = bucketSums.get(apb);
            BigInteger newBucket = bucket.subtract(BigInteger.valueOf(taken));
            if (newBucket.signum() < 0) newBucket = BigInteger.ZERO;
            usedBytesCached = usedBytesCached.add(
                    ceilDiv(newBucket, BigInteger.valueOf(apb)).subtract(ceilDiv(bucket, BigInteger.valueOf(apb))));
            if (newBucket.signum() > 0)
            {
                bucketSums.put(apb, newBucket);
            }
            else
            {
                bucketSums.remove(apb);
            }

            BigInteger next = current.subtract(BigInteger.valueOf(taken));
            if (next.signum() > 0)
            {
                storage.put(what, next);
            }
            else
            {
                storage.remove(what);
            }
            markChanged();
        }
        return taken;
    }

    @Override
    public void getAvailableStacks(KeyCounter out)
    {
        for (Object2ObjectMap.Entry<AEKey, BigInteger> entry : storage.object2ObjectEntrySet())
        {
            BigInteger value = nonNegative(entry.getValue());
            if (value.signum() <= 0) continue;
            long existing = out.get(entry.getKey());
            long headroom = (existing <= 0) ? Long.MAX_VALUE : (Long.MAX_VALUE - existing);
            if (headroom <= 0) continue;

            long add = clampToLong(value);

            if (add > headroom) add = headroom;

            if (add > 0) out.add(entry.getKey(), add);
        }
    }

    @Override
    public Component getDescription()
    {
        return this.itemStack.getHoverName();
    }
    private boolean canNestStorageCells(AEKey what)
    {
        if (what instanceof AEItemKey itemKey)
        {
            ItemStack s = itemKey.toStack();
            StorageCell nested = StorageCells.getCellInventory(s, null);
            return nested == null || nested.canFitInsideCell();
        }
        return true;
    }
    private boolean matchesPartitionAndUpgrades(AEKey what)
    {
        // 升级槽
        final IUpgradeInventory upgrades = cellType.getUpgrades(itemStack);
        final boolean hasInverter = upgrades.isInstalled(AEItems.INVERTER_CARD);
        final boolean hasFuzzy = upgrades.isInstalled(AEItems.FUZZY_CARD);

        // 分区配置
        ConfigInventory config = null;
        FuzzyMode fuzzyMode = FuzzyMode.IGNORE_ALL;
        if (cellType instanceof ICellWorkbenchItem cellWorkbenchItem)
        {
            config = cellWorkbenchItem.getConfigInventory(itemStack);
            if (hasFuzzy) fuzzyMode = cellWorkbenchItem.getFuzzyMode(itemStack);
        }
        if (config == null || config.keySet().isEmpty())
        {
            return true;
        }

        IncludeExclude mode = hasInverter ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST;


        return hasInverter;
    }
    private void markChanged()
    {
        cellData.setDirty();

        isPersisted = false;
        if (saveContainer != null)
            saveContainer.saveChanges();
        else
            persist();
    }
    private void updateItemTooltipState()
    {
        BigInteger used = usedBytesCached.signum() > 0 ? usedBytesCached : BigInteger.ZERO;

        IAEBigIntegerCell.setUsedBytes(itemStack, used);
        IAEBigIntegerCell.setUsedTypes(itemStack, storage.size());
        IAEBigIntegerCell.setCellState(itemStack, getStatus());
        List<GenericStack> show = new ArrayList<>(5);
        int count = 0;
        for (Object2ObjectMap.Entry<AEKey, BigInteger> e : storage.object2ObjectEntrySet())
        {
            BigInteger v = nonNegative(e.getValue());
            if (v.signum() <= 0) continue;
            show.add(new GenericStack(e.getKey(), clampToLong(v)));
            if (++count >= 5) break;
        }
        IAEBigIntegerCell.setTooltipShowStacks(itemStack, show);
    }
    private static BigInteger ceilDiv(BigInteger a, BigInteger b)
    {
        if (b.signum() <= 0) throw new IllegalArgumentException("div by non-positive");
        if (a.signum() <= 0) return BigInteger.ZERO;
        return a.add(b.subtract(BigInteger.ONE)).divide(b);
    }
    private static long clampToLong(BigInteger v)
    {
        if (v.signum() <= 0) return 0L;
        if (v.bitLength() > 63) return Long.MAX_VALUE;
        long r = v.longValue();
        return (r < 0) ? Long.MAX_VALUE : r;
    }
    private static BigInteger nonNegative(BigInteger v)
    {
        if (v == null || v.signum() <= 0) return BigInteger.ZERO;
        return v;
    }
    private static BigInteger minBI(BigInteger a, BigInteger b)
    {
        return a.compareTo(b) <= 0 ? a : b;
    }
    @SuppressWarnings("unused")
    private static BigInteger maxBI(BigInteger a, BigInteger b)
    {
        return a.compareTo(b) >= 0 ? a : b;
    }
}

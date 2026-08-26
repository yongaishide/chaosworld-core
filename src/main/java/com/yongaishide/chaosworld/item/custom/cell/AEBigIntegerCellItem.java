package com.yongaishide.chaosworld.item.custom.cell;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.AEConfig;
import appeng.core.localization.PlayerMessages;
import appeng.items.contents.CellConfig;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.items.storage.StorageTier;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import appeng.util.ConfigInventory;
import appeng.util.InteractionUtil;
import appeng.util.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** BigInteger版元件物品的承载类，仅用于创造元件 */
public class AEBigIntegerCellItem extends Item implements IAEBigIntegerCell, ICellWorkbenchItem
{
    private final double idleDrain;
    private final StorageTier tier;
    private final AEKeyType keyType;
    protected final long maxBytesOverride;
    protected final int maxTypesOverride;

    public AEBigIntegerCellItem(Item.Properties pProperties, double idleDrain,AEKeyType keyType,StorageTier tier)
    {
        super(pProperties);
        this.idleDrain = idleDrain;
        this.keyType = keyType;
        this.tier = tier;
        this.maxBytesOverride = -1;
        this.maxTypesOverride = -1;
    }

    public AEBigIntegerCellItem(Item.Properties pProperties, double idleDrain, AEKeyType keyType, long maxBytes, int maxTypes)
    {
        super(pProperties);
        this.idleDrain = idleDrain;
        this.keyType = keyType;
        this.tier = null;
        this.maxBytesOverride = maxBytes;
        this.maxTypesOverride = maxTypes;
    }

    public AEBigIntegerCellItem(Item.Properties pProperties, double idleDrain, long maxBytes, int maxTypes)
    {
        this(pProperties, idleDrain, null, maxBytes, maxTypes);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> lines,
                                @NotNull TooltipFlag tooltipFlag)
    {
        if (Platform.isClient())
        {
            BigInteger used = IAEBigIntegerCell.getUsedBytes(stack);
            long maxBytes = getMaxBytes(stack);
            lines.add(AEUniversalTooltips.bytesUsed(used, maxBytes != Long.MAX_VALUE ? maxBytes : -1));
            
            long typesUsed = IAEBigIntegerCell.getUsedTypes(stack);
            long maxTypes = getMaxTypes(stack);
            lines.add(AEUniversalTooltips.typesUsed(typesUsed, maxTypes != Integer.MAX_VALUE ? maxTypes : -1));
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack)
    {
        final boolean showUpg = AEConfig.instance().isTooltipShowCellUpgrades();
        final boolean showCnt = AEConfig.instance().isTooltipShowCellContent();

        // 升级图标
        List<ItemStack> upgrades = Collections.emptyList();
        if (showUpg) {
            List<ItemStack> tmp = new ArrayList<>();
            getUpgrades(stack).forEach(tmp::add);
            upgrades = tmp;
        }

        // 内容预览：从 IAEUniversalCell 约定的 NBT 中读取展示用 GenericStack 列表
        // 为了兼容 AE2 的组件，按需裁剪数量，并设置 hasMore
        List<GenericStack> content = Collections.emptyList();
        boolean hasMore = false;
        if (showCnt) {
            List<GenericStack> show = IAEBigIntegerCell.getTooltipShowStacks(stack);
            if (!show.isEmpty()) {
                // AE2 通常展示不超过 5 个条目；超过则裁剪并设置 hasMore = true
                final int limit = 5;
                if (show.size() > limit) {
                    content = new ArrayList<>(show.subList(0, limit));
                    hasMore = true;
                } else {
                    content = new ArrayList<>(show);
                }
            }
        }

        // 显示进度条：true（进度由组件内部根据存储状态/配色绘制）
        return Optional.of(new StorageCellTooltipComponent(upgrades, content, hasMore, true));
    }

    @Override
    public double getIdleDrain()
    {
        return idleDrain;
    }

    @Override
    public StorageTier getTier() { return this.tier; }

    @Override
    public AEKeyType getKeyType() { return this.keyType; }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is)
    {
        return UpgradeInventories.forItem(is, 2);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is)
    {
        return CellConfig.create(is);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is)
    {
        return is.getOrDefault(AEComponents.STORAGE_CELL_FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode)
    {
        is.set(AEComponents.STORAGE_CELL_FUZZY_MODE, fzMode);
    }

    @Override
    public long getMaxBytes(ItemStack stack) {
        return getCapacityBytes();
    }

    public long getCapacityBytes() {
        if (this.maxBytesOverride > 0) return this.maxBytesOverride;
        if (this.maxBytesOverride == 0) return Long.MAX_VALUE;
        if (this.tier == null) return Long.MAX_VALUE;
        if (this.tier.bytes() == Integer.MAX_VALUE) return Long.MAX_VALUE;
        return this.tier.bytes();
    }

    @Override
    public int getMaxTypes(ItemStack stack) {
        if (this.maxTypesOverride > 0) return this.maxTypesOverride;
        if (this.maxTypesOverride == 0) return Integer.MAX_VALUE;
        if (this.tier == null || this.tier.bytes() == Integer.MAX_VALUE) return Integer.MAX_VALUE;
        int bytes = this.tier.bytes();
        if (bytes >= 750_000_000) return 8192;
        if (bytes >= 250_000_000) return 4096;
        if (bytes >= 100_000_000) return 2048;
        return 1024;
    }

    @Override
    public int getBytesPerType(ItemStack stack) {
        if (this.tier == null || this.tier.bytes() == Integer.MAX_VALUE || this.maxBytesOverride >= 0) return 0;
        return 0; // We define overhead as 0 for all our custom cells so items cost 1 byte exactly.
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        this.disassembleDrive(player.getItemInHand(hand), level, player);
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()),
                player.getItemInHand(hand));
    }

    private boolean disassembleDrive(ItemStack stack, Level level, Player player) {
        if (!InteractionUtil.isInAlternateUseMode(player)) {
            return false;
        }

        var disassembledStacks = StorageCellDisassemblyRecipe.getDisassemblyResult(level, stack.getItem());
        if (disassembledStacks.isEmpty()) {
            return false;
        }

        var playerInventory = player.getInventory();
        if (playerInventory.getSelected() != stack) {
            return false;
        }

        var inv = StorageCells.getCellInventory(stack, null);
        if (inv != null && !inv.getAvailableStacks().isEmpty()) {
            player.displayClientMessage(PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text(), true);
            return false;
        }

        playerInventory.setItem(playerInventory.selected, ItemStack.EMPTY);

        for (var disassembledStack : disassembledStacks) {
            playerInventory.placeItemBackInInventory(disassembledStack.copy());
        }

        getUpgrades(stack).forEach(playerInventory::placeItemBackInInventory);

        return true;
    }

    @Override
    public @NotNull InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context)
    {
        return this.disassembleDrive(stack, context.getLevel(), context.getPlayer())
                ? InteractionResult.sidedSuccess(context.getLevel().isClientSide())
                : InteractionResult.PASS;
    }
}
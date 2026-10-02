package com.yongaishide.chaosworld.patch.contents;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.AEConfig;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
import appeng.core.localization.Tooltips;
import appeng.items.contents.CellConfig;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.items.storage.StorageTier;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import appeng.util.ConfigInventory;
import appeng.util.InteractionUtil;
import appeng.util.Platform;
import com.raishxn.ufo.item.custom.cell.AEUniversalTooltips;
import com.raishxn.ufo.item.custom.cell.IAEBigIntegerCell;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

/**
 * Patch layer: big-integer storage cells with capacities beyond the int range
 * (UFO Future v3 only exposes a StorageTier based constructor, whose byte count
 * is an int). Implements v3's {@link IAEBigIntegerCell} so UFO's cell handler
 * still drives the storage backend.
 */
public class ChaosBigCellItem extends Item implements IAEBigIntegerCell, ICellWorkbenchItem {

    private final double idleDrain;
    private final AEKeyType keyType;
    private final long maxBytes;
    private final String baseNameKey;
    private final ChatFormatting[] baseNameColors;
    private final ChatFormatting[] tierColors;
    private final StorageTier displayTier;

    public ChaosBigCellItem(Item.Properties properties, double idleDrain, AEKeyType keyType, long maxBytes,
            String baseNameKey, String tierNameKey, ChatFormatting[] baseNameColors, ChatFormatting... tierColors) {
        super(properties);
        this.idleDrain = idleDrain;
        this.keyType = keyType;
        this.maxBytes = maxBytes;
        this.baseNameKey = baseNameKey;
        this.baseNameColors = baseNameColors;
        this.tierColors = tierColors;
        this.displayTier = new StorageTier(300, tierNameKey, (int) Math.min(maxBytes, Integer.MAX_VALUE), idleDrain,
                () -> this);
    }

    @Override
    public Component getName(ItemStack stack) {
        String capacity = maxBytes >= Long.MAX_VALUE ? "∞" : chaosworld$formatHumanReadable(maxBytes);
        ChatFormatting color = baseNameColors != null && baseNameColors.length > 0 ? baseNameColors[0] : ChatFormatting.WHITE;
        return Component.literal(capacity + " ")
                .append(Component.translatable(baseNameKey).withStyle(color));
    }

    private static final long C_K = 1024L;
    private static final long C_M = C_K * 1024L;
    private static final long C_G = C_M * 1024L;
    private static final long C_T = C_G * 1024L;
    private static final long C_P = C_T * 1024L;
    private static final long C_E = C_P * 1024L;

    @org.spongepowered.asm.mixin.Unique
    private static String chaosworld$formatHumanReadable(long value) {
        if (value < 0) return "∞";
        if (value < C_K) return Long.toString(value);
        if (value < C_M) return chaosworld$formatUnit(value, C_K, "K");
        if (value < C_G) return chaosworld$formatUnit(value, C_M, "M");
        if (value < C_T) return chaosworld$formatUnit(value, C_G, "G");
        if (value < C_P) return chaosworld$formatUnit(value, C_T, "T");
        if (value < C_E) return chaosworld$formatUnit(value, C_P, "P");
        return chaosworld$formatUnit(value, C_E, "E");
    }

    private static String chaosworld$formatUnit(long value, long unit, String suffix) {
        double scaled = (double) value / (double) unit;
        if (scaled == Math.floor(scaled)) {
            return (long) scaled + suffix;
        }
        return String.format(Locale.ROOT, "%.1f%s", scaled, suffix);
    }

    private static String chaosworld$formatHumanReadable(BigInteger value) {
        if (value.signum() < 0) return "∞";
        if (value.bitLength() <= 63) {
            return chaosworld$formatHumanReadable(value.longValue());
        }
        return value.divide(BigInteger.valueOf(C_E)) + "E";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
            @NotNull List<Component> lines, @NotNull TooltipFlag tooltipFlag) {
        if (Platform.isClient()) {
            BigInteger used = IAEBigIntegerCell.getUsedBytes(stack);
            lines.add(chaosworld$bytesUsed(used, maxBytes));
            long typesUsed = IAEBigIntegerCell.getUsedTypes(stack);
            lines.add(AEUniversalTooltips.typesUsed(typesUsed, -1));
        }
    }

    private static Component chaosworld$bytesUsed(BigInteger used, long max) {
        double ratio = max > 0 ? used.doubleValue() / (double) max : 1.0;
        if (ratio > 1.0) {
            ratio = 1.0;
        }

        MutableComponent usedComp = Component.literal(chaosworld$formatHumanReadable(used))
                .withStyle(Tooltips.colorFromRatio(ratio, false));
        MutableComponent maxComp = Component.literal(max <= 0 ? "∞" : chaosworld$formatHumanReadable(max))
                .withStyle(ChatFormatting.GRAY);

        return Tooltips.of(GuiText.BytesUsed,
                Tooltips.of(
                        usedComp,
                        Tooltips.of(" "),
                        Tooltips.of(GuiText.Of),
                        Tooltips.of(" "),
                        maxComp));
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        final boolean showUpg = AEConfig.instance().isTooltipShowCellUpgrades();
        final boolean showCnt = AEConfig.instance().isTooltipShowCellContent();

        List<ItemStack> upgrades = Collections.emptyList();
        if (showUpg) {
            List<ItemStack> tmp = new ArrayList<>();
            getUpgrades(stack).forEach(tmp::add);
            upgrades = tmp;
        }

        List<GenericStack> content = Collections.emptyList();
        boolean hasMore = false;
        if (showCnt) {
            List<GenericStack> show = IAEBigIntegerCell.getTooltipShowStacks(stack);
            if (!show.isEmpty()) {
                final int limit = 5;
                if (show.size() > limit) {
                    content = new ArrayList<>(show.subList(0, limit));
                    hasMore = true;
                } else {
                    content = new ArrayList<>(show);
                }
            }
        }

        return Optional.of(new StorageCellTooltipComponent(upgrades, content, hasMore, true));
    }

    @Override
    public double getIdleDrain() {
        return idleDrain;
    }

    @Override
    public StorageTier getTier() {
        return displayTier;
    }

    @Override
    public AEKeyType getKeyType() {
        return keyType;
    }

    @Override
    public long getMaxBytes(ItemStack stack) {
        return maxBytes;
    }

    @Override
    public int getMaxTypes(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getBytesPerType(ItemStack stack) {
        return 0;
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 2);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(is);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        return is.getOrDefault(AEComponents.STORAGE_CELL_FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
        is.set(AEComponents.STORAGE_CELL_FUZZY_MODE, fzMode);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
            @NotNull InteractionHand hand) {
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
    public @NotNull InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context) {
        return this.disassembleDrive(stack, context.getLevel(), context.getPlayer())
                ? InteractionResult.sidedSuccess(context.getLevel().isClientSide())
                : InteractionResult.PASS;
    }
}

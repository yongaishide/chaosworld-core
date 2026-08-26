package com.yongaishide.chaosworld.item.custom.cell;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.AEConfig;
import appeng.items.contents.CellConfig;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import appeng.util.ConfigInventory;
import appeng.util.InteractionUtil;
import appeng.util.Platform;
import cn.dancingsnow.neoecoae.all.NERegistries;
import cn.dancingsnow.neoecoae.api.ECOTier;
import cn.dancingsnow.neoecoae.api.IECOTier;
import cn.dancingsnow.neoecoae.api.storage.ECOCellType;
import cn.dancingsnow.neoecoae.api.storage.ECOStorageCells;
import cn.dancingsnow.neoecoae.api.storage.IECOStorageCell;
import cn.dancingsnow.neoecoae.api.storage.IECOStorageCellItem;
import com.wintercogs.ae2omnicells.common.me.IAEUniversalCell;
import com.wintercogs.ae2omnicells.common.me.localization.AEUniversalTooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Quantum Omni Storage Matrix: NeoECOAE ECO storage subsystem cell.
 * Faithful port of NeoECOAE's eco_quantum_omni_cell behavior, only capacity and idle
 * drain differ. Deliberately does NOT implement IAEUniversalCell, so it cannot be
 * inserted into a regular ME storage drive - ECO drive only.
 */
public class QuantumOmniStorageCellItem extends Item implements IECOStorageCellItem, ICellWorkbenchItem {

    /** Shared with NeoECOAE's eco_quantum_omni_cell so the storage subsystem treats them as one type. */
    private static final ResourceLocation SHARED_CELL_TYPE_ID =
            ResourceLocation.fromNamespaceAndPath("neoecoae", "quantum_omni");
    private static volatile ECOCellType cachedCellType;

    /** NeoECOAE quantum omni theme color (0xE15CFF). */
    private static final int NAME_COLOR = 14769407;

    private final double idleDrain;
    private final long ecoStorageTotalBytes;

    public QuantumOmniStorageCellItem(Item.Properties properties, double idleDrain, long totalBytes) {
        super(properties.stacksTo(1));
        this.idleDrain = idleDrain;
        this.ecoStorageTotalBytes = totalBytes;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).withColor(NAME_COLOR);
    }

    public double getIdleDrain() {
        return this.idleDrain;
    }

    public long getECOStorageTotalBytes() {
        return this.ecoStorageTotalBytes;
    }

    public boolean isExternallyUnlimited() {
        return true;
    }

    @Override
    public IECOTier getTier() {
        return ECOTier.L9;
    }

    @Override
    public ECOCellType getCellType() {
        ECOCellType type = cachedCellType;
        if (type == null) {
            type = NERegistries.CELL_TYPE.get(SHARED_CELL_TYPE_ID);
            if (type != null) {
                cachedCellType = type;
            }
        }
        return type;
    }

    @Override
    public Set<AEKeyType> getKeyTypes() {
        return Set.copyOf(AEKeyTypes.getAll());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag tooltipFlag) {
        if (Platform.isClient()) {
            long used = IAEUniversalCell.getUsedBytes(stack);
            lines.add(AEUniversalTooltips.bytesUsed(used, this.getECOStorageTotalBytes()));
            long typesUsed = IAEUniversalCell.getUsedTypes(stack);
            lines.add(AEUniversalTooltips.typesUsed(typesUsed, -1));
        }
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
            List<GenericStack> show = IAEUniversalCell.getTooltipShowStacks(stack);
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
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 4);
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

        IECOStorageCell inv = ECOStorageCells.getCellInventory(stack, null);
        if (inv != null && !inv.getAvailableStacks().isEmpty()) {
            player.displayClientMessage(appeng.core.localization.PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text(), true);
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

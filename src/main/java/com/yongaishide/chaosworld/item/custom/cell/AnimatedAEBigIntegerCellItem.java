package com.yongaishide.chaosworld.item.custom.cell;

import appeng.api.stacks.AEKeyType;
import appeng.items.storage.StorageTier;
import com.yongaishide.chaosworld.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AnimatedAEBigIntegerCellItem extends AEBigIntegerCellItem {

    private final String baseNameKey;
    private final String tierNameKey;
    private final ChatFormatting[] baseNameColors;
    private final ChatFormatting[] tierColors;

    public AnimatedAEBigIntegerCellItem(Item.Properties props, double idleDrain, AEKeyType keyType, StorageTier tier, String baseNameKey, String tierNameKey, ChatFormatting[] baseNameColors, ChatFormatting... tierColors) {
        super(props, idleDrain, keyType, tier);
        this.baseNameKey = baseNameKey;
        this.tierNameKey = tierNameKey;
        this.baseNameColors = baseNameColors;
        this.tierColors = tierColors;
    }

    public AnimatedAEBigIntegerCellItem(Item.Properties props, double idleDrain, AEKeyType keyType, long maxBytes, int maxTypes, String baseNameKey, String tierNameKey, ChatFormatting[] baseNameColors, ChatFormatting... tierColors) {
        super(props, idleDrain, keyType, maxBytes, maxTypes);
        this.baseNameKey = baseNameKey;
        this.tierNameKey = tierNameKey;
        this.baseNameColors = baseNameColors;
        this.tierColors = tierColors;
    }

    public AnimatedAEBigIntegerCellItem(Item.Properties props, double idleDrain, long maxBytes, int maxTypes, String baseNameKey, String tierNameKey, ChatFormatting[] baseNameColors, ChatFormatting... tierColors) {
        super(props, idleDrain, maxBytes, maxTypes);
        this.baseNameKey = baseNameKey;
        this.tierNameKey = tierNameKey;
        this.baseNameColors = baseNameColors;
        this.tierColors = tierColors;
    }

    @Override
    public Component getName(ItemStack stack) {
        long capacity = getCapacityBytes();
        String capacityStr = capacity >= Long.MAX_VALUE ? "∞" : AEUniversalTooltips.formatHumanReadable(capacity);
        Component baseName = ColorHelper.getAnimatedColoredText(Component.translatable(this.baseNameKey).getString(), this.baseNameColors);

        return Component.literal(capacityStr + " ").append(baseName);
    }
}
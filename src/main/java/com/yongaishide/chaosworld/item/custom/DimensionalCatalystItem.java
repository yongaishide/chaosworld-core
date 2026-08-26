package com.yongaishide.chaosworld.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import appeng.items.materials.UpgradeCardItem;

import java.util.List;

/**
 * Catalisador Criativo.
 * Extends UpgradeCardItem for AE2 upgrade slot compatibility.
 */
public class DimensionalCatalystItem extends UpgradeCardItem {

    public DimensionalCatalystItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return CatalystUpgradeUseHelper.tryInstallHeldCatalyst(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.ufo.catalyst.creative_title").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            components.add(Component.empty());
            components.add(Component.translatable("tooltip.ufo.catalyst.effects_header").withStyle(ChatFormatting.AQUA));
            components.add(Component.translatable("tooltip.ufo.catalyst.effect_instant").withStyle(ChatFormatting.GREEN));
            components.add(Component.translatable("tooltip.ufo.catalyst.effect_no_energy").withStyle(ChatFormatting.GREEN));
            components.add(Component.translatable("tooltip.ufo.catalyst.effect_no_heat").withStyle(ChatFormatting.GREEN));
            components.add(Component.translatable("tooltip.ufo.catalyst.effect_consumes").withStyle(ChatFormatting.AQUA));
        } else {
            components.add(Component.translatable("tooltip.ufo.press_shift").withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, components, flag);
    }
}

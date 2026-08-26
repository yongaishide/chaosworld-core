package com.yongaishide.chaosworld.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AstralNexusArmorItem extends ArmorItem {

    public AstralNexusArmorItem(Holder<ArmorMaterial> material, Type type, Item.Properties properties) {
        super(material, type, properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.ufo.astral_nexus.infinite").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.ufo.astral_nexus.immunity").withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tooltip.ufo.astral_nexus.reflect").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.ufo.astral_nexus.buffs").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.ufo.astral_nexus.flight").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}

package com.yongaishide.chaosworld.compat.ftbquests;

import appeng.items.materials.UpgradeCardItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class QuestDetectionCardItem extends UpgradeCardItem {
    public QuestDetectionCardItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("item.chaosworld_core.quest_detection_card.tooltip").withStyle(ChatFormatting.GRAY));
    }
}

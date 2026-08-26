package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AnimatedNameItem extends Item {

    private final ChatFormatting[] colors;

    public AnimatedNameItem(Properties props, ChatFormatting... colors) {
        super(props);
        this.colors = colors;
    }

    @Override
    public Component getName(ItemStack stack) {
        return ColorHelper.getSolidColoredText(
                Component.translatable(stack.getDescriptionId()).getString(),
                this.colors
        );
    }
}

package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AnimatedNameBlockItem extends BlockItem {

    private final ChatFormatting[] colors;

    public AnimatedNameBlockItem(Block block, Properties props, ChatFormatting... colors) {
        super(block, props);
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

package com.yongaishide.chaosworld.item.custom;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHasCycleableModes {
    void cycleMode(ItemStack stack, Player player);
}
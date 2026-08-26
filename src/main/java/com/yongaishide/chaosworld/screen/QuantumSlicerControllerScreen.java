package com.yongaishide.chaosworld.screen;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class QuantumSlicerControllerScreen extends AbstractUniversalMultiblockControllerScreen<QuantumSlicerControllerMenu> {

    public QuantumSlicerControllerScreen(QuantumSlicerControllerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}

package com.yongaishide.chaosworld.screen;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class QuantumCryoforgeControllerScreen extends AbstractUniversalMultiblockControllerScreen<QuantumCryoforgeControllerMenu> {

    public QuantumCryoforgeControllerScreen(QuantumCryoforgeControllerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}

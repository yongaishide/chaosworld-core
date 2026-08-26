package com.yongaishide.chaosworld.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AccessorAbstractContainerScreen {
    @Accessor("menu")
    AbstractContainerMenu ufo$getMenu();
}

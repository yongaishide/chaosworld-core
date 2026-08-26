package com.yongaishide.chaosworld.mixin;

import appeng.api.behaviors.ContainerItemStrategies;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MEStorageScreen.class)
public abstract class MixinMEStorageScreen {
    @Inject(method = "handleGridInventoryEntryMouseClick", at = @At("HEAD"), cancellable = true)
    private void ufo$allowEmptyingChemicalContainersFromHands(@Nullable GridInventoryEntry entry, int mouseButton, ClickType clickType, CallbackInfo ci) {
        AbstractContainerMenu rawMenu = ((AccessorAbstractContainerScreen) this).ufo$getMenu();
        if (!(rawMenu instanceof MEStorageMenu menu)) {
            return;
        }
        if (mouseButton != 1 || !menu.getCarried().isEmpty()) {
            return;
        }

        var player = menu.getPlayer();
        var mainHand = player.getMainHandItem();
        var emptyingAction = ContainerItemStrategies.getEmptyingAction(mainHand);
        if (emptyingAction == null) {
            var offHand = player.getOffhandItem();
            emptyingAction = ContainerItemStrategies.getEmptyingAction(offHand);
        }

        if (emptyingAction == null || !menu.isKeyVisible(emptyingAction.what())) {
            return;
        }

        menu.handleInteraction(-1, clickType == ClickType.QUICK_MOVE
                ? InventoryAction.EMPTY_ENTIRE_ITEM
                : InventoryAction.EMPTY_ITEM);
        ci.cancel();
    }
}

package com.yongaishide.chaosworld.mixin;

import appeng.api.util.KeyTypeSelection;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyTypeSelection.class)
public class MixinKeyTypeSelection {
    @Inject(method = "readFromNBT", at = @At("HEAD"), cancellable = true)
    private void ufo$keepDefaultSelectionWhenTagMissing(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (!tag.contains("enabledKeyTypes")) {
            ci.cancel();
        }
    }
}

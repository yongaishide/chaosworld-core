package com.yongaishide.chaosworld.mixin;

import appeng.core.AppEngBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AppEngBase.class, remap = false)
public class MixinAppEngBase {

    private static boolean ufo$postInitComplete = false;

    @Inject(method = "postRegistrationInitialization", at = @At("HEAD"), cancellable = true)
    private void onPostRegistrationInitialization(CallbackInfo ci) {
        if (ufo$postInitComplete) {
            ci.cancel();
        }
        ufo$postInitComplete = true;
    }
}

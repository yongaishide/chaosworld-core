package com.yongaishide.chaosworld.mixin.ftbquests;

import com.yongaishide.chaosworld.compat.ftbquests.client.LongCountResource;
import dev.ftb.mods.ftblibrary.config.ui.resource.ResourceSelectorScreen;
import dev.ftb.mods.ftblibrary.config.ui.resource.SelectableResource;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.ftb.mods.ftblibrary.config.ui.resource.ResourceSelectorScreen$CountTextBox", remap = false)
public abstract class CountTextBoxMixin {
    @Shadow(remap = false)
    @Final
    private ResourceSelectorScreen this$0;

    @Inject(method = "onTextChanged", at = @At("HEAD"), cancellable = true, remap = false)
    private void chaosworld$applyLongCount(CallbackInfo callbackInfo) {
        SelectableResource<?> selected = ((ResourceSelectorScreenAccessor) this$0).chaosworld$getSelectedStack();
        if (selected == null || selected.isEmpty()) {
            return;
        }

        long value = parseCount(text());
        if (selected instanceof LongCountResource longResource) {
            longResource.setCountLong(value);
        } else {
            selected.setCount((int) Math.min(Integer.MAX_VALUE, value));
        }
        callbackInfo.cancel();
    }

    @Inject(method = "getCount", at = @At("RETURN"), cancellable = true, remap = false)
    private void chaosworld$clampCount(CallbackInfoReturnable<Integer> callbackInfo) {
        callbackInfo.setReturnValue((int) Math.min(Integer.MAX_VALUE, parseCount(text())));
    }

    @Unique
    private String text() {
        return ((TextBox) (Object) this).getText();
    }

    @Unique
    private static long parseCount(String text) {
        try {
            return Math.max(1L, Long.parseLong(text));
        } catch (NumberFormatException e) {
            return 1L;
        }
    }
}

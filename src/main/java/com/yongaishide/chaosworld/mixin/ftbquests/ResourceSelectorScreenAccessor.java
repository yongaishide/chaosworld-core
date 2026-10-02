package com.yongaishide.chaosworld.mixin.ftbquests;

import dev.ftb.mods.ftblibrary.config.ui.resource.ResourceSelectorScreen;
import dev.ftb.mods.ftblibrary.config.ui.resource.SelectableResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ResourceSelectorScreen.class, remap = false)
public interface ResourceSelectorScreenAccessor {
    @Accessor(value = "selectedStack", remap = false)
    SelectableResource<?> chaosworld$getSelectedStack();
}

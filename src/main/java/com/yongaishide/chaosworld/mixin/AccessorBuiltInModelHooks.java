package com.yongaishide.chaosworld.mixin;

import appeng.hooks.BuiltInModelHooks;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = BuiltInModelHooks.class, remap = false)
public interface AccessorBuiltInModelHooks {
    @Accessor("builtInModels")
    static Map<ResourceLocation, UnbakedModel> getBuiltInModels() {
        throw new AssertionError();
    }
}
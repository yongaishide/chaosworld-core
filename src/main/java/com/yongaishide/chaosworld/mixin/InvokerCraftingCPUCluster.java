package com.yongaishide.chaosworld.mixin;

import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public interface InvokerCraftingCPUCluster {
    @Invoker("addBlockEntity")
    void ufo$addBlockEntity(CraftingBlockEntity blockEntity);

    @Invoker("done")
    void ufo$done();
}

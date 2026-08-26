package com.yongaishide.chaosworld.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables the vanilla wither auto-spawn (placing the third skull no longer
 * summons the wither). The wither can only be summoned by right-clicking the
 * center soul sand/soul soil block with a Cryptid Core.
 */
@Mixin(WitherSkullBlock.class)
public abstract class WitherSkullBlockMixin {

    @Inject(
            method = "checkSpawn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/SkullBlockEntity;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void ufo$disableVanillaWitherSpawn(Level level, BlockPos pos, SkullBlockEntity blockEntity, CallbackInfo ci) {
        ci.cancel();
    }
}

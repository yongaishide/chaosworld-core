package com.yongaishide.chaosworld.mixin.ae2;

import appeng.api.AECapabilities;
import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.storage.MEStorage;
import appeng.parts.automation.ForgeExternalStorageStrategy;
import cool.furry.mc.neoforge.projectexpansion.block.entity.BlockEntityTransmutationInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeExternalStorageStrategy.class, remap = false)
public class ForgeExternalStorageStrategyMixin {

    @Inject(method = "createItem", at = @At("HEAD"), cancellable = true, remap = false)
    private static void chaosworld_createItem(ServerLevel level, BlockPos pos, Direction dir,
                                               CallbackInfoReturnable<ExternalStorageStrategy> cir) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockEntityTransmutationInterface ti) {
            MEStorage storage = level.getCapability(AECapabilities.ME_STORAGE, pos, ti.getBlockState(), ti, null);
            if (storage != null) {
                cir.setReturnValue(new ExternalStorageStrategy() {
                    @Override
                    public MEStorage createWrapper(boolean extractableOnly, Runnable changeListener) {
                        return storage;
                    }
                });
            }
        }
    }
}

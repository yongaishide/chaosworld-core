package com.yongaishide.chaosworld.mixin.ae2;

import appeng.api.stacks.AEItemKey;
import appeng.blockentity.misc.CondenserBlockEntity;
import appeng.util.inv.AppEngInternalInventory;
import com.yongaishide.chaosworld.ae2.OmniCellComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 物质聚合器：ae2omnicells 全能存储组件兼容。
 * <p>
 * 顶部存储组件槽放入 omni 存储组件时，按组件字节容量提供聚合器存储上限；
 * 内部输入槽与 ME 存储接口拒绝 omni 组件，防止其被当作可聚合材料销毁。
 */
@Mixin(value = CondenserBlockEntity.class, remap = false)
public abstract class CondenserBlockEntityMixin {

    @Shadow(remap = false)
    private AppEngInternalInventory storageSlot;

    @Inject(method = "getStorage", at = @At("HEAD"), cancellable = true, remap = false)
    private void ufo$getStorage(CallbackInfoReturnable<Double> cir) {
        ItemStack stack = this.storageSlot.getStackInSlot(0);
        long bytes = OmniCellComponents.bytesOf(stack);
        if (bytes > 0) {
            cir.setReturnValue((double) bytes * 8);
        }
    }

    @Mixin(targets = "appeng.blockentity.misc.CondenserBlockEntity$CondenseItemHandler", remap = false)
    public abstract static class CondenseItemHandlerMixin {

        @Inject(method = "insertItem", at = @At("HEAD"), cancellable = true, remap = false)
        private void chaosworld$blockOmniComponent(int slot, ItemStack stack, boolean simulate,
                CallbackInfoReturnable<ItemStack> cir) {
            if (OmniCellComponents.isOmniCellComponent(stack)) {
                cir.setReturnValue(stack);
            }
        }
    }

    @Mixin(targets = "appeng.blockentity.misc.CondenserMEStorage", remap = false)
    public abstract static class CondenserMEStorageMixin {

        @Inject(method = "insert", at = @At("HEAD"), cancellable = true, remap = false)
        private void chaosworld$blockOmniComponent(appeng.api.stacks.AEKey key, long amount,
                appeng.api.config.Actionable mode, appeng.api.networking.security.IActionSource source,
                CallbackInfoReturnable<Long> cir) {
            if (key instanceof AEItemKey itemKey && OmniCellComponents.isOmniCellComponent(itemKey.toStack())) {
                cir.setReturnValue(0L);
            }
        }
    }
}

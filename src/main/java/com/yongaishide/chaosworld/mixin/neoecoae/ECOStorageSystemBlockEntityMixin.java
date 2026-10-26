package com.yongaishide.chaosworld.mixin.neoecoae;

import appeng.util.inv.AppEngInternalInventory;
import cn.dancingsnow.neoecoae.blocks.entity.storage.ECOStorageSystemBlockEntity;
import com.yongaishide.chaosworld.compat.neoecoae.CreativeCellIdlePowerProfile;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 控制器自己不会在无限元件槽变化时重算待机耗电（21.2.10 的 {@code onChangeInventory}
 * 只标脏落盘），这里在槽位 0 变化后刷新一次控制器与簇内驱动器。
 * 待机耗电本身由 {@link CreativeCellIdlePowerProfile} 通过 NeoEcoAE 的
 * {@code ECOPowerProfiles} 扩展点计算，本 mixin 不再碰网格节点。
 */
@Mixin(value = ECOStorageSystemBlockEntity.class, remap = false)
public abstract class ECOStorageSystemBlockEntityMixin {

    @Inject(method = "onReady", at = @At("RETURN"))
    private void chaosworld$pinInitialState(CallbackInfo ci) {
        ECOStorageSystemBlockEntity self = (ECOStorageSystemBlockEntity) (Object) this;
        if (self.getLevel() instanceof ServerLevel) {
            CreativeCellIdlePowerProfile.onControllerReady(self);
        }
    }

    @Inject(method = "onChangeInventory", at = @At("RETURN"))
    private void chaosworld$onInfiniteComponentSlotChanged(AppEngInternalInventory inv, int slot, CallbackInfo ci) {
        if (slot != 0) {
            return;
        }
        ECOStorageSystemBlockEntity self = (ECOStorageSystemBlockEntity) (Object) this;
        if (!(self.getLevel() instanceof ServerLevel)) {
            return;
        }
        if (inv != ((ECOStorageSystemAccess) (Object) self).chaosworld$componentInventory()) {
            return;
        }
        CreativeCellIdlePowerProfile.onComponentsChanged(self);
    }
}

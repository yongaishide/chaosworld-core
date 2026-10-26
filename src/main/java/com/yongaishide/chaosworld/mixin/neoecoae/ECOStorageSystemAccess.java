package com.yongaishide.chaosworld.mixin.neoecoae;

import appeng.util.inv.AppEngInternalInventory;
import cn.dancingsnow.neoecoae.blocks.entity.storage.ECOStorageSystemBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 21.2.10 里无限元件槽只有包内可见访问器，用 accessor mixin 取代反射。
 * 目标类会实现该接口，调用方转型即可。
 */
@Mixin(value = ECOStorageSystemBlockEntity.class, remap = false)
public interface ECOStorageSystemAccess {

    @Accessor("infiniteComponentInventory")
    AppEngInternalInventory chaosworld$componentInventory();
}

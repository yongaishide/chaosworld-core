package com.yongaishide.chaosworld.mixin.ufo;

import com.raishxn.ufo.item.custom.cell.AEBigIntegerCellItem;

import appeng.api.stacks.AEKeyType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Patch layer: the pack's white dwarf storage cells were designed as universal
 * (all resource types), so they report a null key type and are served by this
 * mod's universal big-integer cell inventory instead of UFO's item-only one.
 */
@Mixin(value = AEBigIntegerCellItem.class, remap = false)
public abstract class AEBigIntegerCellItemMixin {

    @Inject(method = "getKeyType", at = @At("HEAD"), cancellable = true, remap = false)
    private void chaosworld$universalWhiteDwarfCells(CallbackInfoReturnable<AEKeyType> cir) {
        Item self = (Item) (Object) this;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(self);
        if ("ufo".equals(id.getNamespace()) && id.getPath().startsWith("white_dwarf_cell_")) {
            cir.setReturnValue(null);
        }
    }
}

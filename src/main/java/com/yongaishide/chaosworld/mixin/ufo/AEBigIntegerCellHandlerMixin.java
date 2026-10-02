package com.yongaishide.chaosworld.mixin.ufo;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.item.custom.cell.AEBigIntegerCellData;
import com.raishxn.ufo.item.custom.cell.AEBigIntegerCellHandler;
import com.raishxn.ufo.item.custom.cell.IAEBigIntegerCell;
import com.yongaishide.chaosworld.patch.contents.ChaosUniversalBigCellInventory;

import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Patch layer: restores universal (all AE key types) storage for the legacy
 * white dwarf / neutron star / pulsar big-integer cells. UFO Future 3.x's own
 * inventory only accepts the single key type returned by the cell item, so
 * cells with a null key type are redirected to this mod's universal backend.
 */
@Mixin(value = AEBigIntegerCellHandler.class, remap = false)
public abstract class AEBigIntegerCellHandlerMixin {

    @Inject(method = "getCellInventory", at = @At("HEAD"), cancellable = true, remap = false)
    private void chaosworld$universalCell(ItemStack itemStack, ISaveProvider saveProvider,
            CallbackInfoReturnable<StorageCell> cir) {
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return;
        }
        if (itemStack.getCount() != 1 || !(itemStack.getItem() instanceof IAEBigIntegerCell cellItem)) {
            return;
        }
        if (cellItem.getKeyType() != null) {
            return;
        }
        boolean hadCellId = itemStack.has(ModDataComponents.CELL_UUID.get());
        AEBigIntegerCellData cellData = AEBigIntegerCellData.computeIfAbsentCellDataForItemStack(itemStack);
        if (cellData == null) {
            cir.setReturnValue(null);
            return;
        }
        if (!hadCellId && saveProvider != null) {
            saveProvider.saveChanges();
        }
        cir.setReturnValue(new ChaosUniversalBigCellInventory(cellData, itemStack, cellItem, saveProvider));
    }
}

package com.yongaishide.chaosworld.mixin.neoecoae;

import appeng.api.networking.IManagedGridNode;
import appeng.util.inv.AppEngInternalInventory;
import cn.dancingsnow.neoecoae.blocks.entity.storage.ECOStorageSystemBlockEntity;
import cn.dancingsnow.neoecoae.multiblock.cluster.NEStorageCluster;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;

/**
 * Forces the ECO drive idle power usage to 0 while the storage subsystem
 * controller holds an avaritia Enhancement Core in its infinite component slot.
 */
@Mixin(value = cn.dancingsnow.neoecoae.blocks.entity.storage.ECODriveBlockEntity.class, remap = false)
public class ECODriveBlockEntityMixin {

    private static final ResourceLocation CREATIVE_ENERGY_CELL_ID =
            ResourceLocation.fromNamespaceAndPath("ae2", "creative_energy_cell");

    @Redirect(method = "updateState",
            at = @At(value = "INVOKE",
                    target = "Lappeng/api/networking/IManagedGridNode;setIdlePowerUsage(D)Lappeng/api/networking/IManagedGridNode;",
                    remap = false))
    private IManagedGridNode chaosworld$zeroIdlePowerWithEnhancementCore(IManagedGridNode node, double power) {
        return node.setIdlePowerUsage(chaosworld$hasEnhancementCore() ? 0.0 : power);
    }

    private boolean chaosworld$hasEnhancementCore() {
        try {
            Field clusterField = chaosworld$findField(this.getClass(), "cluster");
            if (clusterField == null) return false;
            Object cluster = clusterField.get(this);
            if (cluster instanceof NEStorageCluster storageCluster && storageCluster.getController() != null) {
                ECOStorageSystemBlockEntity controller = storageCluster.getController();
                Field compField = ECOStorageSystemBlockEntity.class.getDeclaredField("infiniteComponentInventory");
                compField.setAccessible(true);
                ItemStack stack = ((AppEngInternalInventory) compField.get(controller)).getStackInSlot(0);
                return stack.is(BuiltInRegistries.ITEM.get(CREATIVE_ENERGY_CELL_ID));
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static Field chaosworld$findField(Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }
}

package com.yongaishide.chaosworld.mixin.neoecoae;

import appeng.api.networking.IManagedGridNode;
import appeng.util.inv.AppEngInternalInventory;
import cn.dancingsnow.neoecoae.api.ECOTier;
import cn.dancingsnow.neoecoae.blocks.entity.storage.ECODriveBlockEntity;
import cn.dancingsnow.neoecoae.blocks.entity.storage.ECOStorageSystemBlockEntity;
import cn.dancingsnow.neoecoae.multiblock.cluster.NECluster;
import cn.dancingsnow.neoecoae.multiblock.cluster.NEStorageCluster;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

/**
 * Creative Energy Cell (ae2:creative_energy_cell) in the storage subsystem's
 * infinite component slot zeroes out the subsystem idle power usage.
 */
@Mixin(value = ECOStorageSystemBlockEntity.class, remap = false)
public class ECOStorageSystemBlockEntityMixin {

    private static final ResourceLocation CREATIVE_ENERGY_CELL_ID =
            ResourceLocation.fromNamespaceAndPath("ae2", "creative_energy_cell");
    private static final java.util.Map<ECOStorageSystemBlockEntity, Boolean> LAST_STATE =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    @Inject(method = "onReady", at = @At("RETURN"))
    private void chaosworld$onReady(CallbackInfo ci) {
        ECOStorageSystemBlockEntity self = (ECOStorageSystemBlockEntity) (Object) this;
        if (self.getLevel() != null && self.getLevel().getServer() != null) {
            self.getLevel().getServer().execute(() -> chaosworld$applyState(self));
        }
    }

    @Inject(method = "onChangeInventory", at = @At("HEAD"))
    private void chaosworld$onInfiniteComponentSlotChanged(AppEngInternalInventory inv, int slot, CallbackInfo ci) {
        ECOStorageSystemBlockEntity self = (ECOStorageSystemBlockEntity) (Object) this;
        if (slot != 0) return;
        try {
            Field compField = ECOStorageSystemBlockEntity.class.getDeclaredField("infiniteComponentInventory");
            compField.setAccessible(true);
            if (inv != compField.get(self)) return;
        } catch (Exception ignored) {
            return;
        }

        if (self.getLevel() != null && self.getLevel().getServer() != null) {
            self.getLevel().getServer().execute(() -> chaosworld$applyState(self));
        }
    }

    private static void chaosworld$applyState(ECOStorageSystemBlockEntity self) {
        boolean core = chaosworld$hasEnhancementCore(self);
        Boolean prev = LAST_STATE.put(self, core);
        if (prev == null || prev != core) {
            chaosworld$refreshIdlePower(self, core);
            chaosworld$broadcast(core);
        }
    }

    private static boolean chaosworld$hasEnhancementCore(ECOStorageSystemBlockEntity self) {
        try {
            Field compField = ECOStorageSystemBlockEntity.class.getDeclaredField("infiniteComponentInventory");
            compField.setAccessible(true);
            ItemStack stack = ((AppEngInternalInventory) compField.get(self)).getStackInSlot(0);
            return stack.is(BuiltInRegistries.ITEM.get(CREATIVE_ENERGY_CELL_ID));
        } catch (Exception ignored) {
            return false;
        }
    }

    private static void chaosworld$refreshIdlePower(ECOStorageSystemBlockEntity self, boolean core) {
        try {
            double idle = core ? 0.0 : chaosworld$baseIdlePower(self);
            self.getMainNode().setIdlePowerUsage(idle);

            Field clusterField = chaosworld$findField(ECOStorageSystemBlockEntity.class, "cluster");
            if (clusterField != null) {
                Object cluster = clusterField.get(self);
                if (cluster instanceof NEStorageCluster storageCluster) {
                    for (ECODriveBlockEntity drive : storageCluster.getDrives()) {
                        var method = drive.getClass().getDeclaredMethod("updateState");
                        method.setAccessible(true);
                        method.invoke(drive);
                    }
                }
            }
        } catch (Exception ignored) {
        }
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

    private static double chaosworld$baseIdlePower(ECOStorageSystemBlockEntity self) {
        try {
            Field tierField = ECOStorageSystemBlockEntity.class.getDeclaredField("tier");
            tierField.setAccessible(true);
            ECOTier tier = (ECOTier) tierField.get(self);
            return 256.0 + (1 << (1 + 4 * tier.getTier()));
        } catch (Exception ignored) {
            return 256.0;
        }
    }

    private static void chaosworld$broadcast(boolean core) {
        if (ServerLifecycleHooks.getCurrentServer() == null) return;
        Component msg = Component.translatable(core
                ? "chaosworld_core.storage.enhancement_core.activated"
                : "chaosworld_core.storage.enhancement_core.removed");
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            player.sendSystemMessage(msg);
        }
    }
}

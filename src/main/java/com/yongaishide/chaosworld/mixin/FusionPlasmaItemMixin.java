package com.yongaishide.chaosworld.mixin;

import com.yongaishide.chaosworld.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Throwing an Avaritia Iron Singularity (avaritia:singularity with
 * singularity_id "avaritia:iron") into the plasma of a burning Mekanism fusion
 * reactor converts it into chaosworld_core:neutronite_ingot.
 * <p>
 * Runs on the always-present ItemEntity. Mekanism Generators classes are only
 * touched via reflection, so the mod loads fine without Mekanism Generators.
 */
@Mixin(ItemEntity.class)
public abstract class FusionPlasmaItemMixin {

    private static final String CONTROLLER_CLASS = "mekanism.generators.common.tile.fusion.TileEntityFusionReactorController";
    private static final ResourceLocation SINGULARITY_ITEM_ID = ResourceLocation.parse("avaritia:singularity");
    private static final ResourceLocation SINGULARITY_ID_COMPONENT = ResourceLocation.parse("avaritia:singularity_id");
    private static final String TARGET_SINGULARITY = "avaritia:iron";

    @Inject(method = "tick", at = @At("HEAD"))
    private void ufo$convertInPlasma(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        Level level = self.level();
        if (level.isClientSide || self.tickCount % 10 != 0) {
            return;
        }
        if (!isIronSingularity(self.getItem())) {
            return;
        }
        if (!net.neoforged.fml.ModList.get().isLoaded("mekanismgenerators")) {
            return;
        }
        try {
            convertInFusionReactor(self);
        } catch (Throwable ignored) {
            // Never let a compat issue break item ticking
        }
    }

    private static void convertInFusionReactor(ItemEntity item) throws Exception {
        Level level = item.level();
        BlockPos pos = item.blockPosition();
        Class<?> controllerClass = Class.forName(CONTROLLER_CLASS);
        for (BlockPos check : BlockPos.betweenClosed(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            BlockEntity be = level.getBlockEntity(check);
            if (be == null || !controllerClass.isInstance(be)) {
                continue;
            }
            Object data = controllerClass.getMethod("getMultiblock").invoke(be);
            if (data == null) {
                continue;
            }
            if (!(boolean) data.getClass().getMethod("isFormed").invoke(data)) {
                continue;
            }
            if (!(boolean) data.getClass().getMethod("isBurning").invoke(data)) {
                continue;
            }
            BlockPos min = (BlockPos) data.getClass().getMethod("getMinPos").invoke(data);
            BlockPos max = (BlockPos) data.getClass().getMethod("getMaxPos").invoke(data);
            AABB interior = AABB.encapsulatingFullBlocks(min.offset(1, 1, 1), max.offset(-1, -1, -1));
            if (!interior.contains(item.getX(), item.getY(), item.getZ())) {
                continue;
            }

            ItemStack stack = item.getItem();
            item.setItem(new ItemStack(ModItems.NEUTRONITE_INGOT.get(), stack.getCount()));
            // Eject the result out of the plasma (the reactor's interior is lethal)
            item.setDeltaMovement(0, 0.4, 0);
            item.setPos(item.getX(), interior.maxY + 0.5, item.getZ());
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
                        item.getX(), item.getY() - 1.0, item.getZ(), 24, 0.5, 0.5, 0.5, 0.06);
                serverLevel.playSound(null, item.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.7F);
            }
            return;
        }
    }

    private static boolean isIronSingularity(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item singularity = BuiltInRegistries.ITEM.get(SINGULARITY_ITEM_ID);
        if (singularity == null || singularity == Items.AIR || !stack.is(singularity)) {
            return false;
        }
        DataComponentType<?> type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(SINGULARITY_ID_COMPONENT);
        if (type == null) {
            return false;
        }
        Object value = stack.get(type);
        return value != null && TARGET_SINGULARITY.equals(value.toString());
    }
}

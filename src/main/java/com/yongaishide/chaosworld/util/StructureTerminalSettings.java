package com.yongaishide.chaosworld.util;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StructureTerminalSettings {

    private static final String TAG_REPEAT = "repeat";
    private static final String TAG_REPLACE = "replace";
    private static final String TAG_FLIP = "flip";
    private static final String TAG_DISMANTLE = "dismantle";
    private static final String TAG_AE = "ae";
    private static final String TAG_TIER = "tier";
    private static final String TAG_BOUND_X = "boundX";
    private static final String TAG_BOUND_Y = "boundY";
    private static final String TAG_BOUND_Z = "boundZ";
    private static final String TAG_BOUND_DIM = "boundDim";

    private StructureTerminalSettings() {
    }

    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.TERMINAL_SETTINGS.get(), new CompoundTag());
    }

    private static void saveTag(ItemStack stack, CompoundTag tag) {
        stack.set(ModDataComponents.TERMINAL_SETTINGS.get(), tag);
    }

    public static int getRepeatCount(ItemStack stack) {
        return Math.max(1, Math.min(64, getTag(stack).getInt(TAG_REPEAT)));
    }

    public static void setRepeatCount(ItemStack stack, int repeat) {
        CompoundTag tag = getTag(stack).copy();
        tag.putInt(TAG_REPEAT, Math.max(1, Math.min(64, repeat)));
        saveTag(stack, tag);
    }

    public static boolean getReplaceMode(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return !tag.contains(TAG_REPLACE) || tag.getBoolean(TAG_REPLACE);
    }

    public static void setReplaceMode(ItemStack stack, boolean replace) {
        CompoundTag tag = getTag(stack).copy();
        tag.putBoolean(TAG_REPLACE, replace);
        saveTag(stack, tag);
    }

    public static boolean getFlipped(ItemStack stack) {
        return getTag(stack).getBoolean(TAG_FLIP);
    }

    public static void setFlipped(ItemStack stack, boolean flipped) {
        CompoundTag tag = getTag(stack).copy();
        tag.putBoolean(TAG_FLIP, flipped);
        saveTag(stack, tag);
    }

    public static boolean getDismantleMode(ItemStack stack) {
        return getTag(stack).getBoolean(TAG_DISMANTLE);
    }

    public static void setDismantleMode(ItemStack stack, boolean dismantle) {
        CompoundTag tag = getTag(stack).copy();
        tag.putBoolean(TAG_DISMANTLE, dismantle);
        saveTag(stack, tag);
    }

    public static boolean getAeMode(ItemStack stack) {
        return getTag(stack).getBoolean(TAG_AE);
    }

    public static void setAeMode(ItemStack stack, boolean ae) {
        CompoundTag tag = getTag(stack).copy();
        tag.putBoolean(TAG_AE, ae);
        saveTag(stack, tag);
    }

    public static int getFieldTier(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return Math.max(1, Math.min(3, tag.contains(TAG_TIER) ? tag.getInt(TAG_TIER) : 1));
    }

    public static void setFieldTier(ItemStack stack, int tier) {
        CompoundTag tag = getTag(stack).copy();
        tag.putInt(TAG_TIER, Math.max(1, Math.min(3, tier)));
        saveTag(stack, tag);
    }

    public static GlobalPos getBoundPos(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        if (!tag.contains(TAG_BOUND_X) || !tag.contains(TAG_BOUND_DIM)) {
            return null;
        }
        try {
            ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION,
                    ResourceLocation.parse(tag.getString(TAG_BOUND_DIM)));
            return GlobalPos.of(dim, new BlockPos(tag.getInt(TAG_BOUND_X), tag.getInt(TAG_BOUND_Y), tag.getInt(TAG_BOUND_Z)));
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public static void setBoundPos(ItemStack stack, GlobalPos pos) {
        CompoundTag tag = getTag(stack).copy();
        tag.putInt(TAG_BOUND_X, pos.pos().getX());
        tag.putInt(TAG_BOUND_Y, pos.pos().getY());
        tag.putInt(TAG_BOUND_Z, pos.pos().getZ());
        tag.putString(TAG_BOUND_DIM, pos.dimension().location().toString());
        saveTag(stack, tag);
    }
}

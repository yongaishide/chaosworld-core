package com.yongaishide.chaosworld.block.entity.pattern;

import com.yongaishide.chaosworld.block.MultiblockBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuantumPatternPredicates {

    private static final ResourceLocation AE2_QUARTZ_VIBRANT_GLASS = ResourceLocation.fromNamespaceAndPath("ae2", "quartz_vibrant_glass");

    private QuantumPatternPredicates() {
    }

    public static Map<Character, BlockState> getDefaultCreativeStates() {
        Map<Character, BlockState> map = new HashMap<>();
        map.put('C', MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get().defaultBlockState());
        map.put('F', MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get().defaultBlockState());
        map.put('P', MultiblockBlocks.QUANTUM_PATTERN_HATCH.get().defaultBlockState());

        Block vibrantGlass = BuiltInRegistries.BLOCK.get(AE2_QUARTZ_VIBRANT_GLASS);
        if (vibrantGlass != null && vibrantGlass != Blocks.AIR) {
            map.put('G', vibrantGlass.defaultBlockState());
        }

        return map;
    }

    public static boolean isQuantumCasing(BlockState state) {
        return state.is(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get());
    }

    public static boolean isUniversalHatch(BlockState state) {
        return state.is(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get())
                || state.is(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())
                || state.is(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())
                || state.is(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())
                || state.is(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get());
    }

    public static boolean isQuantumCasingOrUniversalHatch(BlockState state) {
        return isQuantumCasing(state) || isUniversalHatch(state);
    }

    public static boolean isAnyFieldGenerator(BlockState state) {
        return state.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get())
                || state.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get())
                || state.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get());
    }

    public static boolean isQuartzVibrantGlass(BlockState state) {
        Block block = BuiltInRegistries.BLOCK.get(AE2_QUARTZ_VIBRANT_GLASS);
        return block != null && block != Blocks.AIR && state.is(block);
    }

    public static Component casingName() {
        return MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get().getName();
    }

    public static Component casingOrHatchName() {
        return Component.translatable("message.ufo.multiblock.casing_or_hatch");
    }

    public static Component patternHatchName() {
        return MultiblockBlocks.QUANTUM_PATTERN_HATCH.get().getName();
    }

    public static Component fieldName() {
        return Component.translatable("message.ufo.multiblock.field_generator");
    }

    public static List<BlockState> fieldCandidates() {
        return allFieldCandidates();
    }

    public static List<BlockState> allFieldCandidates() {
        return List.of(
                MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get().defaultBlockState(),
                MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get().defaultBlockState(),
                MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get().defaultBlockState()
        );
    }

    public static Component glassName() {
        Block block = BuiltInRegistries.BLOCK.get(AE2_QUARTZ_VIBRANT_GLASS);
        return block != null && block != Blocks.AIR ? block.getName() : Component.literal("AE2 Quartz Vibrant Glass");
    }

    public static List<BlockState> glassCandidates() {
        Block block = BuiltInRegistries.BLOCK.get(AE2_QUARTZ_VIBRANT_GLASS);
        if (block == null || block == Blocks.AIR) {
            return List.of();
        }
        return List.of(block.defaultBlockState());
    }

    public static List<BlockState> casingAndHatchCandidates() {
        return List.of(
                MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get().defaultBlockState(),
                MultiblockBlocks.QUANTUM_PATTERN_HATCH.get().defaultBlockState(),
                MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get().defaultBlockState(),
                MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get().defaultBlockState(),
                MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get().defaultBlockState(),
                MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get().defaultBlockState()
        );
    }

    public static List<BlockState> casingOrHatchCandidates() {
        return List.of(
                MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get().defaultBlockState(),
                MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get().defaultBlockState(),
                MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get().defaultBlockState(),
                MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get().defaultBlockState(),
                MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get().defaultBlockState(),
                MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get().defaultBlockState()
        );
    }
}

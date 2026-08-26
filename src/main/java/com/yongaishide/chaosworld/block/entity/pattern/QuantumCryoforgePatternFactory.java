package com.yongaishide.chaosworld.block.entity.pattern;

import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public final class QuantumCryoforgePatternFactory {

    private QuantumCryoforgePatternFactory() {
    }

    public static Map<Character, BlockState> getDefaultCreativeStates() {
        Map<Character, BlockState> map = new HashMap<>();
        map.put('B', MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get().defaultBlockState());
        map.put('D', Blocks.BLUE_ICE.defaultBlockState());
        map.put('F', MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get().defaultBlockState());

        Block vibrantGlass = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("ae2", "quartz_vibrant_glass"));
        if (vibrantGlass != null && vibrantGlass != Blocks.AIR) {
            map.put('E', vibrantGlass.defaultBlockState());
        }

        return map;
    }

    public static MultiblockPattern getPattern() {
        return new MultiblockPattern.Builder()
                .controllerChar('C')
                .layer(new String[]{
                        "BBBBAA",
                        "BBBBBA",
                        "BBBBBB",
                        "BBBBBB",
                        "ABBBBB",
                        "AABBBA",
                        "BBAAAA"
                })
                .layer(new String[]{
                        "BEBBBA",
                        "BEEEEB",
                        "BEFFEB",
                        "BEEEEC",
                        "BBFFEB",
                        "ABBBBB",
                        "BBBBBA"
                })
                .layer(new String[]{
                        "BEBBBA",
                        "BFFFEB",
                        "BEAAAD",
                        "BFAAAD",
                        "BAAAAD",
                        "BBFFEB",
                        "BBBBBA"
                })
                .layer(new String[]{
                        "BEBBBA",
                        "BEEEFB",
                        "BFAAAD",
                        "BEAAAD",
                        "BAAAAD",
                        "BBEEFB",
                        "BBBBBA"
                })
                .layer(new String[]{
                        "BEBBBA",
                        "BFFFEB",
                        "BEAAAD",
                        "BFAAAD",
                        "BAAAAD",
                        "BBFFEB",
                        "BBBBBA"
                })
                .layer(new String[]{
                        "BEBBBA",
                        "BEEEEB",
                        "BEFFEB",
                        "BEEEFB",
                        "BBFFEB",
                        "BBBBBB",
                        "ABBBBA"
                })
                .layer(new String[]{
                        "BBBBAA",
                        "BBBBBA",
                        "BBBBBA",
                        "BBBBBA",
                        "BBBBBA",
                        "ABBBBA",
                        "BABBAA"
                })
                .where('C', (state, level, pos) -> state.is(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get()))
                .where('B', (state, level, pos) -> QuantumPatternPredicates.isQuantumCasingOrUniversalHatch(state),
                        QuantumPatternPredicates.casingOrHatchName())
                .candidates('B', QuantumPatternPredicates.casingAndHatchCandidates())
                .where('D', (state, level, pos) -> state.is(net.minecraft.world.level.block.Blocks.BLUE_ICE),
                        net.minecraft.world.level.block.Blocks.BLUE_ICE.getName())
                .candidates('D', net.minecraft.world.level.block.Blocks.BLUE_ICE.defaultBlockState())
                .where('F', (state, level, pos) -> QuantumPatternPredicates.isAnyFieldGenerator(state),
                        QuantumPatternPredicates.fieldName())
                .candidates('F', QuantumPatternPredicates.fieldCandidates())
                .where('E', (state, level, pos) -> QuantumPatternPredicates.isQuartzVibrantGlass(state),
                        QuantumPatternPredicates.glassName())
                .candidates('E', QuantumPatternPredicates.glassCandidates())
                .where('A', (state, level, pos) -> state.isAir())
                .shell('B', 'E')
                .build();
    }
}

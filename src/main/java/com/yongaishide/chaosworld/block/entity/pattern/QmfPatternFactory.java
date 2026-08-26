package com.yongaishide.chaosworld.block.entity.pattern;

import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class QmfPatternFactory {

    public static Map<Character, BlockState> getDefaultCreativeStates() {
        return QuantumPatternPredicates.getDefaultCreativeStates();
    }

    public static MultiblockPattern getPattern() {
        return new MultiblockPattern.Builder()
                .controllerChar('H')
                .layer(new String[]{
                        "ACCCA",
                        "CCCCC",
                        "CCCCC",
                        "CCCCC",
                        "ACCCA"
                })
                .layer(new String[]{
                        "CGGGC",
                        "GFFFG",
                        "GFFFG",
                        "GFFFG",
                        "CGGGC"
                })
                .layer(new String[]{
                        "CPHGC",
                        "GFFFG",
                        "GFFFG",
                        "GFFFG",
                        "CGGGC"
                })
                .layer(new String[]{
                        "CGGGC",
                        "GFFFG",
                        "GFFFG",
                        "GFFFG",
                        "CGGGC"
                })
                .layer(new String[]{
                        "ACCCA",
                        "CGGGC",
                        "CGGGC",
                        "CGGGC",
                        "ACCCA"
                })
                .where('H', (state, level, pos) -> state.is(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get()))
                .where('P', (state, level, pos) -> state.is(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get()), QuantumPatternPredicates.patternHatchName())
                .where('C', (state, level, pos) -> QuantumPatternPredicates.isQuantumCasing(state), QuantumPatternPredicates.casingName())
                .where('F', (state, level, pos) -> QuantumPatternPredicates.isAnyFieldGenerator(state), QuantumPatternPredicates.fieldName())
                .candidates('F', QuantumPatternPredicates.fieldCandidates())
                .where('G', (state, level, pos) -> QuantumPatternPredicates.isQuartzVibrantGlass(state), QuantumPatternPredicates.glassName())
                .where('A', (state, level, pos) -> state.isAir())
                .shell('C', 'G')
                .build();
    }
}

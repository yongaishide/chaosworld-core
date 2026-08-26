package com.yongaishide.chaosworld.datagen;

import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.metal.ModMetals;
import com.yongaishide.chaosworld.metal.ModTech;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        // --- Blocos normais estáticos ---
        this.dropSelf(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK.get());
        this.dropSelf(ModBlocks.NEUTRON_STAR_FRAGMENT_BLOCK.get());
        this.dropSelf(ModBlocks.PULSAR_FRAGMENT_BLOCK.get());
        this.dropSelf(ModBlocks.GRAVITON_PLATED_CASING.get());
        this.dropSelf(ModBlocks.QUANTUM_LATTICE_FRAME.get());
        this.dropSelf(ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get());
        this.dropSelf(ModBlocks.UFO_ENERGY_CELL.get());
        this.dropSelf(ModBlocks.QUANTUM_ENERGY_CELL.get());
        this.dropSelf(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_ENTROPY_CASING.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get());
        this.dropSelf(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get());
        this.dropSelf(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get());
        this.dropSelf(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get());
        this.dropSelf(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get());
        this.dropSelf(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get());
        this.dropSelf(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get());
        this.dropSelf(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get());

        ModMetals.METAL_BLOCKS.values().forEach(holder -> this.dropSelf(holder.get()));
        ModTech.TECH_BLOCKS.values().forEach(holder -> this.dropSelf(holder.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Stream.concat(
                ModBlocks.BLOCKS.getEntries().stream().map(Holder::value),
                Stream.concat(
                        MultiblockBlocks.BLOCKS.getEntries().stream().map(Holder::value),
                        Stream.concat(
                                ModMetals.METAL_BLOCKS.values().stream().map(Holder::value),
                                ModTech.TECH_BLOCKS.values().stream().map(Holder::value)
                        )
                )
        ).collect(Collectors.toSet());
    }
}

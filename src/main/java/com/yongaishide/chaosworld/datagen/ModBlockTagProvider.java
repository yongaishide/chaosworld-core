package com.yongaishide.chaosworld.datagen;

import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.metal.ModMetals;
import com.yongaishide.chaosworld.metal.ModTech;
import com.yongaishide.chaosworld.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "chaosworld_core", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // --- IN闂佸吋鎯屽鐭稯 DA CORRE闂佺厧顕崰鏇㈠礈缁?---
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.QUANTUM_LATTICE_FRAME.get())         // <<-- ADICIONE .get() AQUI
                .add(ModBlocks.GRAVITON_PLATED_CASING.get())       // <<-- E AQUI
                .add(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK.get())  // <<-- E AQUI
                .add(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get())
                .add(MultiblockBlocks.QUANTUM_ENTROPY_CASING.get())
                .add(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get())
                .add(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get())
                .add(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get())
                .add(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())
                .add(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())
                .add(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())
                .add(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.QUANTUM_LATTICE_FRAME.get())         // <<-- ADICIONE .get() AQUI
                .add(ModBlocks.GRAVITON_PLATED_CASING.get())       // <<-- E AQUI
                .add(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK.get())  // <<-- E AQUI
                .add(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get())
                .add(MultiblockBlocks.QUANTUM_ENTROPY_CASING.get())
                .add(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get())
                .add(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get())
                .add(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get())
                .add(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())
                .add(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())
                .add(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())
                .add(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get());
        // --- FIM DA CORRE闂佺厧顕崰鏇㈠礈缁?---


        tag(BlockTags.NEEDS_DIAMOND_TOOL);


        tag(ModTags.Blocks.NEEDS_UFO_TOOL)
                .addTag(BlockTags.NEEDS_IRON_TOOL)
                .addTag(BlockTags.NEEDS_DIAMOND_TOOL)
                .addOptionalTag(ResourceLocation.parse("allthemodium:needs_allthemodium_tool"))
                .addOptionalTag(ResourceLocation.parse("allthemodium:needs_vibranium_tool"))
                .addOptionalTag(ResourceLocation.parse("allthemodium:needs_unobtainium_tool"));

        tag(ModTags.Blocks.INCORRECT_FOR_UFO_TOOL)
                .addTag(BlockTags.INCORRECT_FOR_IRON_TOOL)
                .addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                .remove(ModTags.Blocks.NEEDS_UFO_TOOL);

        ModMetals.METAL_BLOCKS.values().forEach(holder -> {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(holder.get());
            addStorageBlockTag(holder);
        });
        ModTech.TECH_BLOCKS.values().forEach(holder -> {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(holder.get());
            addStorageBlockTag(holder);
        });
    }

    private void addStorageBlockTag(DeferredHolder<Block, ? extends Block> holder) {
        String id = holder.getId().getPath();
        if (id.endsWith("_block")) {
            String material = id.substring(0, id.length() - 6);
            TagKey<Block> specific = TagKey.create(Registries.BLOCK,
                    ResourceLocation.parse("c:storage_blocks/" + material));
            tag(specific).add(holder.get());
            tag(TagKey.create(Registries.BLOCK, ResourceLocation.parse("c:storage_blocks"))).add(holder.get());
        }
    }
}

package com.yongaishide.chaosworld.datagen;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.block.MultiblockBlocks; // Importa a nova classe
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, "chaosworld_core", exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // --- Blocos Simples Originais ---
        simpleBlockWithItem(ModBlocks.QUANTUM_LATTICE_FRAME);
        simpleBlockWithItem(ModBlocks.GRAVITON_PLATED_CASING);
        blockWithFluidTexture(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK, "white_dwarf_fragment");
        blockWithFluidTexture(ModBlocks.PULSAR_FRAGMENT_BLOCK, "pulsar_fragment");
        blockWithFluidTexture(ModBlocks.NEUTRON_STAR_FRAGMENT_BLOCK, "neutron_star_fragment");

        // --- Registro dos Novos Blocos Multiblock ---
        // Blocos que s茫o um cubo simples
        multiblockCube(MultiblockBlocks.ENTROPY_SINGULARITY_CASING);
        multiblockCubeWithTexture(MultiblockBlocks.QUANTUM_ENTROPY_CASING, "quantum_hyper_mechanical_casing");
        multiblockCube(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING);
        qmfControllerBlock(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER);
        controllerWithBase(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER, "quantum_hyper_mechanical_casing", "quantum_slicer/overlay_front");
        controllerWithBase(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER, "quantum_hyper_mechanical_casing", "quantum_processing_factory/overlay_front");
        controllerWithBase(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER, "quantum_hyper_mechanical_casing", "multiblock/overlay_front");

        // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?STELLAR NEXUS 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?        stellarNexusControllerBlock(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER);
        hatchWithOverlay(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH, "me_massive_output_hatch_overlay");
        hatchWithOverlay(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH, "me_massive_fluid_hatch_overlay");
        hatchWithOverlay(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH, "me_massive_input_hatch_overlay");
        hatchWithOverlay(MultiblockBlocks.AE_ENERGY_INPUT_HATCH, "ae_energy_input_hatch_overlay");
        multiblockCube(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1);
        multiblockCube(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2);
        multiblockCube(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3);

    }

    private void simpleBlockWithItem(DeferredBlock<Block> blockHolder) {
        String registryName = blockHolder.getId().getPath();
        ModelFile model = models().cubeAll(registryName, modLoc("block/" + registryName));
        simpleBlock(blockHolder.get(), model);
        simpleBlockItem(blockHolder.get(), model);
    }

    // --- M脡TODOS AUXILIARES PARA OS NOVOS BLOCOS ---

    /**
     * Registra um bloco de multiblock que 茅 um cubo simples.
     */
    private void multiblockCube(DeferredBlock<? extends Block> block) {
        String name = block.getId().getPath();
        ResourceLocation texture = modLoc("block/multiblock/" + name);
        simpleBlock(block.get(), models().cubeAll(name, texture));
        simpleBlockItem(block.get(), models().getExistingFile(modLoc("block/" + name)));
    }

    private void multiblockCubeWithTexture(DeferredBlock<? extends Block> block, String textureName) {
        String name = block.getId().getPath();
        ResourceLocation texture = modLoc("block/multiblock/" + textureName);
        simpleBlock(block.get(), models().cubeAll(name, texture));
        simpleBlockItem(block.get(), models().getExistingFile(modLoc("block/" + name)));
    }

    private void craftingLikeCube(DeferredBlock<? extends Block> block, String textureName) {
        String name = block.getId().getPath();
        ResourceLocation texture = modLoc("block/multiblock/" + textureName);
        ModelFile model = models().cubeAll(name, texture);

        getVariantBuilder(block.get())
                .partialState().with(AbstractCraftingUnitBlock.FORMED, false)
                .setModels(new ConfiguredModel(model))
                .partialState().with(AbstractCraftingUnitBlock.FORMED, true)
                .setModels(new ConfiguredModel(model));

        simpleBlockItem(block.get(), model);
    }

    private void entropicMachineCube(DeferredBlock<? extends Block> block, String textureName) {
        String name = block.getId().getPath();
        ResourceLocation texture = modLoc("block/multiblock/" + textureName);
        ModelFile model = models().cubeAll(name, texture);

        getVariantBuilder(block.get()).forAllStates(state -> {
            boolean formed = getBooleanPropertyByName(state, "formed");
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .build();
        });

        simpleBlockItem(block.get(), model);
    }

    private boolean getBooleanPropertyByName(BlockState state, String propertyName) {
        for (var property : state.getProperties()) {
            if (property.getName().equals(propertyName) && property instanceof BooleanProperty booleanProperty) {
                return state.getValue(booleanProperty);
            }
        }
        return false;
    }

    /**
     * Directional multiblock cube 鈥?same texture on all faces, rotated by FACING.
     */
    private void directionalMultiblockCube(DeferredBlock<? extends Block> block) {
        String name = block.getId().getPath();
        ResourceLocation texture = modLoc("block/multiblock/" + name);
        ModelFile model = models().cubeAll(name, texture);

        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(DirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), model);
    }

    private void blockWithFluidTexture(DeferredBlock<Block> block, String fluidTextureName) {
        String name = block.getId().getPath();
        ResourceLocation texture = modLoc("block/fluid/" + fluidTextureName);
        ModelFile model = models().cubeAll(name, texture);
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }
    private void multiblockComponentBlock(DeferredBlock<Block> block) {
        String name = block.getId().getPath();
        ResourceLocation baseTexture = modLoc("block/multiblock/entropy_assembler_core_casing_base");
        ResourceLocation overlayTexture = modLoc("block/multiblock/" + name);

        ModelFile modelFile = models().withExistingParent(name, "block/block")
                .renderType("cutout") // <<-- CORRE脟脙O: renderType aplicado ao ModelBuilder
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayTexture)
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .allFaces((direction, faceBuilder) -> faceBuilder.texture("#base").cullface(direction))
                .end()
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end()
                .end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(DirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), modelFile);
    }

    private void controllerBlock(DeferredBlock<Block> block) {
        ResourceLocation baseTexture = modLoc("block/multiblock/entropy_assembler_core_casing_base");
        ResourceLocation overlayInactive = modLoc("block/general1/overlay_front");
        ResourceLocation overlayActive = modLoc("block/general1/overlay_front_active");

        var inactiveModel = models().withExistingParent(block.getId().getPath(), "block/block")
                .renderType("cutout") // <<-- CORRE脟脙O: renderType aplicado ao ModelBuilder
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayInactive)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end();

        var activeModel = models().withExistingParent(block.getId().getPath() + "_active", "block/block")
                .renderType("cutout") // <<-- CORRE脟脙O: renderType aplicado ao ModelBuilder
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayActive)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(DirectionalBlock.FACING);
            boolean isActive = state.getValue(MultiblockBlocks.ControllerBlock.ACTIVE);
            return ConfiguredModel.builder()
                    .modelFile((isActive ? activeModel : inactiveModel).end())
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), inactiveModel.end());
    }

    private void controllerWithBase(DeferredBlock<? extends Block> block, String baseTextureName, String overlayTextureName) {
        String name = block.getId().getPath();
        ResourceLocation baseTexture = modLoc("block/multiblock/" + baseTextureName);
        ResourceLocation overlayTexture = modLoc("block/" + overlayTextureName);

        ModelFile model = models().withExistingParent(name, "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayTexture)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(DirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), model);
    }

    private void qmfControllerBlock(DeferredBlock<? extends Block> block) {
        String name = block.getId().getPath();
        ResourceLocation baseTexture = modLoc("block/multiblock/quantum_hyper_mechanical_casing");
        ResourceLocation overlayInactive = modLoc("block/qmf/overlay_front");
        ResourceLocation overlayActive = modLoc("block/qmf/overlay_front_active");

        ModelFile inactiveModel = models().withExistingParent(name, "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayInactive)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        ModelFile activeModel = models().withExistingParent(name + "_active", "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayActive)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(DirectionalBlock.FACING);
            boolean active = state.getValue(com.yongaishide.chaosworld.block.MultiblockBlocks.ControllerBlock.ACTIVE);
            return ConfiguredModel.builder()
                    .modelFile(active ? activeModel : inactiveModel)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), inactiveModel);
    }

    private void stellarNexusControllerBlock(DeferredBlock<? extends Block> block) {
        String name = block.getId().getPath();
        ResourceLocation baseTexture = modLoc("block/multiblock/entropy_singularity_casing");
        ResourceLocation overlayTexture = modLoc("block/multiblock/overlay_front");

        ModelFile normalModel = models().withExistingParent(name, "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayTexture)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        ResourceLocation assembledBase = modLoc("block/multiblock/entropy_assembler_core_casing");
        ModelFile assembledModel = models().withExistingParent(name + "_assembled", "block/block")
                .renderType("cutout")
                .texture("particle", assembledBase)
                .texture("base", assembledBase)
                .texture("overlay", overlayTexture)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(DirectionalBlock.FACING);
            boolean assembled = state.getValue(com.yongaishide.chaosworld.block.StellarNexusControllerBlock.ASSEMBLED);
            return ConfiguredModel.builder()
                    .modelFile(assembled ? assembledModel : normalModel)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), normalModel);
    }

    /**
     * Hatch block with entropy_singularity_casing as base + a per-hatch overlay on the front face.
     * Uses cutout render type to support animated overlay textures.
     */
    private void hatchWithOverlay(DeferredBlock<? extends Block> block, String overlayName) {
        String name = block.getId().getPath();
        ResourceLocation baseTexture = modLoc("block/multiblock/entropy_singularity_casing");
        ResourceLocation overlayTexture = modLoc("block/multiblock/" + overlayName);

        ModelFile modelFile = models().withExistingParent(name, "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayTexture)
                .element()
                    .from(0, 0, 0).to(16, 16, 16)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#base").cullface(direction))
                .end()
                .element()
                    .from(0, 0, 0).to(16, 16, 16)
                    .face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end()
                .end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(DirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), modelFile);
    }
}

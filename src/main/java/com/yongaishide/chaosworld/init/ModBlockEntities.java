package com.yongaishide.chaosworld.init;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.block.entity.QuantumEnergyCellBlockEntity;
import com.yongaishide.chaosworld.block.entity.UfoEnergyCellBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream; // <<-- IMPORT ADICIONADO

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "chaosworld_core");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.yongaishide.chaosworld.block.entity.DimensionalMatterAssemblerBlockEntity>> DIMENSIONAL_MATTER_ASSEMBLER_BE =
            BLOCK_ENTITIES.register("dimensional_matter_assembler", () -> {
                final java.util.concurrent.atomic.AtomicReference<BlockEntityType<com.yongaishide.chaosworld.block.entity.DimensionalMatterAssemblerBlockEntity>> typeHolder = new java.util.concurrent.atomic.AtomicReference<>();
                var type = BlockEntityType.Builder.of(
                        (pos, state) -> new com.yongaishide.chaosworld.block.entity.DimensionalMatterAssemblerBlockEntity(typeHolder.get(), pos, state),
                        ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get()
                ).build(null);
                typeHolder.set(type);
                appeng.blockentity.AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().asItem());
                ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().setBlockEntity(
                        com.yongaishide.chaosworld.block.entity.DimensionalMatterAssemblerBlockEntity.class, 
                        type, 
                        null, 
                        (level, pos, state, be) -> be.serverTick()
                );
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UfoEnergyCellBlockEntity>> UFO_ENERGY_CELL_BE =
            BLOCK_ENTITIES.register("energy_cell", () -> {
                final AtomicReference<BlockEntityType<UfoEnergyCellBlockEntity>> typeHolder = new AtomicReference<>();
                var type = BlockEntityType.Builder.of(
                        (pos, state) -> new UfoEnergyCellBlockEntity(typeHolder.get(), pos, state),
                        ModBlocks.UFO_ENERGY_CELL.get()
                ).build(null);
                typeHolder.set(type);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.UFO_ENERGY_CELL.get().asItem());
                ((appeng.block.AEBaseEntityBlock<?>) ModBlocks.UFO_ENERGY_CELL.get()).setBlockEntity(
                        (Class) UfoEnergyCellBlockEntity.class,
                        (BlockEntityType) type,
                        null,
                        (level, pos, state, be) -> ((UfoEnergyCellBlockEntity) be).serverTick());
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuantumEnergyCellBlockEntity>> QUANTUM_ENERGY_CELL_BE =
            BLOCK_ENTITIES.register("quantum_energy_cell", () -> {
                final AtomicReference<BlockEntityType<QuantumEnergyCellBlockEntity>> typeHolder = new AtomicReference<>();
                var type = BlockEntityType.Builder.of(
                        (pos, state) -> new QuantumEnergyCellBlockEntity(typeHolder.get(), pos, state),
                        ModBlocks.QUANTUM_ENERGY_CELL.get()
                ).build(null);
                typeHolder.set(type);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.QUANTUM_ENERGY_CELL.get().asItem());
                ((appeng.block.AEBaseEntityBlock<?>) ModBlocks.QUANTUM_ENERGY_CELL.get()).setBlockEntity(
                        (Class) QuantumEnergyCellBlockEntity.class,
                        (BlockEntityType) type,
                        null,
                        (level, pos, state, be) -> ((QuantumEnergyCellBlockEntity) be).serverTick());
                return type;
            });

    // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?    //  QUANTUM MATTER FABRICATOR 鈥?Block Entities
    // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.yongaishide.chaosworld.block.entity.QmfControllerBE>> QMF_CONTROLLER =
            BLOCK_ENTITIES.register("qmf_controller", () -> {
                var type = BlockEntityType.Builder.of(
                        (pos, state) -> new com.yongaishide.chaosworld.block.entity.QmfControllerBE(pos, state),
                        com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get()
                ).build(null);
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.yongaishide.chaosworld.block.entity.QuantumSlicerControllerBE>> QUANTUM_SLICER_CONTROLLER_BE =
            BLOCK_ENTITIES.register("quantum_slicer_controller", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new com.yongaishide.chaosworld.block.entity.QuantumSlicerControllerBE(pos, state),
                    com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.yongaishide.chaosworld.block.entity.QuantumProcessorAssemblerControllerBE>> QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_BE =
            BLOCK_ENTITIES.register("quantum_processing_factory_controller", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new com.yongaishide.chaosworld.block.entity.QuantumProcessorAssemblerControllerBE(pos, state),
                    com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.yongaishide.chaosworld.block.entity.QuantumCryoforgeControllerBE>> QUANTUM_CRYOFORGE_CONTROLLER_BE =
            BLOCK_ENTITIES.register("quantum_cryoforge_controller", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new com.yongaishide.chaosworld.block.entity.QuantumCryoforgeControllerBE(pos, state),
                    com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.yongaishide.chaosworld.block.entity.QuantumPatternHatchBE>> QUANTUM_PATTERN_HATCH_BE =
            BLOCK_ENTITIES.register("quantum_pattern_hatch", () -> {
                var type = BlockEntityType.Builder.of(
                        (pos, state) -> new com.yongaishide.chaosworld.block.entity.QuantumPatternHatchBE(pos, state),
                        com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_PATTERN_HATCH.get()
                ).build(null);
                ((appeng.block.AEBaseEntityBlock<?>) com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_PATTERN_HATCH.get()).setBlockEntity(
                        (Class) appeng.blockentity.crafting.PatternProviderBlockEntity.class,
                        (BlockEntityType) type,
                        null,
                        null
                );
                return type;
            });

    // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?    //  STELLAR NEXUS 鈥?Block Entities
    // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.yongaishide.chaosworld.block.entity.StellarNexusControllerBE>> STELLAR_NEXUS_CONTROLLER_BE =
            BLOCK_ENTITIES.register("stellar_nexus_controller", () -> {
                var type = BlockEntityType.Builder.of(
                        (pos, state) -> new com.yongaishide.chaosworld.block.entity.StellarNexusControllerBE(
                                ModBlockEntities.STELLAR_NEXUS_CONTROLLER_BE.get(), pos, state),
                        com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get()
                ).build(null);
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.yongaishide.chaosworld.block.entity.StellarNexusPartBE>> STELLAR_NEXUS_PART_BE =
            BLOCK_ENTITIES.register("stellar_nexus_part", () -> {
                var type = BlockEntityType.Builder.of(
                        (pos, state) -> new com.yongaishide.chaosworld.block.entity.StellarNexusPartBE(
                                ModBlockEntities.STELLAR_NEXUS_PART_BE.get(), pos, state),
                        // Only field generators and non-AE2 structural blocks
                        com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get(),
                        com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get(),
                        com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get()
                ).build(null);
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.yongaishide.chaosworld.block.entity.MassiveOutputHatchBE>> ME_MASSIVE_OUTPUT_HATCH_BE =
            BLOCK_ENTITIES.register("me_massive_output_hatch", () -> {
                final java.util.concurrent.atomic.AtomicReference<BlockEntityType<com.yongaishide.chaosworld.block.entity.MassiveOutputHatchBE>> typeHolder = new java.util.concurrent.atomic.AtomicReference<>();
                var type = BlockEntityType.Builder.of(
                        (pos, state) -> new com.yongaishide.chaosworld.block.entity.MassiveOutputHatchBE(typeHolder.get(), pos, state),
                        com.yongaishide.chaosworld.block.MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get(),
                        com.yongaishide.chaosworld.block.MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get(),
                        com.yongaishide.chaosworld.block.MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get(),
                        com.yongaishide.chaosworld.block.MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get()
                ).build(null);
                typeHolder.set(type);
                // Register item mapping for AE2 wrench/network tool compatibility
                appeng.blockentity.AEBaseBlockEntity.registerBlockEntityItem(type,
                        com.yongaishide.chaosworld.block.MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get().asItem());
                return type;
            });

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

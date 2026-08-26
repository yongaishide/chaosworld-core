package com.yongaishide.chaosworld.api.multiblock;

import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.block.entity.QuantumProcessorAssemblerControllerBE;
import com.yongaishide.chaosworld.block.entity.QuantumCryoforgeControllerBE;
import com.yongaishide.chaosworld.block.entity.QuantumSlicerControllerBE;
import com.yongaishide.chaosworld.block.entity.QmfControllerBE;
import com.yongaishide.chaosworld.block.entity.StellarNexusControllerBE;
import com.yongaishide.chaosworld.block.entity.pattern.QmfPatternFactory;
import com.yongaishide.chaosworld.block.entity.pattern.QpaPatternFactory;
import com.yongaishide.chaosworld.block.entity.pattern.QuantumCryoforgePatternFactory;
import com.yongaishide.chaosworld.block.entity.pattern.QuantumSlicerPatternFactory;
import com.yongaishide.chaosworld.block.entity.pattern.StellarNexusPatternFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public final class MultiblockControllerDefinitions {

    public record PreviewEntry(ResourceLocation id, ItemStack iconStack, MultiblockControllerDefinition definition) {
    }

    private static final List<PreviewEntry> PREVIEW_ENTRIES = List.of(
            new PreviewEntry(
                    ResourceLocation.fromNamespaceAndPath("chaosworld_core", "stellar_nexus"),
                    MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get().asItem().getDefaultInstance(),
                    new MultiblockControllerDefinition(
                            Component.translatable("block.chaosworld_core.stellar_nexus_controller"),
                            StellarNexusPatternFactory.getPattern(),
                            StellarNexusPatternFactory.getDefaultCreativeStates())),
            new PreviewEntry(
                    ResourceLocation.fromNamespaceAndPath("chaosworld_core", "quantum_matter_fabricator"),
                    MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem().getDefaultInstance(),
                    new MultiblockControllerDefinition(
                            Component.translatable("block.chaosworld_core.quantum_matter_fabricator_controller"),
                            QmfPatternFactory.getPattern(),
                            QmfPatternFactory.getDefaultCreativeStates())),
            new PreviewEntry(
                    ResourceLocation.fromNamespaceAndPath("chaosworld_core", "quantum_slicer"),
                    MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get().asItem().getDefaultInstance(),
                    new MultiblockControllerDefinition(
                            Component.translatable("block.chaosworld_core.quantum_slicer_controller"),
                            QuantumSlicerPatternFactory.getPattern(),
                            QuantumSlicerPatternFactory.getDefaultCreativeStates())),
            new PreviewEntry(
                    ResourceLocation.fromNamespaceAndPath("chaosworld_core", "quantum_processing_factory"),
                    MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get().asItem().getDefaultInstance(),
                    new MultiblockControllerDefinition(
                            Component.translatable("block.chaosworld_core.quantum_processing_factory_controller"),
                            QpaPatternFactory.getPattern(),
                            QpaPatternFactory.getDefaultCreativeStates())),
            new PreviewEntry(
                    ResourceLocation.fromNamespaceAndPath("chaosworld_core", "quantum_cryoforge"),
                    MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem().getDefaultInstance(),
                    new MultiblockControllerDefinition(
                            Component.translatable("block.chaosworld_core.quantum_cryoforge_controller"),
                            QuantumCryoforgePatternFactory.getPattern(),
                            QuantumCryoforgePatternFactory.getDefaultCreativeStates()))
    );

    private MultiblockControllerDefinitions() {
    }

    public static List<PreviewEntry> getPreviewEntries() {
        return PREVIEW_ENTRIES;
    }

    public static Optional<MultiblockControllerDefinition> getDefinition(BlockEntity be) {
        if (be instanceof StellarNexusControllerBE) {
            return Optional.of(getPreviewEntries().get(0).definition());
        }
        if (be instanceof QmfControllerBE) {
            return Optional.of(getPreviewEntries().get(1).definition());
        }
        if (be instanceof QuantumSlicerControllerBE) {
            return Optional.of(getPreviewEntries().get(2).definition());
        }
        if (be instanceof QuantumProcessorAssemblerControllerBE) {
            return Optional.of(getPreviewEntries().get(3).definition());
        }
        if (be instanceof QuantumCryoforgeControllerBE) {
            return Optional.of(getPreviewEntries().get(4).definition());
        }
        return Optional.empty();
    }

    public static Direction getPatternFacing(BlockEntity be, BlockState state) {
        Direction facing = state.hasProperty(DirectionalBlock.FACING)
                ? state.getValue(DirectionalBlock.FACING)
                : Direction.NORTH;
        if (be instanceof QuantumCryoforgeControllerBE) {
            return facing.getCounterClockWise();
        }
        return facing;
    }

    public static boolean isSupportedController(BlockState state) {
        return state.is(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get())
                || state.is(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get())
                || state.is(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get())
                || state.is(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get())
                || state.is(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get());
    }
}

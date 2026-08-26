package com.yongaishide.chaosworld.block;

import com.mojang.serialization.MapCodec;
import com.yongaishide.chaosworld.block.entity.QuantumSlicerControllerBE;
import com.yongaishide.chaosworld.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class QuantumSlicerControllerBlock extends AbstractSimpleMultiblockControllerBlock<QuantumSlicerControllerBE> {

    public static final MapCodec<QuantumSlicerControllerBlock> CODEC = simpleCodec(QuantumSlicerControllerBlock::new);

    public QuantumSlicerControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected BlockEntityType<QuantumSlicerControllerBE> getBlockEntityType() {
        return ModBlockEntities.QUANTUM_SLICER_CONTROLLER_BE.get();
    }

    @Override
    protected QuantumSlicerControllerBE createBlockEntity(BlockPos pos, BlockState state) {
        return new QuantumSlicerControllerBE(pos, state);
    }
}

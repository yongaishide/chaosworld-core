package com.yongaishide.chaosworld.block;

import com.mojang.serialization.MapCodec;
import com.yongaishide.chaosworld.block.entity.QuantumCryoforgeControllerBE;
import com.yongaishide.chaosworld.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class QuantumCryoforgeControllerBlock extends AbstractSimpleMultiblockControllerBlock<QuantumCryoforgeControllerBE> {

    public static final MapCodec<QuantumCryoforgeControllerBlock> CODEC = simpleCodec(QuantumCryoforgeControllerBlock::new);

    public QuantumCryoforgeControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected BlockEntityType<QuantumCryoforgeControllerBE> getBlockEntityType() {
        return ModBlockEntities.QUANTUM_CRYOFORGE_CONTROLLER_BE.get();
    }

    @Override
    protected QuantumCryoforgeControllerBE createBlockEntity(BlockPos pos, BlockState state) {
        return new QuantumCryoforgeControllerBE(pos, state);
    }
}

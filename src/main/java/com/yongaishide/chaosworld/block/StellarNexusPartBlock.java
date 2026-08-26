package com.yongaishide.chaosworld.block;

import com.yongaishide.chaosworld.api.multiblock.EntropicMachineLocator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import com.yongaishide.chaosworld.api.multiblock.IMultiblockController;
import com.yongaishide.chaosworld.block.entity.StellarNexusControllerBE;
import com.yongaishide.chaosworld.block.entity.StellarNexusPartBE;
import com.yongaishide.chaosworld.init.ModBlockEntities;

/**
 * Generic structural block for the Stellar Nexus multiblock.
 * <p>
 * This class is reused for various "parts" of the structure:
 * casings, hatches, field generators, coolant matrices, etc.
 * Each instance gets its own unique registry ID and texture, but
 * they all share the same block entity type and linking logic.
 */
public class StellarNexusPartBlock extends Block implements net.minecraft.world.level.block.EntityBlock {

    public StellarNexusPartBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StellarNexusPartBE(ModBlockEntities.STELLAR_NEXUS_PART_BE.get(), pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        var controller = EntropicMachineLocator.findController(level, pos);
        if (controller != null) {
            if (!level.isClientSide() && controller.isAssembled() && controller.isNetworkConnected()
                    && controller instanceof net.minecraft.world.MenuProvider menuProvider) {
                player.openMenu(menuProvider, controller.getControllerPos());
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof StellarNexusPartBE part) {
                BlockPos controllerPos = part.getControllerPos();
                if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof IMultiblockController controller) {
                    controller.removePart(pos);
                    controller.scanStructure(level);
                }
                part.unlinkFromController();
            }
            if (!level.isClientSide()) {
                EntropicMachineLocator.markNearbyDirty(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block changedBlock, BlockPos changedPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, changedPos, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof StellarNexusPartBE part) {
            BlockPos controllerPos = part.getControllerPos();
            if (controllerPos != null) {
                markControllerDirty(level, controllerPos);
            }
            EntropicMachineLocator.markNearbyDirty(level, pos);
        }
    }

    private static void markControllerDirty(Level level, BlockPos controllerPos) {
        BlockEntity entity = level.getBlockEntity(controllerPos);
        if (entity instanceof StellarNexusControllerBE controller) {
            controller.markStructureDirty();
        } else if (entity instanceof IMultiblockController controller) {
            controller.scanStructure(level);
        }
    }
}

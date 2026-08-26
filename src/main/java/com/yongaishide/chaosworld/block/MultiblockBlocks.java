package com.yongaishide.chaosworld.block;

import com.mojang.serialization.MapCodec;
import com.yongaishide.chaosworld.block.entity.AbstractSimpleMultiblockControllerBE;
import appeng.api.orientation.IOrientableBlock;
import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class MultiblockBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks("chaosworld_core");
    // --- Blocos Simples (n茫o direcionais) ---

    // Standalone entropy casings (the entropic convergence engine multiblock was removed)
    public static final DeferredBlock<Block> ENTROPY_SINGULARITY_CASING = registerBlock("entropy_singularity_casing",
            () -> new Block(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> QUANTUM_ENTROPY_CASING = registerBlock("quantum_entropy_casing",
            () -> new Block(BlockBehaviour.Properties.of().strength(10.0f).requiresCorrectToolForDrops()));

    // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?    //  QUANTUM MATTER FABRICATOR 鈥?Multiblock Components
    // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?
    public static final DeferredBlock<Block> QUANTUM_HYPER_MECHANICAL_CASING = registerBlock("quantum_hyper_mechanical_casing",
            () -> new Block(BlockBehaviour.Properties.of().strength(12.0f, 1200.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<ControllerBlock> QUANTUM_MATTER_FABRICATOR_CONTROLLER = BLOCKS.register("quantum_matter_fabricator_controller",
            () -> new ControllerBlock(BlockBehaviour.Properties.of()
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(ControllerBlock.ACTIVE) ? 14 : 0)));

    public static final DeferredBlock<QuantumSlicerControllerBlock> QUANTUM_SLICER_CONTROLLER = BLOCKS.register("quantum_slicer_controller",
            () -> new QuantumSlicerControllerBlock(BlockBehaviour.Properties.of()
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AbstractSimpleMultiblockControllerBlock.ACTIVE) ? 14 : 0)));

    public static final DeferredBlock<QuantumProcessorAssemblerControllerBlock> QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER = BLOCKS.register("quantum_processing_factory_controller",
            () -> new QuantumProcessorAssemblerControllerBlock(BlockBehaviour.Properties.of()
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AbstractSimpleMultiblockControllerBlock.ACTIVE) ? 14 : 0)));

    public static final DeferredBlock<QuantumCryoforgeControllerBlock> QUANTUM_CRYOFORGE_CONTROLLER = BLOCKS.register("quantum_cryoforge_controller",
            () -> new QuantumCryoforgeControllerBlock(BlockBehaviour.Properties.of()
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AbstractSimpleMultiblockControllerBlock.ACTIVE) ? 14 : 0)));

    public static final DeferredBlock<QuantumPatternHatchBlock> QUANTUM_PATTERN_HATCH = BLOCKS.register("quantum_pattern_hatch",
            QuantumPatternHatchBlock::new);

    // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?    //  STELLAR NEXUS 鈥?Multiblock Components
    // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?
    public static final DeferredBlock<StellarNexusControllerBlock> STELLAR_NEXUS_CONTROLLER = BLOCKS.register("stellar_nexus_controller",
            () -> new StellarNexusControllerBlock(BlockBehaviour.Properties.of()
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(StellarNexusControllerBlock.ASSEMBLED) ? 12 : 0)));

    public static final DeferredBlock<MassiveOutputHatchBlock> ME_MASSIVE_OUTPUT_HATCH = BLOCKS.register("me_massive_output_hatch",
            () -> new MassiveOutputHatchBlock(BlockBehaviour.Properties.of()
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<MassiveOutputHatchBlock> ME_MASSIVE_FLUID_HATCH = BLOCKS.register("me_massive_fluid_hatch",
            () -> new MassiveOutputHatchBlock(BlockBehaviour.Properties.of()
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<MassiveOutputHatchBlock> ME_MASSIVE_INPUT_HATCH = BLOCKS.register("me_massive_input_hatch",
            () -> new MassiveOutputHatchBlock(BlockBehaviour.Properties.of()
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<MassiveOutputHatchBlock> AE_ENERGY_INPUT_HATCH = BLOCKS.register("ae_energy_input_hatch",
            () -> new MassiveOutputHatchBlock(BlockBehaviour.Properties.of()
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()));




    public static final DeferredBlock<StellarNexusPartBlock> STELLAR_FIELD_GENERATOR_T1 = BLOCKS.register("stellar_field_generator_t1",
            () -> new StellarNexusPartBlock(BlockBehaviour.Properties.of()
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 4)));

    public static final DeferredBlock<StellarNexusPartBlock> STELLAR_FIELD_GENERATOR_T2 = BLOCKS.register("stellar_field_generator_t2",
            () -> new StellarNexusPartBlock(BlockBehaviour.Properties.of()
                    .strength(35.0f, 800.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 7)));

    public static final DeferredBlock<StellarNexusPartBlock> STELLAR_FIELD_GENERATOR_T3 = BLOCKS.register("stellar_field_generator_t3",
            () -> new StellarNexusPartBlock(BlockBehaviour.Properties.of()
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 10)));


    // --- M茅todos de Registro ---
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    // --- O m茅todo registerBlockItem foi REMOVIDO ---

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    // O resto da classe (OrientableMultiblock, ControllerBlock) permanece igual...
    public static class OrientableMultiblock extends DirectionalBlock {
        public static final MapCodec<OrientableMultiblock> CODEC = simpleCodec(OrientableMultiblock::new);

        public OrientableMultiblock(Properties properties) {
            super(properties);
            // --- CORRE脟脙O: Adiciona o registro do estado padr茫o ---
            this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        }

        @Override
        protected MapCodec<? extends DirectionalBlock> codec() {
            return CODEC;
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING);
        }
    }

    public static class ControllerBlock extends DirectionalBlock implements net.minecraft.world.level.block.EntityBlock, IOrientableBlock {
        public static final MapCodec<ControllerBlock> CODEC = simpleCodec(ControllerBlock::new);
        public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

        public ControllerBlock(Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
        }

        @Override
        protected MapCodec<? extends DirectionalBlock> codec() {
            return CODEC;
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING, ACTIVE);
        }

        @Override
        public IOrientationStrategy getOrientationStrategy() {
            return OrientationStrategies.facing();
        }

        @Override
        protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
            if (stack.is(Tags.Items.TOOLS_WRENCH) && player.isShiftKeyDown()) {
                if (!level.isClientSide()) {
                    level.destroyBlock(pos, true, player);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        @Override
        protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hitResult) {
            if (player.isShiftKeyDown()) {
                if (level.isClientSide) {
                    com.yongaishide.chaosworld.client.GhostHologramRenderer.toggleHologram(pos, state.getValue(FACING));
                }
                return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!level.isClientSide) {
                net.minecraft.world.level.block.entity.BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof com.yongaishide.chaosworld.block.entity.QmfControllerBE controller) {
                    player.openMenu(controller, pos);
                }
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(net.minecraft.core.BlockPos pos, BlockState state) {
            return new com.yongaishide.chaosworld.block.entity.QmfControllerBE(pos, state);
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
            if (level.isClientSide()) return null;
            return type == com.yongaishide.chaosworld.init.ModBlockEntities.QMF_CONTROLLER.get()
                    ? (lvl, pos, st, be) -> ((com.yongaishide.chaosworld.block.entity.QmfControllerBE) be).serverTick()
                    : null;
        }

        @Override
        public void neighborChanged(BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, Block changedBlock, net.minecraft.core.BlockPos changedPos, boolean isMoving) {
            super.neighborChanged(state, level, pos, changedBlock, changedPos, isMoving);
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof com.yongaishide.chaosworld.block.entity.QmfControllerBE be) {
                be.markStructureDirty();
            }
        }

        @Override
        public void onRemove(BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, BlockState newState, boolean moved) {
            if (!state.is(newState.getBlock())) {
                if (level.getBlockEntity(pos) instanceof com.yongaishide.chaosworld.block.entity.QmfControllerBE be) {
                    be.onControllerBroken();
                }
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }
}

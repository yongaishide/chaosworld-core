package com.yongaishide.chaosworld.item;

import appeng.api.AECapabilities;
import appeng.api.config.Actionable;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import com.yongaishide.chaosworld.api.multiblock.IMultiblockController;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinition;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinitions;
import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import com.yongaishide.chaosworld.util.StructureTerminalSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class StructureScannerItem extends Item {

    public StructureScannerItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            // Shift + right-click does not open the settings GUI.
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        if (level.isClientSide) {
            ClientProxy.openSettings(player.getItemInHand(hand));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IMultiblockController controller) {
            return handleController(level, player, stack, controller, pos);
        }

        // Non-controller block: only bind when the clicked block is an AE grid node host
        var gridHost = level.getCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST, pos, null);
        boolean isWap = level.getBlockEntity(pos) instanceof IWirelessAccessPoint;
        if (gridHost == null || (!isWap && !hasExposedGridNode(gridHost))) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            StructureTerminalSettings.setBoundPos(stack, new GlobalPos(level.dimension(), pos));
            if (isWap) {
                player.displayClientMessage(Component.translatable("message.ufo.terminal.wap_bound", pos.toShortString()), true);
            } else {
                player.displayClientMessage(Component.translatable("message.ufo.terminal.bound", pos.toShortString()), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private InteractionResult handleController(Level level, Player player, ItemStack stack,
                                               IMultiblockController controller, BlockPos pos) {
        Optional<MultiblockControllerDefinition> definitionOpt = MultiblockControllerDefinitions.getDefinition((BlockEntity) controller);
        if (definitionOpt.isEmpty()) {
            return InteractionResult.PASS;
        }

        MultiblockControllerDefinition definition = definitionOpt.get();
        BlockState state = level.getBlockState(pos);
        Direction facing = MultiblockControllerDefinitions.getPatternFacing((BlockEntity) controller, state);
        boolean flipped = StructureTerminalSettings.getFlipped(stack);

        if (StructureTerminalSettings.getDismantleMode(stack)) {
            if (!level.isClientSide) {
                dismantleStructure((ServerLevel) level, player, definition.pattern(), pos, facing, flipped);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) {
            MultiblockPattern.MatchResult result = definition.pattern().match(level, pos, facing, flipped);
            if (!result.isValid()) {
                List<MultiblockPattern.PatternError> errors = result.allErrors();
                if (errors != null) {
                    int maxHighlight = Math.min(errors.size(), 50);
                    for (int i = 0; i < maxHighlight; i++) {
                        ClientProxy.highlight(errors.get(i).pos(), 15000);
                    }
                }
            }
            return InteractionResult.sidedSuccess(true);
        }

        return autoBuild((ServerLevel) level, (ServerPlayer) player, controller, definition, stack, pos, facing, flipped);
    }

    private InteractionResult autoBuild(ServerLevel level, ServerPlayer player, IMultiblockController controller,
                                        MultiblockControllerDefinition definition, ItemStack stack,
                                        BlockPos pos, Direction facing, boolean flipped) {
        MultiblockPattern pattern = definition.pattern();

        boolean aeMode = StructureTerminalSettings.getAeMode(stack);
        MEStorage aeStorage = null;
        if (aeMode) {
            GlobalPos bound = StructureTerminalSettings.getBoundPos(stack);
            aeStorage = findMeStorage(level, bound);
            if (aeStorage == null) {
                net.minecraft.network.chat.MutableComponent msg = isBoundToWap(level, bound)
                        ? Component.translatable("message.ufo.terminal.wap_offline")
                        : Component.translatable("message.ufo.terminal.ae_unbound");
                player.displayClientMessage(msg.withStyle(ChatFormatting.YELLOW), true);
            }
        }
        final MEStorage storage = aeStorage;

        int repeat = StructureTerminalSettings.getRepeatCount(stack);
        boolean replace = StructureTerminalSettings.getReplaceMode(stack);
        boolean creative = player.getAbilities().instabuild;

        java.util.function.Function<java.util.List<Block>, Block> provider = creative
                ? candidates -> candidates.isEmpty() ? null : candidates.get(0)
                : candidates -> consumeBest(player, storage, candidates);
        java.util.function.Consumer<Block> ret = creative
                ? block -> {
                }
                : block -> returnBlock(player, block);

        int totalPlaced = 0;
        int totalMissing = 0;
        for (int r = 0; r < repeat; r++) {
            MultiblockPattern.AssembleResult result = pattern.assembleWithProvider(
                    level, pos, facing, definition.defaultCreativeStates(),
                    provider, ret,
                    flipped, replace);
            totalPlaced += result.placed();
            totalMissing += result.missing();
            if (result.placed() == 0) {
                break;
            }
        }

        // Normalize field generator slots to the selected tier
        Block fieldBlock = fieldBlockForTier(StructureTerminalSettings.getFieldTier(stack));
        java.util.Set<Character> fieldChars = new java.util.HashSet<>();
        for (char[][] layer : pattern.getPattern()) {
            for (char[] row : layer) {
                for (char c : row) {
                    if (fieldChars.contains(c)) {
                        continue;
                    }
                    for (BlockState candidate : pattern.getDisplayCandidates(c)) {
                        if (candidate != null && isFieldGenerator(candidate.getBlock())) {
                            fieldChars.add(c);
                            break;
                        }
                    }
                }
            }
        }
        for (char c : fieldChars) {
            for (BlockPos fieldPos : pattern.getExpectedPositions(pos, facing, c, flipped)) {
                if (!level.isLoaded(fieldPos)) {
                    continue;
                }
                Block current = level.getBlockState(fieldPos).getBlock();
                if (current == fieldBlock) {
                    continue;
                }
                Block consumed = creative ? fieldBlock : consumeBest(player, storage, List.of(fieldBlock));
                if (consumed != null) {
                    level.setBlock(fieldPos, fieldBlock.defaultBlockState(), Block.UPDATE_CLIENTS);
                    totalPlaced++;
                    if (!creative && isFieldGenerator(current)) {
                        returnBlock(player, current);
                    }
                } else {
                    totalMissing++;
                }
            }
        }

        controller.scanStructure(level);

        if (totalPlaced > 0) {
            player.displayClientMessage(definition.name().copy()
                    .append(Component.translatable("message.ufo.auto_assemble.complete", totalPlaced)
                            .withStyle(ChatFormatting.GREEN)), true);
            if (totalMissing > 0) {
                player.sendSystemMessage(Component.translatable("message.ufo.auto_assemble.missing", totalMissing)
                        .withStyle(ChatFormatting.RED));
            }
        } else {
            player.sendSystemMessage(Component.translatable("message.ufo.auto_assemble.missing", totalMissing)
                    .withStyle(ChatFormatting.RED));
        }
        return InteractionResult.sidedSuccess(true);
    }

    private static void dismantleStructure(ServerLevel level, Player player, MultiblockPattern pattern,
                                           BlockPos pos, Direction facing, boolean flipped) {
        MultiblockPattern.MatchResult result = pattern.match(level, pos, facing, flipped);
        if (!result.isValid()) {
            player.displayClientMessage(Component.translatable("message.ufo.terminal.not_formed")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        int count = 0;
        for (BlockPos partPos : result.partPositions()) {
            BlockState partState = level.getBlockState(partPos);
            BlockEntity partBe = level.getBlockEntity(partPos);
            List<ItemStack> drops = Block.getDrops(partState, level, partPos, partBe, player, ItemStack.EMPTY);
            for (ItemStack drop : drops) {
                if (!player.getInventory().add(drop)) {
                    player.drop(drop, false);
                }
            }
            level.setBlock(partPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            count++;
        }

        BlockEntity controllerBe = level.getBlockEntity(pos);
        if (controllerBe instanceof IMultiblockController controller) {
            controller.scanStructure(level);
        }

        player.displayClientMessage(Component.translatable("message.ufo.terminal.dismantled", count)
                .withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    private static Block consumeBest(Player player, @Nullable MEStorage aeStorage, List<Block> candidates) {
        Inventory inventory = player.getInventory();
        for (int i = candidates.size() - 1; i >= 0; i--) {
            Block block = candidates.get(i);
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                ItemStack stack = inventory.getItem(slot);
                if (stack.is(block.asItem())) {
                    stack.shrink(1);
                    return block;
                }
            }
        }

        if (aeStorage != null && player instanceof ServerPlayer serverPlayer) {
            IActionSource source = IActionSource.ofPlayer(serverPlayer);
            for (int i = candidates.size() - 1; i >= 0; i--) {
                Block block = candidates.get(i);
                AEItemKey key = AEItemKey.of(block.asItem().getDefaultInstance());
                if (key != null) {
                    long extracted = aeStorage.extract(key, 1, Actionable.MODULATE, source);
                    if (extracted > 0) {
                        return block;
                    }
                }
            }
        }
        return null;
    }

    private static void returnBlock(Player player, Block block) {
        ItemStack stack = new ItemStack(block.asItem(), 1);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static Block fieldBlockForTier(int tier) {
        return switch (tier) {
            case 2 -> com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get();
            case 3 -> com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get();
            default -> com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get();
        };
    }

    private static boolean isFieldGenerator(Block block) {
        return block == com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get()
                || block == com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get()
                || block == com.yongaishide.chaosworld.block.MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get();
    }

    @Nullable
    private static MEStorage findMeStorage(Level level, @Nullable GlobalPos bound) {
        if (bound == null || bound.dimension() != level.dimension() || !level.isLoaded(bound.pos())) {
            return null;
        }
        if (level.getBlockEntity(bound.pos()) instanceof IWirelessAccessPoint wap) {
            IGrid grid = wap.getGrid();
            if (grid == null || !wap.isActive()) {
                return null;
            }
            return grid.getStorageService().getInventory();
        }

        var host = level.getCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST, bound.pos(), null);
        if (host == null) {
            return null;
        }
        IGridNode node = null;
        for (Direction dir : Direction.values()) {
            node = host.getGridNode(dir);
            if (node != null) {
                break;
            }
        }
        if (node == null || node.getGrid() == null) {
            return null;
        }
        return node.getGrid().getStorageService().getInventory();
    }

    private static boolean hasExposedGridNode(IInWorldGridNodeHost host) {
        for (Direction dir : Direction.values()) {
            if (host.getGridNode(dir) != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBoundToWap(Level level, @Nullable GlobalPos bound) {
        return bound != null && bound.dimension() == level.dimension()
                && level.isLoaded(bound.pos())
                && level.getBlockEntity(bound.pos()) instanceof IWirelessAccessPoint;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.chaosworld_core.structure_scanner.tooltip.0").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("item.chaosworld_core.structure_scanner.tooltip.1").withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("item.chaosworld_core.structure_scanner.tooltip.2").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(Component.translatable("tooltip.ufo.structure_scanner.creative_tooltip").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    private static final class ClientProxy {
        private static void openSettings(ItemStack stack) {
            net.minecraft.client.Minecraft.getInstance().setScreen(new com.yongaishide.chaosworld.client.StructureTerminalScreen(stack));
        }

        private static void highlight(BlockPos pos, long duration) {
            com.yongaishide.chaosworld.client.render.StructureHighlightRenderer.highlight(pos, duration);
        }
    }
}

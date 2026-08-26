package com.yongaishide.chaosworld.network.packet;

import com.yongaishide.chaosworld.api.multiblock.IMultiblockController;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinition;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinitions;
import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketAutoAssembleStructure(BlockPos pos) implements CustomPacketPayload {

    public static final Type<PacketAutoAssembleStructure> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "auto_assemble_structure"));

    public static final StreamCodec<FriendlyByteBuf, PacketAutoAssembleStructure> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PacketAutoAssembleStructure::pos,
            PacketAutoAssembleStructure::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            var level = player.level();
            if (!level.isLoaded(pos)) {
                return;
            }
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity == null) {
                return;
            }
            var definitionOpt = MultiblockControllerDefinitions.getDefinition(blockEntity);
            if (definitionOpt.isEmpty()) {
                return;
            }

            MultiblockControllerDefinition definition = definitionOpt.get();
            MultiblockPattern pattern = definition.pattern();
            var facing = MultiblockControllerDefinitions.getPatternFacing(blockEntity, level.getBlockState(pos));

            MultiblockPattern.AssembleResult result = pattern.assembleWithProvider(
                    level, pos, facing, definition.defaultCreativeStates(),
                    candidates -> consumeBest(player, candidates),
                    block -> returnBlock(player, block));

            if (blockEntity instanceof IMultiblockController controller) {
                controller.scanStructure(level);
            }

            if (result.missing() == 0) {
                player.displayClientMessage(definition.name().copy()
                        .append(Component.translatable("message.ufo.auto_assemble.complete", result.placed())
                                .withStyle(ChatFormatting.GREEN)), true);
            } else {
                player.displayClientMessage(Component.translatable("message.ufo.auto_assemble.missing", result.missing())
                        .withStyle(ChatFormatting.RED), true);
            }
        });
    }

    private static Block consumeBest(ServerPlayer player, java.util.List<Block> candidates) {
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
        return null;
    }

    private static void returnBlock(ServerPlayer player, Block block) {
        ItemStack stack = new ItemStack(block.asItem(), 1);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}

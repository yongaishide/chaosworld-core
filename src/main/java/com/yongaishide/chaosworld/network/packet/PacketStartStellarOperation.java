package com.yongaishide.chaosworld.network.packet;

import com.yongaishide.chaosworld.block.entity.StellarNexusControllerBE;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record PacketStartStellarOperation(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketStartStellarOperation> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "start_stellar_operation"));

    public static final StreamCodec<FriendlyByteBuf, PacketStartStellarOperation> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketStartStellarOperation::pos,
            PacketStartStellarOperation::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level().isLoaded(pos)) {
                if (player.level().getBlockEntity(pos) instanceof StellarNexusControllerBE controller) {
                    List<Component> errors = controller.startOperation();
                    if (!errors.isEmpty()) {
                        // Send error messages to the player's chat
                        player.displayClientMessage(
                                Component.literal("§c§l[STELLAR NEXUS] §eCannot start simulation:"),
                                false);
                        for (Component error : errors) {
                            player.displayClientMessage(Component.literal("  ").append(error), false);
                        }
                    } else {
                        player.displayClientMessage(
                                Component.literal("§a§l[STELLAR NEXUS] §fSimulation started successfully!"),
                                true);
                    }
                }
            }
        });
    }
}

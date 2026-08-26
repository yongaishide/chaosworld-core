package com.yongaishide.chaosworld.network.packet;

import com.yongaishide.chaosworld.block.entity.StellarNexusControllerBE;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent from the client when the player clicks the "Scan Structure" button
 * in the Stellar Nexus Controller screen. Forces an immediate re-scan
 * on the server side.
 */
public record PacketScanStellarStructure(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketScanStellarStructure> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "scan_stellar_structure"));

    public static final StreamCodec<FriendlyByteBuf, PacketScanStellarStructure> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketScanStellarStructure::pos,
            PacketScanStellarStructure::new
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
                    controller.scanStructure(player.level(), player);
                }
            }
        });
    }
}

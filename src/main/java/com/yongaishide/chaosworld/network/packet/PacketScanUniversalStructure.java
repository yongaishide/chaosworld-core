package com.yongaishide.chaosworld.network.packet;

import com.yongaishide.chaosworld.api.multiblock.IMultiblockController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketScanUniversalStructure(BlockPos pos) implements CustomPacketPayload {

    public static final Type<PacketScanUniversalStructure> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "scan_universal_structure"));

    public static final StreamCodec<FriendlyByteBuf, PacketScanUniversalStructure> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PacketScanUniversalStructure::pos,
            PacketScanUniversalStructure::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level().isLoaded(pos)
                    && player.level().getBlockEntity(pos) instanceof IMultiblockController controller) {
                controller.scanStructure(player.level());
            }
        });
    }
}

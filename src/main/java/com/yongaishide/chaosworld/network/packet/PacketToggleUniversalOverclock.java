package com.yongaishide.chaosworld.network.packet;

import com.yongaishide.chaosworld.block.entity.IUniversalMultiblockController;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketToggleUniversalOverclock(BlockPos pos) implements CustomPacketPayload {
    public static final Type<PacketToggleUniversalOverclock> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "toggle_universal_overclock"));

    public static final StreamCodec<ByteBuf, PacketToggleUniversalOverclock> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketToggleUniversalOverclock::pos,
            PacketToggleUniversalOverclock::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final PacketToggleUniversalOverclock packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(packet.pos()) instanceof IUniversalMultiblockController controller) {
                controller.toggleOverclock();
            }
        });
    }
}

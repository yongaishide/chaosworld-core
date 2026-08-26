package com.yongaishide.chaosworld.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CycleModeKeyPacket() implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "cycle_mode_key");

    public static final StreamCodec<FriendlyByteBuf, CycleModeKeyPacket> STREAM_CODEC =
            CustomPacketPayload.codec(CycleModeKeyPacket::write, CycleModeKeyPacket::new);

    public CycleModeKeyPacket(final FriendlyByteBuf buffer) {
        this();
    }

    public void write(final FriendlyByteBuf buffer) {
        // Nada a escrever para este pacote
    }

    public static final Type<CycleModeKeyPacket> TYPE = new Type<>(ID);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
package com.yongaishide.chaosworld.network.packet;

import com.yongaishide.chaosworld.util.StructureTerminalSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TerminalSettingsPacket(int repeat, boolean replace, boolean flip, boolean dismantle, boolean ae,
                                     int tier)
        implements CustomPacketPayload {

    public static final Type<TerminalSettingsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "terminal_settings"));

    public static final StreamCodec<FriendlyByteBuf, TerminalSettingsPacket> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeInt(p.repeat);
                buf.writeBoolean(p.replace);
                buf.writeBoolean(p.flip);
                buf.writeBoolean(p.dismantle);
                buf.writeBoolean(p.ae);
                buf.writeInt(p.tier);
            },
            buf -> new TerminalSettingsPacket(
                    buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(),
                    buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                var stack = player.getMainHandItem();
                StructureTerminalSettings.setRepeatCount(stack, Math.max(1, Math.min(64, this.repeat())));
                StructureTerminalSettings.setReplaceMode(stack, this.replace());
                StructureTerminalSettings.setFlipped(stack, this.flip());
                StructureTerminalSettings.setDismantleMode(stack, this.dismantle());
                StructureTerminalSettings.setAeMode(stack, this.ae());
                StructureTerminalSettings.setFieldTier(stack, this.tier());
            }
        });
    }
}

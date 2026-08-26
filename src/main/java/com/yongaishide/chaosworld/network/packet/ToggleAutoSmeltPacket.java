package com.yongaishide.chaosworld.network.packet;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.item.custom.HammerItem;
import com.yongaishide.chaosworld.item.custom.UfoEnergyPickaxeItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleAutoSmeltPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleAutoSmeltPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "toggle_auto_smelt"));

    // Codec vazio pois n茫o precisamos enviar dados extras, apenas o "sinal"
    public static final StreamCodec<ByteBuf, ToggleAutoSmeltPacket> STREAM_CODEC =
            StreamCodec.unit(new ToggleAutoSmeltPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ToggleAutoSmeltPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack stack = player.getMainHandItem();

                // Verifica se 茅 uma Picareta UFO ou um Martelo UFO
                if (stack.getItem() instanceof UfoEnergyPickaxeItem || stack.getItem() instanceof HammerItem) {
                    boolean currentStatus = stack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false);
                    boolean newStatus = !currentStatus;

                    // Salva o novo estado
                    stack.set(ModDataComponents.AUTO_SMELT.get(), newStatus);

                    // Envia mensagem para o jogador
                    ChatFormatting color = newStatus ? ChatFormatting.GREEN : ChatFormatting.RED;
                    Component message = newStatus ? Component.translatable("message.ufo.auto_smelt_on") : Component.translatable("message.ufo.auto_smelt_off");

                    player.sendSystemMessage(message.copy().withStyle(color));
                }
            }
        });
    }
}
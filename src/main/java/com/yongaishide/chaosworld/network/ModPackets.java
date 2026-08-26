package com.yongaishide.chaosworld.network;

import com.yongaishide.chaosworld.item.custom.IHasCycleableModes;
import com.yongaishide.chaosworld.item.custom.IEnergyTool;
import com.yongaishide.chaosworld.network.packet.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPackets {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("chaosworld_core").versioned("1.0");

        registrar.playToServer(CycleToolKeyPacket.TYPE, CycleToolKeyPacket.STREAM_CODEC,
                ModPackets::handleCycleToolKey);
        registrar.playToServer(CycleModeKeyPacket.TYPE, CycleModeKeyPacket.STREAM_CODEC,
                ModPackets::handleCycleModeKey);
        registrar.playToServer(
                ToggleAutoSmeltPacket.TYPE,
                ToggleAutoSmeltPacket.STREAM_CODEC,
                ToggleAutoSmeltPacket::handle
        );
        registrar.playToServer(
                PacketChangeSideConfig.TYPE,
                PacketChangeSideConfig.STREAM_CODEC,
                PacketChangeSideConfig::handle
        );
        registrar.playToServer(
                PacketChangeStellarRecipe.TYPE,
                PacketChangeStellarRecipe.STREAM_CODEC,
                PacketChangeStellarRecipe::handle
        );
        registrar.playToServer(
                PacketStartStellarOperation.TYPE,
                PacketStartStellarOperation.STREAM_CODEC,
                PacketStartStellarOperation::handle
        );
        registrar.playToServer(
                PacketToggleStellarSafeMode.TYPE,
                PacketToggleStellarSafeMode.STREAM_CODEC,
                PacketToggleStellarSafeMode::handle
        );
        registrar.playToServer(
                PacketScanStellarStructure.TYPE,
                PacketScanStellarStructure.STREAM_CODEC,
                PacketScanStellarStructure::handle
        );
        registrar.playToServer(
                PacketToggleStellarAutoStart.TYPE,
                PacketToggleStellarAutoStart.STREAM_CODEC,
                PacketToggleStellarAutoStart::handle
        );
        registrar.playToServer(
                PacketToggleStellarLock.TYPE,
                PacketToggleStellarLock.STREAM_CODEC,
                PacketToggleStellarLock::handle
        );
        registrar.playToServer(
                PacketToggleStellarOverclock.TYPE,
                PacketToggleStellarOverclock.STREAM_CODEC,
                PacketToggleStellarOverclock::handle
        );
        registrar.playToServer(
                PacketToggleUniversalSafeMode.TYPE,
                PacketToggleUniversalSafeMode.STREAM_CODEC,
                PacketToggleUniversalSafeMode::handle
        );
        registrar.playToServer(
                PacketToggleUniversalOverclock.TYPE,
                PacketToggleUniversalOverclock.STREAM_CODEC,
                PacketToggleUniversalOverclock::handle
        );
        registrar.playToServer(
                PacketScanUniversalStructure.TYPE,
                PacketScanUniversalStructure.STREAM_CODEC,
                PacketScanUniversalStructure::handle
        );
        registrar.playToServer(
                PacketAutoAssembleStructure.TYPE,
                PacketAutoAssembleStructure.STREAM_CODEC,
                PacketAutoAssembleStructure::handle
        );
        registrar.playToServer(
                TerminalSettingsPacket.TYPE,
                TerminalSettingsPacket.STREAM_CODEC,
                TerminalSettingsPacket::handle
        );
    }

    private static void handleCycleToolKey(final CycleToolKeyPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player.getMainHandItem().getItem() instanceof IEnergyTool tool) {
                tool.transformTool(player.level(), player, InteractionHand.MAIN_HAND, packet.forward());
            }
        });
    }

    private static void handleCycleModeKey(final CycleModeKeyPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IHasCycleableModes tool) {
                tool.cycleMode(stack, player);
            }
        });
    }

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }
}

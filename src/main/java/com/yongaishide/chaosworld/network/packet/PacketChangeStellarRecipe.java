package com.yongaishide.chaosworld.network.packet;

import com.yongaishide.chaosworld.block.entity.StellarNexusControllerBE;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketChangeStellarRecipe(BlockPos pos, ResourceLocation recipeId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketChangeStellarRecipe> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "change_stellar_recipe"));

    public static final StreamCodec<FriendlyByteBuf, PacketChangeStellarRecipe> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketChangeStellarRecipe::pos,
            ResourceLocation.STREAM_CODEC, PacketChangeStellarRecipe::recipeId,
            PacketChangeStellarRecipe::new
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
                    controller.setActiveRecipe(recipeId);
                }
            }
        });
    }
}

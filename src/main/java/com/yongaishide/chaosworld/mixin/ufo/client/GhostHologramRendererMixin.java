package com.yongaishide.chaosworld.mixin.ufo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.raishxn.ufo.client.GhostHologramRenderer;
import com.yongaishide.chaosworld.patch.contents.TintedVertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Patch layer: ghost hologram blocks are drawn with a stronger translucent tint.
 *
 * <p>UFO 3.x no longer draws the hologram through a {@code MultiBufferSource}: it bakes the
 * ghost blocks once into a cached {@link com.mojang.blaze3d.vertex.VertexBuffer} inside
 * {@code rebuildMesh(Minecraft, Vec3, long)} and only re-submits that buffer every frame. The
 * tint is therefore applied to the vertex sink that
 * {@link BlockRenderDispatcher#renderBatched} writes the ghost blocks into.</p>
 */
@Mixin(value = GhostHologramRenderer.class, remap = false)
public abstract class GhostHologramRendererMixin {

    @Redirect(method = "rebuildMesh", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;)V"))
    private static void chaosworld$tintHologram(BlockRenderDispatcher dispatcher, BlockState state, BlockPos pos,
            BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean checkSides,
            RandomSource random) {
        dispatcher.renderBatched(state, pos, level, poseStack, new TintedVertexConsumer(consumer, 90), checkSides,
                random);
    }
}

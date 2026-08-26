package com.yongaishide.chaosworld.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinition;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinitions;
import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT)
public class GhostHologramRenderer {

    private static BlockPos activeControllerPos;
    private static net.minecraft.core.Direction activeFacing;
    private static final List<HologramBlock> cachedBlocks = new ArrayList<>();

    private record HologramBlock(BlockPos pos, BlockState state) {
    }

    public static void toggleHologram(BlockPos pos, net.minecraft.core.Direction facing) {
        if (activeControllerPos != null && activeControllerPos.equals(pos)) {
            activeControllerPos = null;
            activeFacing = null;
            cachedBlocks.clear();
            return;
        }

        activeControllerPos = pos;
        activeFacing = getPatternFacing(pos, facing);
        rebuildCache();
    }

    private static net.minecraft.core.Direction getPatternFacing(BlockPos pos, net.minecraft.core.Direction fallbackFacing) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return fallbackFacing;
        }

        BlockEntity be = mc.level.getBlockEntity(pos);
        BlockState state = mc.level.getBlockState(pos);
        return MultiblockControllerDefinitions.getPatternFacing(be, state);
    }

    public static boolean isActive(BlockPos pos) {
        return activeControllerPos != null && activeControllerPos.equals(pos);
    }

    private static void rebuildCache() {
        cachedBlocks.clear();
        if (activeControllerPos == null || activeFacing == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        Optional<MultiblockControllerDefinition> definitionOpt = getDefinition(mc.level.getBlockEntity(activeControllerPos));
        if (definitionOpt.isEmpty()) {
            return;
        }

        MultiblockControllerDefinition definition = definitionOpt.get();
        MultiblockPattern pattern = definition.pattern();
        Map<Character, BlockState> defaults = definition.defaultCreativeStates();

        char[][][] charPattern = pattern.getPattern();
        int controllerCol = pattern.getControllerCol();
        int controllerRow = pattern.getControllerRow();
        int controllerLayer = pattern.getControllerLayer();

        for (int y = 0; y < charPattern.length; y++) {
            for (int z = 0; z < charPattern[y].length; z++) {
                for (int x = 0; x < charPattern[y][z].length; x++) {
                    char c = charPattern[y][z][x];
                    if (c == ' ' || c == 'A' || c == pattern.getControllerChar()) {
                        continue;
                    }

                    BlockState targetState = defaults.get(c);
                    if (targetState == null) {
                        continue;
                    }

                    int offsetX = x - controllerCol;
                    int offsetY = y - controllerLayer;
                    int offsetZ = z - controllerRow;

                    BlockPos worldPos = getRotatedPos(activeControllerPos, offsetX, offsetY, offsetZ, activeFacing);
                    cachedBlocks.add(new HologramBlock(worldPos, targetState));
                }
            }
        }
    }

    private static Optional<MultiblockControllerDefinition> getDefinition(BlockEntity be) {
        return be == null ? Optional.empty() : MultiblockControllerDefinitions.getDefinition(be);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        if (activeControllerPos == null || activeFacing == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        if (!MultiblockControllerDefinitions.isSupportedController(mc.level.getBlockState(activeControllerPos))) {
            activeControllerPos = null;
            activeFacing = null;
            cachedBlocks.clear();
            return;
        }

        if (mc.player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(activeControllerPos)) > 64 * 64) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        net.minecraft.world.phys.Vec3 cameraPos = event.getCamera().getPosition();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        var vertexConsumer = new TintedVertexConsumer(bufferSource.getBuffer(RenderType.translucent()), 90);

        for (HologramBlock hb : cachedBlocks) {
            BlockState existingState = mc.level.getBlockState(hb.pos());
            if (!existingState.isAir() && existingState.getFluidState().isEmpty()) {
                continue;
            }

            poseStack.pushPose();
            poseStack.translate(hb.pos().getX(), hb.pos().getY(), hb.pos().getZ());
            mc.getBlockRenderer().renderBatched(
                    hb.state(),
                    hb.pos(),
                    mc.level,
                    poseStack,
                    vertexConsumer,
                    true,
                    mc.level.getRandom());
            poseStack.popPose();
        }

        bufferSource.endBatch();
        poseStack.popPose();
    }

    private static final class TintedVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final int alpha;

        private TintedVertexConsumer(VertexConsumer delegate, int alpha) {
            this.delegate = delegate;
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            return delegate.addVertex(x, y, z);
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int a) {
            return delegate.setColor(red, green, blue, a * alpha / 255);
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            return delegate.setUv(u, v);
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            return delegate.setUv1(u, v);
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            return delegate.setUv2(u, v);
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            return delegate.setNormal(x, y, z);
        }
    }

    private static BlockPos getRotatedPos(BlockPos center, int localX, int localY, int localZ, net.minecraft.core.Direction facing) {
        return switch (facing) {
            case SOUTH -> center.offset(-localX, localY, -localZ);
            case WEST -> center.offset(localZ, localY, -localX);
            case EAST -> center.offset(-localZ, localY, localX);
            case NORTH, UP, DOWN -> center.offset(localX, localY, localZ);
        };
    }
}

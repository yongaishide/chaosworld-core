package com.yongaishide.chaosworld.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = "chaosworld_core", value = Dist.CLIENT)
public class StructureHighlightRenderer {

    private static final Map<BlockPos, Long> HIGHLIGHTS = new ConcurrentHashMap<>();

    public static void highlight(BlockPos pos, long durationMs) {
        HIGHLIGHTS.put(pos, System.currentTimeMillis() + durationMs);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        
        long now = System.currentTimeMillis();
        if (HIGHLIGHTS.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        // 1. Prepare to draw
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());

        VertexConsumer vertexConsumer = mc.renderBuffers().bufferSource().getBuffer(RenderType.lines());

        HIGHLIGHTS.entrySet().removeIf(entry -> entry.getValue() <= now);

        for (Map.Entry<BlockPos, Long> entry : HIGHLIGHTS.entrySet()) {
            BlockPos pos = entry.getKey();
            
            // Blink effect: true for 500ms, false for 500ms
            boolean blink = (now / 500L % 2L) == 0;
            if (!blink) continue;

            AABB box = new AABB(pos).inflate(0.02D);
            
            // Draw red bounding box
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, box, 1.0F, 0.0F, 0.0F, 1.0F);
        }

        poseStack.popPose();
    }
}

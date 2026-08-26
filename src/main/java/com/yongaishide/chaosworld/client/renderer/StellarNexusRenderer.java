package com.yongaishide.chaosworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yongaishide.chaosworld.block.entity.StellarNexusControllerBE;
import com.yongaishide.chaosworld.client.render.StellarModelRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

import java.util.List;

/**
 * Custom renderer for the Stellar Nexus multiblock.
 * Renders a complete scene: outer space shell + central star + orbiting dimension objects.
 * Based directly on GTO Core's EyeOfHarmonyRenderer.
 */
public class StellarNexusRenderer implements BlockEntityRenderer<StellarNexusControllerBE> {

    private static final List<ResourceLocation> ORBIT_OBJECTS = List.of(
            StellarModelRegistry.THE_NETHER,
            StellarModelRegistry.OVERWORLD,
            StellarModelRegistry.THE_END
    );

    public StellarNexusRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(StellarNexusControllerBE blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!blockEntity.isAssembled()) return;
        if (blockEntity.getLevel() == null) return;

        boolean isActive = blockEntity.isActive();

        // Determine which star model to use based on active recipe
        ResourceLocation starModelLoc = StellarModelRegistry.STAR;
        if (isActive) {
            String simulationKey = "Unknown";
            var recipeId = blockEntity.getActiveRecipeId();
            if (recipeId != null) {
                var recipe = blockEntity.getLevel().getRecipeManager().byKey(recipeId);
                if (recipe.isPresent() && recipe.get().value() instanceof com.yongaishide.chaosworld.recipe.StellarSimulationRecipe stellarRecipe) {
                    simulationKey = stellarRecipe.getSimulationTranslationKey();
                    if (simulationKey.isEmpty()) simulationKey = stellarRecipe.getSimulationName();
                }
            }
            starModelLoc = StellarModelRegistry.getModelForSimulation(simulationKey);
        }

        // Calculate animation tick
        float tick = blockEntity.getLevel().getGameTime() + partialTick;

        // Calculate center offset based on controller facing direction
        // Assume controller is horizontally centered relative to the shape
        Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.FACING);
        double x = 0.5, y = 0.5, z = 0.5; 
        switch (facing) {
            case NORTH -> z += 16;
            case SOUTH -> z -= 16;
            case WEST  -> x += 16;
            case EAST  -> x -= 16;
        }

        poseStack.pushPose();
        poseStack.translate(x, y, z);

        // 1. Render outer space shell (largest layer)
        renderOuterSpaceShell(tick, poseStack, bufferSource);

        // 2. Render the central star (SUN — large and dominant)
        renderStar(tick, starModelLoc, poseStack, bufferSource, isActive);

        // 3. Render orbiting dimension objects (smaller, orbiting)
        if (isActive) {
            renderOrbitObjects(tick, poseStack, bufferSource);
        }

        poseStack.popPose();
    }

    /**
     * Renders the central star (SUN) — the dominant object.
     */
    private static void renderStar(float tick, ResourceLocation modelLoc, PoseStack poseStack,
                                    MultiBufferSource buffer, boolean isActive) {
        BakedModel model = StellarModelRegistry.getBakedModel(modelLoc);
        if (model == null) return;

        poseStack.pushPose();

        // Star scale: larger when active
        float scale = isActive ? 0.04F : 0.02F;
        poseStack.scale(scale, scale, scale);

        // Rotation: GTO Core pattern
        poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(0.0F, 1.0F, 1.0F, (tick / 2) % 360.0F));

        // Pulse effect when active
        if (isActive) {
            float pulse = 1.0F + 0.05F * (float) Math.sin(tick * 0.15);
            poseStack.scale(pulse, pulse, pulse);
        }

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                buffer.getBuffer(RenderType.translucent()),
                null,
                model,
                1.0F, 1.0F, 1.0F,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.translucent()
        );

        poseStack.popPose();
    }

    /**
     * Renders orbiting dimension objects — smaller than the star.
     * 3 objects (Nether, Overworld, End) orbit at different speeds and distances.
     */
    private static void renderOrbitObjects(float tick, PoseStack poseStack, MultiBufferSource buffer) {
        for (int a = 1; a < 4; a++) {
            BakedModel model = StellarModelRegistry.getBakedModel(ORBIT_OBJECTS.get(a - 1));
            if (model == null) continue;

            // Planets are SMALLER than the star (0.01 to 0.018)
            float scale = 0.008F + 0.004F * a;

            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);

            // Tilt + rotate
            poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(1.0F, 0.0F, 1.0F, (tick * 1.5F / a) % 360.0F));

            // Orbital translation — distance proportional to orbit index
            double orbitRadius = (a * 120 + 80);
            double orbitSpeed = tick * 0.02 / a; // Much slower orbit
            poseStack.translate(
                    orbitRadius * Math.sin(orbitSpeed + a * 2.094), // 120° apart
                    0,
                    orbitRadius * Math.cos(orbitSpeed + a * 2.094)
            );

            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                    poseStack.last(),
                    buffer.getBuffer(RenderType.solid()),
                    null,
                    model,
                    1.0F, 1.0F, 1.0F,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY,
                    RenderType.solid()
            );

            poseStack.popPose();
        }
    }

    /**
     * Renders the outer space sphere shell — the largest object wrapping everything.
     * Scale 0.01 * 17.5 = 0.175 for the 35-block structure.
     */
    private static void renderOuterSpaceShell(float tick, PoseStack poseStack, MultiBufferSource buffer) {
        BakedModel model = StellarModelRegistry.getBakedModel(StellarModelRegistry.SPACE);
        if (model == null) return;

        float scale = 0.01F * 17.0F;

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        // Very slow rotation for ambient effect
        poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(0.0F, 1.0F, 0.0F, (tick * 0.05F) % 360.0F));

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                buffer.getBuffer(RenderType.translucent()),
                null,
                model,
                1.0F, 1.0F, 1.0F,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.translucent()
        );

        // Render a second time rotated 180 degrees to cover the texture/mesh gap on the sphere
        poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(0.0F, 1.0F, 0.0F, 180.0F));

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                buffer.getBuffer(RenderType.translucent()),
                null,
                model,
                1.0F, 1.0F, 1.0F,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.translucent()
        );

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(StellarNexusControllerBE blockEntity) {
        return true; // Always render — the model extends far beyond the controller block
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public AABB getRenderBoundingBox(StellarNexusControllerBE blockEntity) {
        // Return a massive bounding box so the renderer is never culled
        // This prevents the model from disappearing when the player gets close
        return new AABB(
                blockEntity.getBlockPos().getX() - 50, blockEntity.getBlockPos().getY() - 50, blockEntity.getBlockPos().getZ() - 50,
                blockEntity.getBlockPos().getX() + 50, blockEntity.getBlockPos().getY() + 50, blockEntity.getBlockPos().getZ() + 50
        );
    }
}

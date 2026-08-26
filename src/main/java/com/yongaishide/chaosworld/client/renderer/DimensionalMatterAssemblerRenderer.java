package com.yongaishide.chaosworld.client.renderer;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yongaishide.chaosworld.block.entity.DimensionalMatterAssemblerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.DirectionalBlock;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import com.mojang.math.Axis;

public class DimensionalMatterAssemblerRenderer implements BlockEntityRenderer<DimensionalMatterAssemblerBlockEntity> {
    private static final float LEFT_MIN_X = 1.0F / 16.0F;
    private static final float LEFT_MAX_X = 7.0F / 16.0F;
    private static final float RIGHT_MIN_X = 9.0F / 16.0F;
    private static final float RIGHT_MAX_X = 15.0F / 16.0F;
    private static final float MIN_Y = 4.0F / 16.0F;
    private static final float MAX_Y = 14.0F / 16.0F;
    private static final float MIN_Z = 2.0F / 16.0F;
    private static final float MAX_Z = 14.0F / 16.0F;
    private static final float EDGE_INSET = 0.01F / 16.0F;

    public DimensionalMatterAssemblerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            DimensionalMatterAssemblerBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        poseStack.pushPose();
        rotateToBlockFacing(blockEntity, poseStack);
        renderTank(blockEntity, true, poseStack, bufferSource, packedLight, packedOverlay, 3, 2, 0, 1);
        renderTank(blockEntity, false, poseStack, bufferSource, packedLight, packedOverlay, 2, 3, 1, 0);
        poseStack.popPose();
    }

    private static void renderTank(
            DimensionalMatterAssemblerBlockEntity blockEntity,
            boolean leftTank,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            int... slots) {
        var tank = blockEntity.getTank();
        GenericStack genericStack = null;
        for (int slot : slots) {
            genericStack = tank.getStack(slot);
            if (genericStack != null && genericStack.amount() > 0) {
                break;
            }
        }
        if (!(genericStack != null && genericStack.what() instanceof AEFluidKey fluidKey) || genericStack.amount() <= 0) {
            return;
        }

        FluidStack fluidStack = fluidKey.toStack((int) Math.min(Integer.MAX_VALUE, genericStack.amount()));
        if (fluidStack.isEmpty()) {
            return;
        }

        IClientFluidTypeExtensions fluidType = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        var spriteLocation = fluidType.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(spriteLocation);
        int tint = fluidType.getTintColor(fluidStack);
        if (tint == 0) {
            tint = 0xFFFFFFFF;
        } else if ((tint >>> 24) == 0) {
            tint |= 0xFF000000;
        }
        long maxAmount = Math.max(1L, tank.getMaxAmount(fluidKey));
        float fill = Math.min(1.0F, genericStack.amount() / (float) maxAmount);
        float topY = MIN_Y + (MAX_Y - MIN_Y) * fill;
        if (topY <= MIN_Y) {
            return;
        }

        float minX = leftTank ? LEFT_MIN_X : RIGHT_MIN_X;
        float maxX = leftTank ? LEFT_MAX_X : RIGHT_MAX_X;
        VertexConsumer consumer = bufferSource.getBuffer(Sheets.translucentCullBlockSheet());
        drawBox(consumer, poseStack, minX, MIN_Y, MIN_Z, maxX, topY, MAX_Z, sprite, packedLight, packedOverlay, tint);
    }

    private static void rotateToBlockFacing(DimensionalMatterAssemblerBlockEntity blockEntity, PoseStack poseStack) {
        var blockState = blockEntity.getBlockState();
        if (!blockState.hasProperty(DirectionalBlock.FACING)) {
            return;
        }

        Direction facing = blockState.getValue(DirectionalBlock.FACING);
        float degrees = switch (facing) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
        if (degrees == 0.0F) {
            return;
        }

        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(degrees));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
    }

    private static void drawBox(
            VertexConsumer consumer,
            PoseStack poseStack,
            float minX,
            float minY,
            float minZ,
            float maxX,
            float maxY,
            float maxZ,
            TextureAtlasSprite sprite,
            int light,
            int overlay,
            int color) {
        minX += EDGE_INSET;
        minY += EDGE_INSET;
        minZ += EDGE_INSET;
        maxX -= EDGE_INSET;
        maxY -= EDGE_INSET;
        maxZ -= EDGE_INSET;

        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();

        drawDoubleSidedQuad(consumer, poseStack, minX, maxY, minZ, maxX, minY, minZ, u0, v0, u1, v1, light, overlay, color, Direction.NORTH);
        drawDoubleSidedQuad(consumer, poseStack, maxX, maxY, maxZ, minX, minY, maxZ, u0, v0, u1, v1, light, overlay, color, Direction.SOUTH);
        drawDoubleSidedQuad(consumer, poseStack, minX, maxY, maxZ, minX, minY, minZ, u0, v0, u1, v1, light, overlay, color, Direction.WEST);
        drawDoubleSidedQuad(consumer, poseStack, maxX, maxY, minZ, maxX, minY, maxZ, u0, v0, u1, v1, light, overlay, color, Direction.EAST);
        drawDoubleSidedHorizontalQuad(consumer, poseStack, minX, maxY, maxZ, maxX, minZ, u0, v0, u1, v1, light, overlay, color, Direction.UP);
    }

    private static void drawDoubleSidedQuad(
            VertexConsumer consumer,
            PoseStack poseStack,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float u0,
            float v0,
            float u1,
            float v1,
            int light,
            int overlay,
            int color,
            Direction normal) {
        drawQuad(consumer, poseStack, x0, y0, z0, x1, y1, z1, u0, v0, u1, v1, light, overlay, color, normal);
        drawQuadReversed(consumer, poseStack, x0, y0, z0, x1, y1, z1, u0, v0, u1, v1, light, overlay, color, normal.getOpposite());
    }

    private static void drawDoubleSidedHorizontalQuad(
            VertexConsumer consumer,
            PoseStack poseStack,
            float minX,
            float y,
            float z0,
            float maxX,
            float z1,
            float u0,
            float v0,
            float u1,
            float v1,
            int light,
            int overlay,
            int color,
            Direction normal) {
        drawHorizontalQuad(consumer, poseStack, minX, y, z0, maxX, z1, u0, v0, u1, v1, light, overlay, color, normal);
        drawHorizontalQuadReversed(consumer, poseStack, minX, y, z0, maxX, z1, u0, v0, u1, v1, light, overlay, color, normal.getOpposite());
    }

    private static void drawQuad(
            VertexConsumer consumer,
            PoseStack poseStack,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float u0,
            float v0,
            float u1,
            float v1,
            int light,
            int overlay,
            int color,
            Direction normal) {
        drawVertex(consumer, poseStack, x0, y0, z0, u0, v1, light, overlay, color, normal);
        drawVertex(consumer, poseStack, x1, y0, z1, u1, v1, light, overlay, color, normal);
        drawVertex(consumer, poseStack, x1, y1, z1, u1, v0, light, overlay, color, normal);
        drawVertex(consumer, poseStack, x0, y1, z0, u0, v0, light, overlay, color, normal);
    }

    private static void drawQuadReversed(
            VertexConsumer consumer,
            PoseStack poseStack,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float u0,
            float v0,
            float u1,
            float v1,
            int light,
            int overlay,
            int color,
            Direction normal) {
        drawVertex(consumer, poseStack, x0, y1, z0, u0, v0, light, overlay, color, normal);
        drawVertex(consumer, poseStack, x1, y1, z1, u1, v0, light, overlay, color, normal);
        drawVertex(consumer, poseStack, x1, y0, z1, u1, v1, light, overlay, color, normal);
        drawVertex(consumer, poseStack, x0, y0, z0, u0, v1, light, overlay, color, normal);
    }

    private static void drawHorizontalQuad(
            VertexConsumer consumer,
            PoseStack poseStack,
            float minX,
            float y,
            float z0,
            float maxX,
            float z1,
            float u0,
            float v0,
            float u1,
            float v1,
            int light,
            int overlay,
            int color,
            Direction normal) {
        drawVertex(consumer, poseStack, minX, y, z0, u0, v0, light, overlay, color, normal);
        drawVertex(consumer, poseStack, maxX, y, z0, u1, v0, light, overlay, color, normal);
        drawVertex(consumer, poseStack, maxX, y, z1, u1, v1, light, overlay, color, normal);
        drawVertex(consumer, poseStack, minX, y, z1, u0, v1, light, overlay, color, normal);
    }

    private static void drawHorizontalQuadReversed(
            VertexConsumer consumer,
            PoseStack poseStack,
            float minX,
            float y,
            float z0,
            float maxX,
            float z1,
            float u0,
            float v0,
            float u1,
            float v1,
            int light,
            int overlay,
            int color,
            Direction normal) {
        drawVertex(consumer, poseStack, minX, y, z1, u0, v1, light, overlay, color, normal);
        drawVertex(consumer, poseStack, maxX, y, z1, u1, v1, light, overlay, color, normal);
        drawVertex(consumer, poseStack, maxX, y, z0, u1, v0, light, overlay, color, normal);
        drawVertex(consumer, poseStack, minX, y, z0, u0, v0, light, overlay, color, normal);
    }

    private static void drawVertex(
            VertexConsumer consumer,
            PoseStack poseStack,
            float x,
            float y,
            float z,
            float u,
            float v,
            int light,
            int overlay,
            int color,
            Direction normal) {
        consumer.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light);
        consumer.setNormal(
                poseStack.last(),
                normal.getStepX(),
                normal.getStepY(),
                normal.getStepZ());
    }
}

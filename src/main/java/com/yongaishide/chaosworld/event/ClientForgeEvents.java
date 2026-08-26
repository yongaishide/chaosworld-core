package com.yongaishide.chaosworld.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinitions;
import com.yongaishide.chaosworld.item.custom.HammerItem;
import com.yongaishide.chaosworld.item.custom.IHasModeHUD;
import com.yongaishide.chaosworld.item.custom.UfoEnergyHoeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onRightClickBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide() && event.getEntity().isShiftKeyDown() && event.getItemStack().isEmpty()) {
            var state = event.getLevel().getBlockState(event.getPos());
            if (MultiblockControllerDefinitions.isSupportedController(state)) {
                var facing = state.getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);
                com.yongaishide.chaosworld.client.GhostHologramRenderer.toggleHologram(event.getPos(), facing);

                if (com.yongaishide.chaosworld.client.GhostHologramRenderer.isActive(event.getPos())) {
                    event.getEntity().displayClientMessage(Component.translatable("message.ufo.ghost_hologram_enabled").withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE), true);
                } else {
                    event.getEntity().displayClientMessage(Component.translatable("message.ufo.ghost_hologram_disabled").withStyle(net.minecraft.ChatFormatting.GRAY), true);
                }
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack heldStack = player.getMainHandItem();
        int range = 0;
        List<BlockPos> positions = null;

        if (heldStack.getItem() instanceof HammerItem) {
            range = HammerItem.getRange(heldStack);
            if (range > 0) {
                positions = HammerItem.getBlocksToBeDestroyed(range, event.getTarget().getBlockPos(), player);
            }
        } else if (heldStack.getItem() instanceof UfoEnergyHoeItem) {
            range = UfoEnergyHoeItem.getRange(heldStack);
            if (range > 0) {
                positions = UfoEnergyHoeItem.getPositions(event.getTarget().getBlockPos(), range);
            }
        }

        if (range > 0 && positions != null) {
            event.setCanceled(true);
            VertexConsumer vertexConsumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
            renderBlockOutlines(event.getPoseStack(), vertexConsumer, positions);
        }
    }

    private static void renderBlockOutlines(PoseStack poseStack, VertexConsumer vertexConsumer, List<BlockPos> positions) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        for (BlockPos pos : positions) {
            double x = pos.getX() - cameraPos.x();
            double y = pos.getY() - cameraPos.y();
            double z = pos.getZ() - cameraPos.z();

            AABB box = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(0.002);
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, box, 0.0F, 0.0F, 0.0F, 0.4F);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.getItem() instanceof IHasModeHUD hudItem) {
            Component hudText = hudItem.getModeHudComponent(heldStack);
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int screenHeight = guiGraphics.guiHeight();
            guiGraphics.drawString(Minecraft.getInstance().font, hudText, 10, screenHeight - 20, 0xFFFFFF, true);
        }
    }
}

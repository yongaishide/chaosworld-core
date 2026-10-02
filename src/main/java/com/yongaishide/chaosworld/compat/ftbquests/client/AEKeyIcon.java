package com.yongaishide.chaosworld.compat.ftbquests.client;

import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.AEKey;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class AEKeyIcon extends Icon {
    private final AEKey key;

    public AEKeyIcon(AEKey key) {
        this.key = key;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int w, int h) {
        if (AEKeyRendering.get(key.getType()) == null) {
            return;
        }

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + w / 2.0, y + h / 2.0, 0.0);
        if (w != 16 || h != 16) {
            float scale = Math.min(w, h) / 16.0F;
            pose.scale(scale, scale, 1.0F);
        }
        AEKeyRendering.drawInGui(Minecraft.getInstance(), graphics, -8, -8, key);
        pose.popPose();
    }

    @Override
    public Object getIngredient() {
        return key;
    }
}


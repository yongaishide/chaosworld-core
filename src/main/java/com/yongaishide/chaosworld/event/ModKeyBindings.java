package com.yongaishide.chaosworld.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final String KEY_CATEGORY_UFO = "key.category.ufo";
    public static final String KEY_TOGGLE_AUTO_SMELT = "key.ufo.toggle_auto_smelt";
    public static final KeyMapping CYCLE_TOOL_FORWARD = new KeyMapping(
            "key.ufo.cycle_tool_forward",
            KeyConflictContext.IN_GAME,
            KeyModifier.ALT,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            KEY_CATEGORY_UFO
    );

    public static final KeyMapping CYCLE_TOOL_BACKWARD = new KeyMapping(
            "key.ufo.cycle_tool_backward",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            KEY_CATEGORY_UFO
    );

    public static final KeyMapping CYCLE_MODE = new KeyMapping(
            "key.ufo.cycle_mode",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KEY_CATEGORY_UFO
    );
    public static final KeyMapping TOGGLE_AUTO_SMELT = new KeyMapping(KEY_TOGGLE_AUTO_SMELT, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, KEY_CATEGORY_UFO);
}

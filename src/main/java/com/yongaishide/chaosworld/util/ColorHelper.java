package com.yongaishide.chaosworld.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import java.awt.Color;

public class ColorHelper {

    private static final ChatFormatting[] LOW_CONTRAST_DARKS = new ChatFormatting[] {
            ChatFormatting.BLACK,
            ChatFormatting.DARK_GRAY
    };

    public static MutableComponent getAnimatedColoredText(String text, ChatFormatting... colors) {
        return Component.literal(text).withStyle(pickReadableColor(colors));
    }

    public static MutableComponent getSolidColoredText(String text, ChatFormatting... colors) {
        return Component.literal(text).withStyle(pickReadableColor(colors));
    }

    public static MutableComponent getRainbowGradientText(String text) {
        MutableComponent result = Component.literal("");
        int length = text.length();
        for (int i = 0; i < length; i++) {
            float t = length <= 1 ? 0f : (float) i / (length - 1);
            int rgb = Color.HSBtoRGB(t * 0.83f, 0.85f, 1.0f) & 0xFFFFFF;
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(Style.EMPTY.withColor(rgb)));
        }
        return result;
    }

    public static ChatFormatting pickReadableColor(ChatFormatting... colors) {
        if (colors == null || colors.length == 0) {
            return ChatFormatting.WHITE;
        }

        boolean hasWhiteFamily = false;
        boolean hasLowContrastDark = false;

        for (ChatFormatting color : colors) {
            if (color == null) {
                continue;
            }

            if (color == ChatFormatting.WHITE || color == ChatFormatting.GRAY) {
                hasWhiteFamily = true;
            }

            for (ChatFormatting dark : LOW_CONTRAST_DARKS) {
                if (color == dark) {
                    hasLowContrastDark = true;
                    break;
                }
            }
        }

        if (hasWhiteFamily && hasLowContrastDark) {
            return ChatFormatting.WHITE;
        }

        for (ChatFormatting color : colors) {
            if (color == null || color == ChatFormatting.BLACK || color == ChatFormatting.DARK_GRAY) {
                continue;
            }
            return color;
        }

        return ChatFormatting.WHITE;
    }

    public static int getRainbowColor(int offset) {
        float hue = (System.currentTimeMillis() / 10 % 360 + offset) / 360f;
        return Color.HSBtoRGB(hue, 0.8f, 1.0f);
    }

    public static int getBluePurplePinkColor(int offset) {
        float hue = (System.currentTimeMillis() / 10 % 360 + offset) / 360f;
        if (hue > 0.83f) {
            hue = 0.66f + (hue - 0.83f) * 0.5f;
        } else if (hue < 0.66f) {
            hue = 0.66f;
        }
        return java.awt.Color.HSBtoRGB(hue, 0.7f, 1.0f);
    }

    public static int getGrayBlackColor(int offset) {
        long time = System.currentTimeMillis() / 2;
        float brightness = 0.5f + 0.5f * (float) Math.sin((time + offset) / 500.0);
        brightness = Mth.clamp(brightness * 0.6f, 0.1f, 0.6f);
        return new java.awt.Color(brightness, brightness, brightness).getRGB();
    }

    public static int getRedCyanPinkColor(int offset) {
        float hue = (System.currentTimeMillis() / 15 % 360 + offset) / 360f;
        if (hue < 0.33f) {
            hue = 0.0f;
        } else if (hue < 0.66f) {
            hue = 0.5f;
        } else {
            hue = 0.83f;
        }
        return java.awt.Color.HSBtoRGB(hue, 0.8f, 1.0f);
    }
}

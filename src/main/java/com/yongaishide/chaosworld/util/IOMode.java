package com.yongaishide.chaosworld.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum IOMode implements StringRepresentable {
    NONE("none", Component.translatable("gui.ufo.io_mode.none").withStyle(ChatFormatting.GRAY)),

    // Item Modes
    ITEM_IN("item_in", Component.translatable("gui.ufo.io_mode.item_input").withStyle(ChatFormatting.BLUE)),
    ITEM_OUT("item_out", Component.translatable("gui.ufo.io_mode.item_output").withStyle(ChatFormatting.GOLD)),
    ITEM_OUT2("item_out", Component.translatable("gui.ufo.io_mode.item_output").withStyle(ChatFormatting.GOLD)),
    ITEM_IO("item_io", Component.translatable("gui.ufo.io_mode.item_io").withStyle(ChatFormatting.LIGHT_PURPLE)), // Novo
    ITEM_IO2("item_io", Component.translatable("gui.ufo.io_mode.item_io").withStyle(ChatFormatting.LIGHT_PURPLE)), // Novo

    // Fluid Modes
    FLUID_IN("fluid_in", Component.translatable("gui.ufo.io_mode.fluid_input").withStyle(ChatFormatting.AQUA)),
    FLUID_OUT_1("fluid_out_1", Component.translatable("gui.ufo.io_mode.fluid_output_1").withStyle(ChatFormatting.DARK_AQUA)),
    FLUID_IO_1("fluid_io_1", Component.translatable("gui.ufo.io_mode.fluid_io_1").withStyle(ChatFormatting.LIGHT_PURPLE)), // Novo
    FLUID_OUT_2("fluid_out_2", Component.translatable("gui.ufo.io_mode.fluid_output_2").withStyle(ChatFormatting.DARK_BLUE)),
    FLUID_IO_2("fluid_io_2", Component.translatable("gui.ufo.io_mode.fluid_io_2").withStyle(ChatFormatting.LIGHT_PURPLE)), // Novo

    // Other Modes
    COOLANT_IN("coolant_in", Component.translatable("gui.ufo.io_mode.coolant_input").withStyle(ChatFormatting.GREEN)),
    ENERGY("energy", Component.translatable("gui.ufo.io_mode.energy").withStyle(ChatFormatting.RED));

    private final String name;
    private final Component tooltip;

    IOMode(String name, Component tooltip) {
        this.name = name;
        this.tooltip = tooltip;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public Component getTooltip() {
        return this.tooltip;
    }
}
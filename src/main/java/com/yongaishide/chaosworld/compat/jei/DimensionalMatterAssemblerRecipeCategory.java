package com.yongaishide.chaosworld.compat.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.recipe.DimensionalMatterAssemblerRecipe;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

public class DimensionalMatterAssemblerRecipeCategory implements IRecipeCategory<DimensionalMatterAssemblerRecipe> {

    public static final RecipeType<DimensionalMatterAssemblerRecipe> RECIPE_TYPE =
            RecipeType.create("chaosworld_core", "dimensional_assembly", DimensionalMatterAssemblerRecipe.class);

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "textures/guis/dimensional_matter_assembler_jei_ui.png");

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;

    // Heat/energy bar position (matches GUI)
    private static final int ENERGY_BAR_X = 9;
    private static final int ENERGY_BAR_Y = 81;
    private static final int ENERGY_BAR_W = 91;
    private static final int ENERGY_BAR_H = 10;

    public DimensionalMatterAssemblerRecipeCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();
        // Crop the top part of the GUI texture (0,0 -> 175,98), excluding player inventory
        background = guiHelper.createDrawable(BACKGROUND, 0, 0, 175, 98);
        icon = guiHelper.createDrawableItemStack(ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().asItem().getDefaultInstance());

        IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 234, 0, 20, 11);
        this.progress =
                guiHelper.createAnimatedDrawable(progressDrawable, 60, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<DimensionalMatterAssemblerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().getName();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DimensionalMatterAssemblerRecipe recipe, IFocusGroup focuses) {
        // Background starts at (0,0), so slot positions match the actual GUI texture pixel coords directly.
        // Item Input slots: start at (47, 21) on the texture
        // Input Coolant: (9, 21)
        // Input Fluid: (28, 21)
        // Item Output 1: (132, 21)
        // Item Output 2: (132, 49)

        // 1. Item inputs
        var itemInputs = recipe.getItemInputs();
        for (int i = 0; i < itemInputs.size(); i++) {
            if (itemInputs.get(i) != null && !itemInputs.get(i).isEmpty()) {
                int col = i % 3;
                int row = i / 3;
                builder.addInputSlot(47 + (col * 18), 21 + (row * 18))
                       .addIngredients(UfoJeiPlugin.stackOf(itemInputs.get(i)));
            }
        }

        // 2. Fluid inputs (no coolant - only base fluid shown at position 28,21)
        var fluidInputs = recipe.getFluidInputs();
        for (int i = 0; i < fluidInputs.size(); i++) {
            if (!fluidInputs.get(i).isEmpty()) {
                var slot = builder.addInputSlot(28, 21).setFluidRenderer(16000, false, 12, 54);
                slot.addIngredients(NeoForgeTypes.FLUID_STACK, UfoJeiPlugin.stackOf(fluidInputs.get(i)));
            }
        }

        // 3. Item Outputs
        var itemOutputs = recipe.getItemOutputs();
        if (itemOutputs.size() > 0 && itemOutputs.get(0).what() instanceof AEItemKey itemKey) {
            builder.addOutputSlot(132, 21).addItemStack(itemKey.toStack((int)itemOutputs.get(0).amount()));
        }
        if (itemOutputs.size() > 1 && itemOutputs.get(1).what() instanceof AEItemKey itemKey) {
            builder.addOutputSlot(132, 49).addItemStack(itemKey.toStack((int)itemOutputs.get(1).amount()));
        }

        // 4. Fluid Outputs (Fluid 1 & 2)
        var fluidOutputs = recipe.getFluidOutputs();
        // fluid output 1: (119, 75) W: 14, H: 17
        if (fluidOutputs.size() > 0 && fluidOutputs.get(0).what() instanceof AEFluidKey fluidKey) {
            var slot = builder.addOutputSlot(119, 75).setFluidRenderer(16000, false, 14, 17);
            slot.addFluidStack(fluidKey.getFluid(), (int)fluidOutputs.get(0).amount());
        }
        // fluid output 2: (148, 76) W: 14, H: 17
        if (fluidOutputs.size() > 1 && fluidOutputs.get(1).what() instanceof AEFluidKey fluidKey) {
            var slot = builder.addOutputSlot(148, 76).setFluidRenderer(16000, false, 14, 17);
            slot.addFluidStack(fluidKey.getFluid(), (int)fluidOutputs.get(1).amount());
        }
    }

    @Override
    public void draw(
            DimensionalMatterAssemblerRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics,
            double mouseX,
            double mouseY) {
        this.background.draw(guiGraphics);
        
        // Progress bar at (105, 42)
        this.progress.draw(guiGraphics, 105, 42);

        // Top black box to host energy reading
        guiGraphics.fill(9, 81, 100, 91, 0xFF101010); // Lighly offset black
        
        var font = Minecraft.getInstance().font;
        String energyText = formatEnergy(recipe.getEnergy());

        // Blue energy gradient fill
        guiGraphics.fillGradient(
                ENERGY_BAR_X,
                ENERGY_BAR_Y,
                ENERGY_BAR_X + ENERGY_BAR_W,
                ENERGY_BAR_Y + ENERGY_BAR_H,
                0x880055FF, // Top Color
                0xDD0022AA  // Bottom Color
        );

        // Center energy text
        int textWidth = font.width(energyText);
        int textX = ENERGY_BAR_X + (ENERGY_BAR_W - textWidth) / 2;
        int textY = ENERGY_BAR_Y + 1;
        guiGraphics.drawString(font, energyText, textX, textY, 0xFFFFFFFF, true);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, DimensionalMatterAssemblerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= ENERGY_BAR_X && mouseX <= ENERGY_BAR_X + ENERGY_BAR_W
                && mouseY >= ENERGY_BAR_Y && mouseY <= ENERGY_BAR_Y + ENERGY_BAR_H) {
            int baseTicks = recipe.getTime();
            double seconds = baseTicks / 20.0;
            tooltip.add(Component.translatable("jei.ufo.dma.energy", formatEnergy(recipe.getEnergy())));
            tooltip.add(Component.translatable("jei.ufo.dma.base_time", String.format("%.1f", seconds), baseTicks));
            tooltip.add(Component.translatable("jei.ufo.dma.chrono_hint"));
            return;
        }

        if (mouseX >= 105 && mouseX <= 125 && mouseY >= 42 && mouseY <= 53) {
            int baseTicks = recipe.getTime();
            double seconds = baseTicks / 20.0;
            tooltip.add(Component.translatable("jei.ufo.dma.processing_time", String.format("%.1f", seconds)));
            tooltip.add(Component.translatable("jei.ufo.dma.base_time_hint"));
        }
    }

    /**
     * Formats energy values into human-readable strings.
     * K = thousands, M = millions, G = billions
     */
    private static String formatEnergy(int energy) {
        if (energy >= 1_000_000_000) {
            double val = energy / 1_000_000_000.0;
            if (val == (long) val) return (long) val + "G AE";
            return String.format("%.1fG AE", val);
        } else if (energy >= 1_000_000) {
            double val = energy / 1_000_000.0;
            if (val == (long) val) return (long) val + "M AE";
            return String.format("%.1fM AE", val);
        } else if (energy >= 1_000) {
            double val = energy / 1_000.0;
            if (val == (long) val) return (long) val + "K AE";
            return String.format("%.1fK AE", val);
        }
        return energy + " AE";
    }
}

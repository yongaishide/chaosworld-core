package com.yongaishide.chaosworld.compat.jei;

import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.recipe.QMFRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class QmfRecipeCategory implements IRecipeCategory<QMFRecipe> {
    public static final RecipeType<QMFRecipe> RECIPE_TYPE =
            RecipeType.create("chaosworld_core", "qmf_recipe", QMFRecipe.class);

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "textures/guis/dimensional_matter_assembler_jei_ui.png");

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;

    private static final int ENERGY_BAR_X = 9;
    private static final int ENERGY_BAR_Y = 81;
    private static final int ENERGY_BAR_W = 91;
    private static final int ENERGY_BAR_H = 10;

    public QmfRecipeCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, 175, 98);
        this.icon = guiHelper.createDrawableItemStack(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem().getDefaultInstance());
        IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 234, 0, 20, 11);
        this.progress = guiHelper.createAnimatedDrawable(progressDrawable, 60, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<QMFRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().getName();
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return 175;
    }

    @Override
    public int getHeight() {
        return 98;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, QMFRecipe recipe, IFocusGroup focuses) {
        var itemInputs = recipe.getItemInputs();
        for (int i = 0; i < itemInputs.size(); i++) {
            var ingredient = itemInputs.get(i);
            int col = i % 3;
            int row = i / 3;
            builder.addInputSlot(47 + (col * 18), 21 + (row * 18))
                    .addIngredients(UfoJeiPlugin.stackOfQmf(ingredient))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.translatable("jei.ufo.universal.required_items", formatAmount(ingredient.amount()))));
        }

        var fluidInputs = recipe.getFluidInputs();
        for (int i = 0; i < fluidInputs.size(); i++) {
            var ingredient = fluidInputs.get(i);
            int x = i == 0 ? 28 : 9;
            builder.addInputSlot(x, 21)
                    .setFluidRenderer(16000, false, 12, 54)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, ingredient.fluid().copyWithAmount((int) ingredient.amount()))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.translatable("jei.ufo.universal.required_mb", formatAmount(ingredient.amount()))));
        }

        ItemStack output = recipe.getResultItem();
        builder.addOutputSlot(132, 21)
                .addItemStack(output.copy())
                .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.translatable("jei.ufo.universal.output_items", formatAmount(output.getCount()))));
    }

    @Override
    public void draw(QMFRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics, 105, 42);

        guiGraphics.fill(9, 81, 100, 91, 0xFF101010);

        var font = net.minecraft.client.Minecraft.getInstance().font;
        String energyText = "AE " + formatEnergy(recipe.getEnergy());
        guiGraphics.fillGradient(
                ENERGY_BAR_X,
                ENERGY_BAR_Y,
                ENERGY_BAR_X + ENERGY_BAR_W,
                ENERGY_BAR_Y + ENERGY_BAR_H,
                0x880055FF,
                0xDD0022AA
        );

        int textWidth = font.width(energyText);
        int textX = ENERGY_BAR_X + (ENERGY_BAR_W - textWidth) / 2;
        guiGraphics.drawString(font, energyText, textX, ENERGY_BAR_Y + 1, 0xFFFFFFFF, true);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, QMFRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= ENERGY_BAR_X && mouseX <= ENERGY_BAR_X + ENERGY_BAR_W
                && mouseY >= ENERGY_BAR_Y && mouseY <= ENERGY_BAR_Y + ENERGY_BAR_H) {
            tooltip.add(Component.translatable("jei.ufo.universal.energy", formatEnergy(recipe.getEnergy())));
            tooltip.add(Component.translatable("jei.ufo.universal.base_time", String.format("%.1f", recipe.getTime() / 20.0), recipe.getTime()));
            tooltip.add(Component.translatable("jei.ufo.universal.required_tier", recipe.getRequiredTier()));
            return;
        }

        if (mouseX >= 105 && mouseX <= 125 && mouseY >= 42 && mouseY <= 53) {
            tooltip.add(Component.translatable("jei.ufo.universal.processing_time", String.format("%.1f", recipe.getTime() / 20.0), recipe.getTime()));
            tooltip.add(Component.translatable("jei.ufo.universal.required_tier", recipe.getRequiredTier()));
        }
    }

    private static String formatEnergy(long energy) {
        if (energy >= 1_000_000_000L) {
            return String.format("%.1fG", energy / 1_000_000_000.0) + " AE";
        }
        if (energy >= 1_000_000L) {
            return String.format("%.1fM", energy / 1_000_000.0) + " AE";
        }
        if (energy >= 1_000L) {
            return String.format("%.1fK", energy / 1_000.0) + " AE";
        }
        return energy + " AE";
    }

    private static String formatAmount(long amount) {
        return Long.toString(amount);
    }
}

package com.yongaishide.chaosworld.compat.jei;

import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.recipe.UniversalMultiblockMachineKind;
import com.yongaishide.chaosworld.recipe.UniversalMultiblockRecipe;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class UniversalMultiblockRecipeCategory implements IRecipeCategory<UniversalMultiblockRecipe> {
    public static final RecipeType<UniversalMultiblockRecipe> QMF_RECIPE_TYPE =
            RecipeType.create("chaosworld_core", "universal_multiblock_qmf", UniversalMultiblockRecipe.class);
    public static final RecipeType<UniversalMultiblockRecipe> QUANTUM_SLICER_RECIPE_TYPE =
            RecipeType.create("chaosworld_core", "universal_multiblock_quantum_slicer", UniversalMultiblockRecipe.class);
    public static final RecipeType<UniversalMultiblockRecipe> QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE =
            RecipeType.create("chaosworld_core", "universal_multiblock_quantum_processing_factory", UniversalMultiblockRecipe.class);
    public static final RecipeType<UniversalMultiblockRecipe> QUANTUM_CRYOFORGE_RECIPE_TYPE =
            RecipeType.create("chaosworld_core", "universal_multiblock_quantum_cryoforge", UniversalMultiblockRecipe.class);

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "textures/guis/dimensional_matter_assembler_jei_ui.png");

    /**
     * Renders item icons with an abbreviated count label (e.g. 64K) instead of
     * the full number, while the underlying stack keeps the real amount so AE2
     * pattern encoding ("send to AE") still imports the correct quantities.
     */
    private static final mezz.jei.api.ingredients.IIngredientRenderer<ItemStack> COUNTED_ITEM_RENDERER =
            new mezz.jei.api.ingredients.IIngredientRenderer<>() {
                @Override
                public void render(GuiGraphics gfx, ItemStack stack) {
                    // Render the icon at count 1 so no vanilla count label is drawn
                    gfx.renderItem(stack.copyWithCount(1), 0, 0);
                    int count = stack.getCount();
                    if (count > 1) {
                        String text = abbreviateCount(count);
                        var font = net.minecraft.client.Minecraft.getInstance().font;
                        int w = font.width(text);
                        gfx.drawString(font, text, 16 - w, 8, 0xFFFFFFFF, true);
                    }
                }

                @Override
                public java.util.List<Component> getTooltip(ItemStack stack, net.minecraft.world.item.TooltipFlag flag) {
                    return java.util.List.of(stack.getHoverName());
                }
            };

    private static String abbreviateCount(long amount) {
        if (amount >= 1_000_000_000L) {
            return stripDecimal(amount / 1_000_000_000.0) + "B";
        }
        if (amount >= 1_000_000L) {
            return stripDecimal(amount / 1_000_000.0) + "M";
        }
        if (amount >= 1_000L) {
            return stripDecimal(amount / 1_000.0) + "K";
        }
        return Long.toString(amount);
    }

    private static final int ENERGY_BAR_X = 9;
    private static final int ENERGY_BAR_Y = 81;
    private static final int ENERGY_BAR_W = 91;
    private static final int ENERGY_BAR_H = 10;
    private static final int ITEM_OUTPUT_X = 133;
    private static final int ITEM_OUTPUT_Y = 22;
    private static final int CONTROLLER_X = 150;
    private static final int CONTROLLER_Y = 2;

    private final UniversalMultiblockMachineKind machineKind;
    private final Component title;
    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;

    public UniversalMultiblockRecipeCategory(IJeiHelpers helpers,
                                             UniversalMultiblockMachineKind machineKind,
                                             ItemStack iconStack,
                                             Component title) {
        this.machineKind = machineKind;
        this.title = title;

        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, 175, 98);
        this.icon = guiHelper.createDrawableItemStack(iconStack);
        IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 234, 0, 20, 11);
        this.progress = guiHelper.createAnimatedDrawable(progressDrawable, 60, IDrawableAnimated.StartDirection.LEFT, false);
    }

    public static RecipeType<UniversalMultiblockRecipe> recipeTypeFor(UniversalMultiblockMachineKind machineKind) {
        return switch (machineKind) {
            case QMF -> QMF_RECIPE_TYPE;
            case QUANTUM_SLICER -> QUANTUM_SLICER_RECIPE_TYPE;
            case QUANTUM_PROCESSOR_ASSEMBLER -> QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE;
            case QUANTUM_CRYOFORGE -> QUANTUM_CRYOFORGE_RECIPE_TYPE;
        };
    }

    @Override
    public RecipeType<UniversalMultiblockRecipe> getRecipeType() {
        return recipeTypeFor(this.machineKind);
    }

    @Override
    public Component getTitle() {
        return this.title;
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
    public void setRecipe(IRecipeLayoutBuilder builder, UniversalMultiblockRecipe recipe, IFocusGroup focuses) {
        var itemInputs = recipe.getItemInputs();
        for (int i = 0; i < itemInputs.size(); i++) {
            var ingredient = itemInputs.get(i);
            int col = i % 3;
            int row = i / 3;
            var stacks = java.util.Arrays.stream(ingredient.ingredient().getItems())
                    .map(s -> s.copyWithCount((int) Math.min(ingredient.amount(), Integer.MAX_VALUE)))
                    .toList();
            builder.addInputSlot(48 + (col * 18), 22 + (row * 18))
                    .setCustomRenderer(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, COUNTED_ITEM_RENDERER)
                    .addIngredients(net.minecraft.world.item.crafting.Ingredient.of(stacks.toArray(new net.minecraft.world.item.ItemStack[0])))
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

        if (!recipe.getItemOutput().isEmpty()) {
            ItemStack itemOutput = recipe.getItemOutput();
            builder.addOutputSlot(ITEM_OUTPUT_X, ITEM_OUTPUT_Y)
                    .setCustomRenderer(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, COUNTED_ITEM_RENDERER)
                    .addItemStack(itemOutput.copyWithCount((int) Math.min(recipe.getItemOutputAmount(), Integer.MAX_VALUE)))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.translatable("jei.ufo.universal.output_items", formatAmount(recipe.getItemOutputAmount()))));
        }

        if (!recipe.getFluidOutput().isEmpty() && recipe.getFluidOutputAmount() > 0) {
            FluidStack fluidOutput = recipe.getFluidOutput().copyWithAmount((int) recipe.getFluidOutputAmount());
            builder.addOutputSlot(148, 76)
                    .setFluidRenderer(16000, false, 14, 17)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, fluidOutput)
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.translatable("jei.ufo.universal.output_mb", formatAmount(recipe.getFluidOutputAmount()))));
        }
    }

    @Override
    public void draw(UniversalMultiblockRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics, 105, 42);
        guiGraphics.renderItem(controllerStackFor(this.machineKind), CONTROLLER_X, CONTROLLER_Y);

        guiGraphics.fill(9, 81, 100, 91, 0xFF101010);

        var font = net.minecraft.client.Minecraft.getInstance().font;
        String tierText = "MK" + recipe.getRequiredTier();
        int tierX = CONTROLLER_X + 8 - font.width(tierText) / 2;
        guiGraphics.drawString(font, tierText, tierX, CONTROLLER_Y + 18, 0xFFFFD966, true);

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
    public void getTooltip(ITooltipBuilder tooltip, UniversalMultiblockRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
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
            return;
        }

        if (mouseX >= CONTROLLER_X && mouseX <= CONTROLLER_X + 16
                && mouseY >= CONTROLLER_Y && mouseY <= CONTROLLER_Y + 28) {
            tooltip.add(Component.translatable("jei.ufo.universal.controller"));
            tooltip.add(Component.translatable("jei.ufo.universal.machine_tier", recipe.getRequiredTier()));
            tooltip.add(Component.translatable("jei.ufo.universal.controller_click"));
        }
    }

    private static ItemStack controllerStackFor(UniversalMultiblockMachineKind machineKind) {
        return switch (machineKind) {
            case QMF -> MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem().getDefaultInstance();
            case QUANTUM_SLICER -> MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get().asItem().getDefaultInstance();
            case QUANTUM_PROCESSOR_ASSEMBLER -> MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get().asItem().getDefaultInstance();
            case QUANTUM_CRYOFORGE -> MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem().getDefaultInstance();
        };
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
        if (amount >= 1_000_000_000L) {
            return stripDecimal(amount / 1_000_000_000.0) + "B";
        }
        if (amount >= 1_000_000L) {
            return stripDecimal(amount / 1_000_000.0) + "M";
        }
        if (amount >= 1_000L) {
            return stripDecimal(amount / 1_000.0) + "K";
        }
        return Long.toString(amount);
    }

    private static String stripDecimal(double value) {
        if (value == (long) value) {
            return Long.toString((long) value);
        }
        return String.format("%.1f", value);
    }
}

package com.yongaishide.chaosworld.compat.jei;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.recipe.StellarSimulationRecipe;
import java.util.Locale;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

public class StellarSimulationRecipeCategory implements IRecipeCategory<StellarSimulationRecipe> {

    public static final RecipeType<StellarSimulationRecipe> RECIPE_TYPE =
            RecipeType.create("chaosworld_core", "stellar_simulation", StellarSimulationRecipe.class);

    private static final int WIDTH = 191;
    private static final int HEIGHT = 128;
    private static final int CONTROLLER_X = 170;
    private static final int CONTROLLER_Y = 4;
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "textures/guis/stellar_nexus_jei.png");

    private final IDrawable icon;
    private final IDrawable background;

    public StellarSimulationRecipeCategory(IJeiHelpers helpers) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(
                MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get().asItem().getDefaultInstance());
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public RecipeType<StellarSimulationRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.ufo.stellar_simulation");
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, StellarSimulationRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(CONTROLLER_X, CONTROLLER_Y)
                .addItemStack(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get().asItem().getDefaultInstance())
                .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                    tooltip.add(Component.translatable("jei.ufo.stellar.controller"));
                    tooltip.add(Component.translatable("jei.ufo.stellar.controller_tooltip"));
                });

        var itemInputs = recipe.getItemInputs();
        for (int i = 0; i < itemInputs.size() && i < 9; i++) {
            if (!itemInputs.get(i).isEmpty()) {
                int col = i % 3;
                int row = i / 3;
                int finalI = i;

                var visualStacks = java.util.Arrays.stream(UfoJeiPlugin.stackOf(itemInputs.get(i)).getItems())
                        .map(stack -> {
                            var copy = stack.copy();
                            copy.setCount(1);
                            return copy;
                        }).toList();

                builder.addInputSlot(12 + (col * 18), 17 + (row * 18))
                        .addItemStacks(visualStacks)
                        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                            long amount = itemInputs.get(finalI).getAmount();
                            tooltip.add(Component.translatable("jei.ufo.stellar.amount_required", formatAmount(amount)));
                        });
            }
        }

        var fluidInputs = recipe.getFluidInputs();
        for (int i = 0; i < fluidInputs.size() && i < 3; i++) {
            if (!fluidInputs.get(i).isEmpty()) {
                int yPos = 17 + (i * 20);
                int finalI = i;

                var visualFluids = UfoJeiPlugin.stackOf(fluidInputs.get(i)).stream()
                        .map(stack -> new FluidStack(stack.getFluid(), 1000))
                        .toList();

                var slot = builder.addInputSlot(72, yPos)
                        .setFluidRenderer(1_000_000, false, 10, 13);
                slot.addIngredients(NeoForgeTypes.FLUID_STACK, visualFluids)
                        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                            long amount = fluidInputs.get(finalI).getAmount();
                            tooltip.add(Component.translatable("jei.ufo.stellar.amount_required_mb", formatAmount(amount)));
                        });
            }
        }

        var itemOutputs = recipe.getItemOutputs();
        for (int i = 0; i < itemOutputs.size() && i < 9; i++) {
            if (itemOutputs.get(i).what() instanceof AEItemKey itemKey) {
                int col = i % 3;
                int row = i / 3;
                int finalI = i;
                builder.addOutputSlot(128 + (col * 18), 17 + (row * 18))
                        .addItemStack(itemKey.toStack(1))
                        .addRichTooltipCallback((recipeSlotView, tooltip) ->
                                tooltip.add(Component.translatable("jei.ufo.stellar.amount_produced", formatAmount(itemOutputs.get(finalI).amount()))));
            }
        }

        var fluidOutputs = recipe.getFluidOutputs();
        for (int i = 0; i < fluidOutputs.size() && i < 6; i++) {
            if (fluidOutputs.get(i).what() instanceof AEFluidKey fluidKey) {
                int col = i % 3;
                int row = i / 3;
                int finalI = i;
                int[] xOffsets = {128, 149, 170};
                var slot = builder.addOutputSlot(xOffsets[col], 76 + (row * 20))
                        .setFluidRenderer(1_000_000, false, 10, 13);
                slot.addFluidStack(fluidKey.getFluid(), 1000)
                        .addRichTooltipCallback((recipeSlotView, tooltip) ->
                                tooltip.add(Component.translatable("jei.ufo.stellar.amount_produced_mb", formatAmount(fluidOutputs.get(finalI).amount()))));
            }
        }
    }

    @Override
    public void draw(StellarSimulationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gfx, double mouseX, double mouseY) {
        this.background.draw(gfx);
        Font font = Minecraft.getInstance().font;

        Component simulationName;
        String simKey = recipe.getSimulationTranslationKey();
        if (!simKey.isEmpty()) {
            simulationName = Component.translatable(simKey);
        } else {
            String rawName = recipe.getSimulationName();
            simulationName = rawName != null && !rawName.isEmpty() ? Component.literal(rawName) : Component.literal("Unknown Simulation");
        }
        drawScaledCenteredString(gfx, font, simulationName.getString(), WIDTH / 2, 4, 0xFFFFFF, 1.0f);

        int pWidth = 20;
        int animWidth = (int) ((System.currentTimeMillis() / 40) % pWidth);
        gfx.fillGradient(94, 38, 94 + animWidth, 49, 0x558B5CF6, 0x556D28D9);

        drawScaledCenteredString(gfx, font, getFuelDisplayShortName(recipe), 29, 76, 0xFFFFFF, 0.7f);
        drawScaledCenteredString(gfx, font, getCoolantDisplayTier(recipe), 75, 76, 0xFFFFFF, 0.7f);
        drawScaledCenteredString(gfx, font, recipe.getFormattedTime(), 29, 88, 0xFFFFFF, 0.8f);
        drawScaledCenteredString(gfx, font, "Mk." + toRoman(recipe.getFieldTier()), 73, 88, 0xFFFFFF, 0.8f);
        drawScaledCenteredString(gfx, font, formatAmount(recipe.getTotalEnergy()) + " AE", 51, 100, 0xFFDF00, 0.7f);
    }

    private static void drawBackground(GuiGraphics gfx) {
        drawPanel(gfx, 0, 0, WIDTH, HEIGHT, 0xFFC8C8C8, 0xFFFFFFFF, 0xFF6A6A6A);
        drawPanel(gfx, 5, 12, 186, 123, 0xFFD6D6D6, 0xFFFFFFFF, 0xFF8B8B8B);

        gfx.fill(8, 15, 105, 116, 0xFF111111);
        gfx.fill(122, 15, 184, 116, 0xFF111111);

        drawSlot(gfx, CONTROLLER_X, CONTROLLER_Y);

        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            drawSlot(gfx, 11 + (col * 18), 16 + (row * 18));
            drawSlot(gfx, 127 + (col * 18), 16 + (row * 18));
        }

        for (int i = 0; i < 3; i++) {
            drawTankSlot(gfx, 71, 16 + (i * 20));
        }

        for (int i = 0; i < 6; i++) {
            int col = i % 3;
            int row = i / 3;
            int[] xOffsets = {127, 148, 169};
            drawTankSlot(gfx, xOffsets[col], 75 + (row * 20));
        }

        gfx.fill(94, 38, 114, 49, 0xFF171019);
        gfx.fill(95, 39, 113, 48, 0xFF080808);
    }

    private static void drawSlot(GuiGraphics gfx, int x, int y) {
        drawPanel(gfx, x - 1, y - 1, x + 17, y + 17, 0xFF121212, 0xFFBDBDBD, 0xFF414141);
    }

    private static void drawTankSlot(GuiGraphics gfx, int x, int y) {
        drawPanel(gfx, x - 1, y - 1, x + 12, y + 15, 0xFF121212, 0xFFBDBDBD, 0xFF414141);
    }

    private static void drawPanel(GuiGraphics gfx, int left, int top, int right, int bottom, int fill, int light, int dark) {
        gfx.fill(left, top, right, bottom, dark);
        gfx.fill(left, top, right - 1, bottom - 1, light);
        gfx.fill(left + 1, top + 1, right - 1, bottom - 1, fill);
    }

    private void drawScaledCenteredString(GuiGraphics gfx, Font font, String text, int x, int y, int color, float scale) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        gfx.drawString(font, text, -font.width(text) / 2, 0, color, false);
        gfx.pose().popPose();
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, StellarSimulationRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseY >= 74 && mouseY <= 84 && mouseX >= 10 && mouseX <= 49) {
            if (!recipe.getFuelFluid().isEmpty() && recipe.getFuelAmount() > 0) {
                tooltip.add(Component.translatable("jei.ufo.stellar.fuel_required", getFuelDisplayName(recipe)));
                tooltip.add(Component.translatable("jei.ufo.stellar.amount_mb", formatAmount(recipe.getFuelAmount())));
                tooltip.add(Component.translatable("jei.ufo.stellar.fuel_from_me"));
            } else {
                tooltip.add(Component.translatable("jei.ufo.stellar.no_fuel"));
            }
            return;
        }

        if (mouseY >= 74 && mouseY <= 84 && mouseX >= 53 && mouseX <= 98) {
            if (recipe.getCoolantAmount() > 0) {
                tooltip.add(Component.translatable("jei.ufo.stellar.coolant_required", getCoolantDisplayName(recipe)));
                tooltip.add(Component.translatable("jei.ufo.stellar.coolant_tier", getCoolantDisplayTier(recipe)));
                tooltip.add(Component.translatable("jei.ufo.stellar.amount_mb", formatAmount(recipe.getCoolantAmount())));
                tooltip.add(Component.translatable("jei.ufo.stellar.coolant_desc"));
            } else {
                tooltip.add(Component.translatable("jei.ufo.stellar.cooling_level", recipe.getCoolingLevel()));
                tooltip.add(Component.translatable("jei.ufo.stellar.generic_coolant"));
            }
            return;
        }

        if (mouseY >= 86 && mouseY <= 96 && mouseX >= 10 && mouseX <= 49) {
            tooltip.add(Component.translatable("jei.ufo.stellar.duration", recipe.getFormattedTime(), recipe.getTime()));
            return;
        }

        if (mouseY >= 86 && mouseY <= 96 && mouseX >= 53 && mouseX <= 92) {
            tooltip.add(Component.translatable("jei.ufo.stellar.field_requirement", toRoman(recipe.getFieldTier())));
            return;
        }

        if (mouseY >= 98 && mouseY <= 108 && mouseX >= 32 && mouseX <= 71) {
            tooltip.add(Component.translatable("jei.ufo.stellar.total_energy", String.format(Locale.ROOT, "%,d", recipe.getTotalEnergy())));
            tooltip.add(Component.translatable("jei.ufo.stellar.energy_desc"));
            return;
        }

        if (mouseX >= CONTROLLER_X && mouseX <= CONTROLLER_X + 16 && mouseY >= CONTROLLER_Y && mouseY <= CONTROLLER_Y + 16) {
            tooltip.add(Component.translatable("jei.ufo.stellar.controller"));
            tooltip.add(Component.translatable("jei.ufo.stellar.controller_info"));
            return;
        }

        if (mouseX >= 94 && mouseX <= 114 && mouseY >= 38 && mouseY <= 49) {
            tooltip.add(Component.literal(recipe.getFormattedTime()));
            tooltip.add(Component.literal("(" + recipe.getTime() + " ticks)"));
            tooltip.add(Component.translatable("jei.ufo.stellar.outputs_me"));
        }
    }

    public static String formatAmount(long amount) {
        if (amount >= 1_000_000_000L) {
            double value = amount / 1_000_000_000.0;
            return value == (long) value ? (long) value + "G" : String.format(Locale.ROOT, "%.1fG", value);
        }
        if (amount >= 1_000_000L) {
            double value = amount / 1_000_000.0;
            return value == (long) value ? (long) value + "M" : String.format(Locale.ROOT, "%.1fM", value);
        }
        if (amount >= 1_000L) {
            double value = amount / 1_000.0;
            return value == (long) value ? (long) value + "K" : String.format(Locale.ROOT, "%.1fK", value);
        }
        return String.valueOf(amount);
    }

    private static String formatFluidName(String path) {
        if (path.startsWith("source_")) {
            path = path.substring(7);
        }
        if (path.startsWith("flowing_")) {
            path = path.substring(8);
        }

        String[] words = path.split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return builder.toString().trim();
    }

    private static String getFuelDisplayShortName(StellarSimulationRecipe recipe) {
        if (recipe.getFuelFluid().isEmpty()) {
            return "None";
        }
        return abbreviateFluidName(getFuelDisplayName(recipe), 10);
    }

    private static String getFuelDisplayName(StellarSimulationRecipe recipe) {
        if (recipe.getFuelFluid().isEmpty()) {
            return "None";
        }
        return getFluidDisplayName(ResourceLocation.parse(recipe.getFuelFluid()));
    }

    private static String getCoolantDisplayTier(StellarSimulationRecipe recipe) {
        int tier = recipe.getCoolingLevel();
        return tier <= 0 ? "None" : "MK" + Math.min(3, tier);
    }

    private static String getCoolantDisplayName(StellarSimulationRecipe recipe) {
        return switch (recipe.getCoolingLevel()) {
            case 1 -> getFluidDisplayName(ResourceLocation.parse("chaosworld_core:source_gelid_cryotheum"));
            case 2 -> getFluidDisplayName(ResourceLocation.parse("chaosworld_core:source_stable_coolant"));
            case 3 -> getFluidDisplayName(ResourceLocation.parse("chaosworld_core:source_temporal_fluid"));
            default -> "None";
        };
    }

    private static String getFluidDisplayName(ResourceLocation fluidId) {
        var fluid = BuiltInRegistries.FLUID.getOptional(fluidId).orElse(null);
        if (fluid == null) {
            return formatFluidName(fluidId.getPath());
        }

        String hoverName = new FluidStack(fluid, 1).getHoverName().getString();
        return hoverName == null || hoverName.isBlank() ? formatFluidName(fluidId.getPath()) : hoverName;
    }

    private static String abbreviateFluidName(String fullName, int maxLength) {
        if (fullName == null || fullName.isBlank()) {
            return "None";
        }
        if (fullName.length() <= maxLength) {
            return fullName;
        }

        String[] words = fullName.trim().split("\\s+");
        if (words.length > 1) {
            StringBuilder initials = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    initials.append(Character.toUpperCase(word.charAt(0)));
                }
            }
            if (!initials.isEmpty()) {
                return initials.toString();
            }
        }

        return fullName.substring(0, Math.max(1, maxLength - 1)).toUpperCase(Locale.ROOT) + ".";
    }

    private static String toRoman(int tier) {
        return switch (tier) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> String.valueOf(tier);
        };
    }
}

package com.yongaishide.chaosworld.compat.jei;

import com.yongaishide.chaosworld.ChaosWorld;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * JEI diagram page for the custom wither summoning structure:
 * <pre>
 *  头 头 头    <- 3 wither skeleton skulls
 *  沙 立方 沙  <- middle of the row is avaritia:blaze_cube_block
 *     沙       <- soul sand / soul soil (bottom center)
 * </pre>
 * Right-click any structure block with a Cryptid Core to summon a 4000 HP wither.
 */
public class WitherSummonRecipeCategory implements IRecipeCategory<WitherSummonInfo> {

    public static final RecipeType<WitherSummonInfo> RECIPE_TYPE =
            RecipeType.create("chaosworld_core", "wither_summon", WitherSummonInfo.class);

    private static final int WIDTH = 140;
    private static final int HEIGHT = 116;
    private static final ResourceLocation BLAZE_CUBE_ID = ResourceLocation.parse("avaritia:blaze_cube_block");

    private final IDrawable icon;
    private final IDrawable background;

    public WitherSummonRecipeCategory(IJeiHelpers helpers) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.WITHER_SKELETON_SKULL));
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
    }

    @Override
    public RecipeType<WitherSummonInfo> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.ufo.wither_summon.title");
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
    public void setRecipe(IRecipeLayoutBuilder builder, WitherSummonInfo recipe, IFocusGroup focuses) {
        ItemStack soul = new ItemStack(Blocks.SOUL_SAND);
        ItemStack blazeCube = blazeCubeStack();
        ItemStack skull = new ItemStack(Items.WITHER_SKELETON_SKULL);

        // Structure slots (side view)
        builder.addInputSlot(42, 14).addItemStack(skull).addRichTooltipCallback(this::skullTooltip);
        builder.addInputSlot(60, 14).addItemStack(skull).addRichTooltipCallback(this::skullTooltip);
        builder.addInputSlot(78, 14).addItemStack(skull).addRichTooltipCallback(this::skullTooltip);

        builder.addInputSlot(42, 32).addItemStack(soul).addRichTooltipCallback(this::soulTooltip);
        builder.addInputSlot(60, 32).addItemStack(blazeCube).addRichTooltipCallback(this::blazeCubeTooltip);
        builder.addInputSlot(78, 32).addItemStack(soul).addRichTooltipCallback(this::soulTooltip);

        builder.addInputSlot(60, 50).addItemStack(soul).addRichTooltipCallback(this::soulTooltip);

        // Cryptid Core input
        builder.addInputSlot(6, 32).addItemStack(new ItemStack(ChaosWorld.CRYPTID_CORE.get()))
                .addRichTooltipCallback(this::coreTooltip);
    }

    private void skullTooltip(mezz.jei.api.gui.ingredient.IRecipeSlotView view, ITooltipBuilder tooltip) {
        tooltip.add(Component.translatable("jei.ufo.wither_summon.skull"));
    }

    private void soulTooltip(mezz.jei.api.gui.ingredient.IRecipeSlotView view, ITooltipBuilder tooltip) {
        tooltip.add(Component.translatable("jei.ufo.wither_summon.soul"));
    }

    private void blazeCubeTooltip(mezz.jei.api.gui.ingredient.IRecipeSlotView view, ITooltipBuilder tooltip) {
        tooltip.add(Component.translatable("jei.ufo.wither_summon.blaze_cube"));
    }

    private void coreTooltip(mezz.jei.api.gui.ingredient.IRecipeSlotView view, ITooltipBuilder tooltip) {
        tooltip.add(Component.translatable("jei.ufo.wither_summon.core"));
    }

    private static ItemStack blazeCubeStack() {
        var block = BuiltInRegistries.BLOCK.get(BLAZE_CUBE_ID);
        return block != null && block != Blocks.AIR ? new ItemStack(block) : new ItemStack(Blocks.SOUL_SAND);
    }

    @Override
    public void draw(WitherSummonInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gfx, double mouseX, double mouseY) {
        drawPanel(gfx, 0, 0, WIDTH, HEIGHT, 0xFFC8C8C8, 0xFFFFFFFF, 0xFF6A6A6A);
        Font font = Minecraft.getInstance().font;

        gfx.drawCenteredString(font, Component.translatable("jei.ufo.wither_summon.title").getString(),
                WIDTH / 2 + 20, 2, 0x333333);

        gfx.drawCenteredString(font, Component.translatable("jei.ufo.wither_summon.hint").getString(),
                WIDTH / 2, 74, 0x555555);
        gfx.drawCenteredString(font, Component.translatable("jei.ufo.wither_summon.hint2").getString(),
                WIDTH / 2, 84, 0x555555);
        gfx.drawCenteredString(font, Component.translatable("jei.ufo.wither_summon.hint3").getString(),
                WIDTH / 2, 94, 0x555555);
    }

    private static void drawPanel(GuiGraphics gfx, int left, int top, int width, int height, int border, int fill, int dark) {
        gfx.fill(left, top, left + width, top + height, border);
        gfx.fill(left + 1, top + 1, left + width - 1, top + height - 1, fill);
        gfx.fill(left + 3, top + 3, left + width - 3, top + height - 3, dark);
    }
}

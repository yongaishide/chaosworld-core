package com.yongaishide.chaosworld.screen;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.SlotSemantics;
import com.mojang.blaze3d.systems.RenderSystem;
import com.yongaishide.chaosworld.api.multiblock.IMultiblockController;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinition;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinitions;
import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import com.yongaishide.chaosworld.block.entity.UniversalDisplayedRecipe;
import com.yongaishide.chaosworld.client.render.StructureHighlightRenderer;
import com.yongaishide.chaosworld.network.ModPackets;
import com.yongaishide.chaosworld.network.packet.PacketAutoAssembleStructure;
import com.yongaishide.chaosworld.network.packet.PacketScanUniversalStructure;
import com.yongaishide.chaosworld.network.packet.PacketToggleUniversalOverclock;
import com.yongaishide.chaosworld.network.packet.PacketToggleUniversalSafeMode;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractUniversalMultiblockControllerScreen<M extends AbstractUniversalMultiblockControllerMenu<?>>
        extends UpgradeableScreen<M> {

    private Button safeModeButton;
    private Button overclockButton;
    private Button scanButton;
    private Button autoAssembleButton;
    private List<UniversalDisplayedRecipe> cachedDisplayedRecipes = List.of();
    private List<GroupedRecipe> cachedGroupedRecipes = List.of();
    private int cachedRecipeSignature = 0;

    protected AbstractUniversalMultiblockControllerScreen(M menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.inventoryLabelY = 1000;
        this.titleLabelY = 1000;
    }

    @Override
    protected void init() {
        super.init();
        setSlotsHidden(SlotSemantics.PLAYER_INVENTORY, true);
        setSlotsHidden(SlotSemantics.PLAYER_HOTBAR, true);

        this.scanButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.ufo.universal.scan"), btn -> {
                    BlockPos pos = this.menu.getBlockEntity().getBlockPos();
                    ModPackets.sendToServer(new PacketScanUniversalStructure(pos));
                    runLocalStructureScan(pos);
                })
                .bounds(this.leftPos + this.imageWidth - 98, this.topPos + this.imageHeight - 24, 42, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.ufo.scan_button_tooltip")))
                .build());

        this.autoAssembleButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.ufo.universal.auto_assemble"), btn ->
                        ModPackets.sendToServer(new PacketAutoAssembleStructure(this.menu.getBlockEntity().getBlockPos())))
                .bounds(this.leftPos + this.imageWidth - 125, this.topPos + this.imageHeight - 24, 25, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.ufo.universal.auto_assemble_tooltip")))
                .build());

        this.safeModeButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.ufo.universal.safe_mode"), btn ->
                        ModPackets.sendToServer(new PacketToggleUniversalSafeMode(this.menu.getBlockEntity().getBlockPos())))
                .bounds(this.leftPos + this.imageWidth - 54, this.topPos + this.imageHeight - 24, 25, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.ufo.universal.toggle_safe_mode")))
                .build());

        this.overclockButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.ufo.universal.overclock"), btn ->
                        ModPackets.sendToServer(new PacketToggleUniversalOverclock(this.menu.getBlockEntity().getBlockPos())))
                .bounds(this.leftPos + this.imageWidth - 27, this.topPos + this.imageHeight - 24, 25, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.ufo.universal.toggle_overclock")))
                .build());
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTick) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTick);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderTemperatureBar(guiGraphics);
        renderRecipeList(guiGraphics);
    }

    private void renderTemperatureBar(GuiGraphics guiGraphics) {
        int barX = this.leftPos + 14;
        int barY = this.topPos + 9;
        int barWidth = 146;
        int barHeight = 10;
        int filled = (int) (barWidth * Math.min(1.0F, this.menu.getTemperature() / (float) this.menu.getMaxTemperature()));

        if (filled > 0) {
            guiGraphics.fill(barX, barY, barX + filled, barY + barHeight, 0xCCB32020);
        }

        Component tempText = Component.translatable("gui.ufo.universal.temperature_bar",
                this.menu.getTemperature(), this.menu.getMaxTemperature());
        int textWidth = this.font.width(tempText);
        guiGraphics.drawString(this.font, tempText, barX + (barWidth - textWidth) / 2, barY + 1, 0xFFFFFF, true);
    }

    private void renderRecipeList(GuiGraphics guiGraphics) {
        int listX = this.leftPos + 7;
        int listY = this.topPos + 30;
        int lineHeight = 10;
        int maxTextWidth = 156;
        List<GroupedRecipe> recipes = buildGroupedRecipes();

        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(getScreenTitle().getString(), maxTextWidth), listX, listY, 0xF0F0F0, false);
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(buildStatusLine(), maxTextWidth), listX, listY + 10, 0xD0D7E6, false);
        renderLine(guiGraphics, buildEnergyComponent(), listX, listY + 20, maxTextWidth, 0xB9D8FF);
        renderLine(guiGraphics, buildInfoComponent(recipes), listX, listY + 30, maxTextWidth, 0xC7D3E8);
        if (this.menu.getOverloadTimer() > 0) {
            renderLine(guiGraphics, Component.translatable("gui.ufo.universal.status.overload", (this.menu.getOverloadTimer() + 19) / 20), listX, listY + 40, maxTextWidth, 0xFF5555);
        } else {
            renderLine(guiGraphics, buildOutputComponent(recipes), listX, listY + 40, maxTextWidth, 0x9AB8E0);
        }

        for (int i = 0; i < 7; i++) {
            int rowY = listY + 52 + i * lineHeight;
            if (i < recipes.size()) {
                renderRecipeRow(guiGraphics, recipes.get(i), listX, rowY);
            } else if (i == 0 && recipes.isEmpty()) {
                Component emptyText = Component.translatable(this.menu.isAssembled()
                        ? "gui.ufo.universal.no_recipes"
                        : "gui.ufo.universal.structure_incomplete");
                guiGraphics.drawString(this.font, emptyText, listX, rowY, 0x8A91A6, false);
            }
        }
    }

    private void renderRecipeRow(GuiGraphics guiGraphics, GroupedRecipe groupedRecipe, int x, int y) {
        UniversalDisplayedRecipe recipe = groupedRecipe.recipe();
        ItemStack iconStack = recipe.itemIcon();
        boolean fluidRecipe = false;
        if (iconStack.isEmpty()) {
            FluidStack fluid = recipe.fluidIcon();
            if (!fluid.isEmpty()) {
                iconStack = new ItemStack(fluid.getFluid().getBucket());
                fluidRecipe = true;
            }
        }

        int textX = x;
        if (!iconStack.isEmpty()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x, y - 1, 0.0F);
            guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
            guiGraphics.renderItem(iconStack, 0, 0);
            guiGraphics.pose().popPose();
            textX += 10;
        }

        String amount = fluidRecipe ? formatAmount(groupedRecipe.totalOutputAmount()) + "mB" : formatAmount(groupedRecipe.totalOutputAmount()) + "x";
        String time = groupedRecipe.displayMaxProgress() > 0
                ? formatSeconds(groupedRecipe.displayProgress()) + "/" + formatSeconds(groupedRecipe.displayMaxProgress()) + "s"
                : "-/-";
        int timeWidth = this.font.width(time);
        int availableWidth = Math.max(20, 156 - (textX - x) - timeWidth - 4);
        String leftText = amount + " " + recipe.label().getString();
        if (groupedRecipe.copyCount() > 1) {
            leftText += " [" + groupedRecipe.copyCount() + "]";
        }
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(leftText, availableWidth), textX, y, 0xE6EBF5, false);
        guiGraphics.drawString(this.font, time, this.leftPos + 162 - timeWidth, y, 0xB9D8FF, false);
    }

    private String buildStatusLine() {
        if (!this.menu.isAssembled()) {
            return Component.translatable("gui.ufo.universal.status.incomplete").getString();
        }
        StringBuilder builder = new StringBuilder(Component.translatable(
                this.menu.isRunning() ? "gui.ufo.universal.status.run" : "gui.ufo.universal.status.idle").getString());
        builder.append(" | MK").append(this.menu.getMachineTier());
        builder.append(" | ").append(Component.translatable(
                this.menu.isSafeMode() ? "gui.ufo.universal.status.safe" : "gui.ufo.universal.status.risk").getString());
        builder.append(" | ").append(Component.translatable(
                this.menu.isOverclocked() ? "gui.ufo.universal.status.oc" : "gui.ufo.universal.status.std").getString());
        builder.append(" | ").append(Component.translatable(
                this.menu.isAeConnected() ? "gui.ufo.universal.status.ae_connected" : "gui.ufo.universal.status.ae_disconnected").getString());
        return builder.toString();
    }

    private void renderLine(GuiGraphics guiGraphics, Component text, int x, int y, int maxTextWidth, int color) {
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(text.getString(), maxTextWidth), x, y, color, false);
    }

    private Component buildEnergyComponent() {
        return Component.translatable("gui.ufo.universal.energy_full",
                formatAmount(this.menu.getStoredEnergy()),
                formatAmount(this.menu.getMaxEnergy()));
    }

    private Component buildInfoComponent(List<GroupedRecipe> recipes) {
        return Component.translatable("gui.ufo.universal.info",
                this.menu.getActiveParallels(),
                this.menu.getMaxParallels(),
                recipes.size());
    }

    private Component buildOutputComponent(List<GroupedRecipe> recipes) {
        long totalOutput = 0L;
        double totalPerSecond = 0.0D;
        for (GroupedRecipe recipe : recipes) {
            totalOutput += recipe.totalOutputAmount();
            if (recipe.displayMaxProgress() > 0) {
                totalPerSecond += recipe.totalOutputAmount() * 20.0D / recipe.displayMaxProgress();
            }
        }
        int repeated = Math.max(0, this.menu.getActiveParallels() - recipes.size());
        return Component.translatable("gui.ufo.universal.details",
                formatAmount((long) totalPerSecond),
                formatAmount(totalOutput),
                repeated);
    }

    private Component getScreenTitle() {
        String raw = this.title.getString();
        if (raw.endsWith(" Controller")) {
            raw = raw.substring(0, raw.length() - " Controller".length());
        }
        return Component.literal(raw);
    }

    private static String formatAmount(long amount) {
        if (amount >= 1_000_000_000L) {
            return String.format(Locale.ROOT, "%.1fB", amount / 1_000_000_000.0);
        }
        if (amount >= 1_000_000L) {
            return String.format(Locale.ROOT, "%.1fM", amount / 1_000_000.0);
        }
        if (amount >= 1_000L) {
            return String.format(Locale.ROOT, "%.1fK", amount / 1_000.0);
        }
        return Long.toString(amount);
    }

    private static String formatSeconds(int ticks) {
        double seconds = ticks / 20.0;
        return seconds >= 100
                ? String.format(Locale.ROOT, "%.0f", seconds)
                : String.format(Locale.ROOT, "%.1f", seconds);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        refreshRecipeCache();
        if (this.safeModeButton != null) {
            boolean safe = this.menu.isSafeMode();
            this.safeModeButton.setMessage(Component.translatable(safe ? "gui.ufo.universal.status.safe" : "gui.ufo.universal.status.risk"));
            this.safeModeButton.setTooltip(Tooltip.create(Component.translatable(safe ? "gui.ufo.universal.safe_mode_enabled" : "gui.ufo.universal.safe_mode_disabled")));
            this.safeModeButton.active = !this.menu.isRunning() && this.menu.getDisplayedRecipes().isEmpty();
        }
        if (this.overclockButton != null) {
            boolean oc = this.menu.isOverclocked();
            this.overclockButton.setMessage(Component.translatable(oc ? "gui.ufo.universal.status.oc" : "gui.ufo.universal.status.std"));
            this.overclockButton.setTooltip(Tooltip.create(Component.translatable(oc ? "gui.ufo.universal.overclock_enabled" : "gui.ufo.universal.overclock_disabled")));
            this.overclockButton.active = !this.menu.isRunning() && this.menu.getDisplayedRecipes().isEmpty();
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovering(14, 9, 146, 10, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font,
                    Component.translatable("gui.ufo.universal.temperature", this.menu.getTemperature(), this.menu.getMaxTemperature()),
                    mouseX, mouseY);
            return;
        }
        GroupedRecipe hoveredRecipe = getHoveredGroupedRecipe(mouseX, mouseY);
        if (hoveredRecipe != null) {
            guiGraphics.renderTooltip(this.font, buildGroupedRecipeTooltip(hoveredRecipe).stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
            return;
        }
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private List<GroupedRecipe> buildGroupedRecipes() {
        refreshRecipeCache();
        return this.cachedGroupedRecipes;
    }

    private void refreshRecipeCache() {
        List<UniversalDisplayedRecipe> displayedRecipes = this.menu.getDisplayedRecipes();
        int signature = computeRecipeSignature(displayedRecipes);
        if (signature == this.cachedRecipeSignature && displayedRecipes.size() == this.cachedDisplayedRecipes.size()) {
            return;
        }

        this.cachedDisplayedRecipes = displayedRecipes;
        this.cachedRecipeSignature = signature;

        Map<RecipeGroupKey, GroupAccumulator> groups = new LinkedHashMap<>();
        for (UniversalDisplayedRecipe recipe : displayedRecipes) {
            RecipeGroupKey key = RecipeGroupKey.of(recipe);
            groups.computeIfAbsent(key, ignored -> new GroupAccumulator(recipe)).add(recipe);
        }

        List<GroupedRecipe> groupedRecipes = new ArrayList<>();
        for (GroupAccumulator accumulator : groups.values()) {
            groupedRecipes.add(accumulator.toGroupedRecipe());
        }
        this.cachedGroupedRecipes = groupedRecipes;
    }

    private static int computeRecipeSignature(List<UniversalDisplayedRecipe> recipes) {
        int signature = 1;
        for (UniversalDisplayedRecipe recipe : recipes) {
            signature = 31 * signature + recipe.progress();
            signature = 31 * signature + recipe.maxProgress();
            signature = 31 * signature + Long.hashCode(recipe.outputAmount());
            signature = 31 * signature + recipe.label().getString().hashCode();
            signature = 31 * signature + Objects.hashCode(BuiltInRegistries.ITEM.getKey(recipe.itemIcon().getItem()));
            signature = 31 * signature + Objects.hashCode(BuiltInRegistries.FLUID.getKey(recipe.fluidIcon().getFluid()));
        }
        return signature;
    }

    private GroupedRecipe getHoveredGroupedRecipe(int mouseX, int mouseY) {
        int listX = this.leftPos + 7;
        int listY = this.topPos + 30;
        int lineHeight = 10;
        List<GroupedRecipe> recipes = buildGroupedRecipes();
        for (int i = 0; i < Math.min(7, recipes.size()); i++) {
            int rowY = listY + 52 + i * lineHeight;
            if (mouseX >= listX && mouseX < listX + 156 && mouseY >= rowY && mouseY < rowY + lineHeight) {
                return recipes.get(i);
            }
        }
        return null;
    }

    private List<Component> buildGroupedRecipeTooltip(GroupedRecipe groupedRecipe) {
        UniversalDisplayedRecipe recipe = groupedRecipe.recipe();
        List<Component> lines = new ArrayList<>();
        lines.add(recipe.label());
        lines.add(Component.translatable("gui.ufo.universal.parallel_copies", groupedRecipe.copyCount()));
        lines.add(Component.translatable(
                recipe.fluidIcon().isEmpty() ? "gui.ufo.universal.total_output" : "gui.ufo.universal.total_output_mb",
                formatAmount(groupedRecipe.totalOutputAmount())));
        lines.add(Component.translatable("gui.ufo.universal.displayed_time",
                formatSeconds(groupedRecipe.displayProgress()), formatSeconds(groupedRecipe.displayMaxProgress())));
        if (groupedRecipe.hasMixedProgress()) {
            lines.add(Component.translatable("gui.ufo.universal.progress_spread",
                    formatSeconds(groupedRecipe.minProgress()), formatSeconds(groupedRecipe.maxProgress())));
        }
        return lines;
    }

    private record GroupedRecipe(
            UniversalDisplayedRecipe recipe,
            int copyCount,
            long totalOutputAmount,
            int displayProgress,
            int displayMaxProgress,
            int minProgress,
            int maxProgress,
            boolean hasMixedProgress) {
    }

    private record RecipeGroupKey(String itemId, String fluidId, String label, long outputAmount, int maxProgress) {
        private static RecipeGroupKey of(UniversalDisplayedRecipe recipe) {
            String itemId = recipe.itemIcon().isEmpty()
                    ? ""
                    : String.valueOf(BuiltInRegistries.ITEM.getKey(recipe.itemIcon().getItem()));
            String fluidId = recipe.fluidIcon().isEmpty()
                    ? ""
                    : String.valueOf(BuiltInRegistries.FLUID.getKey(recipe.fluidIcon().getFluid()));
            return new RecipeGroupKey(itemId, fluidId, recipe.label().getString(), recipe.outputAmount(), recipe.maxProgress());
        }
    }

    private static final class GroupAccumulator {
        private final UniversalDisplayedRecipe representative;
        private int count;
        private long totalOutput;
        private int totalProgress;
        private int minProgress = Integer.MAX_VALUE;
        private int maxProgress;

        private GroupAccumulator(UniversalDisplayedRecipe representative) {
            this.representative = representative;
        }

        private void add(UniversalDisplayedRecipe recipe) {
            this.count++;
            this.totalOutput += recipe.outputAmount();
            this.totalProgress += recipe.progress();
            this.minProgress = Math.min(this.minProgress, recipe.progress());
            this.maxProgress = Math.max(this.maxProgress, recipe.progress());
        }

        private GroupedRecipe toGroupedRecipe() {
            int averageProgress = this.count == 0 ? 0 : this.totalProgress / this.count;
            return new GroupedRecipe(
                    this.representative,
                    this.count,
                    this.totalOutput,
                    averageProgress,
                    this.representative.maxProgress(),
                    this.minProgress == Integer.MAX_VALUE ? 0 : this.minProgress,
                    this.maxProgress,
                    this.minProgress != this.maxProgress);
        }
    }

    private void runLocalStructureScan(BlockPos pos) {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return;
        }

        var blockEntity = this.minecraft.level.getBlockEntity(pos);
        if (!(blockEntity instanceof IMultiblockController controller)) {
            return;
        }

        var definition = MultiblockControllerDefinitions.getDefinition(blockEntity);
        if (definition.isEmpty()) {
            return;
        }

        var state = this.minecraft.level.getBlockState(pos);
        Direction facing = MultiblockControllerDefinitions.getPatternFacing(blockEntity, state);

        MultiblockPattern.MatchResult result = definition.get().pattern().match(this.minecraft.level, pos, facing);
        if (result.isValid()) {
            if (controller.isAssembled()) {
                this.minecraft.player.displayClientMessage(
                        Component.translatable("message.ufo.structure_formed").withStyle(ChatFormatting.GREEN), true);
            } else {
                this.minecraft.player.displayClientMessage(
                        definition.get().name().copy().append(Component.translatable("gui.ufo.universal.extra_validation_failed")
                                .withStyle(ChatFormatting.RED)),
                        false);
            }
            return;
        }

        reportStructureErrors(definition.get(), result.allErrors());
    }

    private void reportStructureErrors(MultiblockControllerDefinition definition, List<MultiblockPattern.PatternError> errors) {
        if (this.minecraft == null || this.minecraft.player == null || errors == null || errors.isEmpty()) {
            return;
        }

        int shown = Math.min(errors.size(), 10);
        this.minecraft.player.displayClientMessage(
                definition.name().copy()
                        .append(Component.translatable("gui.ufo.universal.blocks_missing", errors.size()).withStyle(ChatFormatting.RED)),
                false);

        for (int i = 0; i < shown; i++) {
            var error = errors.get(i);
            BlockPos errorPos = error.pos();
            Component message = Component.translatable("gui.ufo.universal.expected_block", errorPos.getX(), errorPos.getY(), errorPos.getZ())
                    .withStyle(ChatFormatting.GRAY)
                    .append(error.expected().copy().withStyle(ChatFormatting.YELLOW));
            this.minecraft.player.displayClientMessage(message, false);
        }

        if (errors.size() > shown) {
            this.minecraft.player.displayClientMessage(
                    Component.translatable("gui.ufo.universal.and_more", errors.size() - shown).withStyle(ChatFormatting.GRAY),
                    false);
        }

        int maxHighlight = Math.min(errors.size(), 50);
        for (int i = 0; i < maxHighlight; i++) {
            StructureHighlightRenderer.highlight(errors.get(i).pos(), 5000);
        }
    }
}

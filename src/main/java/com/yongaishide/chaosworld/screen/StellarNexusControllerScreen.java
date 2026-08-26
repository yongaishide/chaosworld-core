package com.yongaishide.chaosworld.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yongaishide.chaosworld.init.ModRecipes;
import com.yongaishide.chaosworld.network.ModPackets;
import com.yongaishide.chaosworld.network.packet.PacketChangeStellarRecipe;
import com.yongaishide.chaosworld.network.packet.PacketScanStellarStructure;
import com.yongaishide.chaosworld.network.packet.PacketStartStellarOperation;
import com.yongaishide.chaosworld.network.packet.PacketToggleStellarSafeMode;
import com.yongaishide.chaosworld.network.packet.PacketToggleStellarAutoStart;
import com.yongaishide.chaosworld.network.packet.PacketToggleStellarLock;
import com.yongaishide.chaosworld.network.packet.PacketToggleStellarOverclock;
import com.yongaishide.chaosworld.recipe.StellarSimulationRecipe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;

public class StellarNexusControllerScreen extends AbstractContainerScreen<StellarNexusControllerMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "textures/gui/stellar_nexus_controller.png");

    private List<RecipeHolder<StellarSimulationRecipe>> availableRecipes = new ArrayList<>();
    private int currentRecipeIndex = 0;
    
    private Button prevButton;
    private Button nextButton;
    private Button startButton;
    private Button safeModeButton;
    private Button scanButton;
    private Button autoStartButton;
    private Button lockButton;
    private Button overclockButton;

    public StellarNexusControllerScreen(StellarNexusControllerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 191;
        this.imageHeight = 160;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000; // hide player inventory label
        this.titleLabelY = 8;
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

        if (this.minecraft != null && this.minecraft.level != null) {
            this.availableRecipes = new ArrayList<>(this.minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipes.STELLAR_SIMULATION_TYPE.get()));
            
            // Persist: align current index with BE's active recipe (remembered from last session)
            ResourceLocation activeId = this.menu.getBlockEntity().getActiveRecipeId();
            if (activeId != null) {
                for (int i = 0; i < this.availableRecipes.size(); i++) {
                    if (this.availableRecipes.get(i).id().equals(activeId)) {
                        this.currentRecipeIndex = i;
                        break;
                    }
                }
            }
        }

        int recipeY = this.topPos + 64;
        this.prevButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.ufo.stellar.prev_recipe"), btn -> cycleRecipe(-1))
                .bounds(this.leftPos + 19, recipeY, 22, 22).build());

        this.nextButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.ufo.stellar.next_recipe"), btn -> cycleRecipe(1))
                .bounds(this.leftPos + 150, recipeY, 22, 22).build());
        
        // Start Operation button
        this.startButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.ufo.stellar.start"), btn -> {
                    ModPackets.sendToServer(new PacketStartStellarOperation(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos + 70, this.topPos + 126, 52, 16).build());

        // Safe Mode toggle
        this.safeModeButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.ufo.stellar.safe_mode"), btn -> {
                    ModPackets.sendToServer(new PacketToggleStellarSafeMode(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 54, 50, 16).build());

        // Auto Start toggle
        this.autoStartButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.ufo.stellar.auto_start"), btn -> {
                    ModPackets.sendToServer(new PacketToggleStellarAutoStart(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 72, 50, 16).build());

        // Lock Simulation toggle
        this.lockButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.ufo.stellar.lock"), btn -> {
                    ModPackets.sendToServer(new PacketToggleStellarLock(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 36, 50, 16).build());

        // Overclock toggle
        this.overclockButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.ufo.stellar.overclock"), btn -> {
                    ModPackets.sendToServer(new PacketToggleStellarOverclock(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 90, 50, 16).build());

        // Scan Structure button
        this.scanButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.ufo.stellar.scan"), btn -> {
                    ModPackets.sendToServer(new PacketScanStellarStructure(this.menu.getBlockEntity().getBlockPos()));
                    
                    if (this.minecraft != null && this.minecraft.level != null && this.minecraft.player != null) {
                        BlockPos pos = this.menu.getBlockEntity().getBlockPos();
                        net.minecraft.world.level.block.state.BlockState state = this.minecraft.level.getBlockState(pos);
                        net.minecraft.core.Direction facing = net.minecraft.core.Direction.NORTH;
                        if (state.hasProperty(net.minecraft.world.level.block.DirectionalBlock.FACING)) {
                            facing = state.getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);
                        }
                        com.yongaishide.chaosworld.api.multiblock.MultiblockPattern.MatchResult result = com.yongaishide.chaosworld.block.entity.pattern.StellarNexusPatternFactory.getPattern().match(this.minecraft.level, pos, facing);
                        
                        if (!result.isValid()) {
                            java.util.List<com.yongaishide.chaosworld.api.multiblock.MultiblockPattern.PatternError> errors = result.allErrors();
                            if (errors != null && !errors.isEmpty()) {
                                int shown = Math.min(errors.size(), 10);
                                this.minecraft.player.displayClientMessage(Component.translatable("gui.ufo.stellar.scan_errors", errors.size()), false);
                                for (int i = 0; i < shown; i++) {
                                    var error = errors.get(i);
                                    BlockPos errorPos = error.pos();
                                    Component message = Component.translatable("gui.ufo.stellar.error_pos", errorPos.getX(), errorPos.getY(), errorPos.getZ())
                                            .append(error.expected().copy().withStyle(net.minecraft.ChatFormatting.YELLOW));
                                    this.minecraft.player.displayClientMessage(message, false);
                                }
                                if (errors.size() > shown) {
                                    this.minecraft.player.displayClientMessage(Component.translatable("gui.ufo.stellar.scan_more_errors", (errors.size() - shown)), false);
                                }
                                int maxHighlight = Math.min(errors.size(), 50);
                                for (int i = 0; i < maxHighlight; i++) {
                                    com.yongaishide.chaosworld.client.render.StructureHighlightRenderer.highlight(errors.get(i).pos(), 5000);
                                }
                            }
                        } else if (!this.menu.getBlockEntity().isAssembled()) {
                             this.minecraft.player.displayClientMessage(Component.translatable("gui.ufo.stellar.hatch_requirements"), false);
                             this.minecraft.player.displayClientMessage(Component.translatable("gui.ufo.stellar.hatch_requirements_detail"), false);
                        } else {
                            this.minecraft.player.displayClientMessage(Component.translatable("message.ufo.structure_formed").withStyle(net.minecraft.ChatFormatting.GREEN), true);
                        }
                    }
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 18, 50, 16).build());
        this.scanButton.setTooltip(Tooltip.create(Component.translatable("gui.ufo.scan_button_tooltip")));

        updateButtonTooltips();
    }

    private void updateButtonTooltips() {
        if (this.availableRecipes.isEmpty() || this.prevButton == null || this.nextButton == null) return;
        
        int prevIndex = (this.currentRecipeIndex - 1 + this.availableRecipes.size()) % this.availableRecipes.size();
        int nextIndex = (this.currentRecipeIndex + 1) % this.availableRecipes.size();
        
        Component prevComp = Component.translatable("gui.ufo.previous").append(": ").append(getRecipeDisplayName(this.availableRecipes.get(prevIndex)));
        Component nextComp = Component.translatable("gui.ufo.next").append(": ").append(getRecipeDisplayName(this.availableRecipes.get(nextIndex)));
        
        this.prevButton.setTooltip(Tooltip.create(prevComp));
        this.nextButton.setTooltip(Tooltip.create(nextComp));
        
        // Update start button state with detailed checklist
        if (this.startButton != null) {
            boolean canStart = this.menu.isAssembled() && !this.menu.isRunning() && this.menu.getCooldownTimer() == 0;
            
            StellarSimulationRecipe recipe = this.availableRecipes.isEmpty() ? null : this.availableRecipes.get(this.currentRecipeIndex).value();

            List<Component> tipLines = new ArrayList<>();
            tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.start_title"));
            
            if (recipe != null) {
                // Structure check
                if (this.menu.isAssembled()) {
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.assembled"));
                } else {
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.not_assembled"));
                    canStart = false;
                }
                
                // Running check
                if (this.menu.isRunning()) {
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.already_running"));
                    canStart = false;
                }
                
                // Cooldown check
                if (this.menu.getCooldownTimer() > 0) {
                    int secLeft = this.menu.getCooldownTimer() / 20;
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.cooling_down", secLeft));
                    canStart = false;
                }
                
                // Field tier check
                int fl = this.menu.getFieldLevel();
                if (fl >= recipe.getFieldTier()) {
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.field_ok", toRoman(fl), toRoman(recipe.getFieldTier())));
                } else {
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.field_low", toRoman(fl), toRoman(recipe.getFieldTier())));
                    canStart = false;
                }
                
                // Energy check
                long eBuffer = this.menu.getEnergyBuffer();
                double multiplier = this.menu.isOverclocked() ? 10.0 : 1.0;
                long eCost = (long)(recipe.getEnergyCost() * multiplier);
                String safeNote = this.menu.isOverclocked() ? " (10x O.C.)" : "";
                if (eBuffer >= eCost) {
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.energy_ok", formatAmount(eBuffer), formatAmount(eCost), safeNote));
                } else {
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.energy_low", formatAmount(eBuffer), formatAmount(eCost), safeNote));
                    canStart = false;
                }
                
                if (recipe.getFuelAmount() > 0 && !recipe.getFuelFluid().isEmpty()) {
                    ResourceLocation fRL = ResourceLocation.parse(recipe.getFuelFluid());
                    double fMult = this.menu.isOverclocked() ? 5.0 : 1.0;
                    long fAmount = (long)(recipe.getFuelAmount() * fMult);
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.fuel_required", formatAmount(fAmount), getFluidDisplayName(fRL)));
                }
                if (recipe.getCoolantAmount() > 0) {
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.coolant_required", formatAmount(recipe.getCoolantAmount()), recipe.getCoolingLevel()));
                }
                if (!recipe.getItemInputs().isEmpty() || !recipe.getFluidInputs().isEmpty()) {
                    tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.items_required"));
                }
            } else {
                tipLines.add(Component.translatable("gui.ufo.stellar.tooltip.no_program"));
                canStart = false;
            }
            
            this.startButton.active = canStart;
            
            // Build tooltip
            Component combined = tipLines.get(0);
            for (int i = 1; i < tipLines.size(); i++) {
                combined = combined.copy().append(Component.literal("\n")).append(tipLines.get(i));
            }
            this.startButton.setTooltip(Tooltip.create(combined));
        }
        
        // Safe mode tooltip
        if (this.safeModeButton != null) {
            boolean safe = this.menu.isSafeMode();
            this.safeModeButton.setTooltip(Tooltip.create(
                    Component.translatable(safe ? "gui.ufo.stellar.tooltip.safe_on" : "gui.ufo.stellar.tooltip.safe_off")
            ));
        }

        // Auto Start tooltip
        if (this.autoStartButton != null) {
            boolean auto = this.menu.isAutoStart();
            this.autoStartButton.setTooltip(Tooltip.create(
                    Component.translatable(auto ? "gui.ufo.stellar.tooltip.auto_on" : "gui.ufo.stellar.tooltip.auto_off")
            ));
        }

        // Lock Simulation tooltip
        if (this.lockButton != null) {
            boolean locked = this.menu.isSimulationLocked();
            this.lockButton.setTooltip(Tooltip.create(
                    Component.translatable(locked ? "gui.ufo.stellar.tooltip.locked" : "gui.ufo.stellar.tooltip.unlocked")
            ));
        }

        // Overclock tooltip
        if (this.overclockButton != null) {
            boolean isOC = this.menu.isOverclocked();
            this.overclockButton.setTooltip(Tooltip.create(
                    Component.translatable(isOC ? "gui.ufo.stellar.tooltip.oc_on" : "gui.ufo.stellar.tooltip.oc_off")
            ));
        }
    }

    private void cycleRecipe(int delta) {
        if (this.availableRecipes.isEmpty() || this.menu.isSimulationLocked()) return;
        
        this.currentRecipeIndex = (this.currentRecipeIndex + delta) % this.availableRecipes.size();
        if (this.currentRecipeIndex < 0) {
            this.currentRecipeIndex += this.availableRecipes.size();
        }

        ResourceLocation newId = this.availableRecipes.get(this.currentRecipeIndex).id();
        ModPackets.sendToServer(new PacketChangeStellarRecipe(this.menu.getBlockEntity().getBlockPos(), newId));
        updateButtonTooltips();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, 191, 160);

        // Progress Bar Calculation
        int p = this.menu.getProgress();
        int max = this.menu.getTotalTime();
        if (max > 0 && p > 0) {
            int progressWidth = (int) ((float) p / max * 157.0f);
            guiGraphics.blit(TEXTURE, this.leftPos + 17, this.topPos + 106, 17, 180, progressWidth, 12);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        renderCustomTooltips(guiGraphics, mouseX, mouseY);
        updateButtonTooltips();
    }

    private void renderCustomTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int localX = mouseX - this.leftPos;
        int localY = mouseY - this.topPos;

        // Field Level area (13,129 to 66,139)
        if (localX >= 13 && localX <= 66 && localY >= 129 && localY <= 139) {
            int fl = this.menu.getFieldLevel();
            List<Component> tips = new ArrayList<>();
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.field_title"));
            if (fl > 0) {
                tips.add(Component.translatable("gui.ufo.stellar.tooltip.field_tier", toRoman(fl)));
                tips.add(Component.translatable("gui.ufo.stellar.tooltip.field_benefit"));
            } else {
                tips.add(Component.translatable("gui.ufo.stellar.tooltip.field_none"));
                tips.add(Component.translatable("gui.ufo.stellar.tooltip.field_none_hint"));
            }
            guiGraphics.renderTooltip(this.font, tips, java.util.Optional.empty(), mouseX, mouseY);
        }

        // Energy area (125,129 to 178,139) 鈥?previously labeled "Fuel"
        if (localX >= 125 && localX <= 178 && localY >= 129 && localY <= 139) {
            int energyPct = this.menu.getEnergyPercent();
            long energyBuffer = this.menu.getEnergyBuffer();
            long energyCapacity = this.menu.getEnergyCapacity();
            List<Component> tips = new ArrayList<>();
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.energy_title"));
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.energy_stored", String.format("%,d", energyBuffer), String.format("%,d", energyCapacity)));
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.energy_charged", energyPct));
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.energy_desc1"));
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.energy_desc2"));
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.energy_desc3"));
            if (energyPct >= 100) {
                tips.add(Component.translatable("gui.ufo.stellar.tooltip.energy_full"));
            }
            guiGraphics.renderTooltip(this.font, tips, java.util.Optional.empty(), mouseX, mouseY);
        }

        // Heat area (125,40 to 175,55)
        if (localX >= 125 && localX <= 175 && localY >= 40 && localY <= 55) {
            int heat = this.menu.getHeatLevel();
            float heatPct = heat / 10.0f;
            List<Component> tips = new ArrayList<>();
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.thermal_title"));
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.thermal_level", String.format("%.1f%%", heatPct)));
            tips.add(Component.translatable("gui.ufo.stellar.tooltip.thermal_coolant"));
            if (this.menu.isSafeMode()) {
                tips.add(Component.translatable("gui.ufo.stellar.tooltip.thermal_safe"));
            } else {
                tips.add(Component.translatable("gui.ufo.stellar.tooltip.thermal_danger"));
            }
            guiGraphics.renderTooltip(this.font, tips, java.util.Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        // Status Mode (13,43)
        Component statusText;
        if (this.menu.getCooldownTimer() > 0) {
            int secLeft = this.menu.getCooldownTimer() / 20;
            int minLeft = secLeft / 60;
            int secRemain = secLeft % 60;
            statusText = Component.translatable("gui.ufo.stellar.cooldown", String.format("%dm%02ds", minLeft, secRemain)).withStyle(ChatFormatting.GOLD);
        } else if (this.menu.isRunning()) {
            statusText = Component.translatable("gui.ufo.stellar.running").withStyle(ChatFormatting.AQUA);
        } else if (this.menu.isAssembled()) {
            statusText = Component.translatable("gui.ufo.stellar.assembled").withStyle(ChatFormatting.GREEN);
        } else {
            statusText = Component.translatable("gui.ufo.stellar.offline").withStyle(ChatFormatting.RED);
        }
        guiGraphics.drawString(this.font, statusText, 15, 44, 0xFFFFFF);

        // Thermal status (125,43)
        int heat = this.menu.getHeatLevel();
        float heatPct = heat / 10.0f;
        int heatColor;
        String heatStr;
        if (heat == 0) {
            heatStr = Component.translatable("gui.ufo.stellar.thermal_stable").getString();
            heatColor = 0x00FFFF;
        } else if (heatPct < 50) {
            heatStr = String.format("%.0f%%", heatPct);
            heatColor = 0xFFFF00;
        } else if (heatPct < 80) {
            heatStr = String.format("%.0f%%", heatPct);
            heatColor = 0xFF8800;
        } else {
            heatStr = String.format("%.0f%% \u26A0", heatPct);
            heatColor = 0xFF0000;
        }
        guiGraphics.drawString(this.font, heatStr, 127, 44, heatColor);

        // Recipe Selection 鈥?use simulationName if available
        Component recipeName = Component.translatable("gui.ufo.stellar.no_program");
        if (!this.availableRecipes.isEmpty()) {
            recipeName = getRecipeDisplayName(this.availableRecipes.get(this.currentRecipeIndex));
        }
        
        int textWidth = this.font.width(recipeName);
        int textX = 44 + ((103 - textWidth) / 2);
        guiGraphics.drawString(this.font, recipeName, textX, 72, 0xFFFFFF);
        
        // Progress Text (22,95)
        int p = this.menu.getProgress();
        int max = this.menu.getTotalTime();
        if (max > 0) {
            String prog = String.format("%.1f %%", (p / (float)max) * 100f);
            guiGraphics.drawString(this.font, prog, 22, 95, 0x00FF00);
        }
        
        // Field Level (13,129 to 66,139)
        int fl = this.menu.getFieldLevel();
        String fieldLevel = Component.translatable("gui.ufo.stellar.field_level", fl > 0 ? toRoman(fl) : "0").getString();
        int fieldColor = fl >= 3 ? 0xFF00FF : (fl >= 2 ? 0xAA00AA : (fl >= 1 ? 0x8800AA : 0x666666));
        guiGraphics.drawString(this.font, fieldLevel, 15, 131, fieldColor);
        
        // Current Energy (125,129 to 178,139) 鈥?label changed from Fuel to Energy
        int energyPct = this.menu.getEnergyPercent();
        String energyAmount = energyPct + "%";
        int energyColor = energyPct >= 100 ? 0x00FF00 : (energyPct > 50 ? 0xFFFF00 : 0xFF4444);
        guiGraphics.drawString(this.font, energyAmount, 127, 131, energyColor);

        // Safe Mode indicator
        boolean safeMode = this.menu.isSafeMode();
        guiGraphics.drawString(this.font, safeMode ? "§a●" : "§c●", -3, 110, 0xFFFFFF);

        // Auto Start indicator
        boolean autoStart = this.menu.isAutoStart();
        guiGraphics.drawString(this.font, autoStart ? "§a●" : "§c●", -3, 92, 0xFFFFFF);

        // Lock indicator
        boolean isLocked = this.menu.isSimulationLocked();
        guiGraphics.drawString(this.font, isLocked ? "§a●" : "§c●", -3, 128, 0xFFFFFF);

        // Overclock indicator
        boolean overclocked = this.menu.isOverclocked();
        guiGraphics.drawString(this.font, overclocked ? "§a●" : "§c●", -3, 74, 0xFFFFFF);

        // AE network connection indicator
        boolean aeConnected = this.menu.isAeConnected();
        Component aeText = Component.translatable(aeConnected ? "gui.ufo.stellar.ae_connected" : "gui.ufo.stellar.ae_disconnected")
                .withStyle(aeConnected ? ChatFormatting.GREEN : ChatFormatting.RED);
        guiGraphics.drawString(this.font, aeText, 15, 56, 0xFFFFFF);
    }

    /**
     * Gets the display name for a recipe.
     * Uses the simulationName field if available, otherwise formats the recipe ID.
     */
    private Component getRecipeDisplayName(RecipeHolder<StellarSimulationRecipe> holder) {
        String key = holder.value().getSimulationTranslationKey();
        if (!key.isEmpty()) {
            return Component.translatable(key);
        }
        String simName = holder.value().getSimulationName();
        if (simName != null && !simName.isEmpty()) {
            return Component.literal(simName);
        }
        return Component.literal(formatRecipeId(holder.id()));
    }

    private String formatRecipeId(ResourceLocation id) {
        String path = id.getPath();
        int slashIndex = path.lastIndexOf('/');
        if (slashIndex != -1) {
            path = path.substring(slashIndex + 1);
        }
        if (path.startsWith("stellar_")) {
            path = path.substring("stellar_".length());
        }
        String[] words = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
    
    private static String getFluidDisplayName(ResourceLocation fluidId) {
        var fluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.getOptional(fluidId).orElse(null);
        if (fluid == null) {
            return formatFluidName(fluidId.getPath());
        }
        String hoverName = new net.neoforged.neoforge.fluids.FluidStack(fluid, 1).getHoverName().getString();
        return hoverName == null || hoverName.isBlank() ? formatFluidName(fluidId.getPath()) : hoverName;
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

    private static String formatAmount(long amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB", amount / 1_000_000_000.0);
        if (amount >= 1_000_000) return String.format("%.1fM", amount / 1_000_000.0);
        if (amount >= 1_000) return String.format("%.1fK", amount / 1_000.0);
        return String.valueOf(amount);
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

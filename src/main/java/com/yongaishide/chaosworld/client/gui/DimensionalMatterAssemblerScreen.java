package com.yongaishide.chaosworld.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import com.yongaishide.chaosworld.menu.DimensionalMatterAssemblerMenu;

import net.pedroksl.ae2addonlib.api.IFluidTankScreen;
import net.pedroksl.ae2addonlib.client.widgets.AddonActionItems;
import net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot;
import net.pedroksl.ae2addonlib.client.widgets.ToolbarActionButton;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.*;

public class DimensionalMatterAssemblerScreen extends UpgradeableScreen<DimensionalMatterAssemblerMenu> implements IFluidTankScreen {

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> autoExportBtn;
    private final ToolbarActionButton outputConfigure;
    private final AlertWidget powerAlert;

    private FluidTankSlot inputCoolantSlot;
    private FluidTankSlot inputFluidSlot;
    private FluidTankSlot outputFluid1Slot;
    private FluidTankSlot outputFluid2Slot;

    // Energy bar region on the texture
    private static final int ENERGY_X = 155;
    private static final int ENERGY_Y = 34;
    private static final int ENERGY_W = 6;
    private static final int ENERGY_H = 18;

    // Heat bar region (new coordinates from user)
    private static final int HEAT_BAR_X = 9;
    private static final int HEAT_BAR_Y = 5;
    private static final int HEAT_BAR_W = 91; // 100 - 9
    private static final int HEAT_BAR_H = 10; // 15 - 5

    public DimensionalMatterAssemblerScreen(
            DimensionalMatterAssemblerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        // progress rendering X=105, Y=42, W=21, H=12
        this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.HORIZONTAL);
        widgets.add("progressBar", this.pb);

        this.autoExportBtn = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(autoExportBtn);

        this.outputConfigure =
                new ToolbarActionButton(AddonActionItems.DIRECTIONAL_OUTPUT, btn -> menu.configureOutput());
        this.outputConfigure.setVisibility(getMenu().getAutoExport() == YesNo.YES);
        this.addToLeftToolbar(this.outputConfigure);

        this.powerAlert = new AlertWidget(style.getImage("powerAlert"));
        this.powerAlert.setTooltip(Tooltip.create(Component.translatable("gui.ufo.dma.insufficient_power")));
        this.widgets.add("powerAlert", this.powerAlert);
    }

    @Override
    protected void init() {
        // fluid output 1 começa em 119,75 e termina em 133,92 -> W: 14, H: 17
        this.outputFluid1Slot = this.addRenderableWidget(new FluidTankSlot(
                this, 0, this.leftPos + 119, this.topPos + 75, 14, 17, this.menu.OUTPUT_FLUID_SIZE));
        
        // fluid output 2 começa em 148,76 e termina em 162,92 -> W: 14, H: 17
        this.outputFluid2Slot = this.addRenderableWidget(new FluidTankSlot(
                this, 1, this.leftPos + 148, this.topPos + 76, 14, 17, this.menu.OUTPUT_FLUID_SIZE));

        // input coolant começa em 9,21 e termina em 20,74 -> W: 12, H: 54
        this.inputCoolantSlot = this.addRenderableWidget(new FluidTankSlot(
                this, 2, this.leftPos + 9, this.topPos + 21, 12, 54, this.menu.INPUT_FLUID_SIZE));

        // input fluid/liquid começa em 28,21 e termina em 39,74 -> W: 12, H: 54
        this.inputFluidSlot = this.addRenderableWidget(new FluidTankSlot(
                this, 3, this.leftPos + 28, this.topPos + 21, 12, 54, this.menu.INPUT_FLUID_SIZE));

        super.init();

    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        int progress = 0;
        if (this.menu.getMaxProgress() > 0) {
            progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        }
        this.pb.setFullMsg(Component.literal(progress + "%"));

        this.autoExportBtn.set(getMenu().getAutoExport());
        this.outputConfigure.setVisibility(getMenu().getAutoExport() == YesNo.YES);

        this.outputFluid1Slot.setPosition(this.leftPos + 119, this.topPos + 75);
        this.outputFluid2Slot.setPosition(this.leftPos + 148, this.topPos + 76);
        this.inputCoolantSlot.setPosition(this.leftPos + 9, this.topPos + 21);
        this.inputFluidSlot.setPosition(this.leftPos + 28, this.topPos + 21);

        this.powerAlert.visible = this.getMenu().getShowWarning();
    }

    @Override
    public void updateFluidTankContents(int index, FluidStack stack) {
        if (index == 0) this.outputFluid1Slot.setFluidStack(stack);
        else if (index == 1) this.outputFluid2Slot.setFluidStack(stack);
        else if (index == 2) this.inputCoolantSlot.setFluidStack(stack);
        else if (index == 3) this.inputFluidSlot.setFluidStack(stack);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;
        
        Runnable playClickSound = () -> 
            net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F)
            );

        // Limpar tanques com Botão Esquerdo nas posições customizadas
        if (button == 0) {
            // botao tank input coolant: (7,78) a (14,85) -> Tank 2
            if (isInTankRegion(mx, my, this.leftPos + 7, this.topPos + 78, 7, 7)) {
                playClickSound.run();
                this.menu.clearTank(2);
                return true;
            }
            // botao tank input fluid: (34,78) a (41,85) -> Tank 3
            if (isInTankRegion(mx, my, this.leftPos + 34, this.topPos + 78, 7, 7)) {
                playClickSound.run();
                this.menu.clearTank(3);
                return true;
            }
            // botão tank output 1: (108,87) a (115,94) -> Tank 0
            if (isInTankRegion(mx, my, this.leftPos + 108, this.topPos + 87, 7, 7)) {
                playClickSound.run();
                this.menu.clearTank(0);
                return true;
            }
            // botao tank output 2: (166,87) a (173,94) -> Tank 1
            if (isInTankRegion(mx, my, this.leftPos + 166, this.topPos + 87, 7, 7)) {
                playClickSound.run();
                this.menu.clearTank(1);
                return true;
            }
        }

        // Interação com Baldes (Right-click / Button 1)
        if (button == 1) {
            if (isInTankRegion(mx, my, this.leftPos + 119, this.topPos + 75, 14, 17)) {
                playClickSound.run();
                this.menu.fillOrDrainTank(0);
                return true;
            }
            if (isInTankRegion(mx, my, this.leftPos + 148, this.topPos + 76, 14, 17)) {
                playClickSound.run();
                this.menu.fillOrDrainTank(1);
                return true;
            }
            if (isInTankRegion(mx, my, this.leftPos + 9, this.topPos + 21, 12, 54)) {
                playClickSound.run();
                this.menu.fillOrDrainTank(2);
                return true;
            }
            if (isInTankRegion(mx, my, this.leftPos + 28, this.topPos + 21, 12, 54)) {
                playClickSound.run();
                this.menu.fillOrDrainTank(3);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isInTankRegion(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        // --- Energy Bar ---
        double stored = this.menu.currentPower;
        double maxStore = this.menu.getHost().getAEMaxPower();
        int filled = 0;
        if (maxStore > 0) {
            filled = (int) ((stored * ENERGY_H) / maxStore);
        }
        int topEmpty = ENERGY_H - filled;

        Blitter.texture("guis/dimensional_matter_assembler.png")
            .src(225, topEmpty, ENERGY_W, filled)
            .dest(this.leftPos + ENERGY_X, this.topPos + ENERGY_Y + topEmpty)
            .blit(guiGraphics);

        // --- Heat Progress Bar ---
        int temp = this.menu.temperature;
        int maxTemp = this.menu.maxTemperature;
        int overload = this.menu.overloadTimer;

        int barX = this.leftPos + HEAT_BAR_X;
        int barY = this.topPos + HEAT_BAR_Y;

        // Draw dark background for heat bar
        guiGraphics.fill(barX, barY, barX + HEAT_BAR_W, barY + HEAT_BAR_H, 0xFF1A1A1A);
        // Border
        guiGraphics.fill(barX - 1, barY - 1, barX + HEAT_BAR_W + 1, barY, 0xFF333333); // top
        guiGraphics.fill(barX - 1, barY + HEAT_BAR_H, barX + HEAT_BAR_W + 1, barY + HEAT_BAR_H + 1, 0xFF333333); // bottom
        guiGraphics.fill(barX - 1, barY - 1, barX, barY + HEAT_BAR_H + 1, 0xFF333333); // left
        guiGraphics.fill(barX + HEAT_BAR_W, barY - 1, barX + HEAT_BAR_W + 1, barY + HEAT_BAR_H + 1, 0xFF333333); // right

        double ratio = maxTemp > 0 ? (double) temp / maxTemp : 0.0;
        ratio = Math.min(ratio, 1.0);

        int filledWidth = (int) (ratio * HEAT_BAR_W);

        if (filledWidth > 0) {
            if (overload > 0) {
                // Pulsing red during overload
                int pulse = (int) (127 + 128 * Math.sin(System.currentTimeMillis() / 100.0));
                int color = 0xFF000000 | (pulse << 16);
                guiGraphics.fill(barX, barY, barX + filledWidth, barY + HEAT_BAR_H, color);
            } else {
                // Gradient fill: draw pixel columns with interpolated color
                for (int col = 0; col < filledWidth; col++) {
                    double colRatio = (double) col / HEAT_BAR_W;
                    int color = getHeatColor(colRatio);
                    guiGraphics.fill(barX + col, barY, barX + col + 1, barY + HEAT_BAR_H, color);
                }
            }
        }

        // Draw overload text on top of bar if in overload
        if (overload > 0) {
            int seconds = overload / 20;
            String text = "§l⚠ OVERLOAD " + seconds + "s";
            int textWidth = this.font.width(text);
            int textX = barX + (HEAT_BAR_W - textWidth) / 2;
            int textY = barY + 1;
            guiGraphics.drawString(this.font, text, textX, textY, 0xFFFF0000, true);
        }
    }

    /**
     * Interpolates heat color from green (0.0) -> yellow (0.5) -> red (1.0)
     */
    private static int getHeatColor(double ratio) {
        int r, g, b;
        if (ratio < 0.5) {
            // Green to Yellow
            double t = ratio / 0.5;
            r = (int) (t * 255);
            g = 255;
            b = 0;
        } else {
            // Yellow to Red
            double t = (ratio - 0.5) / 0.5;
            r = 255;
            g = (int) ((1 - t) * 255);
            b = 0;
        }
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Check if mouse is over the energy bar area
        int barLeft = this.leftPos + ENERGY_X;
        int barTop = this.topPos + ENERGY_Y;
        int barRight = barLeft + ENERGY_W;
        int barBottom = barTop + ENERGY_H;

        if (mouseX >= barLeft && mouseX <= barRight && mouseY >= barTop && mouseY <= barBottom) {
            double stored = this.menu.currentPower;
            double maxStore = this.menu.getHost().getAEMaxPower();

            List<Component> tooltip = List.of(
                    Component.translatable("gui.ufo.dma.energy", formatEnergy(stored), formatEnergy(maxStore))
            );
            guiGraphics.renderTooltip(this.font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
            return;
        }

        // Check if mouse is over the heat bar area
        int heatLeft = this.leftPos + HEAT_BAR_X;
        int heatTop = this.topPos + HEAT_BAR_Y;
        int heatRight = heatLeft + HEAT_BAR_W;
        int heatBottom = heatTop + HEAT_BAR_H;

        if (mouseX >= heatLeft && mouseX <= heatRight && mouseY >= heatTop && mouseY <= heatBottom) {
            int temp = this.menu.temperature;
            int maxTemp = this.menu.maxTemperature;
            int overload = this.menu.overloadTimer;

            List<Component> tooltip = new java.util.ArrayList<>();
            tooltip.add(Component.translatable("gui.ufo.dma.heat", temp, maxTemp));

            double pct = maxTemp > 0 ? ((double) temp / maxTemp * 100.0) : 0;
            tooltip.add(Component.translatable("gui.ufo.dma.heat_capacity", String.format("%.1f%%", pct)));

            if (overload > 0) {
                tooltip.add(Component.translatable("gui.ufo.dma.heat_critical", overload / 20));
            } else if (pct >= 80) {
                tooltip.add(Component.translatable("gui.ufo.dma.heat_danger"));
            } else if (pct >= 50) {
                tooltip.add(Component.translatable("gui.ufo.dma.heat_warning"));
            }

            guiGraphics.renderTooltip(this.font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
            return;
        }

        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    /**
     * Formats energy values into human-readable strings.
     * K = thousands, M = millions, G = billions
     */
    private static String formatEnergy(double energy) {
        if (energy >= 1_000_000_000) {
            return String.format("%.1fG AE", energy / 1_000_000_000.0);
        } else if (energy >= 1_000_000) {
            return String.format("%.1fM AE", energy / 1_000_000.0);
        } else if (energy >= 1_000) {
            return String.format("%.1fK AE", energy / 1_000.0);
        }
        return String.format("%.0f AE", energy);
    }


    private static class AlertWidget extends AbstractWidget {

        private final Blitter powerAlert;

        public AlertWidget(Blitter powerAlert) {
            super(0, 0, 18, 18, Component.empty());
            this.powerAlert = powerAlert;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
            if (this.powerAlert != null) {
                this.powerAlert.dest(this.getX(), this.getY()).blit(guiGraphics);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    }
}

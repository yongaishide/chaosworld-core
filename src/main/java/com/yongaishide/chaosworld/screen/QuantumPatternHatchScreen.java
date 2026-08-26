package com.yongaishide.chaosworld.screen;

import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.client.gui.widgets.ToggleButton;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.core.localization.GuiText;
import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.ConfigButtonPacket;
import com.extendedae_plus.api.IInputBackgroundRenderer;
import com.extendedae_plus.api.config.EAPSettings;
import com.extendedae_plus.client.gui.widgets.EAPServerSettingToggleButton;
import com.extendedae_plus.util.GuiUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class QuantumPatternHatchScreen extends AEBaseScreen<QuantumPatternHatchMenu>
        implements IInputBackgroundRenderer {
    private final SettingToggleButton<YesNo> blockingModeButton;
    private final SettingToggleButton<LockCraftingMode> lockCraftingModeButton;
    private final ToggleButton showInPatternAccessTerminalButton;
    private final QuantumPatternHatchLockReason lockReason;

    private final EAPServerSettingToggleButton<YesNo> advancedBlockingToggle;
    private final EAPServerSettingToggleButton<YesNo> smartDoublingToggle;
    private final AETextField perProviderLimitInput;
    private int perProviderScalingLimit;

    private int inputBgX;
    private int inputBgY;
    private int inputBgW;
    private int inputBgH;

    public QuantumPatternHatchScreen(QuantumPatternHatchMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.blockingModeButton = new ServerSettingToggleButton<>(Settings.BLOCKING_MODE, YesNo.NO);
        this.addToLeftToolbar(this.blockingModeButton);

        this.lockCraftingModeButton = new ServerSettingToggleButton<>(Settings.LOCK_CRAFTING_MODE, LockCraftingMode.NONE);
        this.addToLeftToolbar(this.lockCraftingModeButton);

        widgets.addOpenPriorityButton();

        this.showInPatternAccessTerminalButton = new ToggleButton(Icon.PATTERN_ACCESS_SHOW,
                Icon.PATTERN_ACCESS_HIDE,
                GuiText.PatternAccessTerminal.text(), GuiText.PatternAccessTerminalHint.text(),
                btn -> selectNextPatternProviderMode());
        this.addToLeftToolbar(this.showInPatternAccessTerminalButton);

        this.advancedBlockingToggle = new EAPServerSettingToggleButton<>(EAPSettings.ADVANCED_BLOCKING, YesNo.YES);
        this.addToLeftToolbar(this.advancedBlockingToggle);

        this.smartDoublingToggle = new EAPServerSettingToggleButton<>(EAPSettings.SMART_DOUBLING, YesNo.YES);
        this.addToLeftToolbar(this.smartDoublingToggle);

        this.perProviderScalingLimit = menu.getPerProviderScalingLimit();
        this.perProviderLimitInput = GuiUtil.createPerProviderLimitInput(style, this.font, this.perProviderScalingLimit,
                newLimit -> {
                    this.perProviderScalingLimit = newLimit;
                    menu.sendScalingLimitFromClient(newLimit);
                });

        this.lockReason = new QuantumPatternHatchLockReason(this);
        widgets.add("lockReason", this.lockReason);

        if (menu.getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, menu.getToolbox().getName()));
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.lockReason.setVisible(menu.getLockCraftingMode() != LockCraftingMode.NONE);
        this.blockingModeButton.set(this.menu.getBlockingMode());
        this.lockCraftingModeButton.set(this.menu.getLockCraftingMode());
        this.showInPatternAccessTerminalButton.setState(this.menu.getShowInAccessTerminal() == YesNo.YES);

        this.advancedBlockingToggle.set(this.menu.getAdvancedBlocking());
        this.smartDoublingToggle.set(this.menu.getSmartDoubling());
        updateLimitInput();
    }

    private void selectNextPatternProviderMode() {
        final boolean backwards = isHandlingRightClick();
        ServerboundPacket message = new ConfigButtonPacket(Settings.PATTERN_ACCESS_TERMINAL, backwards);
        PacketDistributor.sendToServer(message);
    }

    private void updateLimitInput() {
        if (perProviderLimitInput == null) {
            return;
        }

        int limit = menu.getPerProviderScalingLimit();
        if (!perProviderLimitInput.isFocused() && limit != this.perProviderScalingLimit) {
            this.perProviderScalingLimit = limit;
            perProviderLimitInput.setValue(String.valueOf(limit));
        }

        if (smartDoublingToggle.getCurrentValue() == YesNo.YES) {
            if (!renderables.contains(perProviderLimitInput)) {
                addRenderableWidget(perProviderLimitInput);
            }

            int inputWidth = perProviderLimitInput.getWidth() + 4 + this.font.width("_");
            int inputHeight = 16;
            int padding = 2;

            int x = smartDoublingToggle.getX() - inputWidth - 5 - padding;
            int y = smartDoublingToggle.getY() + (smartDoublingToggle.getHeight() - inputHeight) / 2 - padding + 6;

            perProviderLimitInput.setX(x + padding);
            perProviderLimitInput.setY(y + padding);

            this.inputBgX = x;
            this.inputBgY = y;
            this.inputBgW = inputWidth + padding * 2;
            this.inputBgH = inputHeight + padding * 2;

            String value = perProviderLimitInput.getValue();
            if (value == null || value.isBlank()) {
                value = "0";
            }
            perProviderLimitInput.setTooltipMessage(List.of(
                    Component.translatable("gui.extendedae_plus.per_provider_limit.tooltip", value)));
        } else {
            removeWidget(perProviderLimitInput);
        }
    }

    @Override
    public void eap$renderInputBackground(GuiGraphics guiGraphics) {
        if (smartDoublingToggle != null
                && smartDoublingToggle.getCurrentValue() == YesNo.YES
                && perProviderLimitInput != null
                && perProviderLimitInput.isVisible()) {
            Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter()
                    .dest(inputBgX - 5, inputBgY - 3, inputBgW + 6, inputBgH - 2)
                    .blit(guiGraphics);
        }
    }
}

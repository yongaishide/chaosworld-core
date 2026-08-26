package com.yongaishide.chaosworld.client;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen;
import com.lowdragmc.lowdraglib2.gui.texture.SDFRectTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.math.Size;
import com.yongaishide.chaosworld.network.ModPackets;
import com.yongaishide.chaosworld.network.packet.TerminalSettingsPacket;
import com.yongaishide.chaosworld.util.StructureTerminalSettings;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.appliedenergistics.yoga.YogaPositionType;

public class StructureTerminalScreen extends ModularUIScreen {

    private static final ResourceLocation TERMINAL_FONT =
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "main_font");

    private static final float DESIGN_W = 176F;
    private static final float DESIGN_H = 166F;
    private static final float MIN_SCALE = 0.55F;
    private static float uiScale = 1F;

    private final ItemStack stack;

    public StructureTerminalScreen(ItemStack stack) {
        super(buildUI(stack), Component.translatable("gui.ufo.terminal.title"));
        this.stack = stack;
    }

    @Override
    public void init() {
        uiScale = Math.max(MIN_SCALE, Math.min(1.0F,
                Math.min((this.width - 30F) / DESIGN_W, (this.height - 30F) / DESIGN_H)));
        super.init();
    }

    private static ModularUI buildUI(ItemStack stack) {
        UIElement root = new UIElement();
        root.layout(l -> l.positionType(YogaPositionType.RELATIVE).width(DESIGN_W).height(DESIGN_H));
        root.style(s -> s.background(SDFRectTexture.of(0xFFF7F8FA).setRadius(12)
                .setBorderColor(0xFFD8DCE5).setStroke(1.5F)));

        Label title = new Label();
        title.setText(Component.translatable("gui.ufo.terminal.title"));
        layout(title, 0, 4, DESIGN_W, 14);
        title.textStyle(s -> s.textColor(0xFF1F2430).font(TERMINAL_FONT).textAlignHorizontal(Horizontal.CENTER));
        root.addChild(title);

        Label repeatTitle = new Label();
        repeatTitle.setText(Component.translatable("gui.ufo.terminal.repeat",
                String.valueOf(StructureTerminalSettings.getRepeatCount(stack))));
        layout(repeatTitle, 6, 28, 110, 16);
        repeatTitle.textStyle(s -> s.textColor(0xFF3A4150).font(TERMINAL_FONT));
        root.addChild(repeatTitle);

        Button repeatMinus = terminalButton("<", 124, 26, 22, 18);
        repeatMinus.setOnClick(event -> {
            int v = Math.max(1, StructureTerminalSettings.getRepeatCount(stack) - 1);
            StructureTerminalSettings.setRepeatCount(stack, v);
            repeatTitle.setText(Component.translatable("gui.ufo.terminal.repeat", String.valueOf(v)));
            sendSettings(stack);
        });
        root.addChild(repeatMinus);

        Button repeatPlus = terminalButton(">", 148, 26, 22, 18);
        repeatPlus.setOnClick(event -> {
            int v = Math.min(64, StructureTerminalSettings.getRepeatCount(stack) + 1);
            StructureTerminalSettings.setRepeatCount(stack, v);
            repeatTitle.setText(Component.translatable("gui.ufo.terminal.repeat", String.valueOf(v)));
            sendSettings(stack);
        });
        root.addChild(repeatPlus);

        root.addChild(toggleButton(stack, "gui.ufo.terminal.replace", 54,
                StructureTerminalSettings::getReplaceMode, StructureTerminalSettings::setReplaceMode));
        root.addChild(toggleButton(stack, "gui.ufo.terminal.flip", 74,
                StructureTerminalSettings::getFlipped, StructureTerminalSettings::setFlipped));
        root.addChild(toggleButton(stack, "gui.ufo.terminal.dismantle", 94,
                StructureTerminalSettings::getDismantleMode, StructureTerminalSettings::setDismantleMode));
        root.addChild(toggleButton(stack, "gui.ufo.terminal.ae_mode", 114,
                StructureTerminalSettings::getAeMode, StructureTerminalSettings::setAeMode));

        Button tierButton = terminalButton(tierLabel(StructureTerminalSettings.getFieldTier(stack)), 6, 134, DESIGN_W - 12, 18);
        tierButton.setOnClick(event -> {
            int v = (StructureTerminalSettings.getFieldTier(stack) % 3) + 1;
            StructureTerminalSettings.setFieldTier(stack, v);
            tierButton.setText(tierLabel(v));
            sendSettings(stack);
        });
        root.addChild(tierButton);

        Label boundLabel = new Label();
        GlobalPos bound = StructureTerminalSettings.getBoundPos(stack);
        if (bound != null) {
            boundLabel.setText(Component.translatable("gui.ufo.terminal.bound",
                    bound.pos().toShortString() + " (" + bound.dimension().location() + ")"));
        } else {
            boundLabel.setText(Component.translatable("gui.ufo.terminal.unbound"));
        }
        layout(boundLabel, 6, 152, DESIGN_W - 12, 14);
        boundLabel.textStyle(s -> s.textColor(0xFF8A90A0).font(TERMINAL_FONT));
        root.addChild(boundLabel);

        return ModularUI.of(UI.of(root, screenSize -> {
            float s = uiScale;
            return Size.of(Math.round(DESIGN_W * s), Math.round(DESIGN_H * s));
        }));
    }

    private static void layout(UIElement element, float x, float y, float width, float height) {
        float s = uiScale;
        element.layout(l -> l.positionType(YogaPositionType.ABSOLUTE)
                .left(x * s).top(y * s).width(width * s).height(height * s));
    }

    private static Button terminalButton(String text, float x, float y, float width, float height) {
        Button button = new Button();
        layout(button, x, y, width, height);
        button.buttonStyle(style -> {
            style.baseTexture(SDFRectTexture.of(0xFFECEEF3).setRadius(6)
                    .setBorderColor(0xFFD8DCE5).setStroke(1.0F));
            style.hoverTexture(SDFRectTexture.of(0xFFE2E5EC).setRadius(6)
                    .setBorderColor(0xFFC9CED9).setStroke(1.0F));
            style.pressedTexture(SDFRectTexture.of(0xFFD5D9E2).setRadius(6)
                    .setBorderColor(0xFFB9BFCC).setStroke(1.0F));
        });
        button.textStyle(s -> s.textColor(0xFF232833).font(TERMINAL_FONT).textAlignHorizontal(Horizontal.CENTER));
        button.setText(text);
        return button;
    }

    private static Button toggleButton(ItemStack stack, String langKey, float top,
                                       java.util.function.Predicate<ItemStack> getter,
                                       java.util.function.BiConsumer<ItemStack, Boolean> setter) {
        Button button = terminalButton(toggleLabel(langKey, getter.test(stack)), 6, top, DESIGN_W - 12, 18);
        button.setOnClick(event -> {
            boolean value = !getter.test(stack);
            setter.accept(stack, value);
            button.setText(toggleLabel(langKey, value));
            sendSettings(stack);
        });
        return button;
    }

    private static String tierLabel(int tier) {
        return Component.translatable("gui.ufo.terminal.tier",
                Component.translatable("gui.ufo.terminal.tier_" + tier)).getString();
    }

    private static String toggleLabel(String langKey, boolean value) {
        return Component.translatable(langKey,
                Component.translatable(value ? "gui.ufo.terminal.on" : "gui.ufo.terminal.off")).getString();
    }

    private static void sendSettings(ItemStack stack) {
        ModPackets.sendToServer(new TerminalSettingsPacket(
                StructureTerminalSettings.getRepeatCount(stack),
                StructureTerminalSettings.getReplaceMode(stack),
                StructureTerminalSettings.getFlipped(stack),
                StructureTerminalSettings.getDismantleMode(stack),
                StructureTerminalSettings.getAeMode(stack),
                StructureTerminalSettings.getFieldTier(stack)));
    }
}

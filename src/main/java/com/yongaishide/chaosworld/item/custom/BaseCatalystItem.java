package com.yongaishide.chaosworld.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import appeng.items.materials.UpgradeCardItem;

import java.util.List;

/**
 * Classe base para os 12 catalisadores T1-T3.
 * Extends AE2's UpgradeCardItem so it is accepted by AE2's upgrade slot validation.
 * Cuida automaticamente da lógica da tooltip (com SHIFT).
 */
public class BaseCatalystItem extends UpgradeCardItem {

    protected final String family;
    protected final int tier;

    public BaseCatalystItem(Properties properties, String family, int tier) {
        super(properties);
        this.family = family;
        this.tier = tier;
    }

    public String getFamily() { return family; }
    public int getTier() { return tier; }

    public int getStaticHeat() {
        int h = 0;
        switch (family) {
            case "matterflow":
                if (tier == 1) h = 50;
                if (tier == 2) h = 100;
                if (tier == 3) h = 200;
                break;
            case "chrono":
                if (tier == 1) h = 25;
                if (tier == 2) h = 75;
                if (tier == 3) h = 150;
                break;
            case "overflux":
                h = 0;
                break;
            case "quantum":
                if (tier == 1) h = 75;
                if (tier == 2) h = 150;
                if (tier == 3) h = 300;
                break;
        }
        return h;
    }

    public double getPowerMultiplier() {
        if ("matterflow".equals(family)) {
            double baseStat = -10.0;
            double tierMultiplier = 1.0;
            if (tier == 2) tierMultiplier = 2.0;
            if (tier == 3) tierMultiplier = 3.0;
            return Math.max(0.01, 1.0 + (baseStat * tierMultiplier / 100.0));
        }
        return 1.0;
    }

    public double getSpeedMultiplier() {
        if ("chrono".equals(family)) {
            double baseStat = 25.0;
            double tierMultiplier = 1.0;
            if (tier == 2) tierMultiplier = 2.5;
            if (tier == 3) tierMultiplier = 5.0;
            return 1.0 + (baseStat * tierMultiplier / 100.0);
        }
        return 1.0;
    }

    public double getBonusDropChance() {
        if ("quantum".equals(family)) {
            double tierMultiplier = 1.0;
            if (tier == 2) tierMultiplier = 2.5;
            if (tier == 3) tierMultiplier = 5.0;
            return 0.10 * tierMultiplier;
        }
        return 0.0;
    }

    public int getBufferMultiplier() {
        if ("matterflow".equals(family) || "chrono".equals(family)) {
            if (tier == 1) return 10;
            if (tier == 2) return 100;
            if (tier == 3) return 1000;
        }
        return 1;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return CatalystUpgradeUseHelper.tryInstallHeldCatalyst(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            double baseStat = 0;
            String statKey = null;
            double tierMultiplier = 1.0;
            int staticHeat = 0;

            if (tier == 2) tierMultiplier = 2.5;
            if (tier == 3) tierMultiplier = 5.0;
            if ("matterflow".equals(family)) {
                // Matterflow energy reduction was rebalanced to -10/-20/-30%
                if (tier == 2) tierMultiplier = 2.0;
                if (tier == 3) tierMultiplier = 3.0;
            }

            switch (family) {
                case "matterflow" -> {
                    baseStat = -10.0;
                    statKey = "tooltip.ufo.catalyst.stat.energy_cost";
                    if (tier == 1) staticHeat = 50;
                    if (tier == 2) staticHeat = 100;
                    if (tier == 3) staticHeat = 200;
                }                case "chrono" -> {
                    baseStat = 25.0;
                    statKey = "tooltip.ufo.catalyst.stat.speed";
                    if (tier == 1) staticHeat = 25;
                    if (tier == 2) staticHeat = 75;
                    if (tier == 3) staticHeat = 150;
                }
                case "overflux" -> {
                    baseStat = -10.0;
                    statKey = "tooltip.ufo.catalyst.stat.failure_chance";
                    staticHeat = 0;
                }
                case "quantum" -> {
                    baseStat = 10.0;
                    statKey = "tooltip.ufo.catalyst.stat.bonus_drop";
                    if (tier == 1) staticHeat = 75;
                    if (tier == 2) staticHeat = 150;
                    if (tier == 3) staticHeat = 300;
                }
            }

            String familyName = Component.translatable("tooltip.ufo.catalyst.family." + family).getString();
            components.add(Component.translatable("tooltip.ufo.catalyst.family", familyName).withStyle(ChatFormatting.GRAY));
            components.add(Component.translatable("tooltip.ufo.catalyst.tier", tier).withStyle(ChatFormatting.GRAY));
            components.add(Component.empty());

            double finalStat = baseStat * tierMultiplier;
            String sign = finalStat > 0 ? "+" : "";
            ChatFormatting statColor = finalStat > 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
            String statName = Component.translatable(statKey).getString();
            String statText = String.format("%s%.1f%% %s", sign, finalStat, statName);
            components.add(Component.translatable("tooltip.ufo.catalyst.effect", statText).withStyle(statColor));

            double heatMult = 1.0 + Math.max(0, staticHeat / 100.0);
            String heatColorFormat = heatMult > 1.0 ? "§c" : "§b";
            String heatName = Component.translatable("tooltip.ufo.catalyst.heat_production").getString();
            String heatText = String.format("%sx%.1f %s", heatColorFormat, heatMult, heatName);
            components.add(Component.translatable("tooltip.ufo.catalyst.thermal", heatText).withStyle(ChatFormatting.GRAY));

            components.add(Component.empty());
            components.add(Component.translatable("tooltip.ufo.catalyst.stacking").withStyle(ChatFormatting.GOLD));
            components.add(Component.translatable("tooltip.ufo.catalyst.stack_format", "2", "175").withStyle(ChatFormatting.GRAY));
            components.add(Component.translatable("tooltip.ufo.catalyst.stack_format", "3", "225").withStyle(ChatFormatting.GRAY));
            components.add(Component.translatable("tooltip.ufo.catalyst.stack_format", "4", "250").withStyle(ChatFormatting.GRAY));
        } else {
            components.add(Component.translatable("tooltip.ufo.press_shift").withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, components, flag);
    }
}

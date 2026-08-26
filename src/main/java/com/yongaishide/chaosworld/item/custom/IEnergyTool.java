package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.item.ModTools;
import com.yongaishide.chaosworld.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;
import java.util.function.Supplier;

public interface IEnergyTool {

    List<Supplier<? extends Item>> TOOL_CYCLE = List.of(
            ModTools.UFO_STAFF, ModTools.UFO_SWORD, ModTools.UFO_PICKAXE, ModTools.UFO_AXE,
            ModTools.UFO_SHOVEL, ModTools.UFO_HOE, ModTools.UFO_HAMMER, ModTools.UFO_GREATSWORD,
            ModTools.UFO_FISHING_ROD, ModTools.UFO_BOW
    );

    int getEnergyPerUse();

    default int getSoulHarvestDamagePerKill() {
        return 2;
    }

    default boolean consumeEnergy(ItemStack pStack) {
        IEnergyStorage energyStorage = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyStorage != null && energyStorage.getEnergyStored() >= getEnergyPerUse()) {
            energyStorage.extractEnergy(getEnergyPerUse(), false);
            return true;
        }
        return false;
    }

    default void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.ufo.switch_tool_hint").withStyle(ChatFormatting.DARK_AQUA));
        if (Screen.hasShiftDown()) {
            IEnergyStorage energyStorage = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energyStorage != null) {
                String energyText = String.format("%,d / %,d RF", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored());
                pTooltipComponents.add(Component.literal(energyText).withStyle(ChatFormatting.GRAY));
            }
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.ufo.press_shift").withStyle(ChatFormatting.AQUA));
        }
    }

    default void transformTool(Level level, Player player, InteractionHand hand, boolean forward) {
        if (!level.isClientSide()) {
            ItemStack currentStack = player.getItemInHand(hand);
            int currentIndex = currentStack.getOrDefault(ModDataComponents.TOOL_MODE_INDEX.get(), 0);

            int nextIndex;
            if (forward) {
                nextIndex = (currentIndex + 1) % TOOL_CYCLE.size();
            } else {
                nextIndex = (currentIndex - 1 + TOOL_CYCLE.size()) % TOOL_CYCLE.size();
            }

            Item nextItem = TOOL_CYCLE.get(nextIndex).get();
            ItemStack nextStack = currentStack.transmuteCopy(nextItem, 1);
            nextStack.set(ModDataComponents.TOOL_MODE_INDEX.get(), nextIndex);
            player.setItemInHand(hand, nextStack);
        }
    }

    default Component getName(ItemStack stack) {
        String text = Component.translatable(stack.getDescriptionId()).getString();

        ChatFormatting[] rainbowColors = new ChatFormatting[]{
                ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.YELLOW,
                ChatFormatting.GREEN, ChatFormatting.AQUA, ChatFormatting.BLUE,
                ChatFormatting.LIGHT_PURPLE
        };

        return ColorHelper.getSolidColoredText(text, rainbowColors);
    }
}

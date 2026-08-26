package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class UfoEnergyShovelItem extends ShovelItem implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final int ENERGY_COST_NORMAL = 100;
    private static final int ENERGY_COST_FAST = 1000;

    public UfoEnergyShovelItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties.stacksTo(1));
    }

    // --- CORREÇÃO ADICIONADA AQUI ---
    @Override
    public Component getName(ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public int getEnergyPerUse() {
        return ENERGY_COST_NORMAL;
    }

    @Override
    public void cycleMode(ItemStack stack, Player player) {
        boolean newMode = !stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        stack.set(ModDataComponents.FAST_MODE.get(), newMode);
    }

    @Override
    public Component getModeHudComponent(ItemStack stack) {
        boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        Component modeText = isFast
                ? Component.translatable("tooltip.ufo.mode.fast").withStyle(ChatFormatting.RED)
                : Component.translatable("tooltip.ufo.mode.normal").withStyle(ChatFormatting.GREEN);
        return Component.translatable("tooltip.ufo.current_mode", modeText);
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide && pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            int cost = pStack.getOrDefault(ModDataComponents.FAST_MODE.get(), false) ? ENERGY_COST_FAST : ENERGY_COST_NORMAL;
            consumeEnergy(pStack, cost);
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        boolean isFast = pStack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        IEnergyStorage energy = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            if (isFast && energy.getEnergyStored() >= ENERGY_COST_FAST) {
                return Float.MAX_VALUE;
            } else if (!isFast && energy.getEnergyStored() >= ENERGY_COST_NORMAL) {
                return super.getDestroySpeed(pStack, pState);
            }
        }
        return 1.0F;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return EnergyToolHelper.isBarVisible(pStack);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return EnergyToolHelper.getBarWidth(pStack);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return EnergyToolHelper.getBarColor(pStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(getModeHudComponent(pStack));
        IEnergyTool.super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    private boolean consumeEnergy(ItemStack stack, int amount) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null && energy.getEnergyStored() >= amount) {
            energy.extractEnergy(amount, false);
            return true;
        }
        return false;
    }
}

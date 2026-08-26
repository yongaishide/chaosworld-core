package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class UfoEnergyPickaxeItem extends PickaxeItem implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final int ENERGY_COST_NORMAL = 200;
    private static final int ENERGY_COST_FAST = 2000;

    public UfoEnergyPickaxeItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties.stacksTo(1));
    }

    // --- CORREÇÃO DO NOME RAINBOW ---
    @Override
    public Component getName(ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public void cycleMode(ItemStack stack, Player player) {
        boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        boolean newMode = !isFast;
        stack.set(ModDataComponents.FAST_MODE.get(), newMode);

        Component modeText = newMode ?
                Component.translatable("tooltip.ufo.mode.fast").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)) :
                Component.translatable("tooltip.ufo.mode.normal").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

    }



    @Override
    public Component getModeHudComponent(ItemStack stack) {
        boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);

        Component modeText = isFast ?
                Component.translatable("tooltip.ufo.mode.fast").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)) :
                Component.translatable("tooltip.ufo.mode.normal").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

        return Component.translatable("tooltip.ufo.current_mode", modeText);
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
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide && pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            int cost = pStack.getOrDefault(ModDataComponents.FAST_MODE.get(), false) ? ENERGY_COST_FAST : ENERGY_COST_NORMAL;
            consumeEnergy(pStack, cost);
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide && player.isShiftKeyDown()) {
            boolean currentSmelt = stack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false);
            stack.set(ModDataComponents.AUTO_SMELT.get(), !currentSmelt);
            player.sendSystemMessage(Component.literal("Auto-Smelt: " + (!currentSmelt ? "ON" : "OFF"))
                    .withStyle(!currentSmelt ? ChatFormatting.GREEN : ChatFormatting.RED));
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        boolean smite = pStack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false);
        int fortune = pStack.getOrDefault(ModDataComponents.PROGRESSIVE_FORTUNE.get(), 0);

        pTooltipComponents.add(Component.literal("Auto-Smelt: " + (smite ? "ON" : "OFF")).withStyle(smite ? ChatFormatting.GOLD : ChatFormatting.GRAY));
        pTooltipComponents.add(Component.literal("Prog. Fortune: " + fortune + "/300").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) { return EnergyToolHelper.isBarVisible(pStack); }
    @Override
    public int getBarWidth(ItemStack pStack) { return EnergyToolHelper.getBarWidth(pStack); }
    @Override
    public int getBarColor(ItemStack pStack) { return EnergyToolHelper.getBarColor(pStack); }
    @Override
    public int getEnergyPerUse() { return ENERGY_COST_NORMAL; }

    private boolean consumeEnergy(ItemStack stack, int amount) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null && energy.getEnergyStored() >= amount) {
            energy.extractEnergy(amount, false);
            return true;
        }
        return false;
    }
}

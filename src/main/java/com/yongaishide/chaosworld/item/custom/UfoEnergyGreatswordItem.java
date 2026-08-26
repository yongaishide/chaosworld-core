package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.util.ColorHelper;
import com.yongaishide.chaosworld.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class UfoEnergyGreatswordItem extends SwordItem implements IEnergyTool {

    public UfoEnergyGreatswordItem(Tier pTier, Properties pProperties) {
        // 基础攻击力 = 656 + tier(10) = 666（与剑一致）
        super(pTier, pProperties.attributes(SwordItem.createAttributes(pTier, 656, -3.5F)).stacksTo(1));
    }

    // --- CORREÇÃO ADICIONADA AQUI ---
    @Override
    public Component getName(ItemStack stack) {
        return ColorHelper.getRainbowGradientText(Component.translatable(stack.getDescriptionId()).getString());
    }

    @Override
    public int getEnergyPerUse() {
        return 150;
    }

    @Override
    public int getSoulHarvestDamagePerKill() {
        return 10;
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (consumeEnergy(pStack)) {
            return super.hurtEnemy(pStack, pTarget, pAttacker);
        }
        return false;
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
        int kills = pStack.getOrDefault(ModDataComponents.KILL_COUNT.get(), 0);
        int bonusDmg = kills * 10;
        pTooltipComponents.add(Component.translatable("tooltip.ufo.sword.soul_harvest", kills).withStyle(ChatFormatting.DARK_RED));
        pTooltipComponents.add(Component.translatable("tooltip.ufo.sword.bonus_dmg", bonusDmg).withStyle(ChatFormatting.RED));
        IEnergyTool.super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }
}

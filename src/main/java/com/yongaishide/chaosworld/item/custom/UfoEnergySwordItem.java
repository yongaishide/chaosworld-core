package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.util.ColorHelper;
import com.yongaishide.chaosworld.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.List;

public class UfoEnergySwordItem extends SwordItem implements IEnergyTool {

    /** 固定修饰符 id，用于把灵魂收割的额外伤害加到攻击力属性上 */
    public static final ResourceLocation SOUL_HARVEST_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "soul_harvest_damage");

    public UfoEnergySwordItem(Tier pTier, Properties pProperties) {
        // 基础攻击力 = 656 + tier(10) = 666
        super(pTier, pProperties.attributes(SwordItem.createAttributes(pTier, 656, -2.0F)).stacksTo(1));
    }

    /**
     * 根据当前击杀数更新物品的攻击力属性修饰符（额外伤害 = 击杀数 × 每级加成）。
     * 剑每击杀 +5，巨剑每击杀 +10（见 getSoulHarvestDamagePerKill）。
     * 在击杀数变化后调用，使加成直接体现在武器攻击力上（tooltip 会显示）。
     */
    public static void updateSoulHarvestDamage(ItemStack stack) {
        int kills = stack.getOrDefault(ModDataComponents.KILL_COUNT.get(), 0);
        int perKill = stack.getItem() instanceof IEnergyTool tool ? tool.getSoulHarvestDamagePerKill() : 2;
        double bonus = kills * perKill;

        // 以物品默认组件里的属性（.attributes() 实为默认 ATTRIBUTE_MODIFIERS 组件）为基准，避免覆盖掉剑的基础属性
        ItemAttributeModifiers base = stack.getItem().components()
                .getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

        // 移除旧的灵魂收割修饰符，保留基础属性
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        for (var entry : base.modifiers()) {
            if (!(entry.attribute().equals(Attributes.ATTACK_DAMAGE)
                    && entry.modifier().id().equals(SOUL_HARVEST_DAMAGE_ID))) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
        }
        if (bonus > 0) {
            builder.add(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(SOUL_HARVEST_DAMAGE_ID, bonus, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND);
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    // --- CORREÇÃO ADICIONADA AQUI ---
    @Override
    public Component getName(ItemStack stack) {
        return ColorHelper.getRainbowGradientText(Component.translatable(stack.getDescriptionId()).getString());
    }

    @Override
    public int getEnergyPerUse() {
        return 100;
    }

    @Override
    public int getSoulHarvestDamagePerKill() {
        return 5;
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
        int bonusDmg = kills * 5;
        pTooltipComponents.add(Component.translatable("tooltip.ufo.sword.soul_harvest", kills).withStyle(ChatFormatting.DARK_RED));
        pTooltipComponents.add(Component.translatable("tooltip.ufo.sword.bonus_dmg", bonusDmg).withStyle(ChatFormatting.RED));
        IEnergyTool.super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }
}
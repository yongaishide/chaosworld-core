package com.yongaishide.chaosworld.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.List;

public class RealityRipperItem extends SwordItem {

    public RealityRipperItem(Tier tier, Item.Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, 20, -2.4F)).stacksTo(1));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        execute(target, attacker);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        execute(entity, player);
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.ufo.reality_ripper.damage").withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tooltip.ufo.reality_ripper.creative_kill").withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.translatable("tooltip.ufo.reality_ripper.flavor").withStyle(ChatFormatting.LIGHT_PURPLE));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    private static void execute(Entity entity, Entity attacker) {
        if (entity == null || entity.level().isClientSide()) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getHealth() <= 0.0F) {
                livingEntity.remove(RemovalReason.KILLED);
                return;
            }

            if (livingEntity instanceof Player targetPlayer && targetPlayer.getAbilities().invulnerable) {
                targetPlayer.getAbilities().invulnerable = false;
                targetPlayer.onUpdateAbilities();
            }

            livingEntity.invulnerableTime = 0;
            livingEntity.hurt(attacker.damageSources().playerAttack(attacker instanceof Player player ? player : null), Float.MAX_VALUE);
            var maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(0.0D);
            }
            livingEntity.setHealth(0.0F);
            livingEntity.die(attacker.damageSources().genericKill());
            return;
        }

        if (entity instanceof PartEntity<?> partEntity) {
            execute(partEntity.getParent(), attacker);
            return;
        }

        entity.hurt(attacker.damageSources().genericKill(), Float.MAX_VALUE);
        entity.remove(RemovalReason.KILLED);
    }
}

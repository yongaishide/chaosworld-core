package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class UfoEnergyBowItem extends BowItem implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final int ENERGY_COST_NORMAL = 500;
    private static final int ENERGY_COST_FAST = 25000;

    public UfoEnergyBowItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }
    @Override
    public Component getName(ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }
    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (!(pEntityLiving instanceof Player player)) {
            return;
        }

        boolean isFastMode = pStack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        IEnergyStorage energy = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return;

        // --- MODO RÁPIDO ---
        if (isFastMode) {
            if (energy.getEnergyStored() >= ENERGY_COST_FAST) {
                if (!pLevel.isClientSide) {
                    energy.extractEnergy(ENERGY_COST_FAST, false);
                    for (int i = 0; i < 5; i++) {
                        // --- CORREÇÃO 1 APLICADA AQUI ---
                        Arrow arrow = new Arrow(EntityType.ARROW, pLevel);
                        arrow.setOwner(player);
                        arrow.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                        // --- FIM DA CORREÇÃO 1 ---

                        arrow.setCritArrow(true);
                        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 10.0F);
                        pLevel.addFreshEntity(arrow);
                    }
                }
                pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            }
            return;
        }

        // --- MODO NORMAL ---
        ItemStack ammo = player.getProjectile(pStack);
        boolean isCreative = player.getAbilities().instabuild;

        if (!ammo.isEmpty() || isCreative) {
            if (ammo.isEmpty()) {
                ammo = new ItemStack(Items.ARROW);
            }

            int useDuration = this.getUseDuration(pStack, player) - pTimeLeft;
            float power = getPowerForTime(useDuration);

            if (power >= 0.1F && energy.getEnergyStored() >= ENERGY_COST_NORMAL) {
                if (!pLevel.isClientSide) {
                    energy.extractEnergy(ENERGY_COST_NORMAL, false);

                    ArrowItem arrowitem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);

                    // --- CORREÇÃO 2 APLICADA AQUI ---
                    // Adicionado 'pStack' como o quarto argumento
                    AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, ammo, player, pStack);
                    // --- FIM DA CORREÇÃO 2 ---

                    abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);

                    if (power == 1.0F) {
                        abstractarrow.setCritArrow(true);
                    }

                    pStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                    pLevel.addFreshEntity(abstractarrow);
                }

                pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

                if (!isCreative) {
                    ammo.shrink(1);
                    if (ammo.isEmpty()) {
                        player.getInventory().removeItem(ammo);
                    }
                }
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    // --- O resto da sua classe permanece igual ---

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        boolean isFastMode = itemstack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        boolean canFire = !pPlayer.getProjectile(itemstack).isEmpty() || isFastMode;
        if (!pPlayer.getAbilities().instabuild && !canFire) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            pPlayer.startUsingItem(pUsedHand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    @Override
    public void cycleMode(ItemStack stack, Player player) {
        boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        boolean newMode = !isFast;
        stack.set(ModDataComponents.FAST_MODE.get(), newMode);
        Component modeText = newMode ?
                Component.translatable("tooltip.ufo.mode.fast").withStyle(ChatFormatting.RED) :
                Component.translatable("tooltip.ufo.mode.normal").withStyle(ChatFormatting.GREEN);
        player.sendSystemMessage(Component.translatable("tooltip.ufo.mode_changed_to", modeText));
    }

    @Override
    public Component getModeHudComponent(ItemStack stack) {
        boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        Component modeText = isFast ?
                Component.translatable("tooltip.ufo.mode.fast").withStyle(ChatFormatting.RED) :
                Component.translatable("tooltip.ufo.mode.normal").withStyle(ChatFormatting.GREEN);

        return Component.translatable("tooltip.ufo.current_mode", modeText);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(getModeHudComponent(pStack));
        IEnergyTool.super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    @Override public int getUseDuration(ItemStack pStack, LivingEntity p_344558_) { return 72000; }
    @Override public int getEnergyPerUse() { return ENERGY_COST_NORMAL; }
    @Override public boolean isBarVisible(ItemStack pStack) { return EnergyToolHelper.isBarVisible(pStack); }
    @Override public int getBarWidth(ItemStack pStack) { return EnergyToolHelper.getBarWidth(pStack); }
    @Override public int getBarColor(ItemStack pStack) { return EnergyToolHelper.getBarColor(pStack); }
}
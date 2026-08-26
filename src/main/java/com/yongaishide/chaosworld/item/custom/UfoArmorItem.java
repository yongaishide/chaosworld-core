package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

// --- MUDAN脟A 1: Implementar a interface IEnergyTool ---
public class UfoArmorItem extends ArmorItem implements IEnergyTool {

    private static final int ENERGY_COST_PER_SECOND = 400; // 20 RF/tick * 20 ticks
    private static final int DRAIN_INTERVAL = 20; // Drain every 20 ticks (1 second) to avoid triggering equip sound
    private static final ResourceLocation ARMOR_HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "armor_health_boost");

    public UfoArmorItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    // --- MUDAN脟A 2: Adicionar o m茅todo getName para o efeito arco-铆ris ---
    @Override
    public Component getName(ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (this.getType() == Type.CHESTPLATE) {
                ItemStack equippedChestplate = player.getInventory().getArmor(Type.CHESTPLATE.getSlot().getIndex());

                if (equippedChestplate.getItem() instanceof UfoArmorItem) {
                    if (hasFullSuitOfArmorOn(player) && hasEnoughEnergy(player)) {
                        applyAllEffects(player);
                        // Only drain energy every DRAIN_INTERVAL ticks to avoid constant component changes
                        // which trigger the Minecraft equip sound detection
                        if (level.getGameTime() % DRAIN_INTERVAL == 0) {
                            drainEnergy(player);
                        }
                    } else {
                        removeAllEffects(player);
                    }
                } else {
                    removeAllEffects(player);
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    private void drainEnergy(Player player) {
        for (ItemStack armorStack : player.getInventory().armor) {
            if (armorStack.getItem() instanceof UfoArmorItem) {
                int currentEnergy = armorStack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                int newEnergy = Math.max(0, currentEnergy - ENERGY_COST_PER_SECOND);
                if (newEnergy != currentEnergy) {
                    armorStack.set(ModDataComponents.ENERGY.get(), newEnergy);
                }
            }
        }
    }

    private boolean hasEnoughEnergy(Player player) {
        for (ItemStack armorStack : player.getInventory().armor) {
            if (armorStack.getItem() instanceof UfoArmorItem) {
                int currentEnergy = armorStack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                if (currentEnergy < ENERGY_COST_PER_SECOND) {
                    return false;
                }
            }
        }
        return true;
    }

    private void applyAllEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 9, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0, false, false, true));

        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        net.minecraft.world.entity.ai.attributes.AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null && healthAttribute.getModifier(ARMOR_HEALTH_MODIFIER_ID) == null) {
            AttributeModifier modifier = new AttributeModifier(
                    ARMOR_HEALTH_MODIFIER_ID,
                    40.0,
                    AttributeModifier.Operation.ADD_VALUE
            );
            healthAttribute.addPermanentModifier(modifier);
        }
    }

    private void removeAllEffects(Player player) {
        net.minecraft.world.entity.ai.attributes.AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null && healthAttribute.getModifier(ARMOR_HEALTH_MODIFIER_ID) != null) {
            healthAttribute.removeModifier(ARMOR_HEALTH_MODIFIER_ID);
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }

        if (!player.getAbilities().instabuild && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        for (ItemStack armorStack : player.getInventory().armor) {
            if (!(armorStack.getItem() instanceof UfoArmorItem)) {
                return false;
            }
        }
        return true;
    }

    // --- M脡TODOS VISUAIS E DA INTERFACE ---
    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return EnergyToolHelper.getBarWidth(pStack);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return EnergyToolHelper.getBarColor(pStack);
    }

    // --- MUDAN脟A 3: Adicionar o m茅todo getEnergyPerUse para satisfazer a interface ---
    @Override
    public int getEnergyPerUse() {
        return 0; // O consumo de energia da armadura 茅 por tick, n茫o por uso.
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        if (Screen.hasShiftDown()) {
            IEnergyStorage energyStorage = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energyStorage != null) {
                String energyText = String.format("%,d / %,d RF", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored());
                pTooltipComponents.add(Component.literal(energyText).withStyle(ChatFormatting.GRAY));
            }
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.ufo.press_shift").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }
}
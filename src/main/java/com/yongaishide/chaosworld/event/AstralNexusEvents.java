package com.yongaishide.chaosworld.event;

import com.yongaishide.chaosworld.item.ModArmor;
import mekanism.api.radiation.capability.IRadiationEntity;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "chaosworld_core")
public class AstralNexusEvents {
    private static final ResourceLocation STEP_ASSIST_ID = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "astral_nexus_step_assist");
    private static final String FLIGHT_TAG = "ufoAstralNexusFlight";
    private static final float REFLECT_MULTIPLIER = 1_000_000.0F;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !isFullAstralNexus(player)) {
            return;
        }

        event.setCanceled(true);
        player.hurtTime = 0;
        player.deathTime = 0;
        player.setHealth(player.getMaxHealth());

        if (event.getSource().getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker && attacker != player) {
            attacker.invulnerableTime = 0;
            attacker.hurt(player.damageSources().thorns(player), Math.min(Float.MAX_VALUE, event.getAmount() * REFLECT_MULTIPLIER));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player) || !isFullAstralNexus(player)) {
            return;
        }
        event.setCanceled(true);
        player.hurtTime = 0;
        player.deathTime = 0;
        player.setHealth(player.getMaxHealth());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || player.tickCount % 10 != 0) {
            return;
        }

        if (isFullAstralNexus(player)) {
            applySetBonuses(player);
        } else {
            removeSetBonuses(player);
        }
    }

    private static void applySetBonuses(Player player) {
        player.setAirSupply(player.getMaxAirSupply());
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 220, 0, false, false, true));

        var stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight != null && stepHeight.getModifier(STEP_ASSIST_ID) == null) {
            stepHeight.addPermanentModifier(new AttributeModifier(STEP_ASSIST_ID, 0.5D, AttributeModifier.Operation.ADD_VALUE));
        }

        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }
        player.getPersistentData().putBoolean(FLIGHT_TAG, true);

        if (ModList.get().isLoaded("mekanism")) {
            IRadiationEntity radiationEntity = player.getCapability(Capabilities.RADIATION_ENTITY);
            if (radiationEntity != null) {
                radiationEntity.set(0);
            }
        }
    }

    private static void removeSetBonuses(Player player) {
        var stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight != null && stepHeight.getModifier(STEP_ASSIST_ID) != null) {
            stepHeight.removeModifier(STEP_ASSIST_ID);
        }

        if (player.getPersistentData().getBoolean(FLIGHT_TAG)) {
            if (!player.getAbilities().instabuild && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
            player.getPersistentData().remove(FLIGHT_TAG);
        }
    }

    public static boolean isFullAstralNexus(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModArmor.ASTRAL_NEXUS_HELMET.get()
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModArmor.ASTRAL_NEXUS_CHESTPLATE.get()
                && player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModArmor.ASTRAL_NEXUS_LEGGINGS.get()
                && player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModArmor.ASTRAL_NEXUS_BOOTS.get();
    }
}

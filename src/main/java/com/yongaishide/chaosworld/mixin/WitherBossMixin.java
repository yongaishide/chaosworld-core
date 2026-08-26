package com.yongaishide.chaosworld.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Buffs the wither's default max health from 300 to 4000 and restores full
 * health when the summoning charge-up completes (vanilla only heals a flat
 * 10 HP per 10 ticks, which cannot fill a 4000 HP bar).
 */
@Mixin(WitherBoss.class)
public abstract class WitherBossMixin {

    @Redirect(
            method = "createAttributes",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"))
    private static AttributeSupplier.Builder ufo$buffedWitherHealth(
            AttributeSupplier.Builder builder, Holder<Attribute> attribute, double defaultValue) {
        if (attribute.is(Attributes.MAX_HEALTH)) {
            defaultValue = 4000.0;
        }
        return builder.add(attribute, defaultValue);
    }

    @Inject(method = "setInvulnerableTicks(I)V", at = @At("HEAD"))
    private void ufo$restoreFullHealthAfterCharge(int invulnerableTicks, CallbackInfo ci) {
        // When the summoning charge-up finishes, the wither is at max/3 health and
        // vanilla's flat regeneration never fills a 4000 HP bar — restore it fully.
        if (invulnerableTicks <= 0) {
            WitherBoss self = (WitherBoss) (Object) this;
            self.setHealth(self.getMaxHealth());
        }
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void ufo$proportionalChargeHeal(CallbackInfo ci) {
        // Vanilla heals a flat 10 HP per 10 ticks during the charge-up. Make the
        // total heal proportional to max health: 10 (vanilla) + (max/220 - 10) = max/220.
        WitherBoss self = (WitherBoss) (Object) this;
        if (self.getInvulnerableTicks() > 0 && self.tickCount % 10 == 0) {
            self.heal(Math.max(0.0F, self.getMaxHealth() / 220.0F - 10.0F));
        }
    }
}

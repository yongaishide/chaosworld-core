package com.yongaishide.chaosworld.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Colors the Creative Energy Cell name like the ECO Infinite Storage Component
 * (Rarity.EPIC magenta).
 */
@Mixin(Item.class)
public class CreativeEnergyCellNameMixin {

    private static final ResourceLocation CREATIVE_ENERGY_CELL_ID =
            ResourceLocation.fromNamespaceAndPath("ae2", "creative_energy_cell");

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void chaosworld$colorCreativeEnergyCellName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        if ((Object) this == BuiltInRegistries.ITEM.get(CREATIVE_ENERGY_CELL_ID)) {
            cir.setReturnValue(Component.translatable(stack.getDescriptionId())
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }
}

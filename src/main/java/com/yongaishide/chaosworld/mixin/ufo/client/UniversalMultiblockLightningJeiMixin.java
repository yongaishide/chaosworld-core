package com.yongaishide.chaosworld.mixin.ufo.client;

import com.raishxn.ufo.compat.jei.UniversalMultiblockRecipeCategory;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;
import com.yongaishide.chaosworld.patch.ufo.OverloadLightningRequirements;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Patch layer: shows the lightning type/cost of AE2LT overload mappings on the
 * universal multiblock JEI page, matching the AE2LT overload factory category.
 */
@Mixin(value = UniversalMultiblockRecipeCategory.class, remap = false)
public abstract class UniversalMultiblockLightningJeiMixin {

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    private void chaosworld$lightningTooltip(ITooltipBuilder tooltip, UniversalMultiblockRecipe recipe,
            IRecipeSlotsView slots, double mouseX, double mouseY, CallbackInfo ci) {
        OverloadLightningRequirements.Requirement requirement =
                OverloadLightningRequirements.getByRecipeName(recipe.getRecipeName());
        if (requirement == null) {
            return;
        }
        tooltip.add(Component.translatable("jei.chaosworld_core.lightning_cost",
                requirement.amount(), requirement.key().getDisplayName()));
    }
}

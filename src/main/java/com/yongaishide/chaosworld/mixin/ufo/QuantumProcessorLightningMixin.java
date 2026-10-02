package com.yongaishide.chaosworld.mixin.ufo;

import java.util.Map;

import com.raishxn.ufo.block.entity.AbstractParallelMultiblockControllerBE;
import com.raishxn.ufo.block.entity.processing.KeyedTransferBatch;
import com.raishxn.ufo.block.entity.processing.MultiblockProcessingRecipe;
import com.raishxn.ufo.block.entity.processing.ParallelProcessState;
import com.yongaishide.chaosworld.patch.ufo.OverloadLightningRequirements;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Patch layer: AE2LT overload recipes mapped into the Quantum Processing Factory
 * consume their lightning type/cost from the ME network when they start. The
 * amount is recorded by {@link com.yongaishide.chaosworld.patch.ufo.PatchRecipes#fromOverload}
 * and tracked as a buffered input, so cancelling the process refunds it.
 */
@Mixin(value = AbstractParallelMultiblockControllerBE.class, remap = false)
public abstract class QuantumProcessorLightningMixin {

    @Inject(method = "planIngredientPulls", at = @At("RETURN"), cancellable = true, remap = false)
    private void chaosworld$consumeLightning(ParallelProcessState state, MultiblockProcessingRecipe recipe,
            MEStorage inventory, IActionSource src, Map<AEKey, Long> simulatedAvailability,
            KeyedTransferBatch<?, ?> inputBatch, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }
        OverloadLightningRequirements.Requirement requirement = OverloadLightningRequirements.get(recipe.id());
        if (requirement == null) {
            return;
        }
        for (GenericStack buffered : state.getBufferedInputs()) {
            if (requirement.key().equals(buffered.what())) {
                return;
            }
        }
        long extracted = inventory.extract(requirement.key(), requirement.amount(), Actionable.MODULATE, src);
        if (extracted < requirement.amount()) {
            if (extracted > 0L) {
                inventory.insert(requirement.key(), extracted, Actionable.MODULATE, src);
            }
            cir.setReturnValue(false);
            return;
        }
        state.recordBufferedInput(requirement.key(), requirement.amount());
    }
}

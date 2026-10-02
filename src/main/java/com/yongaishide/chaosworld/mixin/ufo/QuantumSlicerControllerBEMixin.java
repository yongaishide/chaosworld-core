package com.yongaishide.chaosworld.mixin.ufo;

import com.raishxn.ufo.block.entity.QuantumSlicerControllerBE;
import com.raishxn.ufo.block.entity.processing.MultiblockProcessingRecipe;
import com.raishxn.ufo.init.ModRecipes;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.List;

/**
 * Patch layer: the Quantum Slicer runs all {@code ufo:universal_multiblock} recipes
 * registered for its machine kind. The ExtendedAE circuit cutter mapping is
 * registered by the RecipeManager mixin as regular universal recipes.
 */
@Mixin(value = QuantumSlicerControllerBE.class, remap = false)
public abstract class QuantumSlicerControllerBEMixin {

    @Overwrite(remap = false)
    protected List<MultiblockProcessingRecipe> getAvailableRecipes() {
        var level = ((BlockEntity) (Object) this).getLevel();
        if (level == null) {
            return List.of();
        }

        List<MultiblockProcessingRecipe> recipes = new ArrayList<>();
        for (RecipeHolder<?> holder : level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get())) {
            if (holder.value() instanceof UniversalMultiblockRecipe recipe
                    && recipe.getMachine() == UniversalMultiblockMachineKind.QUANTUM_SLICER) {
                recipes.add(MultiblockProcessingRecipe.fromUniversal(holder.id(), recipe));
            }
        }
        return recipes;
    }
}

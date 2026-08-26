package com.yongaishide.chaosworld.compat.kubejs;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * KubeJS recipe binding for {@link com.yongaishide.chaosworld.recipe.StellarSimulationRecipe}.
 * The schema (see {@link UfoKubeJSPlugin}) handles serialization in the mod's native
 * JSON format; this class just pins the serializer.
 */
public class StellarKubeRecipe extends KubeRecipe {

    @Override
    public RecipeSerializer<?> kjs$getSerializer() {
        return com.yongaishide.chaosworld.init.ModRecipes.STELLAR_SIMULATION_SERIALIZER.get();
    }
}

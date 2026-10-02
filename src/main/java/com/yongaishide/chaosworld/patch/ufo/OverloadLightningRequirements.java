package com.yongaishide.chaosworld.patch.ufo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.moakiee.ae2lt.me.key.LightningKey;

import appeng.api.stacks.AEKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Lightning cost of the AE2LT overload recipes mapped into the Quantum
 * Processing Factory. The machine mixin extracts this lightning from the ME
 * network when a mapped recipe starts, and the normal buffered-input refund
 * logic returns it if the process is cancelled.
 */
public final class OverloadLightningRequirements {

    public record Requirement(AEKey key, long amount) {
    }

    private static final Map<ResourceLocation, Requirement> REQUIREMENTS = new ConcurrentHashMap<>();
    private static final Map<String, Requirement> BY_RECIPE_NAME = new ConcurrentHashMap<>();

    private OverloadLightningRequirements() {
    }

    public static void record(ResourceLocation recipeId, LightningKey.Tier tier, long amount) {
        if (recipeId == null || tier == null || amount <= 0L) {
            return;
        }
        Requirement requirement = new Requirement(LightningKey.of(tier), amount);
        REQUIREMENTS.put(recipeId, requirement);
        BY_RECIPE_NAME.put(recipeId.getPath(), requirement);
    }

    public static Requirement get(ResourceLocation recipeId) {
        return recipeId == null ? null : REQUIREMENTS.get(recipeId);
    }

    /** Lookup used by the JEI tooltip, keyed by {@code UniversalMultiblockRecipe#getRecipeName()}. */
    public static Requirement getByRecipeName(String recipeName) {
        return recipeName == null ? null : BY_RECIPE_NAME.get(recipeName);
    }
}

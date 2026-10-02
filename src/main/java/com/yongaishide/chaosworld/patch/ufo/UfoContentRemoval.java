package com.yongaishide.chaosworld.patch.ufo;

import java.util.Set;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Patch layer hook for the Chaos World "UFO Future reset" content removal.
 * All removal lists are intentionally empty: no UFO content is hidden from the
 * creative tabs, JEI or the recipe manager anymore.
 */
public final class UfoContentRemoval {

    private UfoContentRemoval() {
    }

    public static final Set<String> REMOVED_ITEM_PATHS = Set.of();

    public static final Set<String> REMOVED_RECIPE_PATHS = Set.of();

    public static boolean isRemovedItemId(@org.jetbrains.annotations.Nullable ResourceLocation id) {
        return false;
    }

    public static boolean isRemovedStack(ItemStack stack) {
        return false;
    }

    public static boolean isRemovedRecipe(ResourceLocation id, JsonElement json) {
        return false;
    }
}

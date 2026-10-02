package com.yongaishide.chaosworld.compat.ftbquests.client;

import dev.ftb.mods.ftblibrary.integration.JEIIntegration;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IIngredientManager;
import net.neoforged.fml.ModList;

import java.util.Optional;

public final class JeiRecipes {
    private JeiRecipes() {
    }

    public static boolean show(Object ingredient) {
        if (ingredient == null || !ModList.get().isLoaded("jei")) {
            return false;
        }

        IJeiRuntime runtime = JEIIntegration.runtime;
        if (runtime == null) {
            return false;
        }

        try {
            IIngredientManager manager = runtime.getIngredientManager();
            Optional<IIngredientType<Object>> type = manager.getIngredientTypeChecked(ingredient);
            if (type.isEmpty()) {
                return false;
            }
            Optional<ITypedIngredient<Object>> typed = manager.createTypedIngredient(type.get(), ingredient);
            if (typed.isEmpty()) {
                return false;
            }
            IFocus<Object> focus = runtime.getJeiHelpers().getFocusFactory()
                    .createFocus(RecipeIngredientRole.OUTPUT, typed.get());
            runtime.getRecipesGui().show(focus);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}

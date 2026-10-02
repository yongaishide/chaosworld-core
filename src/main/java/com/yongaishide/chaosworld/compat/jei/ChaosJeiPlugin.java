package com.yongaishide.chaosworld.compat.jei;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import com.yongaishide.chaosworld.init.ModRecipes;
import com.yongaishide.chaosworld.patch.ufo.UfoContentRemoval;

/**
 * JEI categories for Chaos World Core's own machines and data-driven recipes.
 * UFO Future registers its own JEI plugin for its multiblocks.
 */
@JeiPlugin
public class ChaosJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "jei_plugin");

    public ChaosJeiPlugin() {}

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        List<ItemStack> removed = new java.util.ArrayList<>();
        for (var item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
            if (UfoContentRemoval.isRemovedItemId(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item))) {
                removed.add(new ItemStack(item));
            }
        }
        if (!removed.isEmpty()) {
            jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, removed);
        }

        if (net.neoforged.fml.ModList.get().isLoaded("ae2lt")) {
            try {
                jeiRuntime.getRecipeManager().hideRecipeCategory(
                        com.raishxn.ufo.compat.jei.MultiblockInfoCategory.RECIPE_TYPE);
            } catch (Throwable ignored) {
                // AE2LT/UFO API mismatch: keep the JEI runtime usable.
            }
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        var jeiHelpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new WitherSummonRecipeCategory(jeiHelpers));
        registry.addRecipeCategories(new AssemblingRecipeCategory(jeiHelpers));
        registry.addRecipeCategories(new DragonSoulForgingRecipeCategory(jeiHelpers));
        registry.addRecipeCategories(new VeinDrillRecipeCategory(jeiHelpers));
        registry.addRecipeCategories(new VeinRepairRecipeCategory(jeiHelpers));
        registry.addRecipeCategories(new FusionConversionRecipeCategory(jeiHelpers));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(WitherSummonRecipeCategory.RECIPE_TYPE, List.of(new WitherSummonInfo()));

        registration.addRecipes(
                AssemblingRecipeCategory.RECIPE_TYPE,
                List.copyOf(recipeManager.getAllRecipesFor(com.yongaishide.chaosworld.mekanism.MekanismMachines.ASSEMBLING).stream()
                        .map(RecipeHolder::value)
                        .filter(com.yongaishide.chaosworld.mekanism.recipe.BasicAssemblingRecipe.class::isInstance)
                        .map(com.yongaishide.chaosworld.mekanism.recipe.BasicAssemblingRecipe.class::cast)
                        .toList()));

        registration.addRecipes(
                DragonSoulForgingRecipeCategory.RECIPE_TYPE,
                List.copyOf(recipeManager.getAllRecipesFor(
                                com.yongaishide.chaosworld.mekanism.DragonSoulForgeMachines.DRAGON_SOUL_FORGING).stream()
                        .map(RecipeHolder::value)
                        .filter(com.yongaishide.chaosworld.mekanism.recipe.BasicDragonSoulForgingRecipe.class::isInstance)
                        .map(com.yongaishide.chaosworld.mekanism.recipe.BasicDragonSoulForgingRecipe.class::cast)
                        .toList()));

        List<VeinDrillPoolInfo> veinPools = VeinDrillRecipeCategory.allDimensions();
        registration.addRecipes(VeinDrillRecipeCategory.RECIPE_TYPE, veinPools);

        registration.addRecipes(VeinRepairRecipeCategory.RECIPE_TYPE, VeinRepairRecipeCategory.all());

        registration.addRecipes(
                FusionConversionRecipeCategory.RECIPE_TYPE,
                List.copyOf(recipeManager.getAllRecipesFor(ModRecipes.FUSION_CONVERSION_TYPE.get()).stream()
                        .map(RecipeHolder::value)
                        .toList()));

        if (net.neoforged.fml.ModList.get().isLoaded("ae2lt")) {
            try {
                registration.addRecipes(
                        com.moakiee.ae2lt.integration.jei.category.MultiblockStructureCategory.TYPE,
                        UfoMultiblockStructures.all());
            } catch (Throwable ignored) {
                // AE2LT API mismatch: keep the rest of the JEI pages working.
            }
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                com.yongaishide.chaosworld.mekanism.MekanismMachines.MECHANICAL_ASSEMBLER.asItem().getDefaultInstance(),
                AssemblingRecipeCategory.RECIPE_TYPE);
        var factoryBlocks = com.yongaishide.chaosworld.mekanism.AssemblingFactoryMachines.FACTORY_BLOCKS;
        for (int i = 0; i < com.yongaishide.chaosworld.mekanism.AssemblingFactoryMachines.getCount(); i++) {
            registration.addRecipeCatalyst(factoryBlocks[i].asItem().getDefaultInstance(), AssemblingRecipeCategory.RECIPE_TYPE);
        }

        var forgeBlocks = com.yongaishide.chaosworld.mekanism.DragonSoulForgeMachines.FACTORY_BLOCKS;
        registration.addRecipeCatalyst(
                com.yongaishide.chaosworld.mekanism.DragonSoulForgeMachines.DRAGON_SOUL_FORGE.asItem().getDefaultInstance(),
                DragonSoulForgingRecipeCategory.RECIPE_TYPE);
        for (int i = 0; i < com.yongaishide.chaosworld.mekanism.DragonSoulForgeMachines.getCount(); i++) {
            registration.addRecipeCatalyst(forgeBlocks[i].asItem().getDefaultInstance(), DragonSoulForgingRecipeCategory.RECIPE_TYPE);
        }

        registration.addRecipeCatalyst(
                com.yongaishide.chaosworld.mekanism.vein.VeinDrillMachines.VEIN_DRILL.asItem().getDefaultInstance(),
                VeinDrillRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(
                com.yongaishide.chaosworld.mekanism.vein.VeinDrillMachines.VEIN_DRILL.asItem().getDefaultInstance(),
                VeinRepairRecipeCategory.RECIPE_TYPE);

        var fusionController = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("mekanismgenerators", "fusion_reactor_controller"));
        if (fusionController != net.minecraft.world.item.Items.AIR) {
            registration.addRecipeCatalyst(new ItemStack(fusionController), FusionConversionRecipeCategory.RECIPE_TYPE);
        }

        if (net.neoforged.fml.ModList.get().isLoaded("ae2lt")) {
            try {
                for (var preview : com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions.getPreviewEntries()) {
                    registration.addRecipeCatalyst(preview.iconStack(),
                            com.moakiee.ae2lt.integration.jei.category.MultiblockStructureCategory.TYPE);
                }
            } catch (Throwable ignored) {
                // AE2LT API mismatch: keep the rest of the JEI pages working.
            }
        }
    }
}

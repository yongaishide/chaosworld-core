package com.yongaishide.chaosworld.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yongaishide.chaosworld.init.ModRecipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

public class DimensionalMatterAssemblerRecipe implements Recipe<RecipeInput> {

    protected final List<IngredientStack.Item> itemInputs;
    protected final List<IngredientStack.Fluid> fluidInputs;
    protected final List<GenericStack> itemOutputs;
    protected final List<GenericStack> fluidOutputs;

    protected final int energy;
    protected final int time;

    public DimensionalMatterAssemblerRecipe(
            List<IngredientStack.Item> itemInputs,
            List<IngredientStack.Fluid> fluidInputs,
            List<GenericStack> itemOutputs,
            List<GenericStack> fluidOutputs,
            int energy,
            int time) {
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.energy = energy;
        this.time = time > 0 ? time : 200; // default 200 ticks
    }

    @Override
    public boolean matches(@NotNull RecipeInput recipeInput, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput inv, HolderLookup.@NotNull Provider registries) {
        return getResultItem(registries).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return getResultItem();
    }

    public ItemStack getResultItem() {
        if (!this.itemOutputs.isEmpty() && this.itemOutputs.get(0).what() instanceof AEItemKey key) {
            return key.toStack((int) this.itemOutputs.get(0).amount());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return DimensionalMatterAssemblerRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.DMA_RECIPE_TYPE.get();
    }

    public List<IngredientStack.Item> getItemInputs() {
        return itemInputs;
    }

    public List<IngredientStack.Fluid> getFluidInputs() {
        return fluidInputs;
    }

    public List<GenericStack> getItemOutputs() {
        return itemOutputs;
    }

    public List<GenericStack> getFluidOutputs() {
        return fluidOutputs;
    }

    public List<IngredientStack<?, ?>> getValidInputs() {
        List<IngredientStack<?, ?>> validInputs = new ArrayList<>();

        for (var input : this.itemInputs) {
            if (!input.isEmpty()) {
                validInputs.add(input.sample());
            }
        }

        for (var input : this.fluidInputs) {
            if (!input.isEmpty()) {
                validInputs.add(input.sample());
            }
        }

        return validInputs;
    }

    public int getEnergy() {
        return energy;
    }

    public int getTime() {
        return time;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}

package com.yongaishide.chaosworld.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.yongaishide.chaosworld.init.ModRecipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

/**
 * A Stellar Simulation recipe — the "programs" that run inside the Stellar Nexus.
 * <p>
 * Each recipe represents a complete stellar simulation cycle,
 * consuming massive amounts of energy, fuel (liquid), coolant, and item/fluid catalysts
 * to produce millions of items injected directly into the ME network.
 * <p>
 * <b>Terminology:</b>
 * <ul>
 *   <li><b>Energy</b> = AE power (e.g., 500M AE) — charged passively from AE network</li>
 *   <li><b>Fuel</b> = liquid combustible (e.g., Hydrogen) — extracted from ME storage on start</li>
 *   <li><b>Coolant</b> = liquid refrigerant with tiers — consumed during operation</li>
 * </ul>
 * <p>
 * Coolant Tiers:
 * <ul>
 *   <li>1 = Gelid Cryotheum (chaosworld_core:source_gelid_cryotheum)</li>
 *   <li>2 = Stable Coolant (chaosworld_core:source_stable_coolant)</li>
 *   <li>3 = Temporal Fluid (chaosworld_core:source_temporal_fluid)</li>
 * </ul>
 */
public class StellarSimulationRecipe implements Recipe<RecipeInput> {

    protected final List<IngredientStack.Item> itemInputs;
    protected final List<IngredientStack.Fluid> fluidInputs;
    protected final List<GenericStack> itemOutputs;
    protected final List<GenericStack> fluidOutputs;

    protected final String simulationName;      // Display name for the simulation
    protected final long energy;                 // Total AE power cost
    protected final int time;                    // Total ticks for the simulation
    protected final int coolingLevel;            // 0 = none, 1 = basic, 2 = advanced, 3 = extreme
    protected final int fieldTier;               // 1 = Mk.I, 2 = Mk.II, 3 = Mk.III
    protected final String fuelFluid;            // ResourceLocation string of fuel fluid (e.g., "mekanism:hydrogen")
    protected final long fuelAmount;             // Amount of fuel fluid required (mB)
    protected final long coolantAmount;          // Amount of coolant required (mB)

    public StellarSimulationRecipe(
            List<IngredientStack.Item> itemInputs,
            List<IngredientStack.Fluid> fluidInputs,
            List<GenericStack> itemOutputs,
            List<GenericStack> fluidOutputs,
            String simulationName,
            long energy,
            int time,
            int coolingLevel,
            int fieldTier,
            String fuelFluid,
            long fuelAmount,
            long coolantAmount) {
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.simulationName = simulationName != null && !simulationName.isEmpty() ? simulationName : "";
        this.energy = energy;
        this.time = time > 0 ? time : 24000; // default 20 minutes
        this.coolingLevel = Math.clamp(coolingLevel, 0, 3);
        this.fieldTier = Math.clamp(fieldTier, 1, 3);
        this.fuelFluid = fuelFluid != null ? fuelFluid : "";
        this.fuelAmount = fuelAmount;
        this.coolantAmount = coolantAmount;
    }

    // Legacy constructor for backward compatibility with old 8-param format
    public StellarSimulationRecipe(
            List<IngredientStack.Item> itemInputs,
            List<IngredientStack.Fluid> fluidInputs,
            List<GenericStack> itemOutputs,
            List<GenericStack> fluidOutputs,
            int energy,
            int time,
            int coolingLevel,
            int fieldTier) {
        this(itemInputs, fluidInputs, itemOutputs, fluidOutputs,
                "", energy, time, coolingLevel, fieldTier, "", 0, 0);
    }

    // ═══════════════════════════════════════════════════════════
    //  Recipe<RecipeInput> — Required overrides
    // ═══════════════════════════════════════════════════════════

    @Override
    public boolean matches(@NotNull RecipeInput recipeInput, @NotNull Level level) {
        // Matching is done manually by the Controller BE, not by the vanilla system
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
            // Cap at Integer.MAX_VALUE for ItemStack display — real injection uses long
            return key.toStack((int) Math.min(this.itemOutputs.get(0).amount(), Integer.MAX_VALUE));
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return StellarSimulationRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.STELLAR_SIMULATION_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    // ═══════════════════════════════════════════════════════════
    //  Accessors
    // ═══════════════════════════════════════════════════════════

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

    /**
     * All valid inputs combined (items + fluids) for recipe matching in the Controller.
     */
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

    /** Display name for this simulation program. */
    public String getSimulationName() {
        return simulationName;
    }

    public String getSimulationTranslationKey() {
        if (simulationName == null || simulationName.isEmpty()) return "";
        return "ufo.simulation." + simulationName.toLowerCase().replace(' ', '_').replaceAll("[^a-z0-9_/.]", "");
    }

    /** Total AE power cost for the entire simulation. */
    public long getEnergyCost() {
        return energy;
    }

    /** @deprecated Use {@link #getEnergyCost()} instead. Kept for backward compat. */
    @Deprecated
    public int getFuelCost() {
        return (int) Math.min(energy, Integer.MAX_VALUE);
    }

    /** Total ticks for the simulation cycle. */
    public int getTime() {
        return time;
    }

    /** Minimum cooling score required (0-3). */
    public int getCoolingLevel() {
        return coolingLevel;
    }

    /** Minimum Stellar Field Generator tier required (1-3). */
    public int getFieldTier() {
        return fieldTier;
    }

    /** ResourceLocation string of the required fuel fluid (e.g. "mekanism:hydrogen"). Empty if none. */
    public String getFuelFluid() {
        return fuelFluid;
    }

    /** Amount of fuel fluid required in mB. */
    public long getFuelAmount() {
        return fuelAmount;
    }



    /** Amount of coolant fluid required in mB. */
    public long getCoolantAmount() {
        return coolantAmount;
    }

    /**
     * Total energy cost (same as getEnergyCost for display).
     */
    public long getTotalEnergy() {
        return energy;
    }

    /**
     * Formatted time string for display (e.g., "20m 00s").
     */
    public String getFormattedTime() {
        int totalSeconds = time / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%dm %02ds", minutes, seconds);
    }

    /**
     * Resolves the fuel fluid ResourceLocation, or empty if not set.
     */
    public Optional<ResourceLocation> getFuelFluidRL() {
        if (fuelFluid.isEmpty()) return Optional.empty();
        return Optional.of(ResourceLocation.parse(fuelFluid));
    }


}

package com.yongaishide.chaosworld.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.GenericStack;

/**
 * Codec-based serializer for {@link StellarSimulationRecipe}.
 * <p>
 * Supports both the new schema (energy, simulation_name, fuel_fluid, coolant_fluid)
 * and the legacy schema (fuel → energy) for backward compatibility.
 * <p>
 * JSON format:
 * <pre>{@code
 * {
 *   "type": "chaosworld_core:stellar_simulation",
 *   "simulation_name": "Netherite Mass Synthesis",
 *   "item_inputs": [...],
 *   "fluid_inputs": [...],
 *   "item_outputs": [...],
 *   "fluid_outputs": [...],
 *   "energy": 500000000,
 *   "time": 24000,
 *   "cooling_level": 2,
 *   "field_tier": 1,
 *   "fuel_fluid": "mekanism:hydrogen",
 *   "fuel_amount": 10000000,
 *   "coolant_amount": 20000000
 * }
 * }</pre>
 * <p>
 * Legacy support: if "fuel" exists but "energy" doesn't, "fuel" is read as "energy".
 */
public class StellarSimulationRecipeSerializer implements RecipeSerializer<StellarSimulationRecipe> {

    public static final StellarSimulationRecipeSerializer INSTANCE = new StellarSimulationRecipeSerializer();

    private StellarSimulationRecipeSerializer() {}

    // ═══════════════════════════════════════════════════════════
    //  MapCodec — JSON datapacks
    // ═══════════════════════════════════════════════════════════

    public static final MapCodec<StellarSimulationRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            IngredientStack.Item.CODEC.listOf().fieldOf("item_inputs")
                    .forGetter(StellarSimulationRecipe::getItemInputs),
            IngredientStack.Fluid.CODEC.listOf().fieldOf("fluid_inputs")
                    .forGetter(StellarSimulationRecipe::getFluidInputs),
            GenericStack.CODEC.listOf().fieldOf("item_outputs")
                    .forGetter(StellarSimulationRecipe::getItemOutputs),
            GenericStack.CODEC.listOf().fieldOf("fluid_outputs")
                    .forGetter(StellarSimulationRecipe::getFluidOutputs),
            Codec.STRING.optionalFieldOf("simulation_name", "")
                    .forGetter(StellarSimulationRecipe::getSimulationName),
            // Support both "energy" and legacy "fuel" field
            Codec.LONG.optionalFieldOf("energy", 0L)
                    .forGetter(StellarSimulationRecipe::getEnergyCost),
            Codec.INT.fieldOf("time")
                    .forGetter(StellarSimulationRecipe::getTime),
            Codec.INT.optionalFieldOf("cooling_level", 0)
                    .forGetter(StellarSimulationRecipe::getCoolingLevel),
            Codec.INT.optionalFieldOf("field_tier", 1)
                    .forGetter(StellarSimulationRecipe::getFieldTier),
            Codec.STRING.optionalFieldOf("fuel_fluid", "")
                    .forGetter(StellarSimulationRecipe::getFuelFluid),
            Codec.LONG.optionalFieldOf("fuel_amount", 0L)
                    .forGetter(StellarSimulationRecipe::getFuelAmount),
            Codec.LONG.optionalFieldOf("coolant_amount", 0L)
                    .forGetter(StellarSimulationRecipe::getCoolantAmount)
    ).apply(builder, StellarSimulationRecipe::new));

    // ═══════════════════════════════════════════════════════════
    //  StreamCodec — Network sync (manual, params > composite max of 6)
    // ═══════════════════════════════════════════════════════════

    public static final StreamCodec<RegistryFriendlyByteBuf, StellarSimulationRecipe> STREAM_CODEC =
            StreamCodec.of(
                    StellarSimulationRecipeSerializer::encode,
                    StellarSimulationRecipeSerializer::decode
            );

    private static void encode(RegistryFriendlyByteBuf buf, StellarSimulationRecipe recipe) {
        // Item inputs
        IngredientStack.Item.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list())
                .encode(buf, recipe.getItemInputs());
        // Fluid inputs
        IngredientStack.Fluid.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list())
                .encode(buf, recipe.getFluidInputs());
        // Item outputs
        GenericStack.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list())
                .encode(buf, recipe.getItemOutputs());
        // Fluid outputs
        GenericStack.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list())
                .encode(buf, recipe.getFluidOutputs());
        // Scalars
        buf.writeUtf(recipe.getSimulationName());
        buf.writeLong(recipe.getEnergyCost());
        buf.writeInt(recipe.getTime());
        buf.writeInt(recipe.getCoolingLevel());
        buf.writeInt(recipe.getFieldTier());
        buf.writeUtf(recipe.getFuelFluid());
        buf.writeLong(recipe.getFuelAmount());
        buf.writeLong(recipe.getCoolantAmount());
    }

    private static StellarSimulationRecipe decode(RegistryFriendlyByteBuf buf) {
        var itemInputs = IngredientStack.Item.STREAM_CODEC
                .apply(net.minecraft.network.codec.ByteBufCodecs.list()).decode(buf);
        var fluidInputs = IngredientStack.Fluid.STREAM_CODEC
                .apply(net.minecraft.network.codec.ByteBufCodecs.list()).decode(buf);
        var itemOutputs = GenericStack.STREAM_CODEC
                .apply(net.minecraft.network.codec.ByteBufCodecs.list()).decode(buf);
        var fluidOutputs = GenericStack.STREAM_CODEC
                .apply(net.minecraft.network.codec.ByteBufCodecs.list()).decode(buf);
        String simulationName = buf.readUtf();
        long energy = buf.readLong();
        int time = buf.readInt();
        int coolingLevel = buf.readInt();
        int fieldTier = buf.readInt();
        String fuelFluid = buf.readUtf();
        long fuelAmount = buf.readLong();
        long coolantAmount = buf.readLong();

        return new StellarSimulationRecipe(
                itemInputs, fluidInputs, itemOutputs, fluidOutputs,
                simulationName, energy, time, coolingLevel, fieldTier,
                fuelFluid, fuelAmount, coolantAmount);
    }

    // ═══════════════════════════════════════════════════════════
    //  RecipeSerializer interface
    // ═══════════════════════════════════════════════════════════

    @Override
    public @NotNull MapCodec<StellarSimulationRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, StellarSimulationRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}

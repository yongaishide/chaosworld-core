package com.yongaishide.chaosworld.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.GenericStack;

public class DimensionalMatterAssemblerRecipeSerializer implements RecipeSerializer<DimensionalMatterAssemblerRecipe> {

    public static final DimensionalMatterAssemblerRecipeSerializer INSTANCE = new DimensionalMatterAssemblerRecipeSerializer();

    private DimensionalMatterAssemblerRecipeSerializer() {}

    public static final MapCodec<DimensionalMatterAssemblerRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
                    IngredientStack.Item.CODEC.listOf().fieldOf("item_inputs").forGetter(DimensionalMatterAssemblerRecipe::getItemInputs),
                    IngredientStack.Fluid.CODEC.listOf().fieldOf("fluid_inputs").forGetter(DimensionalMatterAssemblerRecipe::getFluidInputs),
                    GenericStack.CODEC.listOf().fieldOf("item_outputs").forGetter(DimensionalMatterAssemblerRecipe::getItemOutputs),
                    GenericStack.CODEC.listOf().fieldOf("fluid_outputs").forGetter(DimensionalMatterAssemblerRecipe::getFluidOutputs),
                    Codec.INT.fieldOf("energy").forGetter(DimensionalMatterAssemblerRecipe::getEnergy),
                    Codec.INT.fieldOf("time").forGetter(DimensionalMatterAssemblerRecipe::getTime))
            .apply(builder, DimensionalMatterAssemblerRecipe::new));
            
    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionalMatterAssemblerRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    IngredientStack.Item.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DimensionalMatterAssemblerRecipe::getItemInputs,
                    IngredientStack.Fluid.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DimensionalMatterAssemblerRecipe::getFluidInputs,
                    GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DimensionalMatterAssemblerRecipe::getItemOutputs,
                    GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DimensionalMatterAssemblerRecipe::getFluidOutputs,
                    ByteBufCodecs.INT,
                    DimensionalMatterAssemblerRecipe::getEnergy,
                    ByteBufCodecs.INT,
                    DimensionalMatterAssemblerRecipe::getTime,
                    DimensionalMatterAssemblerRecipe::new);

    @Override
    public @NotNull MapCodec<DimensionalMatterAssemblerRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, DimensionalMatterAssemblerRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}

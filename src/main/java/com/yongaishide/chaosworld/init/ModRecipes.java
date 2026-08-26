package com.yongaishide.chaosworld.init;

import com.yongaishide.chaosworld.recipe.DimensionalMatterAssemblerRecipe;
import com.yongaishide.chaosworld.recipe.DimensionalMatterAssemblerRecipeSerializer;
import com.yongaishide.chaosworld.recipe.QMFRecipe;
import com.yongaishide.chaosworld.recipe.StellarSimulationRecipe;
import com.yongaishide.chaosworld.recipe.StellarSimulationRecipeSerializer;
import com.yongaishide.chaosworld.recipe.UniversalMultiblockRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, "chaosworld_core");

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, "chaosworld_core");

    // DMA
    public static final String DMA_ID = "dimensional_assembly";

    public static final Supplier<RecipeType<DimensionalMatterAssemblerRecipe>> DMA_RECIPE_TYPE = RECIPE_TYPES.register(DMA_ID, () -> new RecipeType<DimensionalMatterAssemblerRecipe>() {
        @Override
        public String toString() {
            return DMA_ID;
        }
    });

    public static final Supplier<RecipeSerializer<DimensionalMatterAssemblerRecipe>> DMA_RECIPE_SERIALIZER =
            SERIALIZERS.register(DMA_ID, () -> DimensionalMatterAssemblerRecipeSerializer.INSTANCE);

    // STELLAR NEXUS
    public static final String STELLAR_SIMULATION_ID = "stellar_simulation";

    public static final Supplier<RecipeType<StellarSimulationRecipe>> STELLAR_SIMULATION_TYPE = RECIPE_TYPES.register(STELLAR_SIMULATION_ID, () -> new RecipeType<StellarSimulationRecipe>() {
        @Override
        public String toString() {
            return STELLAR_SIMULATION_ID;
        }
    });

    public static final Supplier<RecipeSerializer<StellarSimulationRecipe>> STELLAR_SIMULATION_SERIALIZER =
            SERIALIZERS.register(STELLAR_SIMULATION_ID, () -> StellarSimulationRecipeSerializer.INSTANCE);

    // QUANTUM MATTER FABRICATOR
    public static final String QMF_ID = "qmf_recipe";

    public static final Supplier<RecipeType<QMFRecipe>> QMF_TYPE = RECIPE_TYPES.register(QMF_ID, () -> new RecipeType<QMFRecipe>() {
        @Override
        public String toString() {
            return QMF_ID;
        }
    });

    public static final Supplier<RecipeSerializer<QMFRecipe>> QMF_SERIALIZER =
            SERIALIZERS.register(QMF_ID, QMFRecipe.Serializer::new);

    // UNIVERSAL MULTIBLOCK
    public static final String UNIVERSAL_MULTIBLOCK_ID = "universal_multiblock";

    public static final Supplier<RecipeType<UniversalMultiblockRecipe>> UNIVERSAL_MULTIBLOCK_TYPE = RECIPE_TYPES.register(UNIVERSAL_MULTIBLOCK_ID, () -> new RecipeType<UniversalMultiblockRecipe>() {
        @Override
        public String toString() {
            return UNIVERSAL_MULTIBLOCK_ID;
        }
    });

    public static final Supplier<RecipeSerializer<UniversalMultiblockRecipe>> UNIVERSAL_MULTIBLOCK_SERIALIZER =
            SERIALIZERS.register(UNIVERSAL_MULTIBLOCK_ID, UniversalMultiblockRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}

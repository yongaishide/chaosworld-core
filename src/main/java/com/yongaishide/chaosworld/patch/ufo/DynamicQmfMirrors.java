package com.yongaishide.chaosworld.patch.ufo;

import java.util.ArrayList;
import java.util.List;

import com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

/**
 * Builds the QMF bulk mirror of a DMA recipe at runtime, scaled by
 * {@link PatchRecipes#QMF_DMA_MULTIPLIER}. Used by the RecipeManager mixin to
 * replace the static data-generated {@code universal/qmf/bulk/*} recipes so the
 * QMF always matches the current (data-pack editable) DMA recipe.
 */
public final class DynamicQmfMirrors {

    public static final String BULK_PREFIX = "universal/qmf/bulk/";

    private DynamicQmfMirrors() {
    }

    public static boolean isBulkMirrorId(ResourceLocation id) {
        return "ufo".equals(id.getNamespace()) && id.getPath().startsWith(BULK_PREFIX);
    }

    public static ResourceLocation recipeId(String path) {
        return ResourceLocation.fromNamespaceAndPath("ufo", path);
    }

    /** The universal recipe format supports one item and one fluid output only. */
    public static boolean isMultiOutput(DimensionalMatterAssemblerRecipe recipe) {
        long outputs = 0L;
        for (var output : recipe.getItemOutputs()) {
            if (output != null && output.what() != null && output.amount() > 0L) {
                outputs++;
            }
        }
        for (var output : recipe.getFluidOutputs()) {
            if (output != null && output.what() != null && output.amount() > 0L) {
                outputs++;
            }
        }
        return outputs > 1L;
    }

    /** @return the DMA recipe id the mirror was generated from, or null. */
    public static ResourceLocation sourceDmaId(ResourceLocation mirrorId) {
        if (!isBulkMirrorId(mirrorId)) {
            return null;
        }
        return ResourceLocation.fromNamespaceAndPath("ufo",
                "dma/" + mirrorId.getPath().substring(BULK_PREFIX.length()));
    }

    public static RecipeHolder<?> buildMirror(ResourceLocation mirrorId, DimensionalMatterAssemblerRecipe recipe) {
        long multiplier = PatchRecipes.QMF_DMA_MULTIPLIER;

        List<UniversalMultiblockRecipe.ItemRequirement> itemInputs = new ArrayList<>();
        for (IngredientStack.Item input : recipe.getItemInputs()) {
            if (input == null || input.isEmpty()) {
                continue;
            }
            itemInputs.add(new UniversalMultiblockRecipe.ItemRequirement(input.getIngredient(),
                    Math.max(1L, input.getAmount()) * multiplier));
        }

        List<UniversalMultiblockRecipe.FluidRequirement> fluidInputs = new ArrayList<>();
        for (IngredientStack.Fluid input : recipe.getFluidInputs()) {
            if (input == null || input.isEmpty()) {
                continue;
            }
            FluidStack[] stacks = input.getIngredient().getStacks();
            if (stacks.length == 0) {
                continue;
            }
            fluidInputs.add(new UniversalMultiblockRecipe.FluidRequirement(stacks[0],
                    Math.max(1L, input.getAmount()) * multiplier));
        }

        ItemStack itemOutput = ItemStack.EMPTY;
        long itemOutputAmount = 0L;
        for (var output : recipe.getItemOutputs()) {
            if (output.what() instanceof AEItemKey itemKey) {
                itemOutput = itemKey.toStack(1);
                itemOutputAmount = Math.max(0L, output.amount()) * multiplier;
                break;
            }
        }

        FluidStack fluidOutput = FluidStack.EMPTY;
        long fluidOutputAmount = 0L;
        for (var output : recipe.getFluidOutputs()) {
            if (output.what() instanceof AEFluidKey fluidKey) {
                fluidOutput = fluidKey.toStack(1);
                fluidOutputAmount = Math.max(0L, output.amount()) * multiplier;
                break;
            }
        }

        if (itemOutput.isEmpty() && fluidOutput.isEmpty()) {
            return null;
        }

        UniversalMultiblockRecipe mirror = new UniversalMultiblockRecipe(
                UniversalMultiblockMachineKind.QMF,
                mirrorId.getPath(),
                itemInputs,
                fluidInputs,
                List.of(),
                itemOutput,
                itemOutputAmount,
                fluidOutput,
                fluidOutputAmount,
                Math.max(0L, (long) recipe.getEnergy()) * multiplier,
                recipe.getTime(),
                1);
        return new RecipeHolder<>(mirrorId, mirror);
    }
}

package com.yongaishide.chaosworld.patch.ufo;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.raishxn.ufo.api.multiblock.MultiblockMachineTier;
import com.raishxn.ufo.block.entity.processing.MultiblockProcessingRecipe;
import com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Patch layer: recipe mappings from the Chaos World "UFO Future reset".
 * <p>
 * These reproduce the reworked mappings that used to live in the ported
 * {@code UniversalMultiblockRecipe}/{@code MultiblockProcessingRecipe}:
 * QMF consumes DMA recipes scaled x64, the Quantum Slicer maps ExtendedAE
 * circuit cutter recipes scaled x64000 and the Quantum Processing Factory
 * maps Lightning Tech overload processing recipes scaled x20000.
 */
public final class PatchRecipes {

    private PatchRecipes() {}

    public static final long QMF_DMA_MULTIPLIER = 64L;

    public static final long SLICER_QUANTITY_MULTIPLIER = 64_000L;
    public static final int SLICER_PROCESSING_TICKS = 100 * 1000;

    public static final long OVERLOAD_QUANTITY_MULTIPLIER = 20_000L;
    public static final int OVERLOAD_PROCESSING_TICKS = 1600;

    public static MultiblockProcessingRecipe fromDma(ResourceLocation id, DimensionalMatterAssemblerRecipe recipe) {
        List<MultiblockProcessingRecipe.ItemRequirement> itemInputs = recipe.getItemInputs().stream()
                .filter(input -> input != null && !input.isEmpty())
                .map(input -> new MultiblockProcessingRecipe.ItemRequirement(input.getIngredient(),
                        Math.max(1L, input.getAmount()) * QMF_DMA_MULTIPLIER))
                .toList();

        List<MultiblockProcessingRecipe.FluidRequirement> fluidInputs = recipe.getFluidInputs().stream()
                .filter(input -> input != null && !input.isEmpty())
                .map(input -> {
                    FluidStack[] stacks = input.getIngredient().getStacks();
                    FluidStack fluid = stacks.length > 0 ? stacks[0] : FluidStack.EMPTY;
                    return new MultiblockProcessingRecipe.FluidRequirement(fluid,
                            Math.max(1L, input.getAmount()) * QMF_DMA_MULTIPLIER);
                })
                .filter(requirement -> !requirement.fluid().isEmpty())
                .toList();

        List<MultiblockProcessingRecipe.OutputStack> outputs = new ArrayList<>();
        for (var output : recipe.getItemOutputs()) {
            if (output.what() instanceof AEItemKey itemKey) {
                outputs.add(new MultiblockProcessingRecipe.OutputStack(itemKey.toStack(1), FluidStack.EMPTY,
                        Math.max(0L, output.amount()) * QMF_DMA_MULTIPLIER));
            }
        }
        for (var output : recipe.getFluidOutputs()) {
            if (output.what() instanceof AEFluidKey fluidKey) {
                outputs.add(new MultiblockProcessingRecipe.OutputStack(ItemStack.EMPTY, fluidKey.toStack(1),
                        Math.max(0L, output.amount()) * QMF_DMA_MULTIPLIER));
            }
        }

        return new MultiblockProcessingRecipe(id, "universal/qmf/dma/" + id.getPath(), itemInputs, fluidInputs,
                List.of(), outputs,
                Math.max(0L, (long) recipe.getEnergy()) * QMF_DMA_MULTIPLIER,
                recipe.getTime(), 1);
    }

    public static MultiblockProcessingRecipe fromCircuitCutter(ResourceLocation id,
            com.glodblock.github.extendedae.recipe.CircuitCutterRecipe recipe) {
        return MultiblockProcessingRecipe.fromUniversal(id, universalFromCircuitCutter(id, recipe));
    }

    /** Registers the ExtendedAE circuit cutter mapping as a data-pack/JEI visible universal recipe. */
    public static UniversalMultiblockRecipe universalFromCircuitCutter(ResourceLocation id,
            com.glodblock.github.extendedae.recipe.CircuitCutterRecipe recipe) {
        List<UniversalMultiblockRecipe.ItemRequirement> itemInputs = new ArrayList<>();
        com.glodblock.github.glodium.recipe.stack.IngredientStack.Item input = recipe.getInput();
        if (input != null && input.getIngredient() != null) {
            itemInputs.add(new UniversalMultiblockRecipe.ItemRequirement(input.getIngredient(),
                    Math.max(1L, input.getAmount()) * SLICER_QUANTITY_MULTIPLIER));
        }
        ItemStack output = recipe.output;
        return new UniversalMultiblockRecipe(
                UniversalMultiblockMachineKind.QUANTUM_SLICER,
                id.getPath(),
                itemInputs,
                List.of(),
                List.of(),
                normalizeItem(output),
                Math.max(1L, output.getCount()) * SLICER_QUANTITY_MULTIPLIER,
                FluidStack.EMPTY,
                0L,
                50_000L * SLICER_QUANTITY_MULTIPLIER,
                SLICER_PROCESSING_TICKS,
                1);
    }

    public static MultiblockProcessingRecipe fromOverload(ResourceLocation id,
            com.moakiee.ae2lt.machine.overloadfactory.recipe.OverloadProcessingRecipe recipe) {
        return MultiblockProcessingRecipe.fromUniversal(id, universalFromOverload(id, recipe));
    }

    /** Registers the AE2LT overload mapping as a data-pack/JEI visible universal recipe. */
    public static UniversalMultiblockRecipe universalFromOverload(ResourceLocation id,
            com.moakiee.ae2lt.machine.overloadfactory.recipe.OverloadProcessingRecipe recipe) {
        OverloadLightningRequirements.record(id, recipe.lightningTier(), recipe.lightningCost());

        List<UniversalMultiblockRecipe.ItemRequirement> itemInputs = recipe.itemInputs().stream()
                .map(input -> new UniversalMultiblockRecipe.ItemRequirement(input.ingredient(),
                        input.count() * OVERLOAD_QUANTITY_MULTIPLIER))
                .toList();

        List<UniversalMultiblockRecipe.FluidRequirement> fluidInputs = new ArrayList<>();
        FluidStack inputFluid = recipe.fluidInput();
        if (!inputFluid.isEmpty()) {
            fluidInputs.add(new UniversalMultiblockRecipe.FluidRequirement(inputFluid,
                    inputFluid.getAmount() * OVERLOAD_QUANTITY_MULTIPLIER));
        }

        ItemStack itemOutput = ItemStack.EMPTY;
        long itemOutputAmount = 0L;
        if (!recipe.itemResults().isEmpty()) {
            ItemStack first = recipe.itemResults().getFirst();
            itemOutput = first;
            itemOutputAmount = first.getCount() * OVERLOAD_QUANTITY_MULTIPLIER;
        }

        FluidStack fluidOutput = recipe.fluidResult();
        long fluidOutputAmount = fluidOutput.isEmpty() ? 0L : fluidOutput.getAmount() * OVERLOAD_QUANTITY_MULTIPLIER;

        UniversalMultiblockRecipe universal = new UniversalMultiblockRecipe(
                UniversalMultiblockMachineKind.QUANTUM_PROCESSOR_ASSEMBLER,
                id.getPath(),
                itemInputs,
                fluidInputs,
                List.of(),
                itemOutput,
                itemOutputAmount,
                fluidOutput,
                fluidOutputAmount,
                recipe.totalEnergy(),
                OVERLOAD_PROCESSING_TICKS,
                tierForLightning(recipe.lightningTier()));
        return universal;
    }

    /** MK tier follows the lightning type: high voltage -&gt; MK1, extreme high voltage -&gt; MK2. */
    private static int tierForLightning(com.moakiee.ae2lt.me.key.LightningKey.Tier tier) {
        return tier == com.moakiee.ae2lt.me.key.LightningKey.Tier.EXTREME_HIGH_VOLTAGE
                ? MultiblockMachineTier.MK2.level()
                : MultiblockMachineTier.MK1.level();
    }

    private static ItemStack normalizeItem(ItemStack stack) {
        ItemStack copy = stack.copy();
        if (!copy.isEmpty()) {
            copy.setCount(1);
        }
        return copy;
    }
}

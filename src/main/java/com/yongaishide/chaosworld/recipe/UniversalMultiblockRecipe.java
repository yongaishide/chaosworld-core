package com.yongaishide.chaosworld.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.moakiee.ae2lt.machine.overloadfactory.recipe.OverloadProcessingRecipe;
import com.yongaishide.chaosworld.api.multiblock.MultiblockMachineTier;
import com.yongaishide.chaosworld.init.ModRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class UniversalMultiblockRecipe implements Recipe<RecipeInput> {
    private final UniversalMultiblockMachineKind machine;
    private final String recipeName;
    private final List<ItemRequirement> itemInputs;
    private final List<FluidRequirement> fluidInputs;
    private final List<ChemicalRequirement> chemicalInputs;
    private final ItemStack itemOutput;
    private final long itemOutputAmount;
    private final FluidStack fluidOutput;
    private final long fluidOutputAmount;
    private final long energy;
    private final int time;
    private final int requiredTier;

    public UniversalMultiblockRecipe(
            UniversalMultiblockMachineKind machine,
            String recipeName,
            List<ItemRequirement> itemInputs,
            List<FluidRequirement> fluidInputs,
            List<ChemicalRequirement> chemicalInputs,
            ItemStack itemOutput,
            long itemOutputAmount,
            FluidStack fluidOutput,
            long fluidOutputAmount,
            long energy,
            int time,
            int requiredTier) {
        this.machine = machine;
        this.recipeName = recipeName;
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.chemicalInputs = chemicalInputs;
        this.itemOutput = normalizeItemOutput(itemOutput);
        this.itemOutputAmount = this.itemOutput.isEmpty() ? 0L : Math.max(0L, itemOutputAmount);
        this.fluidOutput = fluidOutput == null ? FluidStack.EMPTY : fluidOutput;
        this.fluidOutputAmount = this.fluidOutput.isEmpty() ? 0L : Math.max(0L, fluidOutputAmount);
        this.energy = energy;
        this.time = time;
        this.requiredTier = Math.max(MultiblockMachineTier.MK1.level(), requiredTier);
    }

    /**
     * Maps an ExtendedAE Circuit Cutter recipe onto the Quantum Slicer machine
     * (the slicer's own recipes were removed; it now processes the cutter's).
     * Quantity is scaled x64000, runtime x1000 and energy x64000.
     */
    public static final long SLICER_QUANTITY_MULTIPLIER = 64_000L;
    public static final int SLICER_PROCESSING_TICKS = 100 * 1000;
    public static final long SLICER_ENERGY_MULTIPLIER = 64_000L;

    public static UniversalMultiblockRecipe fromCircuitCutter(ResourceLocation id,
            com.glodblock.github.extendedae.recipe.CircuitCutterRecipe recipe) {
        ItemStack output = recipe.output;
        long outputAmount = Math.max(1L, output.getCount()) * SLICER_QUANTITY_MULTIPLIER;
        List<ItemRequirement> itemInputs = new ArrayList<>();
        com.glodblock.github.glodium.recipe.stack.IngredientStack.Item input = recipe.getInput();
        if (input != null && input.getIngredient() != null) {
            itemInputs.add(new ItemRequirement(input.getIngredient(),
                    Math.max(1L, input.getAmount()) * SLICER_QUANTITY_MULTIPLIER));
        }
        return new UniversalMultiblockRecipe(
                UniversalMultiblockMachineKind.QUANTUM_SLICER,
                id.getPath(),
                itemInputs,
                List.of(),
                List.of(),
                output,
                outputAmount,
                FluidStack.EMPTY,
                0L,
                50_000L * SLICER_ENERGY_MULTIPLIER,
                SLICER_PROCESSING_TICKS,
                MultiblockMachineTier.MK1.level());
    }

    public UniversalMultiblockMachineKind getMachine() {
        return machine;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public List<ItemRequirement> getItemInputs() {
        return itemInputs;
    }

    public List<FluidRequirement> getFluidInputs() {
        return fluidInputs;
    }

    public List<ChemicalRequirement> getChemicalInputs() {
        return chemicalInputs;
    }

    public ItemStack getItemOutput() {
        return itemOutput.copy();
    }

    public long getItemOutputAmount() {
        return itemOutputAmount;
    }

    public ItemStack getDisplayedItemOutput() {
        ItemStack stack = this.itemOutput.copy();
        if (!stack.isEmpty()) {
            stack.setCount((int) Math.min(Integer.MAX_VALUE, Math.max(1L, this.itemOutputAmount)));
        }
        return stack;
    }

    public FluidStack getFluidOutput() {
        return fluidOutput.copy();
    }

    public long getFluidOutputAmount() {
        return fluidOutputAmount;
    }

    public long getEnergy() {
        return energy;
    }

    public int getTime() {
        return time;
    }

    public int getRequiredTier() {
        return requiredTier;
    }

    public static final long OVERLOAD_ENERGY_MULTIPLIER = 1000L;
    public static final long OVERLOAD_QUANTITY_MULTIPLIER = 20000L;
    public static final int OVERLOAD_PROCESSING_TICKS = 1600;

    public static UniversalMultiblockRecipe fromOverload(ResourceLocation id, OverloadProcessingRecipe recipe) {
        List<ItemRequirement> itemInputs = recipe.itemInputs().stream()
                .map(input -> new ItemRequirement(input.ingredient(), input.count() * OVERLOAD_QUANTITY_MULTIPLIER))
                .toList();
        List<FluidRequirement> fluidInputs = new ArrayList<>();
        FluidStack inputFluid = recipe.fluidInput();
        if (!inputFluid.isEmpty()) {
            fluidInputs.add(new FluidRequirement(inputFluid, inputFluid.getAmount() * OVERLOAD_QUANTITY_MULTIPLIER));
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
        return new UniversalMultiblockRecipe(
                UniversalMultiblockMachineKind.QUANTUM_PROCESSOR_ASSEMBLER,
                id.getPath(),
                itemInputs,
                fluidInputs,
                List.of(),
                itemOutput,
                itemOutputAmount,
                fluidOutput,
                fluidOutputAmount,
                recipe.totalEnergy() * OVERLOAD_ENERGY_MULTIPLIER,
                OVERLOAD_PROCESSING_TICKS,
                tierForLightningCost(recipe.lightningCost()));
    }

    private static int tierForLightningCost(int lightningCost) {
        if (lightningCost >= 65) {
            return MultiblockMachineTier.MK3.level();
        }
        if (lightningCost >= 9) {
            return MultiblockMachineTier.MK2.level();
        }
        return MultiblockMachineTier.MK1.level();
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput pInput, HolderLookup.Provider pRegistries) {
        return getDisplayedItemOutput();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return getDisplayedItemOutput();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.UNIVERSAL_MULTIBLOCK_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get();
    }

    public record ItemRequirement(Ingredient ingredient, long amount) {
        public static final Codec<ItemRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(ItemRequirement::ingredient),
                Codec.LONG.fieldOf("amount").forGetter(ItemRequirement::amount)
        ).apply(instance, ItemRequirement::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemRequirement> STREAM_CODEC = StreamCodec.of(
                (buf, ingredient) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient.ingredient);
                    buf.writeLong(ingredient.amount);
                },
                buf -> new ItemRequirement(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readLong())
        );
    }

    public record FluidRequirement(FluidStack fluid, long amount) {
        public static final Codec<FluidRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FluidStack.CODEC.fieldOf("fluid").forGetter(FluidRequirement::fluid),
                Codec.LONG.fieldOf("amount").forGetter(FluidRequirement::amount)
        ).apply(instance, FluidRequirement::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidRequirement> STREAM_CODEC = StreamCodec.of(
                (buf, ingredient) -> {
                    FluidStack.STREAM_CODEC.encode(buf, ingredient.fluid);
                    buf.writeLong(ingredient.amount);
                },
                buf -> new FluidRequirement(FluidStack.STREAM_CODEC.decode(buf), buf.readLong())
        );
    }

    public record ChemicalRequirement(ResourceLocation chemicalId, long amount) {
        public static final Codec<ChemicalRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("chemical").forGetter(ChemicalRequirement::chemicalId),
                Codec.LONG.fieldOf("amount").forGetter(ChemicalRequirement::amount)
        ).apply(instance, ChemicalRequirement::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ChemicalRequirement> STREAM_CODEC = StreamCodec.of(
                (buf, ingredient) -> {
                    buf.writeResourceLocation(ingredient.chemicalId);
                    buf.writeLong(ingredient.amount);
                },
                buf -> new ChemicalRequirement(buf.readResourceLocation(), buf.readLong())
        );
    }

    private static ItemStack normalizeItemOutput(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    public record ItemOutputDefinition(Item item, long amount) {
        public static final MapCodec<ItemOutputDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemOutputDefinition::item),
                Codec.LONG.optionalFieldOf("count", 1L).forGetter(ItemOutputDefinition::amount)
        ).apply(instance, ItemOutputDefinition::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemOutputDefinition> STREAM_CODEC = StreamCodec.of(
                (buf, output) -> {
                    buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(output.item));
                    buf.writeLong(output.amount);
                },
                buf -> new ItemOutputDefinition(BuiltInRegistries.ITEM.get(buf.readResourceLocation()), buf.readLong())
        );

        public ItemStack toStack() {
            return new ItemStack(this.item, 1);
        }
    }

    public static class Serializer implements RecipeSerializer<UniversalMultiblockRecipe> {
        public static final MapCodec<UniversalMultiblockRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                UniversalMultiblockMachineKind.CODEC.fieldOf("machine").forGetter(UniversalMultiblockRecipe::getMachine),
                Codec.STRING.optionalFieldOf("recipe_name", "Universal Multiblock Recipe").forGetter(UniversalMultiblockRecipe::getRecipeName),
                ItemRequirement.CODEC.listOf().fieldOf("item_inputs").forGetter(UniversalMultiblockRecipe::getItemInputs),
                FluidRequirement.CODEC.listOf().optionalFieldOf("fluid_inputs", List.of()).forGetter(UniversalMultiblockRecipe::getFluidInputs),
                ChemicalRequirement.CODEC.listOf().optionalFieldOf("chemical_inputs", List.of()).forGetter(UniversalMultiblockRecipe::getChemicalInputs),
                ItemOutputDefinition.CODEC.codec().optionalFieldOf("item_output").forGetter((UniversalMultiblockRecipe recipe) -> recipe.itemOutput.isEmpty()
                        ? java.util.Optional.empty()
                        : java.util.Optional.of(new ItemOutputDefinition(recipe.itemOutput.getItem(), recipe.itemOutputAmount))),
                FluidStack.CODEC.optionalFieldOf("fluid_output", FluidStack.EMPTY).forGetter((UniversalMultiblockRecipe recipe) -> recipe.fluidOutput),
                Codec.LONG.optionalFieldOf("fluid_output_amount", 0L).forGetter(UniversalMultiblockRecipe::getFluidOutputAmount),
                Codec.LONG.fieldOf("energy").forGetter(UniversalMultiblockRecipe::getEnergy),
                Codec.INT.fieldOf("time").forGetter(UniversalMultiblockRecipe::getTime),
                Codec.INT.optionalFieldOf("required_tier", MultiblockMachineTier.MK1.level()).forGetter(UniversalMultiblockRecipe::getRequiredTier)
        ).apply(instance, (machine, recipeName, itemInputs, fluidInputs, chemicalInputs, itemOutput, fluidOutput, fluidOutputAmount, energy, time, requiredTier) ->
                new UniversalMultiblockRecipe(
                        machine,
                        recipeName,
                        itemInputs,
                        fluidInputs,
                        chemicalInputs,
                        itemOutput.map(ItemOutputDefinition::toStack).orElse(ItemStack.EMPTY),
                        itemOutput.map(ItemOutputDefinition::amount).orElse(0L),
                        fluidOutput,
                        fluidOutputAmount,
                        energy,
                        time,
                        requiredTier
                )));

        public static final StreamCodec<RegistryFriendlyByteBuf, UniversalMultiblockRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.machine.serializedName());
                    buf.writeUtf(recipe.recipeName);
                    ItemRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.itemInputs);
                    FluidRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.fluidInputs);
                    ChemicalRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.chemicalInputs);
                    boolean hasItemOutput = !recipe.itemOutput.isEmpty();
                    buf.writeBoolean(hasItemOutput);
                    if (hasItemOutput) {
                        ItemOutputDefinition.STREAM_CODEC.encode(buf, new ItemOutputDefinition(recipe.itemOutput.getItem(), recipe.itemOutputAmount));
                    }
                    boolean hasFluidOutput = !recipe.fluidOutput.isEmpty() && recipe.fluidOutputAmount > 0;
                    buf.writeBoolean(hasFluidOutput);
                    if (hasFluidOutput) {
                        FluidStack.STREAM_CODEC.encode(buf, recipe.fluidOutput);
                        buf.writeLong(recipe.fluidOutputAmount);
                    }
                    buf.writeLong(recipe.energy);
                    buf.writeInt(recipe.time);
                    buf.writeInt(recipe.requiredTier);
                },
                buf -> {
                    var machine = UniversalMultiblockMachineKind.fromSerializedName(buf.readUtf());
                    var recipeName = buf.readUtf();
                    var itemInputs = ItemRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                    var fluidInputs = FluidRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                    var chemicalInputs = ChemicalRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                    boolean hasItemOutput = buf.readBoolean();
                    var itemOutput = hasItemOutput ? ItemOutputDefinition.STREAM_CODEC.decode(buf) : null;
                    boolean hasFluidOutput = buf.readBoolean();
                    var fluidOutput = hasFluidOutput ? FluidStack.STREAM_CODEC.decode(buf) : FluidStack.EMPTY;
                    long fluidOutputAmount = hasFluidOutput ? buf.readLong() : 0L;
                    long energy = buf.readLong();
                    int time = buf.readInt();
                    int requiredTier = buf.readInt();
                    return new UniversalMultiblockRecipe(
                            machine,
                            recipeName,
                            itemInputs,
                            fluidInputs,
                            chemicalInputs,
                            itemOutput != null ? itemOutput.toStack() : ItemStack.EMPTY,
                            itemOutput != null ? itemOutput.amount() : 0L,
                            fluidOutput,
                            fluidOutputAmount,
                            energy,
                            time,
                            requiredTier
                    );
                }
        );

        @Override
        public MapCodec<UniversalMultiblockRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, UniversalMultiblockRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

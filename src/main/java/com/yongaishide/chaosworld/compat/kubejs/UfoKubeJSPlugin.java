package com.yongaishide.chaosworld.compat.kubejs;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * KubeJS support for {@code chaosworld_core:stellar_simulation} recipes.
 * <p>
 * Script usage:
 * <pre>{@code
 * ServerEvents.recipes(event => {
 *     event.recipes.chaosworld_core.stellar_simulation({
 *         simulation_name: 'Test Simulation',
 *         energy: 1000000000,
 *         time: 7200,
 *         field_tier: 3,
 *         cooling_level: 3,
 *         coolant_amount: 100000,
 *         fuel_amount: 150000,
 *         fuel_fluid: 'chaosworld_core:raw_star_matter_plasma',
 *         item_inputs: ['minecraft:iron_ingot#64', 'minecraft:redstone#16'],
 *         fluid_inputs: ['chaosworld_core:uu_matter#1000'],
 *         item_outputs: [Item.of('minecraft:diamond', 100)],
 *         fluid_outputs: [Fluid.of('chaosworld_core:source_liquid_starlight_fluid', 5000)]
 *     }).id('chaosworld_core:custom_simulation');
 * });
 * }</pre>
 */
public class UfoKubeJSPlugin implements KubeJSPlugin {

    public static final ResourceLocation STELLAR_TYPE =
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "stellar_simulation");

    // ──────────────────── primitives ────────────────────

    private static <T> RecipeComponent<T> unit(ResourceLocation id, Codec<T> codec, TypeInfo typeInfo) {
        return RecipeComponentType.<T>unit(id, type -> new SimpleRecipeComponent(type, codec, typeInfo)).instance();
    }

    private static final RecipeComponent<Integer> INT =
            unit(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "int"), Codec.INT, TypeInfo.of(int.class));
    private static final RecipeComponent<Long> LONG =
            unit(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "long"), Codec.LONG, TypeInfo.of(long.class));
    private static final RecipeComponent<String> STRING =
            unit(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "string"), Codec.STRING, TypeInfo.of(String.class));

    private static final RecipeComponent<ItemStack> ITEM_STACK = ItemStackComponent.ITEM_STACK.instance();
    private static final RecipeComponent<FluidStack> FLUID_STACK = FluidStackComponent.FLUID_STACK.instance();

    // ──────────────────── stellar components ────────────────────
    // These serialize to the mod's native JSON format:
    //   item_inputs:  {"amount": n, "ingredient": {"item": id}}
    //   fluid_inputs: {"amount": n, "ingredient": {"fluid": id}}
    //   item_outputs: GenericStack {"id", "#t": "ae2:i", "#": count}
    //   fluid_outputs:GenericStack {"id", "#t": "ae2:f", "#": amount}

    private static final RecipeComponentType<ItemStack> ITEM_INPUT_TYPE =
            RecipeComponentType.unit(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "stellar_item_input"),
                    type -> new SimpleRecipeComponent(type, ItemStack.OPTIONAL_CODEC, TypeInfo.of(ItemStack.class)));

    private static final RecipeComponentType<FluidStack> FLUID_INPUT_TYPE =
            RecipeComponentType.unit(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "stellar_fluid_input"),
                    type -> new SimpleRecipeComponent(type, FluidStack.CODEC, TypeInfo.of(FluidStack.class)));

    private static final RecipeComponentType<ItemStack> ITEM_OUTPUT_TYPE =
            RecipeComponentType.unit(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "stellar_item_output"),
                    type -> new SimpleRecipeComponent(type, ItemStack.OPTIONAL_CODEC, TypeInfo.of(ItemStack.class)));

    private static final RecipeComponentType<FluidStack> FLUID_OUTPUT_TYPE =
            RecipeComponentType.unit(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "stellar_fluid_output"),
                    type -> new SimpleRecipeComponent(type, FluidStack.CODEC, TypeInfo.of(FluidStack.class)));

    public static final RecipeComponent<ItemStack> STELLAR_ITEM_INPUT = new RecipeComponent<>() {
        @Override
        public RecipeComponentType<?> type() {
            return ITEM_INPUT_TYPE;
        }

        @Override
        public Codec<ItemStack> codec() {
            return ItemStack.OPTIONAL_CODEC;
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.of(ItemStack.class);
        }

        @Override
        public ItemStack wrap(RecipeScriptContext ctx, Object o) {
            return ITEM_STACK.wrap(ctx, o);
        }

        @Override
        public void writeToJson(KubeRecipe recipe, RecipeComponentValue<ItemStack> value, JsonObject json) {
            ItemStack stack = value.value;
            JsonObject entry = new JsonObject();
            entry.addProperty("amount", stack.getCount());
            JsonObject ingredient = new JsonObject();
            ingredient.addProperty("item", stack.getItemHolder().unwrapKey()
                    .map(key -> key.location().toString()).orElse("minecraft:air"));
            entry.add("ingredient", ingredient);
            json.add(value.key.name, entry);
        }

        @Override
        public void readFromJson(KubeRecipe recipe, RecipeComponentValue<ItemStack> value, JsonObject json) {
            if (!json.has(value.key.name)) {
                return;
            }
            JsonObject entry = json.getAsJsonObject(value.key.name);
            JsonObject ingredient = entry.has("ingredient") ? entry.getAsJsonObject("ingredient") : new JsonObject();
            String id = ingredient.has("item") ? ingredient.get("item").getAsString() : "minecraft:air";
            int amount = entry.has("amount") ? entry.get("amount").getAsInt() : 1;
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(id));
            value.value = new ItemStack(item, amount);
        }
    };

    public static final RecipeComponent<FluidStack> STELLAR_FLUID_INPUT = new RecipeComponent<>() {
        @Override
        public RecipeComponentType<?> type() {
            return FLUID_INPUT_TYPE;
        }

        @Override
        public Codec<FluidStack> codec() {
            return FluidStack.CODEC;
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.of(FluidStack.class);
        }

        @Override
        public FluidStack wrap(RecipeScriptContext ctx, Object o) {
            return FLUID_STACK.wrap(ctx, o);
        }

        @Override
        public void writeToJson(KubeRecipe recipe, RecipeComponentValue<FluidStack> value, JsonObject json) {
            FluidStack stack = value.value;
            JsonObject entry = new JsonObject();
            entry.addProperty("amount", stack.getAmount());
            JsonObject ingredient = new JsonObject();
            ingredient.addProperty("fluid", stack.getFluid().builtInRegistryHolder().key().location().toString());
            entry.add("ingredient", ingredient);
            json.add(value.key.name, entry);
        }

        @Override
        public void readFromJson(KubeRecipe recipe, RecipeComponentValue<FluidStack> value, JsonObject json) {
            if (!json.has(value.key.name)) {
                return;
            }
            JsonObject entry = json.getAsJsonObject(value.key.name);
            JsonObject ingredient = entry.has("ingredient") ? entry.getAsJsonObject("ingredient") : new JsonObject();
            String id = ingredient.has("fluid") ? ingredient.get("fluid").getAsString() : "minecraft:water";
            int amount = entry.has("amount") ? entry.get("amount").getAsInt() : 1000;
            var fluid = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(id));
            value.value = new FluidStack(fluid, amount);
        }
    };

    public static final RecipeComponent<ItemStack> STELLAR_ITEM_OUTPUT = new RecipeComponent<>() {
        @Override
        public RecipeComponentType<?> type() {
            return ITEM_OUTPUT_TYPE;
        }

        @Override
        public Codec<ItemStack> codec() {
            return ItemStack.OPTIONAL_CODEC;
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.of(ItemStack.class);
        }

        @Override
        public ItemStack wrap(RecipeScriptContext ctx, Object o) {
            return ITEM_STACK.wrap(ctx, o);
        }

        @Override
        public void writeToJson(KubeRecipe recipe, RecipeComponentValue<ItemStack> value, JsonObject json) {
            ItemStack stack = value.value;
            JsonObject entry = new JsonObject();
            entry.addProperty("id", stack.getItemHolder().unwrapKey()
                    .map(key -> key.location().toString()).orElse("minecraft:air"));
            entry.addProperty("#t", "ae2:i");
            entry.addProperty("#", stack.getCount());
            json.add(value.key.name, entry);
        }

        @Override
        public void readFromJson(KubeRecipe recipe, RecipeComponentValue<ItemStack> value, JsonObject json) {
            if (!json.has(value.key.name)) {
                return;
            }
            JsonObject entry = json.getAsJsonObject(value.key.name);
            String id = entry.has("id") ? entry.get("id").getAsString() : "minecraft:air";
            long amount = entry.has("#") ? entry.get("#").getAsLong() : 1L;
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(id));
            value.value = new ItemStack(item, (int) amount);
        }
    };

    public static final RecipeComponent<FluidStack> STELLAR_FLUID_OUTPUT = new RecipeComponent<>() {
        @Override
        public RecipeComponentType<?> type() {
            return FLUID_OUTPUT_TYPE;
        }

        @Override
        public Codec<FluidStack> codec() {
            return FluidStack.CODEC;
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.of(FluidStack.class);
        }

        @Override
        public FluidStack wrap(RecipeScriptContext ctx, Object o) {
            return FLUID_STACK.wrap(ctx, o);
        }

        @Override
        public void writeToJson(KubeRecipe recipe, RecipeComponentValue<FluidStack> value, JsonObject json) {
            FluidStack stack = value.value;
            JsonObject entry = new JsonObject();
            entry.addProperty("id", stack.getFluid().builtInRegistryHolder().key().location().toString());
            entry.addProperty("#t", "ae2:f");
            entry.addProperty("#", stack.getAmount());
            json.add(value.key.name, entry);
        }

        @Override
        public void readFromJson(KubeRecipe recipe, RecipeComponentValue<FluidStack> value, JsonObject json) {
            if (!json.has(value.key.name)) {
                return;
            }
            JsonObject entry = json.getAsJsonObject(value.key.name);
            String id = entry.has("id") ? entry.get("id").getAsString() : "minecraft:water";
            long amount = entry.has("#") ? entry.get("#").getAsLong() : 1000L;
            var fluid = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(id));
            value.value = new FluidStack(fluid, (int) amount);
        }
    };

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry event) {
        event.register(STELLAR_TYPE, new RecipeSchema(
                LONG.otherKey("energy"),
                INT.otherKey("time"),
                STELLAR_ITEM_INPUT.asListOrSelf().inputKey("item_inputs"),
                STELLAR_FLUID_INPUT.asListOrSelf().inputKey("fluid_inputs"),
                STELLAR_ITEM_OUTPUT.asListOrSelf().outputKey("item_outputs"),
                STELLAR_FLUID_OUTPUT.asListOrSelf().outputKey("fluid_outputs"),
                STRING.otherKey("simulation_name").optional("Stellar Simulation"),
                INT.otherKey("field_tier").optional(1),
                INT.otherKey("cooling_level").optional(0),
                LONG.otherKey("coolant_amount").optional(0L),
                LONG.otherKey("fuel_amount").optional(0L),
                STRING.otherKey("fuel_fluid").optional("")
        ).factory(new KubeRecipeFactory(STELLAR_TYPE, StellarKubeRecipe.class, StellarKubeRecipe::new)));
    }
}

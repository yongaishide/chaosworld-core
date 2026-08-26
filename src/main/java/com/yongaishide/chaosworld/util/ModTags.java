package com.yongaishide.chaosworld.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_UFO_TOOL = createTag("needs_ufo_tool");
        public static final TagKey<Block> INCORRECT_FOR_UFO_TOOL = createTag("incorrect_for_ufo_tool");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("chaosworld_core", name));
        }
    }


    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");
        public static final TagKey<Item> STAFF = createTag("staff");
        public static final TagKey<Item> INGREDIENTS_UFO = createTag("ingredients/ufo");
        public static final TagKey<Item> COOLANTS = createTag("coolants");
        public static final TagKey<Item> HAZARDOUS = createTag("hazardous");
        public static final TagKey<Item> CONTAINMENT_DEVICE = createTag("containment_device");
        public static final TagKey<Item> COMPONENT = createTag("component");
        public static final TagKey<Item> COMPRESSED = createTag("compressed");
        public static final TagKey<Item> RECYCLABLE = createTag("recyclable");
        public static final TagKey<Item> DIM_RESIDUE = createTag("dim_residue");
        public static final TagKey<Item> COOLANT_MATERIAL = createTag("coolant_material");
        public static final TagKey<Item> CATALYST = createTag("catalyst");

        public static final TagKey<Item> ADVANCED_COMPONENT = createTag("advanced_component");
        public static final TagKey<Item> MATTER_STAGE_1 = createTag("matter_stage_1");
        public static final TagKey<Item> MATTER_STAGE_2 = createTag("matter_stage_2");
        public static final TagKey<Item> MATTER_STAGE_FINAL = createTag("matter_stage_final");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("chaosworld_core", name));
        }
    }
    public static class Fluids {
        public static final TagKey<Fluid> COOLANTS = tag("coolants");
        public static final TagKey<Fluid> SYNTHETIC_FLUID = tag("synthetic_fluid");
        public static final TagKey<Fluid> ENERGY_FLUID = tag("energy_fluid");
        public static final TagKey<Fluid> PLASMA = tag("plasma");
        public static final TagKey<Fluid> MATTER_FLUID = tag("matter_fluid");
        public static final TagKey<Fluid> COOLANT = tag("coolant");
        public static final TagKey<Fluid> COOLANT_EXTREME = tag("coolant_extreme");
        public static final TagKey<Fluid> HAZARDOUS = tag("hazardous");
        private static TagKey<Fluid> tag(String name) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath("chaosworld_core", name));
        }
}}
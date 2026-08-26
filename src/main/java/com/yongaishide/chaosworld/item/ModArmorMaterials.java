package com.yongaishide.chaosworld.item;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.EnumMap;
import java.util.List;

public class ModArmorMaterials {
    public static final Holder<ArmorMaterial> THERMAL_EXOSUIT = register(
            "thermal_exosuit",
            new EnumMap<>(ArmorItem.Type.class) {{
                put(ArmorItem.Type.BOOTS, 4);
                put(ArmorItem.Type.LEGGINGS, 7);
                put(ArmorItem.Type.CHESTPLATE, 9);
                put(ArmorItem.Type.HELMET, 4);
            }},
            38,
            SoundEvents.ARMOR_EQUIP_IRON,
            Ingredient.of(Items.NETHERITE_INGOT),
            0.15f,
            3.2f
    );

    public static final Holder<ArmorMaterial> UFO_ARMOR = register(
            "ufo_armor",
            new EnumMap<>(ArmorItem.Type.class) {{
                put(ArmorItem.Type.BOOTS, 4);
                put(ArmorItem.Type.LEGGINGS, 7);
                put(ArmorItem.Type.CHESTPLATE, 9);
                put(ArmorItem.Type.HELMET, 4);
            }},
            40,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            Ingredient.of(Tags.Items.GEMS_DIAMOND),
            2.0f,
            3.0f
    );

    public static final Holder<ArmorMaterial> ASTRAL_NEXUS = register(
            "astral_nexus",
            new EnumMap<>(ArmorItem.Type.class) {{
                put(ArmorItem.Type.BOOTS, 4);
                put(ArmorItem.Type.LEGGINGS, 7);
                put(ArmorItem.Type.CHESTPLATE, 9);
                put(ArmorItem.Type.HELMET, 4);
            }},
            42,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            Ingredient.of(Items.NETHER_STAR),
            2.2f,
            3.4f
    );

    private static Holder<ArmorMaterial> register(
            String name,
            EnumMap<ArmorItem.Type, Integer> defense,
            int durabilityMultiplier,
            Holder<SoundEvent> equipSound,
            Ingredient repairIngredient,
            float knockbackResistance,
            float toughness) {
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("chaosworld_core", name), "", false)
        );

        ArmorMaterial material = new ArmorMaterial(
                defense,
                durabilityMultiplier,
                equipSound,
                () -> repairIngredient,
                layers,
                knockbackResistance,
                toughness
        );

        ResourceKey<ArmorMaterial> key = ResourceKey.create(Registries.ARMOR_MATERIAL, ResourceLocation.fromNamespaceAndPath("chaosworld_core", name));
        Registry.register(BuiltInRegistries.ARMOR_MATERIAL, key.location(), material);
        return BuiltInRegistries.ARMOR_MATERIAL.getHolderOrThrow(key);
    }
}

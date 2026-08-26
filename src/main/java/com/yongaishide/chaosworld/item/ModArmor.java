package com.yongaishide.chaosworld.item;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.item.custom.AstralNexusArmorItem;
import com.yongaishide.chaosworld.item.custom.UfoArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.yongaishide.chaosworld.item.ModItems.ITEMS;

public class ModArmor {

    public static final DeferredItem<Item> UFO_HELMET = ITEMS.register("helmet",
            () -> new UfoArmorItem(ModArmorMaterials.UFO_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()
                    .component(ModDataComponents.ENERGY.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> UFO_CHESTPLATE = ITEMS.register("chestplate",
            () -> new UfoArmorItem(ModArmorMaterials.UFO_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()
                    .component(ModDataComponents.ENERGY.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> UFO_LEGGINGS = ITEMS.register("leggings",
            () -> new UfoArmorItem(ModArmorMaterials.UFO_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()
                    .component(ModDataComponents.ENERGY.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> UFO_BOOTS = ITEMS.register("boots",
            () -> new UfoArmorItem(ModArmorMaterials.UFO_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()
                    .component(ModDataComponents.ENERGY.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> ASTRAL_NEXUS_HELMET = ITEMS.register("astral_nexus_helmet",
            () -> new AstralNexusArmorItem(ModArmorMaterials.ASTRAL_NEXUS, ArmorItem.Type.HELMET, new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> ASTRAL_NEXUS_CHESTPLATE = ITEMS.register("astral_nexus_chestplate",
            () -> new AstralNexusArmorItem(ModArmorMaterials.ASTRAL_NEXUS, ArmorItem.Type.CHESTPLATE, new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> ASTRAL_NEXUS_LEGGINGS = ITEMS.register("astral_nexus_leggings",
            () -> new AstralNexusArmorItem(ModArmorMaterials.ASTRAL_NEXUS, ArmorItem.Type.LEGGINGS, new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> ASTRAL_NEXUS_BOOTS = ITEMS.register("astral_nexus_boots",
            () -> new AstralNexusArmorItem(ModArmorMaterials.ASTRAL_NEXUS, ArmorItem.Type.BOOTS, new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> THERMAL_RESISTOR_PLATING = ITEMS.register(
            "thermal_resistor_plating",
            () -> new Item(new Item.Properties().fireResistant())
    );

    public static final DeferredItem<Item> THERMAL_RESISTOR_MASK = ITEMS.register("thermal_resistor_mask",
            () -> new UfoArmorItem(ModArmorMaterials.THERMAL_EXOSUIT, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final DeferredItem<Item> THERMAL_RESISTOR_CHEST = ITEMS.register("thermal_resistor_chest",
            () -> new UfoArmorItem(ModArmorMaterials.THERMAL_EXOSUIT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final DeferredItem<Item> THERMAL_RESISTOR_PANTS = ITEMS.register("thermal_resistor_pants",
            () -> new UfoArmorItem(ModArmorMaterials.THERMAL_EXOSUIT, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final DeferredItem<Item> THERMAL_RESISTOR_BOOTS = ITEMS.register("thermal_resistor_boots",
            () -> new UfoArmorItem(ModArmorMaterials.THERMAL_EXOSUIT, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        // No-op: armor is registered via ModItems.ITEMS static initializers.
        // Called from ModItems.register() to force this class's <clinit> during registration.
    }
}

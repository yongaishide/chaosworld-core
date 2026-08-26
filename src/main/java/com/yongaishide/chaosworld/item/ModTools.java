package com.yongaishide.chaosworld.item;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.item.custom.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.yongaishide.chaosworld.item.ModItems.ITEMS;

public class ModTools {
    private static Item.Properties unbreakableToolProperties() {
        return new Item.Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(false));
    }

    public static final DeferredItem<Item> UFO_STAFF = ITEMS.register("staff",
            () -> new UfoStaffItem(new Item.Properties().component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<SwordItem> UFO_SWORD = ITEMS.register("sword",
            () -> new UfoEnergySwordItem(ModToolTiers.UFO, unbreakableToolProperties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.UFO, 5, -2.4f))
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<SwordItem> REALITY_RIPPER = ITEMS.register("reality_ripper",
            () -> new RealityRipperItem(ModToolTiers.UFO, unbreakableToolProperties()
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<PickaxeItem> UFO_PICKAXE = ITEMS.register("pickaxe",
            () -> new UfoEnergyPickaxeItem(ModToolTiers.UFO, unbreakableToolProperties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.UFO, 1.0F, -2.8f))
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(ModDataComponents.FAST_MODE.get(), false).stacksTo(1)));

    public static final DeferredItem<ShovelItem> UFO_SHOVEL = ITEMS.register("shovel",
            () -> new UfoEnergyShovelItem(ModToolTiers.UFO, unbreakableToolProperties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.UFO, 1.5F, -3.0f))
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(ModDataComponents.FAST_MODE.get(), false).stacksTo(1)));

    public static final DeferredItem<AxeItem> UFO_AXE = ITEMS.register("axe",
            () -> new UfoEnergyAxeItem(ModToolTiers.UFO, unbreakableToolProperties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.UFO, 6.0F, -3.2f))
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(ModDataComponents.FAST_MODE.get(), false).stacksTo(1)));

    public static final DeferredItem<HoeItem> UFO_HOE = ITEMS.register("hoe",
            () -> new UfoEnergyHoeItem(ModToolTiers.UFO, unbreakableToolProperties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.UFO, 0F, -3.0f))
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag())).stacksTo(1)));

    public static final DeferredItem<FishingRodItem> UFO_FISHING_ROD = ITEMS.register("fishing_rod",
            () -> new UfoEnergyFishingRodItem(unbreakableToolProperties().durability(500)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<SwordItem> UFO_GREATSWORD = ITEMS.register("greatsword",
            () -> new UfoEnergyGreatswordItem(ModToolTiers.UFO, unbreakableToolProperties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.UFO, 8, -3.0f))
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<HammerItem> UFO_HAMMER = ITEMS.register("hammer",
            () -> new HammerItem(ModToolTiers.UFO, unbreakableToolProperties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.UFO, 7.0F, -3.4f))
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<BowItem> UFO_BOW = ITEMS.register("bow",
            () -> new UfoEnergyBowItem(unbreakableToolProperties().durability(5000)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(ModDataComponents.BOW_FAST_MODE.get(), false).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        // No-op: tools are registered via ModItems.ITEMS static initializers.
        // Called from ModItems.register() to force this class's <clinit> during registration.
    }
}

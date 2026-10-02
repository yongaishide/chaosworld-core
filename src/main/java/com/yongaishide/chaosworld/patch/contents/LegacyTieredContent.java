package com.yongaishide.chaosworld.patch.contents;

import com.raishxn.ufo.item.custom.AnimatedNameItem;
import com.yongaishide.chaosworld.ChaosWorld;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Patch layer: re-adds the Chaos World storage tier content that UFO Future 2.1
 * does not have (white dwarf / neutron star / pulsar storage components and the
 * legacy neutron star / pulsar storage cells), so the original editable recipes
 * keep working with this mod's own item ids.
 */
public final class LegacyTieredContent {

    private LegacyTieredContent() {
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ChaosWorld.MODID);

    private static final long BASE_256M = 268_435_456L;
    private static final long BASE_1T = 1_099_511_627_776L;
    private static final long BASE_1P = 1_125_899_906_842_624L;

    // ------------------------------------------------------------------
    // storage components
    // ------------------------------------------------------------------

    public static final DeferredHolder<Item, AnimatedNameItem> WHITE_DWARF_STORAGE_COMPONENT_1G =
            component("white_dwarf_storage_component_1g", ChatFormatting.WHITE, ChatFormatting.RED, ChatFormatting.DARK_RED, ChatFormatting.RED);
    public static final DeferredHolder<Item, AnimatedNameItem> WHITE_DWARF_STORAGE_COMPONENT_4G =
            component("white_dwarf_storage_component_4g", ChatFormatting.WHITE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE);
    public static final DeferredHolder<Item, AnimatedNameItem> WHITE_DWARF_STORAGE_COMPONENT_16G =
            component("white_dwarf_storage_component_16g", ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.AQUA);
    public static final DeferredHolder<Item, AnimatedNameItem> WHITE_DWARF_STORAGE_COMPONENT_64G =
            component("white_dwarf_storage_component_64g", ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.BLUE);
    public static final DeferredHolder<Item, AnimatedNameItem> WHITE_DWARF_STORAGE_COMPONENT_256G =
            component("white_dwarf_storage_component_256g", ChatFormatting.WHITE, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN, ChatFormatting.GREEN);

    public static final DeferredHolder<Item, AnimatedNameItem> NEUTRON_STAR_STORAGE_COMPONENT_1T =
            component("neutron_star_storage_component_1t", ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA);
    public static final DeferredHolder<Item, AnimatedNameItem> NEUTRON_STAR_STORAGE_COMPONENT_4T =
            component("neutron_star_storage_component_4t", ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA);
    public static final DeferredHolder<Item, AnimatedNameItem> NEUTRON_STAR_STORAGE_COMPONENT_16T =
            component("neutron_star_storage_component_16t", ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA);
    public static final DeferredHolder<Item, AnimatedNameItem> NEUTRON_STAR_STORAGE_COMPONENT_64T =
            component("neutron_star_storage_component_64t", ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA);
    public static final DeferredHolder<Item, AnimatedNameItem> NEUTRON_STAR_STORAGE_COMPONENT_256T =
            component("neutron_star_storage_component_256t", ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA);

    public static final DeferredHolder<Item, AnimatedNameItem> PULSAR_STORAGE_COMPONENT_1P =
            component("pulsar_storage_component_1p", ChatFormatting.WHITE, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN);
    public static final DeferredHolder<Item, AnimatedNameItem> PULSAR_STORAGE_COMPONENT_4P =
            component("pulsar_storage_component_4p", ChatFormatting.WHITE, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN);
    public static final DeferredHolder<Item, AnimatedNameItem> PULSAR_STORAGE_COMPONENT_16P =
            component("pulsar_storage_component_16p", ChatFormatting.WHITE, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN);
    public static final DeferredHolder<Item, AnimatedNameItem> PULSAR_STORAGE_COMPONENT_64P =
            component("pulsar_storage_component_64p", ChatFormatting.WHITE, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN);
    public static final DeferredHolder<Item, AnimatedNameItem> PULSAR_STORAGE_COMPONENT_256P =
            component("pulsar_storage_component_256p", ChatFormatting.WHITE, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN);

    // ------------------------------------------------------------------
    // cell housings
    // ------------------------------------------------------------------

    public static final DeferredHolder<Item, AnimatedNameItem> WHITE_DWARF_CELL_HOUSING =
            component("white_dwarf_cell_housing", ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY);
    public static final DeferredHolder<Item, AnimatedNameItem> NEUTRON_STAR_CELL_HOUSING =
            component("neutron_star_cell_housing", ChatFormatting.BLUE, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA);
    public static final DeferredHolder<Item, AnimatedNameItem> PULSAR_CELL_HOUSING =
            component("pulsar_cell_housing", ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE);

    // ------------------------------------------------------------------
    // legacy neutron star / pulsar storage cells
    // ------------------------------------------------------------------

    private static final ChatFormatting[] NEUTRON_COLORS = {ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA};
    private static final ChatFormatting[] PULSAR_COLORS = {ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE};

    public static final DeferredHolder<Item, ChaosBigCellItem> NEUTRON_STAR_CELL_1T =
            cell("neutron_star_cell_echo", 5.5D, BASE_1T, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.echo", NEUTRON_COLORS, ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.RED);
    public static final DeferredHolder<Item, ChaosBigCellItem> NEUTRON_STAR_CELL_4T =
            cell("neutron_star_cell_beaco", 6.0D, BASE_1T * 4, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.beacon", NEUTRON_COLORS, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE);
    public static final DeferredHolder<Item, ChaosBigCellItem> NEUTRON_STAR_CELL_16T =
            cell("neutron_star_cell_nexus", 6.5D, BASE_1T * 16, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.nexus", NEUTRON_COLORS, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA);
    public static final DeferredHolder<Item, ChaosBigCellItem> NEUTRON_STAR_CELL_64T =
            cell("neutron_star_cell_core", 7.0D, BASE_1T * 64, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.core", NEUTRON_COLORS, ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE);
    public static final DeferredHolder<Item, ChaosBigCellItem> NEUTRON_STAR_CELL_256T =
            cell("neutron_star_cell_singularity", 7.5D, BASE_1T * 256, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.singularity", NEUTRON_COLORS, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN);

    public static final DeferredHolder<Item, ChaosBigCellItem> PULSAR_CELL_1P =
            cell("pulsar_cell_echo", 5.5D, BASE_1P, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.echo", PULSAR_COLORS, ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.RED);
    public static final DeferredHolder<Item, ChaosBigCellItem> PULSAR_CELL_4P =
            cell("pulsar_cell_beaco", 6.0D, BASE_1P * 4, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.beacon", PULSAR_COLORS, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE);
    public static final DeferredHolder<Item, ChaosBigCellItem> PULSAR_CELL_16P =
            cell("pulsar_cell_nexus", 6.5D, BASE_1P * 16, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.nexus", PULSAR_COLORS, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA);
    public static final DeferredHolder<Item, ChaosBigCellItem> PULSAR_CELL_64P =
            cell("pulsar_cell_core", 7.0D, BASE_1P * 64, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.core", PULSAR_COLORS, ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE);
    public static final DeferredHolder<Item, ChaosBigCellItem> PULSAR_CELL_256P =
            cell("pulsar_cell_singularity", 7.5D, BASE_1P * 256, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.singularity", PULSAR_COLORS, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN);

    private static DeferredHolder<Item, AnimatedNameItem> component(String id, ChatFormatting... colors) {
        return ITEMS.register(id, () -> new AnimatedNameItem(new Item.Properties(), colors));
    }

    private static DeferredHolder<Item, ChaosBigCellItem> cell(String id, double idleDrain, long maxBytes,
            String baseNameKey, String tierNameKey, ChatFormatting[] baseColors, ChatFormatting... tierColors) {
        return ITEMS.register(id,
                () -> new ChaosBigCellItem(new Item.Properties().stacksTo(1), idleDrain, null, maxBytes,
                        baseNameKey, tierNameKey, baseColors, tierColors));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

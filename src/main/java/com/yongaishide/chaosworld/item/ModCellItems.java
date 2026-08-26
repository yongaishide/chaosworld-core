package com.yongaishide.chaosworld.item;

import appeng.items.materials.StorageComponentItem;
import appeng.items.storage.StorageTier;
import com.yongaishide.chaosworld.item.custom.AnimatedNameItem;
import com.yongaishide.chaosworld.item.custom.cell.AEBigIntegerCellItem;
import com.yongaishide.chaosworld.item.custom.cell.AnimatedAEBigIntegerCellItem;
import com.yongaishide.chaosworld.item.custom.cell.QuantumOmniStorageCellItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCellItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("chaosworld_core");

    // Housings
    public static final DeferredHolder<Item, Item> WHITE_DWARF_CELL_HOUSING = ITEMS.register("white_dwarf_cell_housing",
            () -> new AnimatedNameItem(new Item.Properties(), ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY));
    public static final DeferredHolder<Item, Item> NEUTRON_STAR_CELL_HOUSING = ITEMS.register("neutron_star_cell_housing",
            () -> new AnimatedNameItem(new Item.Properties(), ChatFormatting.BLUE, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA));
    public static final DeferredHolder<Item, Item> PULSAR_CELL_HOUSING = ITEMS.register("pulsar_cell_housing",
            () -> new AnimatedNameItem(new Item.Properties(), ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE));

    // Storage Components & Tiers (sem altera鑾借尗o)
    public static final DeferredHolder<Item, Item> CELL_COMPONENT_40M = component("storage_cell_side_40m", 40 * 1024);
    public static final DeferredHolder<Item, Item> CELL_COMPONENT_100M = component("storage_cell_side_100m", 100 * 1024);
    public static final DeferredHolder<Item, Item> CELL_COMPONENT_250M = component("storage_cell_side_250m", 250 * 1024);
    public static final DeferredHolder<Item, Item> CELL_COMPONENT_750M = component("storage_cell_side_750m", 750 * 1024);
    public static final DeferredHolder<Item, Item> CELL_COMPONENT_INFINITY = component("storage_cell_side_infinity", Integer.MAX_VALUE);
    public static final StorageTier TIER_40M = new StorageTier(11, "40m", 40_000_000, 5.5D, CELL_COMPONENT_40M);
    public static final StorageTier TIER_100M = new StorageTier(12, "100m", 100_000_000, 6.0D, CELL_COMPONENT_100M);
    public static final StorageTier TIER_250M = new StorageTier(13, "250m", 250_000_000, 6.5D, CELL_COMPONENT_250M);
    public static final StorageTier TIER_750M = new StorageTier(14, "750m", 750_000_000, 7.0D, CELL_COMPONENT_750M);
    public static final StorageTier TIER_INFINITY = new StorageTier(15, "infinity", Integer.MAX_VALUE, 7.5D, CELL_COMPONENT_INFINITY);

    
    // Item Cells: White Dwarf 閳?Quantum style (unlimited types, capacity = 256M 鑴?multiplier)
    // 256M base = 268,435,456 bytes
    private static final long BASE_256M = 268_435_456L;

    public static final DeferredHolder<Item, AEBigIntegerCellItem> WHITE_DWARF_CELL_1G = ITEMS.register("white_dwarf_cell_echo",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 5.5D, BASE_256M * 4, 0, "item.chaosworld_core.white_dwarf_cell", "chaosworld_core.cell_tier.echo", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.RED));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> WHITE_DWARF_CELL_4G = ITEMS.register("white_dwarf_cell_beaco",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 6.0D, BASE_256M * 16, 0, "item.chaosworld_core.white_dwarf_cell", "chaosworld_core.cell_tier.beacon", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> WHITE_DWARF_CELL_16G = ITEMS.register("white_dwarf_cell_nexus",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 6.5D, BASE_256M * 64, 0, "item.chaosworld_core.white_dwarf_cell", "chaosworld_core.cell_tier.nexus", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> WHITE_DWARF_CELL_64G = ITEMS.register("white_dwarf_cell_core",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 7.0D, BASE_256M * 256, 0, "item.chaosworld_core.white_dwarf_cell", "chaosworld_core.cell_tier.core", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> WHITE_DWARF_CELL_256G = ITEMS.register("white_dwarf_cell_singularity",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 7.5D, BASE_256M * 1024, 0, "item.chaosworld_core.white_dwarf_cell", "chaosworld_core.cell_tier.singularity", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN));

    // Neutron Star Cells: multi-type, unlimited types, capacity = 1T 鑴?multiplier
    private static final long BASE_1T = 1_099_511_627_776L;

    public static final DeferredHolder<Item, AEBigIntegerCellItem> NEUTRON_STAR_CELL_1T = ITEMS.register("neutron_star_cell_echo",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 5.5D, BASE_1T, 0, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.echo", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.RED));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> NEUTRON_STAR_CELL_4T = ITEMS.register("neutron_star_cell_beaco",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 6.0D, BASE_1T * 4, 0, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.beacon", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> NEUTRON_STAR_CELL_16T = ITEMS.register("neutron_star_cell_nexus",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 6.5D, BASE_1T * 16, 0, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.nexus", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> NEUTRON_STAR_CELL_64T = ITEMS.register("neutron_star_cell_core",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 7.0D, BASE_1T * 64, 0, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.core", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> NEUTRON_STAR_CELL_256T = ITEMS.register("neutron_star_cell_singularity",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 7.5D, BASE_1T * 256, 0, "item.chaosworld_core.neutron_star_cell", "chaosworld_core.cell_tier.singularity", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN));

    // Pulsar Cells: multi-type, unlimited types, capacity = 1P 鑴?multiplier
    // 1P = 1024^5 = 1,125,899,906,842,624
    private static final long BASE_1P = 1_125_899_906_842_624L;

    public static final DeferredHolder<Item, AEBigIntegerCellItem> PULSAR_CELL_1P = ITEMS.register("pulsar_cell_echo",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 5.5D, BASE_1P, 0, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.echo", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE}, ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.RED));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> PULSAR_CELL_4P = ITEMS.register("pulsar_cell_beaco",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 6.0D, BASE_1P * 4, 0, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.beacon", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE}, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> PULSAR_CELL_16P = ITEMS.register("pulsar_cell_nexus",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 6.5D, BASE_1P * 16, 0, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.nexus", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE}, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> PULSAR_CELL_64P = ITEMS.register("pulsar_cell_core",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 7.0D, BASE_1P * 64, 0, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.core", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE}, ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> PULSAR_CELL_256P = ITEMS.register("pulsar_cell_singularity",
            () -> new AnimatedAEBigIntegerCellItem(new Item.Properties().stacksTo(1), 7.5D, BASE_1P * 256, 0, "item.chaosworld_core.pulsar_cell", "chaosworld_core.cell_tier.singularity", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE}, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN));

    // Quantum Omni Storage Matrices: ECO storage subsystem cells (NeoECOAE ECO Drive compatible)
    // omni (all key types), unlimited types, capacity = 16G/64G/256G
    // idle drain scales x3 per tier, base = 59049 (NeoECOAE quantum omni 256m = 4GiB, already exists there)
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_16G = ITEMS.register("quantum_omni_cell_nexus",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 177147D, BASE_256M * 64));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_64G = ITEMS.register("quantum_omni_cell_core",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 531441D, BASE_256M * 256));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_256G = ITEMS.register("quantum_omni_cell_singularity",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 1594323D, BASE_256M * 1024));

    // Quantum Omni Storage Matrices - T series (1T/4T/16T/64T/256T, drain x3 per tier)
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_1T = ITEMS.register("quantum_omni_cell_1t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 4782969D, BASE_1T));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_4T = ITEMS.register("quantum_omni_cell_4t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 14348907D, BASE_1T * 4));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_16T = ITEMS.register("quantum_omni_cell_16t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 43046721D, BASE_1T * 16));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_64T = ITEMS.register("quantum_omni_cell_64t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 129140163D, BASE_1T * 64));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_256T = ITEMS.register("quantum_omni_cell_256t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 387420489D, BASE_1T * 256));

    // Quantum Omni Storage Matrices - P series (1P/4P/16P/64P/256P, drain x3 per tier)
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_1P = ITEMS.register("quantum_omni_cell_1p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 1162261467D, BASE_1P));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_4P = ITEMS.register("quantum_omni_cell_4p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 3486784401D, BASE_1P * 4));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_16P = ITEMS.register("quantum_omni_cell_16p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 10460353203D, BASE_1P * 16));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_64P = ITEMS.register("quantum_omni_cell_64p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 31381059609D, BASE_1P * 64));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_256P = ITEMS.register("quantum_omni_cell_256p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 94143178827D, BASE_1P * 256));


    private static DeferredHolder<Item, Item> component(String id, int kibiBytes) {
        return ITEMS.register(id, () -> new StorageComponentItem(new Item.Properties(), kibiBytes));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}


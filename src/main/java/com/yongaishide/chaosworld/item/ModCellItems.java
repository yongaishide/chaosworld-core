package com.yongaishide.chaosworld.item;

import com.yongaishide.chaosworld.item.custom.cell.QuantumOmniStorageCellItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCellItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("chaosworld_core");

    // 256M base = 268,435,456 bytes
    private static final long BASE_256M = 268_435_456L;
    private static final long BASE_1T = 1_099_511_627_776L;
    private static final long BASE_1P = 1_125_899_906_842_624L;

    // Quantum Omni Storage Matrices: ECO storage subsystem cells (NeoECOAE ECO Drive compatible)
    // omni (all key types), unlimited types, capacity = 16G/64G/256G
    // idle drain scales x3 per tier, base = 59049 (NeoECOAE quantum omni 256m = 4GiB, already exists there)
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_16G = ITEMS.register("white_dwarf_omni_cell_nexus",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 177147D, BASE_256M * 64));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_64G = ITEMS.register("white_dwarf_omni_cell_core",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 531441D, BASE_256M * 256));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_256G = ITEMS.register("white_dwarf_omni_cell_singularity",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 1594323D, BASE_256M * 1024));

    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_1T = ITEMS.register("neutron_star_omni_cell_1t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 4782969D, BASE_1T));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_4T = ITEMS.register("neutron_star_omni_cell_4t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 14348907D, BASE_1T * 4));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_16T = ITEMS.register("neutron_star_omni_cell_16t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 43046721D, BASE_1T * 16));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_64T = ITEMS.register("neutron_star_omni_cell_64t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 129140163D, BASE_1T * 64));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_256T = ITEMS.register("neutron_star_omni_cell_256t",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 387420489D, BASE_1T * 256));

    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_1P = ITEMS.register("pulsar_omni_cell_1p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 1162261467D, BASE_1P));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_4P = ITEMS.register("pulsar_omni_cell_4p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 3486784401D, BASE_1P * 4));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_16P = ITEMS.register("pulsar_omni_cell_16p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 10460353203D, BASE_1P * 16));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_64P = ITEMS.register("pulsar_omni_cell_64p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 31381059609D, BASE_1P * 64));
    public static final DeferredHolder<Item, QuantumOmniStorageCellItem> QUANTUM_OMNI_CELL_256P = ITEMS.register("pulsar_omni_cell_256p",
            () -> new QuantumOmniStorageCellItem(new Item.Properties(), 94143178827D, BASE_1P * 256));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

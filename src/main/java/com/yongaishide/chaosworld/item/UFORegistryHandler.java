package com.yongaishide.chaosworld.item;

import net.minecraft.resources.ResourceLocation;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import cn.dancingsnow.neoecoae.api.ECOCellModels;
import com.yongaishide.chaosworld.item.custom.cell.AEBigIntegerCellHandler;
import com.yongaishide.chaosworld.item.custom.cell.QuantumOmniCellHandler;

public class UFORegistryHandler {

    public static final UFORegistryHandler INSTANCE = new UFORegistryHandler();

    private boolean initialized = false;

    public void onInit() {
        if (initialized) return;
        initialized = true;
        this.registerStorageHandler();
        this.registerUpgrades();
    }

    private void registerUpgrades() {
        // Use the block's asItem() to ensure the same Item instance as UpgradeInventories.forMachine() in the BE constructor
        java.util.List<net.minecraft.world.item.Item> machineItems = java.util.List.of(
                com.yongaishide.chaosworld.block.ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().asItem(),
                com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem(),
                com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get().asItem(),
                com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get().asItem(),
                com.yongaishide.chaosworld.block.MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem());

        for (var machineItem : machineItems) {
            appeng.api.upgrades.Upgrades.add(ModItems.MATTERFLOW_CATALYST_T1.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.MATTERFLOW_CATALYST_T2.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.MATTERFLOW_CATALYST_T3.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.CHRONO_CATALYST_T1.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.CHRONO_CATALYST_T2.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.CHRONO_CATALYST_T3.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.OVERFLUX_CATALYST_T1.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.OVERFLUX_CATALYST_T2.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.OVERFLUX_CATALYST_T3.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.QUANTUM_CATALYST_T1.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.QUANTUM_CATALYST_T2.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.QUANTUM_CATALYST_T3.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.DIMENSIONAL_CATALYST.get(), machineItem, 4);
        }
    }

    private void registerStorageHandler() {
        StorageCells.addCellHandler(AEBigIntegerCellHandler.INSTANCE);

        // --- White Dwarf Item Cells 鈫?3D drive model (gray/silver) ---
        StorageCellModels.registerModel(ModCellItems.WHITE_DWARF_CELL_1G.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/white_dwarf_cell"));
        StorageCellModels.registerModel(ModCellItems.WHITE_DWARF_CELL_4G.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/white_dwarf_cell"));
        StorageCellModels.registerModel(ModCellItems.WHITE_DWARF_CELL_16G.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/white_dwarf_cell"));
        StorageCellModels.registerModel(ModCellItems.WHITE_DWARF_CELL_64G.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/white_dwarf_cell"));
        StorageCellModels.registerModel(ModCellItems.WHITE_DWARF_CELL_256G.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/white_dwarf_cell"));

        // --- Neutron Star Fluid Cells 鈫?3D drive model (deep blue) ---
        StorageCellModels.registerModel(ModCellItems.NEUTRON_STAR_CELL_1T.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/neutron_star_cell"));
        StorageCellModels.registerModel(ModCellItems.NEUTRON_STAR_CELL_4T.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/neutron_star_cell"));
        StorageCellModels.registerModel(ModCellItems.NEUTRON_STAR_CELL_16T.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/neutron_star_cell"));
        StorageCellModels.registerModel(ModCellItems.NEUTRON_STAR_CELL_64T.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/neutron_star_cell"));
        StorageCellModels.registerModel(ModCellItems.NEUTRON_STAR_CELL_256T.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/neutron_star_cell"));

        // --- Pulsar Chemical Cells 鈫?3D drive model (violet) ---
        StorageCellModels.registerModel(ModCellItems.PULSAR_CELL_1P.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/pulsar_cell"));
        StorageCellModels.registerModel(ModCellItems.PULSAR_CELL_4P.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/pulsar_cell"));
        StorageCellModels.registerModel(ModCellItems.PULSAR_CELL_16P.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/pulsar_cell"));
        StorageCellModels.registerModel(ModCellItems.PULSAR_CELL_64P.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/pulsar_cell"));
        StorageCellModels.registerModel(ModCellItems.PULSAR_CELL_256P.get(), ResourceLocation.fromNamespaceAndPath("chaosworld_core", "drive/cells/pulsar_cell"));

        // --- Quantum Omni Storage Matrices (NeoECOAE ECO storage subsystem) ---
        QuantumOmniCellHandler.register();
        ResourceLocation quantumOmniDriveModel = ResourceLocation.fromNamespaceAndPath("neoecoae", "block/cell/storage_cell_l9_quantum_omni");
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_16G.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_64G.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_256G.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_1T.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_4T.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_16T.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_64T.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_256T.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_1P.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_4P.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_16P.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_64P.get(), quantumOmniDriveModel);
        ECOCellModels.register(ModCellItems.QUANTUM_OMNI_CELL_256P.get(), quantumOmniDriveModel);
    }
}

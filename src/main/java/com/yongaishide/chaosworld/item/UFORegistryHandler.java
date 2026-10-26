package com.yongaishide.chaosworld.item;

import net.minecraft.resources.ResourceLocation;

import appeng.api.client.StorageCellModels;
import cn.dancingsnow.neoecoae.api.ECOCellModels;
import com.yongaishide.chaosworld.item.custom.cell.QuantumOmniCellHandler;

/**
 * Runtime registrations for Chaos World Core's own storage cells.
 * UFO Future handles its cells through its own registry handler.
 */
public class UFORegistryHandler {

    public static final UFORegistryHandler INSTANCE = new UFORegistryHandler();

    private boolean initialized = false;

    public void onInit() {
        if (initialized) return;
        initialized = true;
        this.registerStorageHandler();
    }

    private void registerStorageHandler() {
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

        ResourceLocation whiteDwarfDriveModel = ResourceLocation.fromNamespaceAndPath("ufo", "drive/cells/white_dwarf_cell");
        ResourceLocation neutronDriveModel = ResourceLocation.fromNamespaceAndPath("ufo", "drive/cells/neutron_star_cell");
        ResourceLocation pulsarDriveModel = ResourceLocation.fromNamespaceAndPath("ufo", "drive/cells/pulsar_cell");
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_CELL_1G.get(), whiteDwarfDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_CELL_4G.get(), whiteDwarfDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_CELL_16G.get(), whiteDwarfDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_CELL_64G.get(), whiteDwarfDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_CELL_256G.get(), whiteDwarfDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_1T.get(), neutronDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_4T.get(), neutronDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_16T.get(), neutronDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_64T.get(), neutronDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_256T.get(), neutronDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_1P.get(), pulsarDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_4P.get(), pulsarDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_16P.get(), pulsarDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_64P.get(), pulsarDriveModel);
        StorageCellModels.registerModel(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_256P.get(), pulsarDriveModel);
    }
}

package com.yongaishide.chaosworld.datagen;

import com.yongaishide.chaosworld.fluid.ModFluids;
import com.yongaishide.chaosworld.item.ModArmor;
import com.yongaishide.chaosworld.item.ModCellItems;
import com.yongaishide.chaosworld.item.ModItems;
import com.yongaishide.chaosworld.item.ModTools;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraft.world.level.material.Fluid;
import java.util.function.Supplier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.world.item.BucketItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, "chaosworld_core", existingFileHelper);
    }

    private static final ResourceLocation UFO_LED_TEXTURE = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/storage_cell_led");
    private static final ResourceLocation GENERATED_PARENT = ResourceLocation.withDefaultNamespace("item/generated");
    @Override
        protected void registerModels() {
        basicItem(ModItems.BISMUTH);
        basicItem(ModItems.WHITE_DWARF_STORAGE_COMPONENT_1G);
        basicItem(ModItems.WHITE_DWARF_STORAGE_COMPONENT_4G);
        basicItem(ModItems.WHITE_DWARF_STORAGE_COMPONENT_16G);
        basicItem(ModItems.WHITE_DWARF_STORAGE_COMPONENT_64G);
        basicItem(ModItems.WHITE_DWARF_STORAGE_COMPONENT_256G);
        basicItem(ModItems.DIMENSIONAL_PROCESSOR_PRESS);
        basicItem(ModItems.DIMENSIONAL_PROCESSOR);
        basicItem(ModItems.PRINTED_DIMENSIONAL_PROCESSOR);
        basicItem(ModItems.WHITE_DWARF_FRAGMENT_INGOT);
        basicItem(ModItems.WHITE_DWARF_FRAGMENT_ROD);
        basicItem(ModItems.WHITE_DWARF_FRAGMENT_DUST);
        basicItem(ModItems.WHITE_DWARF_FRAGMENT_NUGGET);
        withExistingParent(ModItems.NEUTRON_STAR_FRAGMENT_INGOT.getId().getPath(), GENERATED_PARENT)
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/neutron_star_fragment_ingot"))
                .texture("layer1", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/neutron_star_fragment_ingot_overlay"));
        withExistingParent(ModItems.NEUTRON_STAR_FRAGMENT_NUGGET.getId().getPath(), GENERATED_PARENT)
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/neutron_star_fragment_nugget"))
                .texture("layer1", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/neutron_star_fragment_nugget_overlay"));
        withExistingParent(ModItems.NEUTRON_STAR_FRAGMENT_ROD.getId().getPath(), GENERATED_PARENT)
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/neutron_star_fragment_rod"))
                .texture("layer1", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/neutron_star_fragment_rod_overlay"));
        withExistingParent(ModItems.NEUTRON_STAR_FRAGMENT_DUST.getId().getPath(), GENERATED_PARENT)
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/neutron_star_fragment_dust"))
                .texture("layer1", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/neutron_star_fragment_dust_overlay"));
        basicItem(ModItems.PULSAR_FRAGMENT_INGOT);
        basicItem(ModItems.PULSAR_FRAGMENT_DUST);
        basicItem(ModItems.PULSAR_FRAGMENT_NUGGET);
        basicItem(ModArmor.THERMAL_RESISTOR_PLATING);

        customParentItem(ModTools.UFO_AXE, "item/ufoset/axe");
        customParentItem(ModTools.UFO_HOE, "item/ufoset/hoe");
       // handheldItem(ModTools.UFO_BOW);
        customParentItem(ModTools.UFO_GREATSWORD, "item/ufoset/greatsword");
        customParentItem(ModTools.UFO_HAMMER, "item/ufoset/hammer");
        customParentItem(ModTools.UFO_SHOVEL, "item/ufoset/shovel");
        customParentItem(ModTools.UFO_SWORD, "item/ufoset/sword");
        customParentItem(ModTools.UFO_PICKAXE, "item/ufoset/pickaxe");
        // Custom fishing rod models with overrides are authored in src/main/resources.
        customParentItem(ModTools.UFO_STAFF, "item/ufoset/staff");
        customParentItem(ModTools.REALITY_RIPPER, "item/ufoset/reality_ripper");
        customParentItem(ModArmor.UFO_HELMET, "item/ufoset/helmet");
        withExistingParent(ModArmor.UFO_CHESTPLATE.getId().getPath(), GENERATED_PARENT)
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/ufo_chestplate"));
        withExistingParent(ModArmor.UFO_LEGGINGS.getId().getPath(), GENERATED_PARENT)
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/ufo_leggings"));
        withExistingParent(ModArmor.UFO_BOOTS.getId().getPath(), GENERATED_PARENT)
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/ufo_boots"));
        basicItem(ModArmor.ASTRAL_NEXUS_HELMET);
        basicItem(ModArmor.THERMAL_RESISTOR_BOOTS);
        basicItem(ModArmor.THERMAL_RESISTOR_CHEST);
        basicItem(ModArmor.THERMAL_RESISTOR_MASK);
        basicItem(ModArmor.THERMAL_RESISTOR_PANTS);
        basicItem(ModItems.QUANTUM_ANOMALY);
        basicItem(ModItems.NUCLEAR_STAR);
        basicItem(ModItems.SCAR);
        basicItem(ModItems.SCRAP);
        basicItem(ModItems.SCRAP_BOX);

        // --- Esferas / Componentes Avan莽ados ---
        basicItem(ModItems.NEUTRONIUM_SPHERE);
        basicItem(ModItems.ENRICHED_NEUTRONIUM_SPHERE);
        basicItem(ModItems.CHARGED_ENRICHED_NEUTRONIUM_SPHERE);

        // --- Stages de Matter ---
        basicItem(ModItems.PROTO_MATTER);
        basicItem(ModItems.CORPOREAL_MATTER);
        basicItem(ModItems.WHITE_DWARF_MATTER);
        basicItem(ModItems.NEUTRON_STAR_MATTER);
        basicItem(ModItems.PULSAR_MATTER);
        basicItem(ModItems.DARK_MATTER);
        basicItem(ModItems.UU_MATTER_CRYSTAL);

        // --- Catalyst ---
        basicItem(ModItems.CHRONO_CATALYST_T1);
        basicItem(ModItems.CHRONO_CATALYST_T2);
        basicItem(ModItems.CHRONO_CATALYST_T3);
        basicItem(ModItems.MATTERFLOW_CATALYST_T1);
        basicItem(ModItems.MATTERFLOW_CATALYST_T2);
        basicItem(ModItems.MATTERFLOW_CATALYST_T3);
        basicItem(ModItems.OVERFLUX_CATALYST_T1);
        basicItem(ModItems.OVERFLUX_CATALYST_T2);
        basicItem(ModItems.OVERFLUX_CATALYST_T3);
        basicItem(ModItems.QUANTUM_CATALYST_T1);
        basicItem(ModItems.QUANTUM_CATALYST_T2);
        basicItem(ModItems.QUANTUM_CATALYST_T3);
        basicItem(ModItems.DIMENSIONAL_CATALYST);

        // --- Estrutural / Utilit谩rio ---
        basicItem(ModItems.OBSIDIAN_MATRIX);
        basicItem(ModItems.DUST_CRYOTHEUM);
        basicItem(ModItems.DUST_BLIZZ);
        basicItem(ModItems.UNSTABLE_WHITE_HOLE_MATTER);
        basicItem(ModItems.AETHER_CONTAINMENT_CAPSULE);
        basicItem(ModItems.SAFE_CONTAINMENT_MATTER);

            // Housings
            basicItem(ModCellItems.WHITE_DWARF_CELL_HOUSING);
            basicItem(ModCellItems.NEUTRON_STAR_CELL_HOUSING);
            basicItem(ModCellItems.PULSAR_CELL_HOUSING);

            // Components
            basicItem(ModCellItems.CELL_COMPONENT_40M);
            basicItem(ModCellItems.CELL_COMPONENT_100M);
            basicItem(ModCellItems.CELL_COMPONENT_250M);
            basicItem(ModCellItems.CELL_COMPONENT_750M);
            basicItem(ModCellItems.CELL_COMPONENT_INFINITY);

            // Item Cells
            cellModel(ModCellItems.WHITE_DWARF_CELL_1G, ModCellItems.WHITE_DWARF_CELL_HOUSING, ModCellItems.CELL_COMPONENT_40M);
            cellModel(ModCellItems.WHITE_DWARF_CELL_4G, ModCellItems.WHITE_DWARF_CELL_HOUSING, ModCellItems.CELL_COMPONENT_100M);
            cellModel(ModCellItems.WHITE_DWARF_CELL_16G, ModCellItems.WHITE_DWARF_CELL_HOUSING, ModCellItems.CELL_COMPONENT_250M);
            cellModel(ModCellItems.WHITE_DWARF_CELL_64G, ModCellItems.WHITE_DWARF_CELL_HOUSING, ModCellItems.CELL_COMPONENT_750M);
            cellModel(ModCellItems.WHITE_DWARF_CELL_256G, ModCellItems.WHITE_DWARF_CELL_HOUSING, ModCellItems.CELL_COMPONENT_INFINITY);

            // Fluid Cells
            cellModel(ModCellItems.NEUTRON_STAR_CELL_1T, ModCellItems.NEUTRON_STAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_40M);
            cellModel(ModCellItems.NEUTRON_STAR_CELL_4T, ModCellItems.NEUTRON_STAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_100M);
            cellModel(ModCellItems.NEUTRON_STAR_CELL_16T, ModCellItems.NEUTRON_STAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_250M);
            cellModel(ModCellItems.NEUTRON_STAR_CELL_64T, ModCellItems.NEUTRON_STAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_750M);
            cellModel(ModCellItems.NEUTRON_STAR_CELL_256T, ModCellItems.NEUTRON_STAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_INFINITY);

            // Chemical Cells
            cellModel(ModCellItems.PULSAR_CELL_1P, ModCellItems.PULSAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_40M);
            cellModel(ModCellItems.PULSAR_CELL_4P, ModCellItems.PULSAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_100M);
            cellModel(ModCellItems.PULSAR_CELL_16P, ModCellItems.PULSAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_250M);
            cellModel(ModCellItems.PULSAR_CELL_64P, ModCellItems.PULSAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_750M);
            cellModel(ModCellItems.PULSAR_CELL_256P, ModCellItems.PULSAR_CELL_HOUSING, ModCellItems.CELL_COMPONENT_INFINITY);

            // Quantum Omni Cells
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_16G);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_64G);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_256G);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_1T);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_4T);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_16T);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_64T);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_256T);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_1P);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_4P);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_16P);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_64P);
            quantumOmniCellModel(ModCellItems.QUANTUM_OMNI_CELL_256P);


        dynamicBucketItem(ModItems.NEUTRON_STAR_FRAGMENT_BUCKET, ModFluids.SOURCE_NEUTRON_STAR_FRAGMENT_FLUID);
        dynamicBucketItem(ModItems.PULSAR_FRAGMENT_BUCKET, ModFluids.SOURCE_PULSAR_FRAGMENT_FLUID);
        dynamicBucketItem(ModItems.WHITE_DWARF_FRAGMENT_BUCKET, ModFluids.SOURCE_WHITE_DWARF_FRAGMENT_FLUID);
        dynamicBucketItem(ModItems.LIQUID_STARLIGHT_BUCKET, ModFluids.SOURCE_LIQUID_STARLIGHT_FLUID);
        dynamicBucketItem(ModItems.PRIMORDIAL_MATTER_BUCKET, ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID);
        dynamicBucketItem(ModItems.RAW_STAR_MATTER_PLASMA_BUCKET, ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID);
        dynamicBucketItem(ModItems.TRANSCENDING_MATTER_BUCKET, ModFluids.SOURCE_TRANSCENDING_MATTER_FLUID);
        dynamicBucketItem(ModItems.UU_MATTER_BUCKET, ModFluids.SOURCE_UU_MATTER_FLUID);
        dynamicBucketItem(ModItems.UU_AMPLIFIER_BUCKET, ModFluids.SOURCE_UU_AMPLIFIER_FLUID);
        dynamicBucketItem(ModItems.GELID_CRYOTHEUM_BUCKET, ModFluids.SOURCE_GELID_CRYOTHEUM);
        dynamicBucketItem(ModItems.STABLE_COOLANT_BUCKET, ModFluids.SOURCE_STABLE_COOLANT);
        dynamicBucketItem(ModItems.TEMPORAL_FLUID_BUCKET, ModFluids.SOURCE_TEMPORAL_FLUID);
        dynamicBucketItem(ModItems.SPATIAL_FLUID_BUCKET, ModFluids.SOURCE_SPATIAL_FLUID);

    }

    private ItemModelBuilder handheldItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath("chaosworld_core","item/" + item.getId().getPath()));
    }

    private ItemModelBuilder customParentItem(DeferredItem<?> item, String parent) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.fromNamespaceAndPath("chaosworld_core", parent));
    }

    private ItemModelBuilder basicItem(DeferredHolder<Item, ? extends Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/" + item.getId().getPath()));
    }

    private void dynamicBucketItem(DeferredItem<Item> bucket, Supplier<? extends Fluid> fluid) {
        withExistingParent(bucket.getId().getPath(), ResourceLocation.fromNamespaceAndPath("neoforge", "item/bucket"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(fluid.get());
    }
    private void cellModel(DeferredHolder<Item, ? extends Item> cell, DeferredHolder<Item, ? extends Item> housing, DeferredHolder<Item, ? extends Item> component) {
        withExistingParent(cell.getId().getPath(), ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/" + housing.getId().getPath())) // Camada base: o housing
                .texture("layer1", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/" + component.getId().getPath())) // Camada do meio: o "side"
                .texture("layer2", UFO_LED_TEXTURE);// Camada de cima: o LED do AE2
    }
    private void infinityResourceCellModel(DeferredHolder<Item, ? extends Item> cell) {
        withExistingParent(cell.getId().getPath(), ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/" + cell.getId().getPath())) // Camada base: A textura da pr贸pria c茅lula
                .texture("layer1", ResourceLocation.fromNamespaceAndPath("chaosworld_core", "item/" + ModCellItems.CELL_COMPONENT_INFINITY.getId().getPath())) // Camada do meio: O componente infinity
                .texture("layer2", UFO_LED_TEXTURE); // Camada de cima: O LED
    }

    private void quantumOmniCellModel(DeferredHolder<Item, ? extends Item> cell) {
        withExistingParent(cell.getId().getPath(), ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath("neoecoae", "item/eco_cell_compat/quantum_omni_cell_housing"))
                .texture("layer1", ResourceLocation.fromNamespaceAndPath("neoecoae", "item/eco_cell_light_256m"))
                .texture("layer2", ResourceLocation.fromNamespaceAndPath("neoecoae", "item/eco_cell_compat/quantum_omni_cell_layer"));
    }
}

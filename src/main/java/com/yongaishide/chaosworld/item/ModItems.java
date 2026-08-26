package com.yongaishide.chaosworld.item;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.fluid.ModFluids;
import com.yongaishide.chaosworld.init.ModEntities;
import com.yongaishide.chaosworld.item.custom.*;
import com.yongaishide.chaosworld.part.QuantumPatternProviderPart;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("chaosworld_core");

    public static final DeferredItem<Item> BISMUTH = ITEMS.register("bismuth",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NEUTRONITE_INGOT = ITEMS.register("neutronite_ingot",
            () -> new Item(new Item.Properties()) {
                @Override
                public net.minecraft.network.chat.Component getName(net.minecraft.world.item.ItemStack stack) {
                    return net.minecraft.network.chat.Component.translatable(this.getDescriptionId())
                            .withStyle(ChatFormatting.DARK_PURPLE);
                }
            });

    public static final DeferredItem<Item> DIMENSIONAL_PROCESSOR_PRESS = animatedItem("dimensional_processor_press", ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.BLACK,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY);
    public static final DeferredItem<Item> DIMENSIONAL_PROCESSOR = animatedItem("dimensional_processor", ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.BLACK,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY);
    public static final DeferredItem<Item> WHITE_DWARF_STORAGE_COMPONENT_1G = animatedItem("white_dwarf_storage_component_1g", ChatFormatting.WHITE,
                    ChatFormatting.RED,
                    ChatFormatting.DARK_RED,
                    ChatFormatting.RED);
    public static final DeferredItem<Item> WHITE_DWARF_STORAGE_COMPONENT_4G = animatedItem("white_dwarf_storage_component_4g", ChatFormatting.WHITE,
                    ChatFormatting.LIGHT_PURPLE,
                    ChatFormatting.DARK_PURPLE,
                    ChatFormatting.LIGHT_PURPLE);
    public static final DeferredItem<Item> WHITE_DWARF_STORAGE_COMPONENT_16G = animatedItem("white_dwarf_storage_component_16g", ChatFormatting.WHITE,
                    ChatFormatting.AQUA,
                    ChatFormatting.DARK_AQUA,
                    ChatFormatting.AQUA);
    public static final DeferredItem<Item> WHITE_DWARF_STORAGE_COMPONENT_64G = animatedItem("white_dwarf_storage_component_64g", ChatFormatting.WHITE,
                    ChatFormatting.BLUE,
                    ChatFormatting.DARK_BLUE,
                    ChatFormatting.BLUE);
    public static final DeferredItem<Item> WHITE_DWARF_STORAGE_COMPONENT_256G = animatedItem("white_dwarf_storage_component_256g", ChatFormatting.WHITE,
                    ChatFormatting.GREEN,
                    ChatFormatting.DARK_GREEN,
                    ChatFormatting.GREEN);
    public static final DeferredItem<Item> NEUTRON_STAR_STORAGE_COMPONENT_1T = animatedItem("neutron_star_storage_component_1t", ChatFormatting.WHITE,
                    ChatFormatting.BLUE,
                    ChatFormatting.DARK_BLUE,
                    ChatFormatting.BLUE);
    public static final DeferredItem<Item> NEUTRON_STAR_STORAGE_COMPONENT_4T = animatedItem("neutron_star_storage_component_4t", ChatFormatting.WHITE,
                    ChatFormatting.BLUE,
                    ChatFormatting.DARK_BLUE,
                    ChatFormatting.BLUE);
    public static final DeferredItem<Item> NEUTRON_STAR_STORAGE_COMPONENT_16T = animatedItem("neutron_star_storage_component_16t", ChatFormatting.WHITE,
                    ChatFormatting.BLUE,
                    ChatFormatting.DARK_BLUE,
                    ChatFormatting.BLUE);
    public static final DeferredItem<Item> NEUTRON_STAR_STORAGE_COMPONENT_64T = animatedItem("neutron_star_storage_component_64t", ChatFormatting.WHITE,
                    ChatFormatting.BLUE,
                    ChatFormatting.DARK_BLUE,
                    ChatFormatting.BLUE);
    public static final DeferredItem<Item> NEUTRON_STAR_STORAGE_COMPONENT_256T = animatedItem("neutron_star_storage_component_256t", ChatFormatting.WHITE,
                    ChatFormatting.BLUE,
                    ChatFormatting.DARK_BLUE,
                    ChatFormatting.BLUE);
    public static final DeferredItem<Item> PULSAR_STORAGE_COMPONENT_1P = animatedItem("pulsar_storage_component_1p", ChatFormatting.WHITE,
                    ChatFormatting.AQUA,
                    ChatFormatting.DARK_AQUA,
                    ChatFormatting.AQUA);
    public static final DeferredItem<Item> PULSAR_STORAGE_COMPONENT_4P = animatedItem("pulsar_storage_component_4p", ChatFormatting.WHITE,
                    ChatFormatting.AQUA,
                    ChatFormatting.DARK_AQUA,
                    ChatFormatting.AQUA);
    public static final DeferredItem<Item> PULSAR_STORAGE_COMPONENT_16P = animatedItem("pulsar_storage_component_16p", ChatFormatting.WHITE,
                    ChatFormatting.AQUA,
                    ChatFormatting.DARK_AQUA,
                    ChatFormatting.AQUA);
    public static final DeferredItem<Item> PULSAR_STORAGE_COMPONENT_64P = animatedItem("pulsar_storage_component_64p", ChatFormatting.WHITE,
                    ChatFormatting.AQUA,
                    ChatFormatting.DARK_AQUA,
                    ChatFormatting.AQUA);
    public static final DeferredItem<Item> PULSAR_STORAGE_COMPONENT_256P = animatedItem("pulsar_storage_component_256p", ChatFormatting.WHITE,
                    ChatFormatting.AQUA,
                    ChatFormatting.DARK_AQUA,
                    ChatFormatting.AQUA);
    public static final DeferredItem<Item> PRINTED_DIMENSIONAL_PROCESSOR = animatedItem("printed_dimensional_processor", ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.BLACK,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY);
    public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_INGOT = animatedItem("white_dwarf_fragment_ingot", ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY);
     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_ROD = animatedItem("white_dwarf_fragment_rod", ChatFormatting.WHITE,
                     ChatFormatting.GRAY,
                     ChatFormatting.DARK_GRAY,
                     ChatFormatting.GRAY);
     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_NUGGET = animatedItem("white_dwarf_fragment_nugget", ChatFormatting.WHITE,
                     ChatFormatting.GRAY,
                     ChatFormatting.DARK_GRAY,
                     ChatFormatting.GRAY);
     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_DUST = animatedItem("white_dwarf_fragment_dust", ChatFormatting.WHITE,
                     ChatFormatting.GRAY,
                     ChatFormatting.DARK_GRAY,
                     ChatFormatting.GRAY);
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_INGOT = animatedItem("neutron_star_fragment_ingot", ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA);
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_ROD = animatedItem("neutron_star_fragment_rod", ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA);
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_NUGGET = animatedItem("neutron_star_fragment_nugget", ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA);
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_DUST = animatedItem("neutron_star_fragment_dust", ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA);
     public static final DeferredItem<Item> PULSAR_FRAGMENT_INGOT = animatedItem("pulsar_fragment_ingot", ChatFormatting.WHITE,
                     ChatFormatting.GREEN,
                     ChatFormatting.DARK_GREEN);
     public static final DeferredItem<Item> PULSAR_FRAGMENT_NUGGET = animatedItem("pulsar_fragment_nugget", ChatFormatting.WHITE,
                     ChatFormatting.GREEN,
                     ChatFormatting.DARK_GREEN);
     public static final DeferredItem<Item> PULSAR_FRAGMENT_DUST = animatedItem("pulsar_fragment_dust", ChatFormatting.WHITE,
                     ChatFormatting.GREEN,
                     ChatFormatting.DARK_GREEN);

    // infinite items cells


    // Tools and Armor have been moved to ModTools.java and ModArmor.java


     // Standalone entropy casings (the entropic convergence engine multiblock was removed)
     // entropy_singularity_casing: variacoes de cinza e preto
     public static final DeferredItem<Item> ENTROPY_SINGULARITY_CASING = animatedBlockItem("entropy_singularity_casing", MultiblockBlocks.ENTROPY_SINGULARITY_CASING, ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY, ChatFormatting.BLACK, ChatFormatting.DARK_GRAY, ChatFormatting.GRAY);

     public static final DeferredItem<Item> QUANTUM_ENTROPY_CASING = animatedBlockItem("quantum_entropy_casing", MultiblockBlocks.QUANTUM_ENTROPY_CASING, ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA, ChatFormatting.AQUA);


     // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?     //  QUANTUM MATTER FABRICATOR �?Block Items
     // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?
    public static final DeferredItem<Item> QUANTUM_HYPER_MECHANICAL_CASING = animatedBlockItem("quantum_hyper_mechanical_casing", MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING, ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA);

     public static final DeferredItem<Item> QUANTUM_MATTER_FABRICATOR_CONTROLLER = animatedBlockItem("quantum_matter_fabricator_controller", MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE);

     public static final DeferredItem<Item> QUANTUM_SLICER_CONTROLLER = animatedBlockItem("quantum_slicer_controller", MultiblockBlocks.QUANTUM_SLICER_CONTROLLER, ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA);

     public static final DeferredItem<Item> QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER = animatedBlockItem("quantum_processing_factory_controller", MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER, ChatFormatting.LIGHT_PURPLE, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.WHITE);

     public static final DeferredItem<Item> QUANTUM_CRYOFORGE_CONTROLLER = animatedBlockItem("quantum_cryoforge_controller", MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER, ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA);

     public static final DeferredItem<Item> QUANTUM_PATTERN_HATCH = animatedBlockItem("quantum_pattern_hatch", MultiblockBlocks.QUANTUM_PATTERN_HATCH, ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE);

     public static final DeferredItem<Item> QUANTUM_PATTERN_PROVIDER_PART = registerPartItem(
             "quantum_pattern_provider_part",
             QuantumPatternProviderPart.class,
             QuantumPatternProviderPart::new);

     // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?     //  STELLAR NEXUS 鈥?Block Items
     // 鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?
     public static final DeferredItem<Item> STELLAR_NEXUS_CONTROLLER = animatedBlockItem("stellar_nexus_controller", MultiblockBlocks.STELLAR_NEXUS_CONTROLLER, ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE);

     public static final DeferredItem<Item> ME_MASSIVE_OUTPUT_HATCH = animatedBlockItem("me_massive_output_hatch", MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.WHITE);

     public static final DeferredItem<Item> ME_MASSIVE_FLUID_HATCH = animatedBlockItem("me_massive_fluid_hatch", MultiblockBlocks.ME_MASSIVE_FLUID_HATCH, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE);

     public static final DeferredItem<Item> ME_MASSIVE_INPUT_HATCH = animatedBlockItem("me_massive_input_hatch", MultiblockBlocks.ME_MASSIVE_INPUT_HATCH, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.WHITE);

     public static final DeferredItem<Item> AE_ENERGY_INPUT_HATCH = animatedBlockItem("ae_energy_input_hatch", MultiblockBlocks.AE_ENERGY_INPUT_HATCH, ChatFormatting.YELLOW, ChatFormatting.GOLD, ChatFormatting.WHITE);

     public static final DeferredItem<Item> UFO_ENERGY_CELL = ITEMS.register("energy_cell",
             () -> new EnergyCellBlockItem(com.yongaishide.chaosworld.block.ModBlocks.UFO_ENERGY_CELL.get(), new Item.Properties()));

      public static final DeferredItem<Item> QUANTUM_ENERGY_CELL = ITEMS.register("quantum_energy_cell",
             () -> new BlockItem(com.yongaishide.chaosworld.block.ModBlocks.QUANTUM_ENERGY_CELL.get(), new Item.Properties()));



     public static final DeferredItem<Item> STELLAR_FIELD_GENERATOR_T1 = animatedBlockItem("stellar_field_generator_t1", MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1, ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.YELLOW);

     public static final DeferredItem<Item> STELLAR_FIELD_GENERATOR_T2 = animatedBlockItem("stellar_field_generator_t2", MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2, ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.WHITE);

     public static final DeferredItem<Item> STELLAR_FIELD_GENERATOR_T3 = animatedBlockItem("stellar_field_generator_t3", MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE, ChatFormatting.GOLD, ChatFormatting.YELLOW);

    public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_BUCKET = bucketItem("neutron_star_fragment_bucket", ModFluids.SOURCE_NEUTRON_STAR_FRAGMENT_FLUID);

     public static final DeferredItem<Item> PULSAR_FRAGMENT_BUCKET = bucketItem("pulsar_fragment_bucket", ModFluids.SOURCE_PULSAR_FRAGMENT_FLUID);

     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_BUCKET = bucketItem("white_dwarf_fragment_bucket", ModFluids.SOURCE_WHITE_DWARF_FRAGMENT_FLUID);

     public static final DeferredItem<Item> LIQUID_STARLIGHT_BUCKET = bucketItem("liquid_starlight_bucket", ModFluids.SOURCE_LIQUID_STARLIGHT_FLUID);

     public static final DeferredItem<Item> PRIMORDIAL_MATTER_BUCKET = bucketItem("primordial_matter_bucket", ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID);

     public static final DeferredItem<Item> RAW_STAR_MATTER_PLASMA_BUCKET = bucketItem("raw_star_matter_plasma_bucket", ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID);

     public static final DeferredItem<Item> TRANSCENDING_MATTER_BUCKET = bucketItem("transcending_matter_bucket", ModFluids.SOURCE_TRANSCENDING_MATTER_FLUID);

     public static final DeferredItem<Item> UU_MATTER_BUCKET = bucketItem("uu_matter_bucket", ModFluids.SOURCE_UU_MATTER_FLUID);

     public static final DeferredItem<Item> UU_AMPLIFIER_BUCKET = bucketItem("uu_amplifier_bucket", ModFluids.SOURCE_UU_AMPLIFIER_FLUID);
    public static final DeferredItem<Item> GELID_CRYOTHEUM_BUCKET = bucketItem("gelid_cryotheum_bucket", ModFluids.SOURCE_GELID_CRYOTHEUM);

    public static final DeferredItem<Item> STABLE_COOLANT_BUCKET = bucketItem("stable_coolant_bucket", ModFluids.SOURCE_STABLE_COOLANT);

    public static final DeferredItem<Item> TEMPORAL_FLUID_BUCKET = bucketItem("temporal_fluid_bucket", ModFluids.SOURCE_TEMPORAL_FLUID);

    public static final DeferredItem<Item> SPATIAL_FLUID_BUCKET = bucketItem("spatial_fluid_bucket", ModFluids.SOURCE_SPATIAL_FLUID);


    public static final DeferredItem<Item> QUANTUM_ANOMALY = ITEMS.register("quantum_anomaly",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .fireResistant()));

    public static final DeferredItem<Item> NUCLEAR_STAR = ITEMS.register("nuclear_star",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> SCAR = ITEMS.register("scar",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final DeferredItem<Item> SCRAP = ITEMS.register("scrap",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final DeferredItem<Item> SCRAP_BOX = ITEMS.register("scrap_box",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> STRUCTURE_SCANNER = ITEMS.register("structure_scanner",
            () -> new StructureScannerItem(new Item.Properties()
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> APOCALYPSE_TYPE_A_SPAWN_EGG = ITEMS.register("apocalypse_type_a_spawn_egg",
            () -> new SpawnEggItem(ModEntities.APOCALYPSE_TYPE_A.get(), 0x1a1025, 0x8be9ff, new Item.Properties()));

    // ---------- Esferas / componentes avan莽ados ----------
    public static final DeferredItem<Item> NEUTRONIUM_SPHERE = ITEMS.register("neutronium_sphere",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> ENRICHED_NEUTRONIUM_SPHERE = ITEMS.register("enriched_neutronium_sphere",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> CHARGED_ENRICHED_NEUTRONIUM_SPHERE = ITEMS.register("charged_enriched_neutronium_sphere",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    // ---------- Stages de matter ----------
    public static final DeferredItem<Item> PROTO_MATTER = ITEMS.register("proto_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> CORPOREAL_MATTER = ITEMS.register("corporeal_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> WHITE_DWARF_MATTER = ITEMS.register("white_dwarf_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> NEUTRON_STAR_MATTER = ITEMS.register("neutron_star_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> PULSAR_MATTER = ITEMS.register("pulsar_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> DARK_MATTER = ITEMS.register("dark_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    // ---------- Estrutural / utilit谩rio ----------
    public static final DeferredItem<Item> OBSIDIAN_MATRIX = ITEMS.register("obsidian_matrix",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> UU_MATTER_CRYSTAL = ITEMS.register("uu_matter_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> DUST_CRYOTHEUM = ITEMS.register("dust_cryotheum",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> DUST_BLIZZ = ITEMS.register("dust_blizz",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> UNSTABLE_WHITE_HOLE_MATTER = ITEMS.register("unstable_white_hole_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));
    public static final DeferredItem<Item> AETHER_CONTAINMENT_CAPSULE = ITEMS.register("aether_containment_capsule",
            () -> new AetherContainmentCapsuleItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> SAFE_CONTAINMENT_MATTER = ITEMS.register("safe_containment_matter",
            () -> new SafeContainmentMatterItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    private static DeferredItem<Item> animatedItem(String name, ChatFormatting... colors) {
        return ITEMS.register(name, () -> new AnimatedNameItem(new Item.Properties(), colors));
    }

    private static DeferredItem<Item> animatedBlockItem(String name, Supplier<? extends Block> block, ChatFormatting... colors) {
        return ITEMS.register(name, () -> new AnimatedNameBlockItem(block.get(), new Item.Properties(), colors));
    }

    private static DeferredItem<Item> bucketItem(String name, Supplier<? extends Fluid> fluid) {
        return ITEMS.register(name, () -> new BucketItem(fluid.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    }

    private static DeferredItem<Item> catalystItem(String name, String type, int tier) {
        return ITEMS.register(name, () -> new BaseCatalystItem(new Item.Properties(), type, tier));
    }

    // --- 3) MATTERFLOW CATALYST (Efici锚ncia Energ茅tica) [cite: 67] ---

    public static final DeferredItem<Item> MATTERFLOW_CATALYST_T1 = catalystItem("matterflow_catalyst_t1", "matterflow", 1);
    public static final DeferredItem<Item> MATTERFLOW_CATALYST_T2 = catalystItem("matterflow_catalyst_t2", "matterflow", 2);
    public static final DeferredItem<Item> MATTERFLOW_CATALYST_T3 = catalystItem("matterflow_catalyst_t3", "matterflow", 3);

    public static final DeferredItem<Item> CHRONO_CATALYST_T1 = catalystItem("chrono_catalyst_t1", "chrono", 1);
    public static final DeferredItem<Item> CHRONO_CATALYST_T2 = catalystItem("chrono_catalyst_t2", "chrono", 2);
    public static final DeferredItem<Item> CHRONO_CATALYST_T3 = catalystItem("chrono_catalyst_t3", "chrono", 3);

    public static final DeferredItem<Item> OVERFLUX_CATALYST_T1 = catalystItem("overflux_catalyst_t1", "overflux", 1);
    public static final DeferredItem<Item> OVERFLUX_CATALYST_T2 = catalystItem("overflux_catalyst_t2", "overflux", 2);
    public static final DeferredItem<Item> OVERFLUX_CATALYST_T3 = catalystItem("overflux_catalyst_t3", "overflux", 3);

    public static final DeferredItem<Item> QUANTUM_CATALYST_T1 = catalystItem("quantum_catalyst_t1", "quantum", 1);
    public static final DeferredItem<Item> QUANTUM_CATALYST_T2 = catalystItem("quantum_catalyst_t2", "quantum", 2);
    public static final DeferredItem<Item> QUANTUM_CATALYST_T3 = catalystItem("quantum_catalyst_t3", "quantum", 3);

    // E o criativo:
    public static final DeferredItem<Item> DIMENSIONAL_CATALYST = ITEMS.register("dimensional_catalyst",
            () -> new DimensionalCatalystItem(new Item.Properties()));

    // Thermal Resistor suit has been moved to ModArmor.java

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        ModTools.register(eventBus);
        ModArmor.register(eventBus);
    }

    private static <T extends IPart> DeferredItem<Item> registerPartItem(
            String id,
            Class<T> partClass,
            java.util.function.Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return ITEMS.register(id, () -> new PartItem<>(new Item.Properties(), partClass, factory));
    }
}

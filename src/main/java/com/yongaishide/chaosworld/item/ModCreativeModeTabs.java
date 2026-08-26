package com.yongaishide.chaosworld.item;

import com.yongaishide.chaosworld.ChaosWorld;
import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.metal.ModMetals;
import com.yongaishide.chaosworld.metal.ModTech;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "chaosworld_core");

    public static final Supplier<CreativeModeTab> CHAOS_WORLD_TAB = CREATIVE_MODE_TAB.register("chaos_world_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModTools.UFO_SWORD.get()))
                    .title(Component.translatable("creativetab.chaosworld.chaos_world"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // 工具武器
                        output.accept(ModTools.REALITY_RIPPER.get());
                        output.accept(ModTools.UFO_STAFF);
                        output.accept(ModTools.UFO_SWORD.get());
                        output.accept(ModTools.UFO_GREATSWORD.get());
                        output.accept(ModTools.UFO_PICKAXE.get());
                        output.accept(ModTools.UFO_AXE.get());
                        output.accept(ModTools.UFO_SHOVEL.get());
                        output.accept(ModTools.UFO_HOE.get());
                        output.accept(ModTools.UFO_HAMMER.get());
                        output.accept(ModTools.UFO_FISHING_ROD.get());
                        output.accept(ModTools.UFO_BOW.get());
                        output.accept(ModItems.STRUCTURE_SCANNER.get());

                        // 护甲
                        output.accept(ModArmor.UFO_HELMET.get());
                        output.accept(ModArmor.UFO_CHESTPLATE.get());
                        output.accept(ModArmor.UFO_LEGGINGS.get());
                        output.accept(ModArmor.UFO_BOOTS.get());
                        output.accept(ModArmor.ASTRAL_NEXUS_HELMET.get());
                        output.accept(ModArmor.ASTRAL_NEXUS_CHESTPLATE.get());
                        output.accept(ModArmor.ASTRAL_NEXUS_LEGGINGS.get());
                        output.accept(ModArmor.ASTRAL_NEXUS_BOOTS.get());
                        output.accept(ModArmor.THERMAL_RESISTOR_MASK.get());
                        output.accept(ModArmor.THERMAL_RESISTOR_CHEST.get());
                        output.accept(ModArmor.THERMAL_RESISTOR_PANTS.get());
                        output.accept(ModArmor.THERMAL_RESISTOR_BOOTS.get());

                        // 方块
                        output.accept(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK.get());
                        output.accept(ModBlocks.GRAVITON_PLATED_CASING.get());
                        output.accept(ModBlocks.QUANTUM_LATTICE_FRAME.get());
                        output.accept(ModBlocks.NEUTRON_STAR_FRAGMENT_BLOCK.get());
                        output.accept(ModBlocks.PULSAR_FRAGMENT_BLOCK.get());
                        output.accept(ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get());
                        output.accept(ModBlocks.UFO_ENERGY_CELL.get());
                        output.accept(createQuantumEnergyCellVariant(false));
                        output.accept(createQuantumEnergyCellVariant(true));

                        // --- STELLAR NEXUS ---
                        output.accept(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get());
                        output.accept(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get());
                        output.accept(ModItems.QUANTUM_PATTERN_PROVIDER_PART.get());
                        output.accept(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get());
                        output.accept(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get());
                        output.accept(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get());
                        output.accept(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get());
                        output.accept(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get());
                        output.accept(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get());
                        output.accept(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get());

                        // --- ENTROPY CASINGS (standalone blocks) ---
                        output.accept(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get());
                        output.accept(MultiblockBlocks.QUANTUM_ENTROPY_CASING.get());

                        // 材料
                        // 宝石
                        output.accept(ChaosWorld.STARLIGHT_GEMSTONE.get());
                        output.accept(ChaosWorld.HUIXING_GEMSTONE.get());
                        output.accept(ChaosWorld.NATURE_GEMSTONE.get());
                        output.accept(ChaosWorld.SPARKLING_GEMSTONES.get());
                        output.accept(ChaosWorld.STARS_GEMSTONE.get());
                        output.accept(ChaosWorld.SUN_GEMSTONE.get());
                        output.accept(ChaosWorld.AQUAMARINE.get());
                        output.accept(ChaosWorld.MANA_CRYSTAL1.get());
                        output.accept(ChaosWorld.MANA_CRYSTAL2.get());
                        output.accept(ChaosWorld.MANA_CRYSTAL3.get());

                        // 水晶与合金
                        output.accept(ChaosWorld.CRYSTAL.get());
                        output.accept(ChaosWorld.ZELUOSISHUIJING.get());
                        output.accept(ChaosWorld.LAIZEERSHUIJING.get());
                        output.accept(ChaosWorld.KYRONITE.get());
                        output.accept(ChaosWorld.REDHEJIN.get());
                        output.accept(ChaosWorld.MAGIC_EMERALD_CRYSTAL.get());
                        output.accept(ChaosWorld.CHARGING_MAGIC_EMERALD_CRYSTAL.get());
                        output.accept(ChaosWorld.FORGEPLATE.get());
                        output.accept(ChaosWorld.FURNACE1.get());
                        output.accept(ChaosWorld.FURNACE2.get());
                        output.accept(ChaosWorld.FURNACE3.get());
                        output.accept(ChaosWorld.PARADOX_MATTER_SPHERE.get());

                        // 白矮星碎片
                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_INGOT.get());
                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_DUST.get());
                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_NUGGET.get());
                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_ROD.get());
                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_BUCKET.get());

                        // 中子星碎片
                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_INGOT.get());
                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_DUST.get());
                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_NUGGET.get());
                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_ROD.get());         
                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_BUCKET.get());      
                        output.accept(ModItems.NEUTRONITE_INGOT.get());

                        // 脉冲星碎片
                        output.accept(ModItems.PULSAR_FRAGMENT_INGOT.get());
                        output.accept(ModItems.PULSAR_FRAGMENT_DUST.get());
                        output.accept(ModItems.PULSAR_FRAGMENT_NUGGET.get());
                        output.accept(ModItems.PULSAR_FRAGMENT_BUCKET.get());

                        // 热阻镀层
                        output.accept(ModArmor.THERMAL_RESISTOR_PLATING.get());

                        // 封装与物质
                        output.accept(ModItems.AETHER_CONTAINMENT_CAPSULE.get());
                        output.accept(ModItems.SAFE_CONTAINMENT_MATTER.get());
                        output.accept(ModItems.PROTO_MATTER.get());
                        output.accept(ModItems.CORPOREAL_MATTER.get());
                        output.accept(ModItems.WHITE_DWARF_MATTER.get());
                        output.accept(ModItems.NEUTRON_STAR_MATTER.get());
                        output.accept(ModItems.PULSAR_MATTER.get());
                        output.accept(ModItems.UU_MATTER_CRYSTAL.get());
                        output.accept(ModItems.DARK_MATTER.get());
                        output.accept(ModItems.UNSTABLE_WHITE_HOLE_MATTER.get());
                        output.accept(ModItems.QUANTUM_ANOMALY.get());
                        output.accept(ModItems.NUCLEAR_STAR.get());
                        output.accept(ModItems.NEUTRONIUM_SPHERE.get());
                        output.accept(ModItems.ENRICHED_NEUTRONIUM_SPHERE.get());
                        output.accept(ModItems.CHARGED_ENRICHED_NEUTRONIUM_SPHERE.get());
                        output.accept(ModItems.SCAR.get());
                        output.accept(ModItems.SCRAP.get());
                        output.accept(ModItems.SCRAP_BOX.get());

                        // 尘埃与基质
                        output.accept(ModItems.DUST_CRYOTHEUM.get());
                        output.accept(ModItems.DUST_BLIZZ.get());
                        output.accept(ModItems.OBSIDIAN_MATRIX.get());

                        // 金属与科技
                        for (var entry : ModMetals.METAL_ITEMS.values()) {
                            output.accept(entry.get());
                        }
                        for (var entry : ModMetals.METAL_BLOCK_ITEMS.values()) {
                            output.accept(entry.get());
                        }
                        for (var entry : ModTech.TECH_ITEMS.values()) {
                            output.accept(entry.get());
                        }
                        for (var entry : ModTech.TECH_BLOCK_ITEMS.values()) {
                            output.accept(entry.get());
                        }

                        // 元件
                        // 核心
                        output.accept(ChaosWorld.CRYPTID_CORE.get());
                        output.accept(ChaosWorld.CRYSTAL_CORE_1.get());
                        output.accept(ChaosWorld.CRYSTAL_CORE_2.get());
                        output.accept(ChaosWorld.CRYSTAL_CORE_3.get());
                        output.accept(ChaosWorld.CRYSTAL_CORE_4.get());
                        output.accept(ChaosWorld.CRYSTAL_CORE_5.get());
                        output.accept(ChaosWorld.CRYSTAL_CORE_6.get());
                        output.accept(ChaosWorld.CRYSTAL_CORE_7.get());
                        output.accept(ChaosWorld.CRYSTAL_CORE_8.get());
                        output.accept(ChaosWorld.IRON_GOLEM_CORE.get());
                        output.accept(ChaosWorld.COLORFUL_CORE.get());
                        output.accept(ChaosWorld.COLORFUL_ENERGY_CORE.get());

                        // 组件矩阵
                        output.accept(ModItems.WHITE_DWARF_STORAGE_COMPONENT_1G.get());
                        output.accept(ModItems.WHITE_DWARF_STORAGE_COMPONENT_4G.get());
                        output.accept(ModItems.WHITE_DWARF_STORAGE_COMPONENT_16G.get());
                        output.accept(ModItems.WHITE_DWARF_STORAGE_COMPONENT_64G.get());
                        output.accept(ModItems.WHITE_DWARF_STORAGE_COMPONENT_256G.get());
                        output.accept(ModItems.NEUTRON_STAR_STORAGE_COMPONENT_1T.get());
                        output.accept(ModItems.NEUTRON_STAR_STORAGE_COMPONENT_4T.get());
                        output.accept(ModItems.NEUTRON_STAR_STORAGE_COMPONENT_16T.get());
                        output.accept(ModItems.NEUTRON_STAR_STORAGE_COMPONENT_64T.get());
                        output.accept(ModItems.NEUTRON_STAR_STORAGE_COMPONENT_256T.get());
                        output.accept(ModItems.PULSAR_STORAGE_COMPONENT_1P.get());
                        output.accept(ModItems.PULSAR_STORAGE_COMPONENT_4P.get());
                        output.accept(ModItems.PULSAR_STORAGE_COMPONENT_16P.get());
                        output.accept(ModItems.PULSAR_STORAGE_COMPONENT_64P.get());
                        output.accept(ModItems.PULSAR_STORAGE_COMPONENT_256P.get());

                        // 次元处理器
                        output.accept(ModItems.DIMENSIONAL_PROCESSOR_PRESS.get());
                        output.accept(ModItems.DIMENSIONAL_PROCESSOR.get());
                        output.accept(ModItems.PRINTED_DIMENSIONAL_PROCESSOR.get());

                        // 电路与芯片
                        output.accept(ChaosWorld.CIRCUIT_PROCESSOR.get());
                        output.accept(ChaosWorld.CRYSTAL_CHIP.get());
                        output.accept(ChaosWorld.REDKONGZHIDIANLU.get());
                        output.accept(ChaosWorld.TERMINAL_PASS.get());
                        output.accept(ChaosWorld.WORKSTATION.get());
                        output.accept(ChaosWorld.MICROPROCESSOR.get());
                        output.accept(ChaosWorld.INTEGRATED.get());
                        output.accept(ChaosWorld.PROCESSOR.get());
                        output.accept(ChaosWorld.BASIC_INTEGRATED.get());
                        output.accept(ChaosWorld.ADVANCED_INTEGRATED.get());
                        output.accept(ChaosWorld.CENTRAL_PROCESSING.get());

                        // 湿件
                        output.accept(ChaosWorld.WETWARE_ASSEMBLY.get());
                        output.accept(ChaosWorld.WETWARE_COMPUTER.get());
                        output.accept(ChaosWorld.WETWARE_MAINFRAME.get());
                        output.accept(ChaosWorld.WETWARE_PROCESSOR.get());

                        // 水晶计算机
                        output.accept(ChaosWorld.CRYSTAL_ASSEMBLY.get());
                        output.accept(ChaosWorld.CRYSTAL_COMPUTER.get());
                        output.accept(ChaosWorld.CRYSTAL_MAINFRAME.get());
                        output.accept(ChaosWorld.CRYSTAL_PROCESSOR.get());

                        // 量子计算机
                        output.accept(ChaosWorld.QUANTUM_ASSEMBLY.get());
                        output.accept(ChaosWorld.QUANTUM_COMPUTER.get());
                        output.accept(ChaosWorld.QUANTUM_MAINFRAME.get());
                        output.accept(ChaosWorld.QUANTUM_PROCESSOR.get());

                        // 纳米计算机
                        output.accept(ChaosWorld.NANO_ASSEMBLY.get());
                        output.accept(ChaosWorld.NANO_COMPUTER.get());
                        output.accept(ChaosWorld.NANO_MAINFRAME.get());
                        output.accept(ChaosWorld.NANO_PROCESSOR.get());

                        // 元件外壳
                        output.accept(ModCellItems.WHITE_DWARF_CELL_HOUSING.get());
                        output.accept(ModCellItems.NEUTRON_STAR_CELL_HOUSING.get());
                        output.accept(ModCellItems.PULSAR_CELL_HOUSING.get());

                        // 白矮星元件
                        output.accept(ModCellItems.WHITE_DWARF_CELL_1G.get());
                        output.accept(ModCellItems.WHITE_DWARF_CELL_4G.get());
                        output.accept(ModCellItems.WHITE_DWARF_CELL_16G.get());
                        output.accept(ModCellItems.WHITE_DWARF_CELL_64G.get());
                        output.accept(ModCellItems.WHITE_DWARF_CELL_256G.get());

                        // 中子星元件
                        output.accept(ModCellItems.NEUTRON_STAR_CELL_1T.get());
                        output.accept(ModCellItems.NEUTRON_STAR_CELL_4T.get());
                        output.accept(ModCellItems.NEUTRON_STAR_CELL_16T.get());
                        output.accept(ModCellItems.NEUTRON_STAR_CELL_64T.get());
                        output.accept(ModCellItems.NEUTRON_STAR_CELL_256T.get());

                        // 脉冲星元件
                        output.accept(ModCellItems.PULSAR_CELL_1P.get());
                        output.accept(ModCellItems.PULSAR_CELL_4P.get());
                        output.accept(ModCellItems.PULSAR_CELL_16P.get());
                        output.accept(ModCellItems.PULSAR_CELL_64P.get());
                        output.accept(ModCellItems.PULSAR_CELL_256P.get());

                        // 量子万用元件
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_16G.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_64G.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_256G.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_1T.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_4T.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_16T.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_64T.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_256T.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_1P.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_4P.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_16P.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_64P.get());
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_256P.get());

                        // 催化剂
                        output.accept(ModItems.CHRONO_CATALYST_T1.get());
                        output.accept(ModItems.CHRONO_CATALYST_T2.get());
                        output.accept(ModItems.CHRONO_CATALYST_T3.get());
                        output.accept(ModItems.MATTERFLOW_CATALYST_T1.get());
                        output.accept(ModItems.MATTERFLOW_CATALYST_T2.get());
                        output.accept(ModItems.MATTERFLOW_CATALYST_T3.get());
                        output.accept(ModItems.OVERFLUX_CATALYST_T1.get());
                        output.accept(ModItems.OVERFLUX_CATALYST_T2.get());
                        output.accept(ModItems.OVERFLUX_CATALYST_T3.get());
                        output.accept(ModItems.QUANTUM_CATALYST_T1.get());
                        output.accept(ModItems.QUANTUM_CATALYST_T2.get());
                        output.accept(ModItems.QUANTUM_CATALYST_T3.get());
                        output.accept(ModItems.DIMENSIONAL_CATALYST.get());
                        output.accept(ChaosWorld.DRAGON_CATALYST.get());
                        output.accept(ChaosWorld.TWILIGHT_CATALYST.get());
                        output.accept(ChaosWorld.INFINITE_RUNES.get());
                        output.accept(ChaosWorld.RUNES_1.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

    private static ItemStack createQuantumEnergyCellVariant(boolean charged) {
        ItemStack stack = new ItemStack(ModBlocks.QUANTUM_ENERGY_CELL.get());
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable(charged
                ? "item.chaosworld_core.quantum_energy_cell.charged"
                : "item.chaosworld_core.quantum_energy_cell.discharged"));
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean("ufoQuantumEnergyCellChargedPreview", charged);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }
}

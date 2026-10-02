package com.yongaishide.chaosworld.item;

import com.yongaishide.chaosworld.ChaosWorld;
import com.yongaishide.chaosworld.metal.ModMetals;
import com.yongaishide.chaosworld.metal.ModTech;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, "chaosworld_core");

    public static final Supplier<CreativeModeTab> CHAOS_WORLD_TAB = CREATIVE_MODE_TAB.register("chaos_world_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ChaosWorld.STARLIGHT_GEMSTONE.get()))
                    .title(Component.translatable("creativetab.chaosworld.chaos_world"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // --- MEKANISM 机器 ---
                        output.accept(com.yongaishide.chaosworld.mekanism.MekanismMachines.MECHANICAL_ASSEMBLER.asItem());
                        var factoryBlocks = com.yongaishide.chaosworld.mekanism.AssemblingFactoryMachines.FACTORY_BLOCKS;
                        for (int i = 0; i < com.yongaishide.chaosworld.mekanism.AssemblingFactoryMachines.getCount(); i++) {
                            output.accept(factoryBlocks[i].asItem());
                        }
                        output.accept(com.yongaishide.chaosworld.mekanism.DragonSoulForgeMachines.DRAGON_SOUL_FORGE.asItem());
                        var forgeFactoryBlocks = com.yongaishide.chaosworld.mekanism.DragonSoulForgeMachines.FACTORY_BLOCKS;
                        for (int i = 0; i < com.yongaishide.chaosworld.mekanism.DragonSoulForgeMachines.getCount(); i++) {
                            output.accept(forgeFactoryBlocks[i].asItem());
                        }
                        output.accept(com.yongaishide.chaosworld.mekanism.vein.VeinDrillMachines.VEIN_DRILL.asItem());
                        output.accept(com.yongaishide.chaosworld.mekanism.vein.VeinDrillMachines.VEIN_CORE.get());

                        // --- 量子万用元件 ---
                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_16G.get());                        output.accept(ModCellItems.QUANTUM_OMNI_CELL_64G.get());
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

                        // --- 存储组件 / 外壳 / 中子星与脉冲星元件 ---
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_STORAGE_COMPONENT_1G.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_STORAGE_COMPONENT_4G.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_STORAGE_COMPONENT_16G.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_STORAGE_COMPONENT_64G.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_STORAGE_COMPONENT_256G.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_STORAGE_COMPONENT_1T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_STORAGE_COMPONENT_4T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_STORAGE_COMPONENT_16T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_STORAGE_COMPONENT_64T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_STORAGE_COMPONENT_256T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_STORAGE_COMPONENT_1P.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_STORAGE_COMPONENT_4P.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_STORAGE_COMPONENT_16P.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_STORAGE_COMPONENT_64P.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_STORAGE_COMPONENT_256P.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.WHITE_DWARF_CELL_HOUSING.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_HOUSING.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_HOUSING.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_1T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_4T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_16T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_64T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.NEUTRON_STAR_CELL_256T.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_1P.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_4P.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_16P.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_64P.get());
                        output.accept(com.yongaishide.chaosworld.patch.contents.LegacyTieredContent.PULSAR_CELL_256P.get());

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
                        output.accept(ChaosWorld.STELLAR_ALLOY_CORE.get());
                        output.accept(ChaosWorld.FORGEPLATE.get());
                        output.accept(ChaosWorld.FURNACE1.get());
                        output.accept(ChaosWorld.FURNACE2.get());
                        output.accept(ChaosWorld.FURNACE3.get());
                        output.accept(ChaosWorld.PARADOX_MATTER_SPHERE.get());

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

                        // 微型计算机
                        output.accept(ChaosWorld.MICRO_MAINFRAME.get());
                        output.accept(ChaosWorld.MICRO_ASSEMBLY.get());

                        // 催化剂
                        output.accept(ChaosWorld.DRAGON_CATALYST.get());
                        output.accept(ChaosWorld.TWILIGHT_CATALYST.get());
                        output.accept(ChaosWorld.INFINITE_RUNES.get());
                        output.accept(ChaosWorld.RUNES_1.get());

                        // FTB Quests 任务检测卡
                        if (net.neoforged.fml.ModList.get().isLoaded("ftbquests")) {
                            output.accept(com.yongaishide.chaosworld.compat.ftbquests.FTBQuestsIntegration.QUEST_DETECTION_CARD.get());
                        }
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}

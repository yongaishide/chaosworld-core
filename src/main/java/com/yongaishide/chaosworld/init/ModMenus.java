package com.yongaishide.chaosworld.init;

import com.yongaishide.chaosworld.block.entity.DimensionalMatterAssemblerBlockEntity;
import com.yongaishide.chaosworld.menu.DimensionalMatterAssemblerMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, "chaosworld_core");

    public static final MenuType<DimensionalMatterAssemblerMenu> DIMENSIONAL_MATTER_ASSEMBLER =
            MenuTypeBuilder.create(DimensionalMatterAssemblerMenu::new, DimensionalMatterAssemblerBlockEntity.class)
                    .build(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "dimensional_matter_assembler"));

    public static final Supplier<MenuType<com.yongaishide.chaosworld.screen.StellarNexusControllerMenu>> STELLAR_NEXUS_CONTROLLER_MENU =
            MENUS.register("stellar_nexus_controller_menu",
                    () -> IMenuTypeExtension.create(com.yongaishide.chaosworld.screen.StellarNexusControllerMenu::new));

    public static final Supplier<MenuType<com.yongaishide.chaosworld.screen.QmfControllerMenu>> QMF_CONTROLLER_MENU =
            MENUS.register("qmf_controller_menu",
                    () -> IMenuTypeExtension.create(com.yongaishide.chaosworld.screen.QmfControllerMenu::new));

    public static final Supplier<MenuType<com.yongaishide.chaosworld.screen.QuantumSlicerControllerMenu>> QUANTUM_SLICER_CONTROLLER_MENU =
            MENUS.register("quantum_slicer_controller_menu",
                    () -> IMenuTypeExtension.create(com.yongaishide.chaosworld.screen.QuantumSlicerControllerMenu::new));

    public static final Supplier<MenuType<com.yongaishide.chaosworld.screen.QuantumProcessorAssemblerControllerMenu>> QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_MENU =
            MENUS.register("quantum_processing_factory_controller_menu",
                    () -> IMenuTypeExtension.create(com.yongaishide.chaosworld.screen.QuantumProcessorAssemblerControllerMenu::new));

    public static final Supplier<MenuType<com.yongaishide.chaosworld.screen.QuantumCryoforgeControllerMenu>> QUANTUM_CRYOFORGE_CONTROLLER_MENU =
            MENUS.register("quantum_cryoforge_controller_menu",
                    () -> IMenuTypeExtension.create(com.yongaishide.chaosworld.screen.QuantumCryoforgeControllerMenu::new));

    public static final MenuType<com.yongaishide.chaosworld.screen.QuantumPatternHatchMenu> QUANTUM_PATTERN_HATCH_MENU =
            MenuTypeBuilder
                    .create((id, inv, host) -> new com.yongaishide.chaosworld.screen.QuantumPatternHatchMenu(id, inv, host),
                            appeng.helpers.patternprovider.PatternProviderLogicHost.class)
                    .build(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "quantum_pattern_hatch_menu"));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

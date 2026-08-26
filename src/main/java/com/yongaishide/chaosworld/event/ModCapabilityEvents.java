package com.yongaishide.chaosworld.event;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.init.ModBlockEntities;
import com.yongaishide.chaosworld.item.ModItems;
import com.yongaishide.chaosworld.item.ModArmor;
import com.yongaishide.chaosworld.item.ModTools;
import com.yongaishide.chaosworld.item.custom.AetherContainmentCapsuleItem;
import com.yongaishide.chaosworld.util.ConfigType;
import com.yongaishide.chaosworld.util.IOMode;
import com.yongaishide.chaosworld.util.UfoPersistentEnergyStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import appeng.blockentity.powersink.AEBasePoweredBlockEntity;

@EventBusSubscriber(modid = "chaosworld_core")
public class ModCapabilityEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Registra as capabilities dos Itens (suas ferramentas e armaduras)
        registerItemCapabilities(event);

        // Registra as capabilities dos Blocos (suas m璋﹒uinas)
        event.registerBlockEntity(
                appeng.api.AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.DIMENSIONAL_MATTER_ASSEMBLER_BE.get(),
                (be, context) -> (appeng.api.networking.IInWorldGridNodeHost) be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.QUANTUM_PATTERN_HATCH_BE.get(),
                (be, context) -> (appeng.api.networking.IInWorldGridNodeHost) be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.ME_MASSIVE_OUTPUT_HATCH_BE.get(),
                (be, context) -> (appeng.api.networking.IInWorldGridNodeHost) be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.CRAFTING_MACHINE,
                ModBlockEntities.QMF_CONTROLLER.get(),
                (be, context) -> be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.CRAFTING_MACHINE,
                ModBlockEntities.QUANTUM_SLICER_CONTROLLER_BE.get(),
                (be, context) -> be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.CRAFTING_MACHINE,
                ModBlockEntities.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_BE.get(),
                (be, context) -> be
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.DIMENSIONAL_MATTER_ASSEMBLER_BE.get(),
                appeng.blockentity.AEBaseInvBlockEntity::getExposedItemHandler
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.GENERIC_INTERNAL_INV,
                ModBlockEntities.DIMENSIONAL_MATTER_ASSEMBLER_BE.get(),
                (be, context) -> be.getTank()
        );
        // Expose FE/Forge Energy capability so external cables (Mekanism, etc.) can push energy in
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.DIMENSIONAL_MATTER_ASSEMBLER_BE.get(),
                AEBasePoweredBlockEntity::getEnergyStorage
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.UFO_ENERGY_CELL_BE.get(),
                (be, context) -> be.getExposedEnergy()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.QUANTUM_ENERGY_CELL_BE.get(),
                (be, context) -> be.getExposedEnergy()
        );

        if (ModList.get().isLoaded("mekanism")) {
            com.yongaishide.chaosworld.compat.mekanism.MekanismChemicalCompat.registerCapabilities(event, ModBlockEntities.ME_MASSIVE_OUTPUT_HATCH_BE.get());
        }
    }

    private static void registerItemCapabilities(RegisterCapabilitiesEvent event) {

        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, context) -> new AetherContainmentCapsuleItem.HazardousFluidHandler(stack, AetherContainmentCapsuleItem.CAPACITY),
                ModItems.AETHER_CONTAINMENT_CAPSULE.get()
        );
        // --- Ferramentas e Armas J璋?Existentes ---
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_SWORD.get()
        );
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_PICKAXE.get()
        );
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_AXE.get()
        );
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_SHOVEL.get()
        );
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_HOE.get()
        );
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_STAFF.get()
        );

        // --- ADICIONE ESTES BLOCOS ABAIXO ---

        // Hammer
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_HAMMER.get()
        );

        // Greatsword
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_GREATSWORD.get()
        );

        // Fishing Rod
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_FISHING_ROD.get()
        );

        // Bow
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModTools.UFO_BOW.get()
        );

        // --- Armaduras ---
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModArmor.UFO_HELMET.get()
        );
        // Peitoral com mais capacidade (exemplo mantido do seu c璐竏igo original)
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModArmor.UFO_CHESTPLATE.get()
        );
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModArmor.UFO_LEGGINGS.get()
        );
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1_000_000_000, 0),
                ModArmor.UFO_BOOTS.get()
        );
        if (ModList.get().isLoaded("mekanism")) {
            event.registerItem(mekanism.common.capabilities.Capabilities.RADIATION_SHIELDING, (stack, context) -> () -> 1.0,
                    ModArmor.ASTRAL_NEXUS_HELMET.get(),
                    ModArmor.ASTRAL_NEXUS_CHESTPLATE.get(),
                    ModArmor.ASTRAL_NEXUS_LEGGINGS.get(),
                    ModArmor.ASTRAL_NEXUS_BOOTS.get()
            );
        }
    }
}


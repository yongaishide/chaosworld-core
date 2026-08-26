package com.yongaishide.chaosworld;

import com.yongaishide.chaosworld.init.ModMenus;

import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.client.renderer.ApocalypseTypeARenderer;
import com.yongaishide.chaosworld.event.ModKeyBindings;
import com.yongaishide.chaosworld.util.ModItemProperties;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import appeng.init.client.InitScreens;
import com.yongaishide.chaosworld.client.gui.DimensionalMatterAssemblerScreen;
import com.yongaishide.chaosworld.screen.QuantumPatternHatchScreen;
import com.yongaishide.chaosworld.screen.QuantumCryoforgeControllerScreen;
import com.yongaishide.chaosworld.screen.QuantumProcessorAssemblerControllerScreen;
import com.yongaishide.chaosworld.screen.QuantumSlicerControllerScreen;
import com.yongaishide.chaosworld.screen.StellarNexusControllerScreen;
import com.yongaishide.chaosworld.block.ModBlocks;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.yongaishide.chaosworld.client.renderer.DimensionalMatterAssemblerRenderer;
import com.yongaishide.chaosworld.client.renderer.StellarNexusRenderer;
import com.yongaishide.chaosworld.init.ModBlockEntities;
import com.yongaishide.chaosworld.init.ModEntities;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import com.yongaishide.chaosworld.client.render.layer.AstralNexusWingsLayer;

public class UfoModClient {

    public UfoModClient(IEventBus eventBus) {
        eventBus.addListener(this::onClientSetup);
        eventBus.addListener(this::onRegisterKeyMappings);
        eventBus.addListener(this::registerScreens);
        eventBus.addListener(this::registerRenderers);
        eventBus.addListener(this::onAddLayers);
        eventBus.addListener(com.yongaishide.chaosworld.client.render.StellarModelRegistry::registerAdditional);
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.CYCLE_TOOL_FORWARD);
        event.register(ModKeyBindings.CYCLE_TOOL_BACKWARD);
        event.register(ModKeyBindings.CYCLE_MODE);
        event.register(ModKeyBindings.TOGGLE_AUTO_SMELT);
    }


    private void registerScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(event, ModMenus.DIMENSIONAL_MATTER_ASSEMBLER, DimensionalMatterAssemblerScreen::new, "/screens/dimensional_matter_assembler.json");
        event.register(ModMenus.STELLAR_NEXUS_CONTROLLER_MENU.get(), StellarNexusControllerScreen::new);
        InitScreens.register(event, ModMenus.QMF_CONTROLLER_MENU.get(), com.yongaishide.chaosworld.screen.QmfControllerScreen::new, "/screens/universal_multiblock_controller.json");
        InitScreens.register(event, ModMenus.QUANTUM_SLICER_CONTROLLER_MENU.get(), QuantumSlicerControllerScreen::new, "/screens/universal_multiblock_controller.json");
        InitScreens.register(event, ModMenus.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_MENU.get(), QuantumProcessorAssemblerControllerScreen::new, "/screens/universal_multiblock_controller.json");
        InitScreens.register(event, ModMenus.QUANTUM_CRYOFORGE_CONTROLLER_MENU.get(), QuantumCryoforgeControllerScreen::new, "/screens/universal_multiblock_controller.json");
        InitScreens.register(event, ModMenus.QUANTUM_PATTERN_HATCH_MENU, QuantumPatternHatchScreen::new, "/screens/quantum_pattern_hatch.json");
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DIMENSIONAL_MATTER_ASSEMBLER_BE.get(), DimensionalMatterAssemblerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.STELLAR_NEXUS_CONTROLLER_BE.get(), StellarNexusRenderer::new);
        event.registerEntityRenderer(ModEntities.APOCALYPSE_TYPE_A.get(), ApocalypseTypeARenderer::new);
    }

    private void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            var renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new AstralNexusWingsLayer(playerRenderer));
            }
        }
    }



    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerEnergyCellFillProperty(ModBlocks.UFO_ENERGY_CELL.get().asItem());
            ModItemProperties.addCustomItemProperties();
        });
    }

    private static void registerEnergyCellFillProperty(Item item) {
        ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath("ae2", "fill_level"), (stack, level, entity, seed) -> {
            if (!(item instanceof appeng.block.networking.EnergyCellBlockItem energyCellItem)) {
                return 0.0F;
            }
            double currentPower = energyCellItem.getAECurrentPower(stack);
            double maxPower = energyCellItem.getAEMaxPower(stack);
            return maxPower <= 0 ? 0.0F : (float) (currentPower / maxPower);
        });
    }
}

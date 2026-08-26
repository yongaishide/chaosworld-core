package com.yongaishide.chaosworld;

import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.event.ModKeyBindings;
import com.yongaishide.chaosworld.init.ModBlockEntities;
import com.yongaishide.chaosworld.init.ModEntities;
import com.yongaishide.chaosworld.init.ModMenus;
import com.yongaishide.chaosworld.init.ModRecipes;
import com.yongaishide.chaosworld.init.ModSounds;
import com.yongaishide.chaosworld.item.BaseItem;
import com.yongaishide.chaosworld.item.ModArmor;
import com.yongaishide.chaosworld.item.ModCellItems;
import com.yongaishide.chaosworld.item.ModCreativeModeTabs;
import com.yongaishide.chaosworld.item.ModItems;
import com.yongaishide.chaosworld.item.UFORegistryHandler;
import com.yongaishide.chaosworld.metal.ModMetals;
import com.yongaishide.chaosworld.metal.ModTech;
import com.yongaishide.chaosworld.network.ModPackets;
import com.yongaishide.chaosworld.network.packet.CycleModeKeyPacket;
import com.yongaishide.chaosworld.network.packet.CycleToolKeyPacket;
import com.yongaishide.chaosworld.network.packet.ToggleAutoSmeltPacket;
import com.yongaishide.chaosworld.util.LazyInits;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod("chaosworld_core")
public class ChaosWorld {
    public static final String MODID = "chaosworld_core";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // Manual items
    public static final DeferredHolder<Item, BaseItem> CRYPTID_CORE = item("cryptid_core");
    public static final DeferredHolder<Item, BaseItem> STARLIGHT_GEMSTONE = item("starlight_gemstone");
    public static final DeferredHolder<Item, BaseItem> HUIXING_GEMSTONE = item("huixing_gemstone");
    public static final DeferredHolder<Item, BaseItem> NATURE_GEMSTONE = item("nature_gemstone");
    public static final DeferredHolder<Item, BaseItem> SPARKLING_GEMSTONES = item("sparkling_gemstones");
    public static final DeferredHolder<Item, BaseItem> STARS_GEMSTONE = item("stars_gemstone");
    public static final DeferredHolder<Item, BaseItem> SUN_GEMSTONE = item("sun_gemstone");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_CORE_1 = item("crystal_core_1");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_CORE_2 = item("crystal_core_2");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_CORE_3 = item("crystal_core_3");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_CORE_4 = item("crystal_core_4");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_CORE_5 = item("crystal_core_5");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_CORE_6 = item("crystal_core_6");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_CORE_7 = item("crystal_core_7");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_CORE_8 = item("crystal_core_8");
    public static final DeferredHolder<Item, BaseItem> PARADOX_MATTER_SPHERE = item("paradox_matter_sphere");
    public static final DeferredHolder<Item, BaseItem> DRAGON_CATALYST = item("dragon_catalyst");
    public static final DeferredHolder<Item, BaseItem> INFINITE_RUNES = item("infinite_runes");
    public static final DeferredHolder<Item, BaseItem> RUNES_1 = item("runes_1");
    public static final DeferredHolder<Item, BaseItem> IRON_GOLEM_CORE = item("iron_golem_core");
    public static final DeferredHolder<Item, BaseItem> AQUAMARINE = item("aquamarine");
    public static final DeferredHolder<Item, BaseItem> WETWARE_ASSEMBLY = item("wetware_assembly");
    public static final DeferredHolder<Item, BaseItem> WETWARE_COMPUTER = item("wetware_computer");
    public static final DeferredHolder<Item, BaseItem> WETWARE_MAINFRAME = item("wetware_mainframe");
    public static final DeferredHolder<Item, BaseItem> WETWARE_PROCESSOR = item("wetware_processor");
    public static final DeferredHolder<Item, BaseItem> COLORFUL_CORE = item("colorful_core", true);
    public static final DeferredHolder<Item, BaseItem> COLORFUL_ENERGY_CORE = item("colorful_energy_core", true);
    public static final DeferredHolder<Item, BaseItem> TERMINAL_PASS = item("terminal_pass");
    public static final DeferredHolder<Item, BaseItem> TWILIGHT_CATALYST = item("twilight_catalyst");
    public static final DeferredHolder<Item, BaseItem> MANA_CRYSTAL1 = item("mana_crystal1");
    public static final DeferredHolder<Item, BaseItem> MANA_CRYSTAL2 = item("mana_crystal2");
    public static final DeferredHolder<Item, BaseItem> MANA_CRYSTAL3 = item("mana_crystal3");
    public static final DeferredHolder<Item, BaseItem> FORGEPLATE = item("forgeplate");
    public static final DeferredHolder<Item, BaseItem> FURNACE1 = item("furnace1");
    public static final DeferredHolder<Item, BaseItem> FURNACE2 = item("furnace2");
    public static final DeferredHolder<Item, BaseItem> FURNACE3 = item("furnace3");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_ASSEMBLY = item("crystal_assembly");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_COMPUTER = item("crystal_computer");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_MAINFRAME = item("crystal_mainframe");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_PROCESSOR = item("crystal_processor");
    public static final DeferredHolder<Item, BaseItem> QUANTUM_ASSEMBLY = item("quantum_assembly");
    public static final DeferredHolder<Item, BaseItem> QUANTUM_COMPUTER = item("quantum_computer");
    public static final DeferredHolder<Item, BaseItem> QUANTUM_MAINFRAME = item("quantum_mainframe");
    public static final DeferredHolder<Item, BaseItem> QUANTUM_PROCESSOR = item("quantum_processor");
    public static final DeferredHolder<Item, BaseItem> NANO_ASSEMBLY = item("nano_assembly");
    public static final DeferredHolder<Item, BaseItem> NANO_COMPUTER = item("nano_computer");
    public static final DeferredHolder<Item, BaseItem> NANO_MAINFRAME = item("nano_mainframe");
    public static final DeferredHolder<Item, BaseItem> NANO_PROCESSOR = item("nano_processor");
    public static final DeferredHolder<Item, BaseItem> CENTRAL_PROCESSING = item("central_processing");
    public static final DeferredHolder<Item, BaseItem> CHARGING_MAGIC_EMERALD_CRYSTAL = item("charging_magic_emerald_crystal", true);
    public static final DeferredHolder<Item, BaseItem> CIRCUIT_PROCESSOR = item("circuit_processor");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL = item("crystal");
    public static final DeferredHolder<Item, BaseItem> CRYSTAL_CHIP = item("crystal_chip");
    public static final DeferredHolder<Item, BaseItem> ZELUOSISHUIJING = item("zeluosishuijing");
    public static final DeferredHolder<Item, BaseItem> REDHEJIN = item("redhejin");
    public static final DeferredHolder<Item, BaseItem> REDKONGZHIDIANLU = item("redkongzhidianlu");
    public static final DeferredHolder<Item, BaseItem> WORKSTATION = item("workstation");
    public static final DeferredHolder<Item, BaseItem> MICROPROCESSOR = item("microprocessor");
    public static final DeferredHolder<Item, BaseItem> INTEGRATED = item("integrated");
    public static final DeferredHolder<Item, BaseItem> PROCESSOR = item("processor");
    public static final DeferredHolder<Item, BaseItem> KYRONITE = item("kyronite");
    public static final DeferredHolder<Item, BaseItem> LAIZEERSHUIJING = item("laizeershuijing");
    public static final DeferredHolder<Item, BaseItem> BASIC_INTEGRATED = item("basic_integrated");
    public static final DeferredHolder<Item, BaseItem> ADVANCED_INTEGRATED = item("advanced_integrated");
    public static final DeferredHolder<Item, BaseItem> MAGIC_EMERALD_CRYSTAL = item("magic_emerald_crystal");

    private static DeferredHolder<Item, BaseItem> item(String id) {
        return ITEMS.register(id, () -> new BaseItem(new Item.Properties(), false));
    }

    private static DeferredHolder<Item, BaseItem> item(String id, boolean tinted) {
        return ITEMS.register(id, () -> new BaseItem(new Item.Properties(), tinted));
    }

    public ChaosWorld(IEventBus modEventBus, ModContainer modContainer) {
        if (net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.CLIENT) {
            new UfoModClient(modEventBus);
        }
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::registerPackets);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        ModMetals.register();
        ModTech.register();

        ModDataComponents.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        com.yongaishide.chaosworld.fluid.ModFluidTypes.register(modEventBus);
        com.yongaishide.chaosworld.fluid.ModFluids.register(modEventBus);
        MultiblockBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCellItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenus.register(modEventBus);
        ModSounds.register(modEventBus);
        modEventBus.addListener(ModEntities::registerAttributes);
        ModBlocks.INSTANCE.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        NeoForge.EVENT_BUS.register(this);
    }

    private void registerPackets(final RegisterPayloadHandlersEvent event) {
        ModPackets.register(event);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        event.enqueueWork(() -> {
            UFORegistryHandler.INSTANCE.onInit();
            java.util.Objects.requireNonNull(com.yongaishide.chaosworld.menu.UFOSlotSemantics.MACHINE_OUTPUT_2);
            LazyInits.initCommon();
        });
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        event.enqueueWork(LazyInits::initFinal);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (ModKeyBindings.CYCLE_TOOL_FORWARD.consumeClick()) {
            ModPackets.sendToServer(new CycleToolKeyPacket(true));
        }
        if (ModKeyBindings.CYCLE_TOOL_BACKWARD.consumeClick()) {
            ModPackets.sendToServer(new CycleToolKeyPacket(false));
        }
        if (ModKeyBindings.CYCLE_MODE.consumeClick()) {
            ModPackets.sendToServer(new CycleModeKeyPacket());
        }
        if (ModKeyBindings.TOGGLE_AUTO_SMELT.consumeClick()) {
            ModPackets.sendToServer(new ToggleAutoSmeltPacket());
        }
    }

    @SubscribeEvent
    public void onMouseButton(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (event.getAction() != GLFW.GLFW_PRESS) return;
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;
        long window = mc.getWindow().getWindow();
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_ALT)
                || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_ALT)) {
            ModPackets.sendToServer(new CycleToolKeyPacket(true));
            event.setCanceled(true);
        }
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                var window = Minecraft.getInstance().getWindow().getWindow();
                org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback(window, handle -> {
                    org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose(window, false);
                    Minecraft.getInstance().setScreen(new com.yongaishide.chaosworld.client.ConfirmQuitScreen());
                });
            });
        }

        private static int getItemColor(String path) {
            int c = ModMetals.getColorForItem(path);
            if (c != 0xFFFFFFFF) return c;
            return ModTech.getColorForItem(path);
        }

        @SubscribeEvent
        public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
            var items = new java.util.ArrayList<Item>();
            ModMetals.METAL_ITEMS.values().forEach(ro -> items.add(ro.get()));
            ModMetals.METAL_BLOCK_ITEMS.values().forEach(ro -> items.add(ro.get()));
            ModTech.TECH_ITEMS.values().forEach(ro -> items.add(ro.get()));
            ModTech.TECH_BLOCK_ITEMS.values().forEach(ro -> items.add(ro.get()));

            event.register((stack, tintIndex) -> {
                if (tintIndex > 0) return -1;
                String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
                return getItemColor(path);
            }, items.toArray(new Item[0]));
        }

        @SubscribeEvent
        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            var blocks = new java.util.ArrayList<Block>();
            ModMetals.METAL_BLOCKS.values().forEach(ro -> blocks.add(ro.get()));
            ModTech.TECH_BLOCKS.values().forEach(ro -> blocks.add(ro.get()));

            event.register((state, level, pos, tintIndex) -> {
                if (tintIndex > 0) return -1;
                String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
                return getItemColor(path);
            }, blocks.toArray(new Block[0]));
        }
    }
}

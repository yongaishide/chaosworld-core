package com.yongaishide.chaosworld.botania;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;
import vazkii.botania.api.neoforge.BotaniaNeoForgeCapabilities;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.spark.ManaSparkAttachable;
import vazkii.botania.common.block.mana.ManaPoolBlock;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import com.yongaishide.chaosworld.ChaosWorld;

import appeng.api.AECapabilities;
import appeng.api.behaviors.ContainerItemStrategy;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.stacks.AEKeyTypes;

/**
 * Botania（1.21.1 / 457）与 AE2 的集成入口：魔力池方块、mana 键类型、以及 AE2 在方块/物品上的能力适配。
 */
public final class ChaosBotania {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredHolder<Block, FluixPool> FLUIX_MANA_POOL = ChaosWorld.BLOCKS
            .register("fluix_mana_pool", FluixPool::new);

    public static final DeferredHolder<Item, BlockItem> FLUIX_MANA_POOL_ITEM = ChaosWorld.ITEMS
            .register("fluix_mana_pool", () -> new BlockItem(FLUIX_MANA_POOL.get(), new Item.Properties()));

    private ChaosBotania() {
    }

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ChaosBotania::registerAeKeyType);
        modEventBus.addListener(ChaosBotania::registerCapabilities);
        modEventBus.addListener(ChaosBotania::commonSetup);
        modEventBus.addListener(ChaosBotania::clientSetup);
    }

    private static void registerAeKeyType(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            AEKeyTypes.register(ManaKeyType.TYPE);
        }
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(AECapabilities.IN_WORLD_GRID_NODE_HOST,
                (level, pos, state, blockEntity, context) -> blockEntity instanceof IInWorldGridNodeHost host ? host
                        : null,
                FLUIX_MANA_POOL.get());

        ContainerItemStrategy.register(ManaKeyType.TYPE, ManaKey.class, new ManaContainerItemStrategy());

        StackWorldBehaviors.registerImportStrategy(ManaKeyType.TYPE,
                (level, pos, side) -> new ManaStorageImportStrategy(level, pos, side));
        StackWorldBehaviors.registerExportStrategy(ManaKeyType.TYPE,
                (level, pos, side) -> new ManaStorageExportStrategy(level, pos, side));
        StackWorldBehaviors.registerExternalStorageStrategy(ManaKeyType.TYPE,
                (level, pos, side) -> new ManaExternalStorageStrategy(level, pos, side));

        registerGenericAdapters(event);
    }

    /**
     * 让 AE2 自带内部库存（GENERIC_INTERNAL_INV）的机器也能被魔力灌注/抽取，例如充能器、分子装配室等。
     */
    private static void registerGenericAdapters(RegisterCapabilitiesEvent event) {
        for (Block block : BuiltInRegistries.BLOCK) {
            if (!event.isBlockRegistered(AECapabilities.GENERIC_INTERNAL_INV, block)) {
                continue;
            }

            event.registerBlock(BotaniaNeoForgeCapabilities.getBlockApiLookupById(ManaReceiver.LOOKUP),
                    (Level level, BlockPos pos, BlockState state, BlockEntity blockEntity, Direction context) -> {
                        var inv = level.getCapability(AECapabilities.GENERIC_INTERNAL_INV, pos, context);
                        return inv != null ? new ManaGenericStackInvStorage(inv, level, pos) : null;
                    }, block);

            event.registerBlock(BotaniaNeoForgeCapabilities.getBlockApiLookupById(ManaSparkAttachable.LOOKUP),
                    (Level level, BlockPos pos, BlockState state, BlockEntity blockEntity, Void context) -> {
                        var inv = level.getCapability(AECapabilities.GENERIC_INTERNAL_INV, pos, Direction.UP);
                        return inv != null ? new ManaGenericStackInvStorage(inv, level, pos) : null;
                    }, block);
        }
    }

    private static void commonSetup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 键类型已在注册阶段登记，这里只做一次自检日志，方便排查集成是否生效
            LOGGER.info("Applied Botanics-style mana integration enabled for chaosworld_core");
        });
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(com.yongaishide.chaosworld.botania.client.ManaRenderer::initialize);
    }
}

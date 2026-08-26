package com.yongaishide.chaosworld.metal;

import com.yongaishide.chaosworld.ChaosWorld;
import com.yongaishide.chaosworld.item.BaseItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ModMetals {
    public static final Map<String, DeferredHolder<Item, ? extends Item>> METAL_ITEMS = new LinkedHashMap<>();
    public static final Map<String, DeferredHolder<Block, ? extends Block>> METAL_BLOCKS = new LinkedHashMap<>();
    public static final Map<String, DeferredHolder<Item, ? extends Item>> METAL_BLOCK_ITEMS = new LinkedHashMap<>();

    public static final String[][] METALS = {
        {"ice", "\u51B0\u96EA"},
        {"twilight_alloy", "\u66AE\u8272\u5408\u91D1"},
        {"lich", "\u5DEB\u795E"},
        {"chaotic_metal", "\u6DF7\u6C8C\u91D1\u5C5E"},
        {"draconic_metal", "\u795E\u9F99\u91D1\u5C5E"},
        {"wyvern_metal", "\u98DE\u9F99\u91D1\u5C5E"},
        {"atmium", "\u5168\u80FD\u5408\u91D1"},
        {"quantum", "\u91CF\u5B50"},
        {"stainless_steel", "\u4E0D\u9508\u94A2"},
    };

    public static final String[][] TYPES = {
        {"ingot", "\u952D"},
        {"nugget", "\u7C92"},
        {"plate", "\u677F"},
        {"dust", "\u7C89"},
        {"gear", "\u9F7F\u8F6E"},
        {"rod", "\u68D2"},
    };

    public static final Set<String> NO_TINT_METALS = Set.of("chaotic_metal", "draconic_metal", "wyvern_metal");

    public static final Map<String, Integer> METAL_COLORS = new HashMap<>();
    static {
        METAL_COLORS.put("ice", 0xFF07e5f5);
        METAL_COLORS.put("twilight_alloy", 0xFFac08e2);
        METAL_COLORS.put("lich", 0xFFd69404);
        METAL_COLORS.put("chaotic_metal", 0xFF1b191b);
        METAL_COLORS.put("draconic_metal", 0xFFe06b04);
        METAL_COLORS.put("wyvern_metal", 0xFF7042a2);
        METAL_COLORS.put("atmium", 0xFF7b0b0b);
        METAL_COLORS.put("quantum", 0xFF00ff48);
        METAL_COLORS.put("stainless_steel", 0xFF8d8b8b);
    }

    public static int getColorForItem(String path) {
        if (path.endsWith("_block")) {
            String metal = path.substring(0, path.length() - 6);
            if (NO_TINT_METALS.contains(metal)) return 0xFFFFFFFF;
            return METAL_COLORS.getOrDefault(metal, 0xFFFFFFFF);
        }
        for (String[] metal : METALS) {
            if (path.endsWith("_" + metal[0])) {
                if (NO_TINT_METALS.contains(metal[0])) return 0xFFFFFFFF;
                return METAL_COLORS.getOrDefault(metal[0], 0xFFFFFFFF);
            }
        }
        return 0xFFFFFFFF;
    }

    public static void register() {
        for (String[] metal : METALS) {
            String mname = metal[0];
            for (String[] type : TYPES) {
                String suffix = type[0];
                String itemId = suffix + "_" + mname;
                DeferredHolder<Item, ? extends Item> item = ChaosWorld.ITEMS.register(itemId,
                    () -> new BaseItem(new Item.Properties(), false));
                METAL_ITEMS.put(itemId, item);
            }

            String blockId = mname + "_block";
            DeferredHolder<Block, ? extends Block> block = ChaosWorld.BLOCKS.register(blockId,
                () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0f, 6.0f)
                    .sound(SoundType.METAL)));
            METAL_BLOCKS.put(blockId, block);

            DeferredHolder<Item, ? extends Item> blockItem = ChaosWorld.ITEMS.register(blockId,
                () -> new BlockItem(block.get(), new Item.Properties()));
            METAL_BLOCK_ITEMS.put(blockId, blockItem);
        }
    }
}

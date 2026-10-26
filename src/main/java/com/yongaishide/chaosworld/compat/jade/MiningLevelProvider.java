package com.yongaishide.chaosworld.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * Client-side Jade component that shows the mining level required to harvest
 * the targeted block, e.g. "Mining level: Iron". Shown for every block;
 * blocks that do not require a correct tool display "None".
 * <p>
 * Levels are read from the synced block tags ({@code needs_*_tool} and
 * {@code incorrect_for_*_tool}), so no server data provider is needed.
 */
public final class MiningLevelProvider implements IBlockComponentProvider {

    public static final MiningLevelProvider INSTANCE = new MiningLevelProvider();

    private static final String LANG_LINE = "jade.chaosworld.mining_level.line";
    private static final String LANG_TIER = "jade.chaosworld.tier.";

    private MiningLevelProvider() {
    }

    @Override
    public ResourceLocation getUid() {
        return ChaosWorldJadePlugin.MINING_LEVEL;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        tooltip.add(Component.translatable(LANG_LINE, tierName(accessor.getBlockState())));
    }

    private static Component tierName(BlockState state) {
        if (!state.requiresCorrectToolForDrops()) {
            return Component.translatable(LANG_TIER + "none");
        }
        if (state.is(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)) {
            return Component.translatable(LANG_TIER + "above_netherite");
        }
        if (state.is(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)) {
            return Component.translatable(LANG_TIER + "netherite");
        }
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return Component.translatable(LANG_TIER + "diamond");
        }
        if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
            return Component.translatable(LANG_TIER + "iron");
        }
        if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
            return Component.translatable(LANG_TIER + "stone");
        }
        if (state.is(BlockTags.INCORRECT_FOR_WOODEN_TOOL)) {
            return Component.translatable(LANG_TIER + "modded");
        }
        return Component.translatable(LANG_TIER + "wooden");
    }
}

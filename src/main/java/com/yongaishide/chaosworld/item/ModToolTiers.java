package com.yongaishide.chaosworld.item;

import com.yongaishide.chaosworld.util.ModTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
    public static final int UFO_MINING_LEVEL = 10;

    public static final Tier UFO = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_UFO_TOOL,
            10000,
            10f,
            10f,
            50,
            () -> Ingredient.of(ModTags.Items.INGREDIENTS_UFO)
    );
}

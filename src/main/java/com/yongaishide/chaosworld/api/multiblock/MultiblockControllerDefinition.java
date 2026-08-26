package com.yongaishide.chaosworld.api.multiblock;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public record MultiblockControllerDefinition(
        Component name,
        MultiblockPattern pattern,
        Map<Character, BlockState> defaultCreativeStates) {
}

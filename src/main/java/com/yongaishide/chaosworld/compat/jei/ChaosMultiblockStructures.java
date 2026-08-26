package com.yongaishide.chaosworld.compat.jei;

import com.moakiee.ae2lt.integration.recipeviewer.multiblock.MultiblockStructureRecipe;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinition;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinitions;
import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ChaosMultiblockStructures {

    private ChaosMultiblockStructures() {
    }

    public static List<MultiblockStructureRecipe> all() {
        return MultiblockControllerDefinitions.getPreviewEntries().stream()
                .map(ChaosMultiblockStructures::toStructureRecipe)
                .toList();
    }

    private static MultiblockStructureRecipe toStructureRecipe(MultiblockControllerDefinitions.PreviewEntry entry) {
        MultiblockControllerDefinition definition = entry.definition();
        MultiblockPattern pattern = definition.pattern();
        char[][][] chars = pattern.getPattern();
        char controllerChar = pattern.getControllerChar();
        BlockState controllerState = resolveControllerState(entry.iconStack(), pattern);

        int sizeX = chars[0][0].length;
        int sizeY = chars.length;
        int sizeZ = chars[0].length;

        List<MultiblockStructureRecipe.Cell> cells = new ArrayList<>();
        Map<Block, Component> materialNotes = new LinkedHashMap<>();

        for (int y = 0; y < sizeY; y++) {
            for (int z = 0; z < sizeZ; z++) {
                for (int x = 0; x < sizeX; x++) {
                    char c = chars[y][z][x];
                    BlockState state;
                    Component role;
                    if (c == controllerChar) {
                        state = controllerState;
                        role = definition.name();
                    } else {
                        state = definition.defaultCreativeStates().get(c);
                        if (state == null || state.isAir()) {
                            continue;
                        }
                        role = pattern.getLegendName(c);
                    }

                    List<Block> alternatives = pattern.getDisplayCandidates(c).stream()
                            .map(BlockState::getBlock)
                            .filter(block -> block != Blocks.AIR)
                            .toList();
                    Block ownBlock = state.getBlock();
                    if (!alternatives.contains(ownBlock)) {
                        List<Block> merged = new ArrayList<>(alternatives);
                        merged.add(ownBlock);
                        alternatives = merged;
                    }

                    cells.add(new MultiblockStructureRecipe.Cell(
                            new BlockPos(x, y, z), state, role, alternatives, List.of(), pattern.isShell(c)));
                    materialNotes.putIfAbsent(ownBlock, role);
                }
            }
        }

        List<MultiblockStructureRecipe.MaterialSpec> materialOrder = materialNotes.entrySet().stream()
                .map(e -> MultiblockStructureRecipe.MaterialSpec.of(e.getKey(), e.getValue()))
                .toList();

        return MultiblockStructureRecipe.create(
                entry.id(),
                definition.name(),
                sizeX, sizeY, sizeZ,
                cells,
                materialOrder);
    }

    private static BlockState resolveControllerState(ItemStack iconStack, MultiblockPattern pattern) {
        BlockState state;
        if (iconStack.getItem() instanceof BlockItem blockItem) {
            state = blockItem.getBlock().defaultBlockState();
        } else {
            state = Blocks.IRON_BLOCK.defaultBlockState();
        }
        if (state.hasProperty(DirectionalBlock.FACING)) {
            state = state.setValue(DirectionalBlock.FACING, controllerOutwardFacing(pattern));
        }
        return state;
    }

    private static Direction controllerOutwardFacing(MultiblockPattern pattern) {
        char[][][] chars = pattern.getPattern();
        int sizeX = chars[0][0].length;
        int sizeZ = chars[0].length;
        int col = pattern.getControllerCol();
        int row = pattern.getControllerRow();
        if (col == 0) {
            return Direction.WEST;
        }
        if (col == sizeX - 1) {
            return Direction.EAST;
        }
        if (row == 0) {
            return Direction.NORTH;
        }
        if (row == sizeZ - 1) {
            return Direction.SOUTH;
        }
        return Direction.NORTH;
    }
}

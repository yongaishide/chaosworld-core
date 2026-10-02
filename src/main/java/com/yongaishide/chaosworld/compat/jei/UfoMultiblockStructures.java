package com.yongaishide.chaosworld.compat.jei;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.moakiee.ae2lt.integration.recipeviewer.multiblock.MultiblockStructureRecipe;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinition;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions.PreviewEntry;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Patch layer: mirrors every UFO Future multiblock preview into AE2 Lightning
 * Tech's "Multiblock Structures" JEI category, so the pack's machines appear
 * next to the AE2LT tianshu structures instead of only in a separate UFO tab.
 */
public final class UfoMultiblockStructures {

    private UfoMultiblockStructures() {
    }

    public static List<MultiblockStructureRecipe> all() {
        return MultiblockControllerDefinitions.getPreviewEntries().stream()
                .map(UfoMultiblockStructures::toStructureRecipe)
                .toList();
    }

    private static MultiblockStructureRecipe toStructureRecipe(PreviewEntry entry) {
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
                    List<BlockState> candidates = pattern.getDisplayCandidates(c);
                    BlockState state;
                    Component role;
                    if (c == controllerChar) {
                        state = controllerState;
                        role = definition.name();
                    } else {
                        state = definition.defaultCreativeStates().get(c);
                        if (state == null || state.isAir()) {
                            state = firstVisible(candidates);
                        }
                        if (state == null) {
                            continue;
                        }
                        role = pattern.getLegendName(c);
                    }

                    List<Block> alternatives = new ArrayList<>(new LinkedHashSet<>(candidates.stream()
                            .map(BlockState::getBlock)
                            .filter(block -> block != Blocks.AIR)
                            .toList()));
                    Block ownBlock = state.getBlock();
                    if (!alternatives.contains(ownBlock)) {
                        alternatives.add(0, ownBlock);
                    }

                    cells.add(new MultiblockStructureRecipe.Cell(
                            new BlockPos(x, y, z),
                            state,
                            role,
                            alternatives,
                            List.of(),
                            isStructuralShell(state, c, controllerChar)));
                    materialNotes.putIfAbsent(ownBlock, role);
                }
            }
        }

        List<MultiblockStructureRecipe.MaterialSpec> materialOrder = materialNotes.entrySet().stream()
                .map(entryNote -> MultiblockStructureRecipe.MaterialSpec.of(entryNote.getKey(), entryNote.getValue()))
                .toList();

        return MultiblockStructureRecipe.create(
                entry.id(),
                definition.name(),
                sizeX, sizeY, sizeZ,
                cells,
                materialOrder);
    }

    private static BlockState firstVisible(List<BlockState> states) {
        for (BlockState state : states) {
            if (state != null && !state.isAir()) {
                return state;
            }
        }
        return null;
    }

    private static boolean isStructuralShell(BlockState state, char symbol, char controllerSymbol) {
        if (state.isAir() || symbol == controllerSymbol) {
            return false;
        }
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        return path.contains("casing") || path.contains("glass") || path.contains("frame")
                || path.contains("structure") || path.contains("reinforced_alloy");
    }

    private static BlockState resolveControllerState(ItemStack iconStack, MultiblockPattern pattern) {
        BlockState state = iconStack.getItem() instanceof BlockItem blockItem
                ? blockItem.getBlock().defaultBlockState()
                : Blocks.IRON_BLOCK.defaultBlockState();
        Direction facing = controllerOutwardFacing(pattern);
        if (state.hasProperty(DirectionalBlock.FACING)) {
            state = state.setValue(DirectionalBlock.FACING, facing);
        } else if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            state = state.setValue(HorizontalDirectionalBlock.FACING, facing);
        }
        return state;
    }

    private static Direction controllerOutwardFacing(MultiblockPattern pattern) {
        char[][][] chars = pattern.getPattern();
        int sizeX = chars[0][0].length;
        int sizeZ = chars[0].length;
        int col = pattern.getControllerCol();
        int row = pattern.getControllerRow();
        if (col <= 0) {
            return Direction.WEST;
        }
        if (col >= sizeX - 1) {
            return Direction.EAST;
        }
        if (row <= 0) {
            return Direction.NORTH;
        }
        if (row >= sizeZ - 1) {
            return Direction.SOUTH;
        }
        return Direction.NORTH;
    }
}

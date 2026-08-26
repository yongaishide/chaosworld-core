package com.yongaishide.chaosworld.api.multiblock;

import com.yongaishide.chaosworld.block.MultiblockBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Helper for controller-less cubic multiblocks whose real tier is defined by a fully
 * filled interior made of a single field tier.
 * <p>
 * Intended for low-frequency use only:
 * <ul>
 *     <li>player interaction</li>
 *     <li>dirty-state revalidation</li>
 *     <li>chunk/load recovery</li>
 * </ul>
 * Never call this in hot per-tick loops for many machines.
 */
public final class FieldTieredCubeValidator {

    public static final int OUTER_SIZE = 7;
    public static final int INNER_SIZE = 5;
    public static final int OUTER_RADIUS = OUTER_SIZE - 1;
    public static final int INNER_OFFSET = 1;

    private FieldTieredCubeValidator() {
    }

    @FunctionalInterface
    public interface ShellPredicate {
        boolean test(BlockState state, Level level, BlockPos pos);
    }

    public record ValidationResult(
            boolean valid,
            BlockPos origin,
            BlockPos minCorner,
            BlockPos maxCorner,
            BlockPos clickedPos,
            int machineTier,
            boolean hasUnloadedPositions,
            List<BlockPos> shellPositions,
            List<BlockPos> interiorPositions) {
    }

    /**
     * Finds a valid cube containing {@code clickedPos}. The canonical origin is the minimum corner
     * of the outer 7x7x7 cube.
     */
    public static Optional<ValidationResult> findMatchingCube(Level level, BlockPos clickedPos, ShellPredicate shellPredicate) {
        ValidationResult bestMatch = null;

        for (int originX = clickedPos.getX() - OUTER_RADIUS; originX <= clickedPos.getX(); originX++) {
            for (int originY = clickedPos.getY() - OUTER_RADIUS; originY <= clickedPos.getY(); originY++) {
                for (int originZ = clickedPos.getZ() - OUTER_RADIUS; originZ <= clickedPos.getZ(); originZ++) {
                    BlockPos origin = new BlockPos(originX, originY, originZ);
                    if (!contains(origin, clickedPos)) {
                        continue;
                    }

                    ValidationResult result = validateAt(level, origin, clickedPos, shellPredicate);
                    if (result.valid()) {
                        if (bestMatch == null || compareOrigins(origin, bestMatch.origin()) < 0) {
                            bestMatch = result;
                        }
                    }
                }
            }
        }

        return Optional.ofNullable(bestMatch);
    }

    public static ValidationResult validateAt(Level level, BlockPos origin, BlockPos clickedPos, ShellPredicate shellPredicate) {
        List<BlockPos> shellPositions = new ArrayList<>(OUTER_SIZE * OUTER_SIZE * OUTER_SIZE - INNER_SIZE * INNER_SIZE * INNER_SIZE);
        List<BlockPos> interiorPositions = new ArrayList<>(INNER_SIZE * INNER_SIZE * INNER_SIZE);
        boolean hasUnloadedPositions = false;

        Integer detectedTier = null;
        boolean valid = true;

        for (int x = 0; x < OUTER_SIZE; x++) {
            for (int y = 0; y < OUTER_SIZE; y++) {
                for (int z = 0; z < OUTER_SIZE; z++) {
                    BlockPos currentPos = origin.offset(x, y, z);
                    boolean isInterior = x >= INNER_OFFSET && x < OUTER_SIZE - INNER_OFFSET
                            && y >= INNER_OFFSET && y < OUTER_SIZE - INNER_OFFSET
                            && z >= INNER_OFFSET && z < OUTER_SIZE - INNER_OFFSET;

                    if (!level.isLoaded(currentPos)) {
                        valid = false;
                        hasUnloadedPositions = true;
                        continue;
                    }

                    BlockState state = level.getBlockState(currentPos);
                    if (isInterior) {
                        interiorPositions.add(currentPos);
                        int fieldTier = resolveFieldTier(state);
                        if (fieldTier == 0) {
                            valid = false;
                            continue;
                        }

                        if (detectedTier == null) {
                            detectedTier = fieldTier;
                        } else if (detectedTier != fieldTier) {
                            valid = false;
                        }
                    } else {
                        shellPositions.add(currentPos);
                        if (!shellPredicate.test(state, level, currentPos)) {
                            valid = false;
                        }
                    }
                }
            }
        }

        if (detectedTier == null) {
            valid = false;
            detectedTier = MultiblockMachineTier.MK1.level();
        }

        return new ValidationResult(
                valid,
                origin.immutable(),
                origin.immutable(),
                origin.offset(OUTER_SIZE - 1, OUTER_SIZE - 1, OUTER_SIZE - 1).immutable(),
                clickedPos.immutable(),
                detectedTier,
                hasUnloadedPositions,
                Collections.unmodifiableList(shellPositions),
                Collections.unmodifiableList(interiorPositions));
    }

    public static boolean contains(BlockPos origin, BlockPos pos) {
        return pos.getX() >= origin.getX() && pos.getX() < origin.getX() + OUTER_SIZE
                && pos.getY() >= origin.getY() && pos.getY() < origin.getY() + OUTER_SIZE
                && pos.getZ() >= origin.getZ() && pos.getZ() < origin.getZ() + OUTER_SIZE;
    }

    public static int resolveFieldTier(BlockState state) {
        if (state.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get())) {
            return MultiblockMachineTier.MK1.level();
        }
        if (state.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get())) {
            return MultiblockMachineTier.MK2.level();
        }
        if (state.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get())) {
            return MultiblockMachineTier.MK3.level();
        }
        return 0;
    }

    private static int compareOrigins(BlockPos a, BlockPos b) {
        if (a.getY() != b.getY()) {
            return Integer.compare(a.getY(), b.getY());
        }
        if (a.getZ() != b.getZ()) {
            return Integer.compare(a.getZ(), b.getZ());
        }
        return Integer.compare(a.getX(), b.getX());
    }
}

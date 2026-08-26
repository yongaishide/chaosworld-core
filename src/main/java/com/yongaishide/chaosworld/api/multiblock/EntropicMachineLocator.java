package com.yongaishide.chaosworld.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class EntropicMachineLocator {
    private static final int SEARCH_RADIUS = FieldTieredCubeValidator.OUTER_SIZE - 1;

    private EntropicMachineLocator() {
    }

    @Nullable
    public static IEntropicMachineController findController(Level level, BlockPos origin) {
        IEntropicMachineController best = null;
        BlockPos bestPos = null;

        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
                origin.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof IEntropicMachineController controller) || !controller.canProxyInteract(origin)) {
                continue;
            }

            if (best == null || compare(pos, bestPos) < 0) {
                best = controller;
                bestPos = pos.immutable();
            }
        }

        return best;
    }

    public static void markNearbyDirty(Level level, BlockPos origin) {
    }

    private static int compare(@Nullable BlockPos a, @Nullable BlockPos b) {
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        if (a.getY() != b.getY()) {
            return Integer.compare(a.getY(), b.getY());
        }
        if (a.getZ() != b.getZ()) {
            return Integer.compare(a.getZ(), b.getZ());
        }
        return Integer.compare(a.getX(), b.getX());
    }
}

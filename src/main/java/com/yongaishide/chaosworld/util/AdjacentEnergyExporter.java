package com.yongaishide.chaosworld.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class AdjacentEnergyExporter {
    private AdjacentEnergyExporter() {
    }

    public static void pushEnergy(Level level, BlockPos pos, IEnergyStorage source, int totalBudget, int perSideBudget) {
        if (level == null || level.isClientSide() || source == null || totalBudget <= 0 || perSideBudget <= 0) {
            return;
        }

        int remaining = totalBudget;
        for (Direction direction : Direction.values()) {
            if (remaining <= 0) {
                break;
            }

            BlockPos neighborPos = pos.relative(direction);
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, direction.getOpposite());
            if (target == null || !target.canReceive()) {
                continue;
            }

            int offer = Math.min(perSideBudget, remaining);
            if (offer <= 0) {
                continue;
            }

            int simulated = source.extractEnergy(offer, true);
            if (simulated <= 0) {
                continue;
            }

            int accepted = target.receiveEnergy(simulated, false);
            if (accepted <= 0) {
                continue;
            }

            int extracted = source.extractEnergy(accepted, false);
            if (extracted <= 0) {
                continue;
            }

            remaining -= extracted;
        }
    }
}

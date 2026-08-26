package com.yongaishide.chaosworld.block.entity;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.blockentity.networking.CreativeEnergyCellBlockEntity;
import com.yongaishide.chaosworld.util.AdjacentEnergyExporter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class QuantumEnergyCellBlockEntity extends CreativeEnergyCellBlockEntity {
    private static final int CREATIVE_EXPORT_RATE = Integer.MAX_VALUE;

    private final IEnergyStorage exposedEnergy = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (maxExtract <= 0) {
                return 0;
            }

            return (int) Math.min(Integer.MAX_VALUE,
                    QuantumEnergyCellBlockEntity.this.extractAEPower(maxExtract,
                            simulate ? Actionable.SIMULATE : Actionable.MODULATE,
                            PowerMultiplier.CONFIG));
        }

        @Override
        public int getEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaxEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    public QuantumEnergyCellBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        AdjacentEnergyExporter.pushEnergy(this.level, this.worldPosition, this.exposedEnergy, CREATIVE_EXPORT_RATE, CREATIVE_EXPORT_RATE);
    }

    public IEnergyStorage getExposedEnergy() {
        return this.exposedEnergy;
    }
}

package com.yongaishide.chaosworld.fluid.custom;

import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.fluid.ModFluidTypes;
import com.yongaishide.chaosworld.fluid.ModFluids;
import com.yongaishide.chaosworld.item.ModItems;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class StableCoolantFluid extends BaseFlowingFluid {
    protected StableCoolantFluid() {
        super(new Properties(
                ModFluidTypes.STABLE_COOLANT_TYPE,
                ModFluids.SOURCE_STABLE_COOLANT,
                ModFluids.FLOWING_STABLE_COOLANT)
                .bucket(ModItems.STABLE_COOLANT_BUCKET)
                .block(ModBlocks.STABLE_COOLANT_BLOCK));
    }

    public static class Source extends StableCoolantFluid {
        public Source() {
            super();
        }

        public int getAmount(net.minecraft.world.level.material.FluidState state) {
            return 8;
        }

        public boolean isSource(net.minecraft.world.level.material.FluidState state) {
            return true;
        }
    }

    public static class Flowing extends StableCoolantFluid {
        public Flowing() {
            super();
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, net.minecraft.world.level.material.FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(net.minecraft.world.level.material.FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(net.minecraft.world.level.material.FluidState state) {
            return false;
        }
    }
}

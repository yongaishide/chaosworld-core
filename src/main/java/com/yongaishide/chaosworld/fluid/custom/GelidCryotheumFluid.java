package com.yongaishide.chaosworld.fluid.custom;

import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.fluid.ModFluidTypes;
import com.yongaishide.chaosworld.fluid.ModFluids;
import com.yongaishide.chaosworld.item.ModItems;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class GelidCryotheumFluid extends BaseFlowingFluid {
    protected GelidCryotheumFluid() {
        super(new Properties(
                ModFluidTypes.GELID_CRYOTHEUM_TYPE,
                ModFluids.SOURCE_GELID_CRYOTHEUM,
                ModFluids.FLOWING_GELID_CRYOTHEUM)
                .bucket(ModItems.GELID_CRYOTHEUM_BUCKET)
                .block(ModBlocks.GELID_CRYOTHEUM_BLOCK));
    }

    public static class Source extends GelidCryotheumFluid {
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

    public static class Flowing extends GelidCryotheumFluid {
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
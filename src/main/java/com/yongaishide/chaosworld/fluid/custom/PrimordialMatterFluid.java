package com.yongaishide.chaosworld.fluid.custom;

import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.fluid.ModFluidTypes;
import com.yongaishide.chaosworld.fluid.ModFluids;
import com.yongaishide.chaosworld.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class PrimordialMatterFluid extends BaseFlowingFluid {
    public static final Properties PROPERTIES = new Properties(
            ModFluidTypes.PRIMORDIAL_MATTER_FLUID_TYPE,
            ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID,
            ModFluids.FLOWING_PRIMORDIAL_MATTER_FLUID
    )
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(ModBlocks.PRIMORDIAL_MATTER_FLUID_BLOCK)
            .bucket(ModItems.PRIMORDIAL_MATTER_BUCKET);
    protected PrimordialMatterFluid() {
        super(PROPERTIES);
    }
    @Override
    public Fluid getSource() {
        return ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID.get();
    }
    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_PRIMORDIAL_MATTER_FLUID.get();
    }
    @Override
    public Item getBucket() {
        return ModItems.PRIMORDIAL_MATTER_BUCKET.get();
    }
    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }
    public static class Source extends PrimordialMatterFluid {
        public Source() { super(); }
        @Override
        public int getAmount(FluidState state) {
            return 8; // Bloco cheio
        }
        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
    public static class Flowing extends PrimordialMatterFluid {
        public Flowing() { super(); }
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
}

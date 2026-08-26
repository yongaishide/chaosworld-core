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

public abstract class WhiteDwarfFluid extends BaseFlowingFluid {
    public static final Properties PROPERTIES = new Properties(
            ModFluidTypes.WHITE_DWARF_FRAGMENT_FLUID_TYPE,
            ModFluids.SOURCE_WHITE_DWARF_FRAGMENT_FLUID,
            ModFluids.FLOWING_WHITE_DWARF_FRAGMENT_FLUID
    )
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(ModBlocks.WHITE_DWARF_FRAGMENT_FLUID_BLOCK)
            .bucket(ModItems.WHITE_DWARF_FRAGMENT_BUCKET);
    protected WhiteDwarfFluid() {
        super(PROPERTIES);
    }
    @Override
    public Fluid getSource() {
        return ModFluids.SOURCE_WHITE_DWARF_FRAGMENT_FLUID.get();
    }
    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_WHITE_DWARF_FRAGMENT_FLUID.get();
    }
    @Override
    public Item getBucket() {
        return ModItems.WHITE_DWARF_FRAGMENT_BUCKET.get();
    }
    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }
    public static class Source extends WhiteDwarfFluid {
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
    public static class Flowing extends WhiteDwarfFluid {
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

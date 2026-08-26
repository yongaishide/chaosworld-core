package com.yongaishide.chaosworld.datagen;

import com.yongaishide.chaosworld.fluid.ModFluids;
import com.yongaishide.chaosworld.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagProvider extends FluidTagsProvider {
    public ModFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "chaosworld_core", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Fluids.COOLANTS).add(
                ModFluids.SOURCE_GELID_CRYOTHEUM.get(),
                ModFluids.FLOWING_GELID_CRYOTHEUM.get(),
                ModFluids.SOURCE_STABLE_COOLANT.get(),
                ModFluids.FLOWING_STABLE_COOLANT.get(),
                ModFluids.SOURCE_TEMPORAL_FLUID.get(),
                ModFluids.FLOWING_TEMPORAL_FLUID.get()
        );
        tag(ModTags.Fluids.COOLANT_EXTREME).add(
                ModFluids.SOURCE_TEMPORAL_FLUID.get(),
                ModFluids.FLOWING_TEMPORAL_FLUID.get()
        );
        tag(ModTags.Fluids.SYNTHETIC_FLUID).add(
                ModFluids.SOURCE_UU_AMPLIFIER_FLUID.get(),
                ModFluids.FLOWING_UU_AMPLIFIER_FLUID.get()
        );
        tag(ModTags.Fluids.MATTER_FLUID).add(
                ModFluids.SOURCE_UU_MATTER_FLUID.get(),
                ModFluids.FLOWING_UU_MATTER_FLUID.get(),
                ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID.get(),
                ModFluids.FLOWING_PRIMORDIAL_MATTER_FLUID.get()
        );
        tag(ModTags.Fluids.ENERGY_FLUID).add(
                ModFluids.SOURCE_LIQUID_STARLIGHT_FLUID.get(),
                ModFluids.FLOWING_LIQUID_STARLIGHT_FLUID.get()
        );
        tag(ModTags.Fluids.PLASMA).add(
                ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID.get(),
                ModFluids.FLOWING_RAW_STAR_MATTER_PLASMA_FLUID.get(),
                ModFluids.SOURCE_TRANSCENDING_MATTER_FLUID.get(),
                ModFluids.FLOWING_TRANSCENDING_MATTER_FLUID.get()
        );

        tag(ModTags.Fluids.HAZARDOUS).add(
                ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID.get(),
                ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID.get(),
                ModFluids.FLOWING_RAW_STAR_MATTER_PLASMA_FLUID.get(),
                ModFluids.SOURCE_TRANSCENDING_MATTER_FLUID.get(),
                ModFluids.FLOWING_TRANSCENDING_MATTER_FLUID.get()
        );
    }
}

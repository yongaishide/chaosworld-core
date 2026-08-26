package com.yongaishide.chaosworld.fluid;

import com.yongaishide.chaosworld.fluid.custom.*; // Importe suas classes novas aqui
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, "chaosworld_core");

    // --- PULSAR FRAGMENT ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_PULSAR_FRAGMENT_FLUID = FLUIDS.register("source_pulsar_fragment_fluid",
            PulsarFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_PULSAR_FRAGMENT_FLUID = FLUIDS.register("flowing_pulsar_fragment_fluid",
            PulsarFluid.Flowing::new);

    // --- NEUTRON STAR FRAGMENT ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_NEUTRON_STAR_FRAGMENT_FLUID = FLUIDS.register("source_neutron_star_fragment_fluid",
            NeutronStarFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_NEUTRON_STAR_FRAGMENT_FLUID = FLUIDS.register("flowing_neutron_star_fragment_fluid",
            NeutronStarFluid.Flowing::new);

    // --- WHITE DWARF FRAGMENT ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_WHITE_DWARF_FRAGMENT_FLUID = FLUIDS.register("source_white_dwarf_fragment_fluid",
            WhiteDwarfFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_WHITE_DWARF_FRAGMENT_FLUID = FLUIDS.register("flowing_white_dwarf_fragment_fluid",
            WhiteDwarfFluid.Flowing::new);

    // --- LIQUID STARLIGHT ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_LIQUID_STARLIGHT_FLUID = FLUIDS.register("source_liquid_starlight_fluid",
            LiquidStarlightFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_LIQUID_STARLIGHT_FLUID = FLUIDS.register("flowing_liquid_starlight_fluid",
            LiquidStarlightFluid.Flowing::new);

    // --- PRIMORDIAL MATTER ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_PRIMORDIAL_MATTER_FLUID = FLUIDS.register("source_primordial_matter_fluid",
            PrimordialMatterFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_PRIMORDIAL_MATTER_FLUID = FLUIDS.register("flowing_primordial_matter_fluid",
            PrimordialMatterFluid.Flowing::new);

    // --- RAW STAR MATTER PLASMA ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_RAW_STAR_MATTER_PLASMA_FLUID = FLUIDS.register("raw_star_matter_plasma",
            RawStarMatterPlasmaFluid.Source::new);
    // Antes: "flowing_raw_star_matter_plasma_fluid"
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_RAW_STAR_MATTER_PLASMA_FLUID = FLUIDS.register("flowing_raw_star_matter_plasma",
            RawStarMatterPlasmaFluid.Flowing::new);

    // --- TRANSCENDING MATTER ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_TRANSCENDING_MATTER_FLUID = FLUIDS.register("transcending_matter",
            TranscendingMatterFluid.Source::new);
    // Antes: "flowing_transcending_matter_fluid"
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_TRANSCENDING_MATTER_FLUID = FLUIDS.register("flowing_transcending_matter",
            TranscendingMatterFluid.Flowing::new);

    // --- UU MATTER ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_UU_MATTER_FLUID = FLUIDS.register("uu_matter",
            UuMatterFluid.Source::new);
    // Antes: "flowing_uu_matter_fluid"
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_UU_MATTER_FLUID = FLUIDS.register("flowing_uu_matter",
            UuMatterFluid.Flowing::new);

    // --- UU AMPLIFIER ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_UU_AMPLIFIER_FLUID = FLUIDS.register("source_uu_amplifier_fluid",
            UuAmplifierFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_UU_AMPLIFIER_FLUID = FLUIDS.register("flowing_uu_amplifier_fluid",
            UuAmplifierFluid.Flowing::new);

    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_GELID_CRYOTHEUM = FLUIDS.register("source_gelid_cryotheum",
            GelidCryotheumFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_GELID_CRYOTHEUM = FLUIDS.register("flowing_gelid_cryotheum",
            GelidCryotheumFluid.Flowing::new);

    // --- STABLE COOLANT ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_STABLE_COOLANT = FLUIDS.register("source_stable_coolant",
            StableCoolantFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_STABLE_COOLANT = FLUIDS.register("flowing_stable_coolant",
            StableCoolantFluid.Flowing::new);

    // --- TEMPORAL FLUID ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_TEMPORAL_FLUID = FLUIDS.register("source_temporal_fluid",
            TemporalFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_TEMPORAL_FLUID = FLUIDS.register("flowing_temporal_fluid",
            TemporalFluid.Flowing::new);

    // --- SPATIAL FLUID ---
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_SPATIAL_FLUID = FLUIDS.register("source_spatial_fluid",
            SpatialFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_SPATIAL_FLUID = FLUIDS.register("flowing_spatial_fluid",
            SpatialFluid.Flowing::new);


    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
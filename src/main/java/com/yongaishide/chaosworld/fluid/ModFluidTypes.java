package com.yongaishide.chaosworld.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, "chaosworld_core");
    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
    public static final DeferredHolder<FluidType, FluidType> NEUTRON_STAR_FRAGMENT_FLUID_TYPE = registerBasic(
            "neutron_star_fragment_fluid", "neutron_star_fragment", 20000, 5000, 15);

    public static final DeferredHolder<FluidType, FluidType> PULSAR_FRAGMENT_FLUID_TYPE = registerBasic(
            "pulsar_fragment_fluid", "pulsar_fragment", 25000, 6000, 15);

    public static final DeferredHolder<FluidType, FluidType> WHITE_DWARF_FRAGMENT_FLUID_TYPE = registerBasic(
            "white_dwarf_fragment_fluid", "white_dwarf_fragment", 10000, 3000, 12);

    public static final DeferredHolder<FluidType, FluidType> LIQUID_STARLIGHT_FLUID_TYPE = registerBasic(
            "liquid_starlight_fluid", "liquid_starlight", 500, 100, 15);

    public static final DeferredHolder<FluidType, FluidType> PRIMORDIAL_MATTER_FLUID_TYPE = registerBasic(
            "primordial_matter_fluid", "primordialmatter", 5000, 1000, 8);

    // Plasma ajustado para 2000 conforme o PDF [392]
    public static final DeferredHolder<FluidType, FluidType> RAW_STAR_MATTER_PLASMA_FLUID_TYPE = registerBasic(
            "raw_star_matter_plasma_fluid", "raw_star_matter_plasma", 2000, 10000, 15);

    public static final DeferredHolder<FluidType, FluidType> TRANSCENDING_MATTER_FLUID_TYPE = registerBasic(
            "transcending_matter_fluid", "transcending_matter", 1000, 5000, 10);

    public static final DeferredHolder<FluidType, FluidType> UU_MATTER_FLUID_TYPE = registerBasic(
            "uu_matter_fluid", "uu_matter", 300, 1000, 0);

    public static final DeferredHolder<FluidType, FluidType> UU_AMPLIFIER_FLUID_TYPE = registerBasic(
            "uu_amplifier_fluid", "uuamplifier", 300, 1000, 0);
    public static final DeferredHolder<FluidType, FluidType> TEMPORAL_FLUID_TYPE = registerCoolant(
            "temporal_fluid", "temporal_fluid", -300);

    public static final DeferredHolder<FluidType, FluidType> SPATIAL_FLUID_TYPE = registerCoolant(
            "spatial_fluid", "spatial_fluid", -300);


    public static final DeferredHolder<FluidType, FluidType> GELID_CRYOTHEUM_TYPE = FLUID_TYPES.register("gelid_cryotheum", () -> new BaseFluidType(
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "block/fluid/gelid_cryotheum_still"),
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "block/fluid/gelid_cryotheum_flow"),
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "block/fluid/gelid_cryotheum_still"),
            0xFF00FFFF,
            new Vector3f(0.0f, 1.0f, 1.0f),
            FluidType.Properties.create()
                    .temperature(-500)
                    .density(3000)
                    .viscosity(6000)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_POWDER_SNOW)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_POWDER_SNOW)
    ));

    public static final DeferredHolder<FluidType, FluidType> STABLE_COOLANT_TYPE = FLUID_TYPES.register("stable_coolant", () -> new BaseFluidType(
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "block/fluid/fluid.stable_coolant"),
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "block/fluid/fluid.stable_coolant"),
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "block/fluid/fluid.stable_coolant"),
            0xFF88DDFF,
            new Vector3f(0.2f, 0.8f, 0.9f),
            FluidType.Properties.create()
                    .temperature(-200)
                    .density(3500)
                    .viscosity(5000)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_POWDER_SNOW)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_POWDER_SNOW)
    ));



    private static DeferredHolder<FluidType, FluidType> registerBasic(String name, String textureName, int temp, int density, int lightLevel) {
        ResourceLocation stillTexture = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "block/fluid/" + textureName);
        return FLUID_TYPES.register(name, () -> new BaseFluidType(
                stillTexture, stillTexture, stillTexture,
                0xFFFFFFFF,
                new Vector3f(1f, 1f, 1f),
                FluidType.Properties.create()
                        .temperature(temp)
                        .density(density)
                        .viscosity(1000)
                        .lightLevel(lightLevel)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
        ));
    }

    private static DeferredHolder<FluidType, FluidType> registerCoolant(String name, String textureName, int temperature) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "block/fluid/" + textureName);
        return FLUID_TYPES.register(name, () -> new BaseFluidType(
                texture, texture, texture,
                0xFFFFFFFF, // Usa a cor personalizada (Roxo/Verde)
                new Vector3f(0.5f, 0.8f, 1.0f), // Neblina azulada
                FluidType.Properties.create()
                        .temperature(temperature)
                        .density(3000)     // Densidade padr茫o para seus coolants
                        .viscosity(6000)   // Viscosidade padr茫o para seus coolants
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_POWDER_SNOW)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_POWDER_SNOW)
        ));
    }
}
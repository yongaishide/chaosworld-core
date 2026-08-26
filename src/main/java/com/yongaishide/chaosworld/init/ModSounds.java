package com.yongaishide.chaosworld.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    // 1. Cria o DeferredRegister para sons
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "chaosworld_core");

    // 2. Registra seus tr锚s sons
    public static final Supplier<SoundEvent> DMA_WORK = registerSound("block.dma.work");
    public static final Supplier<SoundEvent> DMA_ALARM = registerSound("block.dma.alarm");


    // 3. Helper para registrar os sons (COM O M脡TODO CORRETO)
    private static Supplier<SoundEvent> registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("chaosworld_core", name);
        // CORRE脟脙O: Usando createVariableRangeEvent
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    // 4. M茅todo para registrar o DeferredRegister no EventBus
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
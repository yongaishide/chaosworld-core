package com.yongaishide.chaosworld.compat.jade;

import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import com.yongaishide.chaosworld.block.QuantumPatternHatchBlock;
import com.yongaishide.chaosworld.block.entity.QuantumPatternHatchBE;

/**
 * Jade integration for the Quantum Pattern Hatch (量子样板供应器).
 * <p>
 * Shows the resources buffered inside the hatch's internal return inventory,
 * so players can see at a glance what is cached and waiting to be pushed back
 * into the ME network.
 */
@WailaPlugin("chaosworld_core")
public class ChaosWorldJadePlugin implements IWailaPlugin {

    public static final ResourceLocation QUANTUM_PATTERN_HATCH_CACHE =
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "quantum_pattern_hatch_cache");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(QuantumPatternHatchCacheProvider.INSTANCE, QuantumPatternHatchBE.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(QuantumPatternHatchCacheProvider.INSTANCE, QuantumPatternHatchBlock.class);
    }
}

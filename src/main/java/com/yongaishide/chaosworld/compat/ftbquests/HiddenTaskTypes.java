package com.yongaishide.chaosworld.compat.ftbquests;

import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public final class HiddenTaskTypes {
    public static final Set<ResourceLocation> HIDDEN = Set.of(
            ResourceLocation.fromNamespaceAndPath("ftbquests", "item"),
            ResourceLocation.fromNamespaceAndPath("ftbquests", "fluid"),
            ResourceLocation.fromNamespaceAndPath("ftbquests", "forge_energy"),
            ResourceLocation.fromNamespaceAndPath("chaosworld_core", "chemical")
    );

    private HiddenTaskTypes() {
    }
}

package com.yongaishide.chaosworld.compat.mekanism;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface MekanismChemicalStorage {
    boolean supportsChemicalIO();

    long getChemicalCapacity();

    @Nullable
    ResourceLocation getStoredChemicalId();

    long getStoredChemicalAmount();

    void setStoredChemical(@Nullable ResourceLocation chemicalId, long amount);
}

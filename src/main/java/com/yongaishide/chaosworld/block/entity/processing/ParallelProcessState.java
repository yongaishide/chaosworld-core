package com.yongaishide.chaosworld.block.entity.processing;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ParallelProcessState {
    private ResourceLocation recipeId;
    private long energyBuffer;
    private long[] itemBuffers = new long[0];
    private long[] fluidBuffers = new long[0];
    private long[] chemicalBuffers = new long[0];
    private int progress;
    private boolean patternPushed;

    public ResourceLocation getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public boolean isActive() {
        return this.recipeId != null;
    }

    public void clear() {
        this.recipeId = null;
        this.energyBuffer = 0L;
        this.itemBuffers = new long[0];
        this.fluidBuffers = new long[0];
        this.chemicalBuffers = new long[0];
        this.progress = 0;
        this.patternPushed = false;
    }

    public void resizeBuffers(int itemSize, int fluidSize, int chemicalSize) {
        if (this.itemBuffers.length != itemSize) {
            this.itemBuffers = new long[itemSize];
        }
        if (this.fluidBuffers.length != fluidSize) {
            this.fluidBuffers = new long[fluidSize];
        }
        if (this.chemicalBuffers.length != chemicalSize) {
            this.chemicalBuffers = new long[chemicalSize];
        }
    }

    public void clearBuffers() {
        java.util.Arrays.fill(this.itemBuffers, 0L);
        java.util.Arrays.fill(this.fluidBuffers, 0L);
        java.util.Arrays.fill(this.chemicalBuffers, 0L);
    }

    public long getEnergyBuffer() {
        return energyBuffer;
    }

    public void setEnergyBuffer(long energyBuffer) {
        this.energyBuffer = energyBuffer;
    }

    public long[] getItemBuffers() {
        return itemBuffers;
    }

    public long[] getFluidBuffers() {
        return fluidBuffers;
    }

    public long[] getChemicalBuffers() {
        return chemicalBuffers;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isPatternPushed() {
        return patternPushed;
    }

    public void setPatternPushed(boolean patternPushed) {
        this.patternPushed = patternPushed;
    }

    public boolean hasBufferedWork() {
        if (this.energyBuffer > 0L || this.progress > 0) {
            return true;
        }

        for (long amount : this.itemBuffers) {
            if (amount > 0L) {
                return true;
            }
        }

        for (long amount : this.fluidBuffers) {
            if (amount > 0L) {
                return true;
            }
        }

        for (long amount : this.chemicalBuffers) {
            if (amount > 0L) {
                return true;
            }
        }

        return false;
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        if (this.recipeId != null) {
            tag.putString("recipeId", this.recipeId.toString());
        }
        tag.putLong("energyBuffer", this.energyBuffer);
        tag.putLongArray("itemBuffers", this.itemBuffers);
        tag.putLongArray("fluidBuffers", this.fluidBuffers);
        tag.putLongArray("chemicalBuffers", this.chemicalBuffers);
        tag.putInt("progress", this.progress);
        tag.putBoolean("patternPushed", this.patternPushed);
        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        this.recipeId = tag.contains("recipeId") ? ResourceLocation.parse(tag.getString("recipeId")) : null;
        this.energyBuffer = tag.getLong("energyBuffer");
        this.itemBuffers = tag.getLongArray("itemBuffers");
        this.fluidBuffers = tag.getLongArray("fluidBuffers");
        this.chemicalBuffers = tag.getLongArray("chemicalBuffers");
        this.progress = tag.getInt("progress");
        this.patternPushed = tag.getBoolean("patternPushed");
    }
}

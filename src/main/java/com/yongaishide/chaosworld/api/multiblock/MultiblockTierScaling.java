package com.yongaishide.chaosworld.api.multiblock;

public final class MultiblockTierScaling {
    private MultiblockTierScaling() {
    }

    public static boolean canRunRecipe(int machineTier, int recipeTier) {
        return machineTier >= recipeTier;
    }

    public static int adjustedTime(int baseTime, int machineTier, int recipeTier) {
        int delta = Math.max(0, machineTier - recipeTier);
        return Math.max(1, (int) Math.ceil(baseTime / Math.pow(2, delta)));
    }

    public static long adjustedEnergy(long baseEnergy, int machineTier, int recipeTier) {
        int delta = Math.max(0, machineTier - recipeTier);
        return Math.max(1L, (long) Math.ceil(baseEnergy * Math.pow(0.75D, delta)));
    }
}

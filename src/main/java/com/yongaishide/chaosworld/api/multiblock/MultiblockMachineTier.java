package com.yongaishide.chaosworld.api.multiblock;

public enum MultiblockMachineTier {
    MK1(1),
    MK2(2),
    MK3(3);

    private final int level;

    MultiblockMachineTier(int level) {
        this.level = level;
    }

    public int level() {
        return this.level;
    }

    public static MultiblockMachineTier fromLevel(int level) {
        return switch (Math.max(1, Math.min(3, level))) {
            case 2 -> MK2;
            case 3 -> MK3;
            default -> MK1;
        };
    }
}

package com.yongaishide.chaosworld.recipe;

import com.mojang.serialization.Codec;

public enum UniversalMultiblockMachineKind {
    QMF("qmf"),
    QUANTUM_SLICER("quantum_slicer"),
    QUANTUM_PROCESSOR_ASSEMBLER("quantum_processing_factory"),
    QUANTUM_CRYOFORGE("quantum_cryoforge");

    public static final Codec<UniversalMultiblockMachineKind> CODEC =
            Codec.STRING.xmap(UniversalMultiblockMachineKind::fromSerializedName, UniversalMultiblockMachineKind::serializedName);

    private final String serializedName;

    UniversalMultiblockMachineKind(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static UniversalMultiblockMachineKind fromSerializedName(String name) {
        for (var value : values()) {
            if (value.serializedName.equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown multiblock machine kind: " + name);
    }
}

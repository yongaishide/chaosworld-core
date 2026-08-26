package com.yongaishide.chaosworld.menu;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;

/**
 * Custom slot semantics for UFO mod machines.
 * These extend AE2's slot system to support non-standard slot layouts.
 */
public final class UFOSlotSemantics {

    private UFOSlotSemantics() {}

    /**
     * Second output slot for machines that need non-standard vertical spacing
     * between output slots (e.g., the Dimensional Matter Assembler).
     */
    public static final SlotSemantic MACHINE_OUTPUT_2 = SlotSemantics.register("UFO_MACHINE_OUTPUT_2", false);
}

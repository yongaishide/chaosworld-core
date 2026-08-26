package com.yongaishide.chaosworld.api.multiblock;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Contract for any block that participates in a multiblock structure as a
 * "part" (i.e. casing, hatch, matrix—anything that is NOT the controller).
 * <p>
 * Parts are aware of their controller and can be linked/unlinked dynamically
 * when the structure forms or breaks.
 */
public interface IMultiblockPart {

    /**
     * Links this part to a specific controller position.
     *
     * @param controllerPos the position of the {@link IMultiblockController}
     */
    void linkToController(BlockPos controllerPos);

    /**
     * Unlinks this part from its current controller, if any.
     * Called when the structure is disassembled or the part is broken.
     */
    void unlinkFromController();

    /**
     * @return the position of the linked controller, or {@code null} if unlinked.
     */
    @Nullable
    BlockPos getControllerPos();

    /**
     * @return {@code true} if this part is currently linked to a valid controller.
     */
    default boolean isLinked() {
        return getControllerPos() != null;
    }
}

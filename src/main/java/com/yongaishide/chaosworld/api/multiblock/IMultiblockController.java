package com.yongaishide.chaosworld.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Contract for the central controller block of any multiblock structure.
 * <p>
 * The controller is the "brain" of the multiblock; it owns the scanning logic,
 * tracks which parts belong to the structure, and exposes the assembled state.
 * <p>
 * <b>Implementation note:</b> concrete controllers should call
 * {@link #scanStructure(Level)} periodically (e.g. on neighbour-changed or via
 * a tick throttle) to keep the assembled state up-to-date.
 */
public interface IMultiblockController {

    /**
     * @return {@code true} if the multiblock structure is currently valid and assembled.
     */
    boolean isAssembled();

    /**
     * Triggers a full re-scan of the surrounding blocks to determine whether the
     * multiblock pattern is satisfied.
     *
     * @param level the world to scan in
     */
    void scanStructure(Level level);

    /**
     * Called by a neighbouring {@link IMultiblockPart} to register itself with this controller.
     *
     * @param partPos the world-position of the part
     */
    void addPart(BlockPos partPos);

    /**
     * Called when a part is removed from the structure (broken, exploded, etc.).
     *
     * @param partPos the world-position of the removed part
     */
    void removePart(BlockPos partPos);

    /**
     * @return an unmodifiable snapshot of all currently registered part positions.
     */
    List<BlockPos> getParts();

    /**
     * @return the controller's own position in the world.
     */
    BlockPos getControllerPos();
}

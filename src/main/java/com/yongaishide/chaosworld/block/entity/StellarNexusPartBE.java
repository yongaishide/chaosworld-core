package com.yongaishide.chaosworld.block.entity;

import com.yongaishide.chaosworld.api.multiblock.IMultiblockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Block Entity for any Stellar Nexus structural part (casing, hatch, generator, etc.).
 * <p>
 * Implements {@link IMultiblockPart} to track its link to a controller.
 * This is a lightweight BE — it stores only the controller position.
 */
public class StellarNexusPartBE extends BlockEntity implements IMultiblockPart {

    @Nullable
    private BlockPos controllerPos = null;

    public StellarNexusPartBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ──────────────────── IMultiblockPart ────────────────────

    @Override
    public void linkToController(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        this.setChanged();
    }

    @Override
    public void unlinkFromController() {
        this.controllerPos = null;
        this.setChanged();
    }

    @Nullable
    @Override
    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    // ──────────────────── NBT Persistence ────────────────────

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.controllerPos != null) {
            tag.put("controllerPos", NbtUtils.writeBlockPos(this.controllerPos));
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("controllerPos")) {
            NbtUtils.readBlockPos(tag.getCompound("controllerPos"), "").ifPresent(pos -> this.controllerPos = pos);
        } else {
            this.controllerPos = null;
        }
    }
}

package com.yongaishide.chaosworld.api.multiblock;

import net.minecraft.core.BlockPos;

public interface IEntropicMachineController extends IMultiblockController {
    boolean canProxyInteract(BlockPos pos);

    boolean isNetworkConnected();

    void markStructureDirty();
}

package com.yongaishide.chaosworld.screen;

import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.block.entity.QuantumCryoforgeControllerBE;
import com.yongaishide.chaosworld.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class QuantumCryoforgeControllerMenu extends AbstractUniversalMultiblockControllerMenu<QuantumCryoforgeControllerBE> {

    public QuantumCryoforgeControllerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public QuantumCryoforgeControllerMenu(int id, Inventory inv, BlockEntity entity) {
        super(
                ModMenus.QUANTUM_CRYOFORGE_CONTROLLER_MENU.get(),
                id,
                inv,
                (QuantumCryoforgeControllerBE) entity,
                ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos()));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.levelAccess, player, getValidBlock());
    }

    @Override
    protected Block getValidBlock() {
        return MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get();
    }
}

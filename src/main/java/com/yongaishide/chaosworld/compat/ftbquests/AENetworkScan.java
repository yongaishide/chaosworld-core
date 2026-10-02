package com.yongaishide.chaosworld.compat.ftbquests;

import appeng.api.networking.IGrid;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.items.tools.powered.WirelessTerminalItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Predicate;

public final class AENetworkScan {
    private AENetworkScan() {
    }

    public static long count(ServerPlayer player, Predicate<AEKey> filter, long limit) {
        Set<IGrid> grids = Collections.newSetFromMap(new IdentityHashMap<>());

        for (ItemStack stack : player.getInventory().items) {
            addTerminalGrid(stack, player, grids);
        }
        for (ItemStack stack : player.getInventory().offhand) {
            addTerminalGrid(stack, player, grids);
        }

        long total = 0L;
        for (IGrid grid : grids) {
            var storageService = grid.getStorageService();
            if (storageService != null) {
                total += countInStorage(storageService.getInventory(), filter, limit - total);
                if (total >= limit) {
                    break;
                }
            }
        }
        return total;
    }

    private static void addTerminalGrid(ItemStack stack, ServerPlayer player, Set<IGrid> grids) {
        if (FTBQuestsIntegration.hasDetectionCard(stack)) {
            WirelessTerminalItem terminal = (WirelessTerminalItem) stack.getItem();
            IGrid grid = terminal.getLinkedGrid(stack, player.level(), null);
            if (grid != null) {
                grids.add(grid);
            }
        }
    }

    private static long countInStorage(MEStorage storage, Predicate<AEKey> filter, long limit) {
        if (storage == null || limit <= 0L) {
            return 0L;
        }

        long total = 0L;
        KeyCounter counter = storage.getAvailableStacks();
        for (var entry : counter) {
            if (filter.test(entry.getKey())) {
                total += entry.getLongValue();
                if (total >= limit) {
                    return limit;
                }
            }
        }
        return total;
    }
}

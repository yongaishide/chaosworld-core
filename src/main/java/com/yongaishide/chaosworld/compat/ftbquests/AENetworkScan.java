package com.yongaishide.chaosworld.compat.ftbquests;

import appeng.api.networking.IGrid;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class AENetworkScan {
    /**
     * 一次扫描结果复用的游戏刻数。
     *
     * <p>任务本身是每 20 tick（1 秒）检查一次，而同一玩家在同一秒内往往有多个任务在跑；
     * 缓存快照后，多个任务共用一次「背包扫描 + 网络枚举」，不再各自把整张 ME 网络点数一遍。</p>
     */
    private static final long CACHE_WINDOW_TICKS = 20L;

    /** 按玩家缓存，弱引用键：玩家离线并被回收后条目自动消失，不会跨存档泄漏。 */
    private static final Map<ServerPlayer, Snapshot> CACHE = new WeakHashMap<>();

    /**
     * 物品类 → {@code getLinkedGrid(ItemStack, Level, Consumer)} 方法。
     *
     * <p>用方法查找而不是 {@code instanceof WirelessTerminalItem}，
     * 这样附属模组的无线终端（无线合成终端等，只要继承自 AE2 的无线终端即可）也能被识别。</p>
     */
    private static final ClassValue<Optional<Method>> LINKED_GRID = new ClassValue<>() {
        @Override
        protected Optional<Method> computeValue(Class<?> type) {
            try {
                Method method = type.getMethod("getLinkedGrid", ItemStack.class, Level.class, Consumer.class);
                method.setAccessible(true);
                return Optional.of(method);
            } catch (Throwable ignored) {
                return Optional.empty();
            }
        }
    };

    private AENetworkScan() {
    }

    /** 该物品类能否链接到 ME 网络（即是不是一个无线终端）。 */
    public static boolean isGridLinkable(Class<?> itemClass) {
        return itemClass != null && LINKED_GRID.get(itemClass).isPresent();
    }

    public static long count(ServerPlayer player, Predicate<AEKey> filter, long limit) {
        if (player == null || filter == null || limit <= 0L) {
            return 0L;
        }

        KeyCounter stacks = snapshot(player);
        long total = 0L;
        for (var entry : stacks) {
            if (filter.test(entry.getKey())) {
                total += entry.getLongValue();
                if (total >= limit) {
                    return limit;
                }
            }
        }
        return total;
    }

    private static KeyCounter snapshot(ServerPlayer player) {
        Level level = player.level();
        long now = level.getGameTime();

        synchronized (CACHE) {
            Snapshot cached = CACHE.get(player);
            if (cached != null && cached.level == level
                    && now >= cached.tick && now - cached.tick < CACHE_WINDOW_TICKS) {
                return cached.stacks;
            }

            KeyCounter stacks = new KeyCounter();
            Set<IGrid> grids = Collections.newSetFromMap(new IdentityHashMap<>());
            for (ItemStack stack : player.getInventory().items) {
                collect(stack, player, grids, stacks);
            }
            for (ItemStack stack : player.getInventory().offhand) {
                collect(stack, player, grids, stacks);
            }

            CACHE.put(player, new Snapshot(level, now, stacks));
            return stacks;
        }
    }

    private static void collect(ItemStack stack, ServerPlayer player, Set<IGrid> grids, KeyCounter stacks) {
        if (stack.isEmpty() || !FTBQuestsIntegration.hasDetectionCard(stack)) {
            return;
        }
        IGrid grid = linkedGrid(stack, player);
        if (grid == null || !grids.add(grid)) {
            return;
        }
        var storageService = grid.getStorageService();
        if (storageService == null) {
            return;
        }
        MEStorage storage = storageService.getInventory();
        if (storage == null) {
            return;
        }
        stacks.addAll(storage.getAvailableStacks());
    }

    private static IGrid linkedGrid(ItemStack stack, ServerPlayer player) {
        Optional<Method> method = LINKED_GRID.get(stack.getItem().getClass());
        if (method.isEmpty()) {
            return null;
        }
        try {
            Object grid = method.get().invoke(stack.getItem(), stack, player.level(), null);
            return grid instanceof IGrid iGrid ? iGrid : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private record Snapshot(Level level, long tick, KeyCounter stacks) {
    }
}

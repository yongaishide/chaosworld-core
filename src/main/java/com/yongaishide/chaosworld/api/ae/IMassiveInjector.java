package com.yongaishide.chaosworld.api.ae;

import appeng.api.stacks.AEKey;
import net.minecraft.world.level.Level;

/**
 * Contract for a block entity capable of injecting massive quantities of items
 * or fluids directly into an AE2 ME network, bypassing the default ItemStack
 * size limits.
 * <p>
 * Implementations are expected to hold a reference to an {@link appeng.api.networking.IGridNode}
 * and use the ME network's storage services to push amounts expressed as {@code long}.
 * <p>
 * <b>Design note:</b> We use {@code long} (max ~9.2 quintillion) instead of
 * {@code BigInteger} because AE2 1.21.1 natively uses {@code long} for all
 * quantity operations, and our target range (2M–10M) fits comfortably.
 */
public interface IMassiveInjector {

    /**
     * Injects a large quantity of a keyed resource (item or fluid) into the
     * connected AE2 network.
     *
     * @param what   the AE2 key representing the item / fluid to inject
     * @param amount the quantity to inject (may be millions)
     * @param level  the server level
     * @return the amount that was actually accepted by the network
     */
    long injectIntoNetwork(AEKey what, long amount, Level level);

    /**
     * @return {@code true} if this injector is currently connected to a powered
     *         and online ME network.
     */
    boolean isNetworkReady();
}

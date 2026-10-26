/*
 * Derived from Applied Botanics 1.6.0-alpha.3
 *   https://github.com/ramidzkh/Applied-Botanics
 * Copyright (C) ramidzkh and contributors, LGPL-3.0-or-later
 * Modified for Chaos World Core by chaosworld on 2026-10-26.
 * See THIRD_PARTY_NOTICES.md for details.
 */
package com.yongaishide.chaosworld.botania;

import com.google.common.primitives.Ints;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import vazkii.botania.api.neoforge.BotaniaNeoForgeCapabilities;
import vazkii.botania.api.mana.ManaReceiver;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;

@SuppressWarnings("UnstableApiUsage")
public class ManaStorageImportStrategy implements StackImportStrategy {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final BlockCapabilityCache<ManaReceiver, Direction> apiCache;

    public ManaStorageImportStrategy(ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.apiCache = BlockCapabilityCache.create(BotaniaNeoForgeCapabilities.getBlockApiLookupById(ManaReceiver.LOOKUP),
                level, fromPos, fromSide);
    }

    @Override
    public boolean transfer(StackTransferContext context) {
        if (!context.isKeyTypeEnabled(ManaKeyType.TYPE)) {
            return false;
        }

        if (context.isInFilter(ManaKey.KEY) == context.isInverted()) {
            return false;
        }

        var receiver = SafeMana.conv(apiCache.getCapability());

        if (receiver == null) {
            return false;
        }

        var amountPerOperation = ManaKeyType.TYPE.getAmountPerOperation();
        long maxTransfer = context.getOperationsRemaining()
                * (long) amountPerOperation;

        if (maxTransfer <= 0) {
            return false;
        }

        // see how much we could take
        var inventory = context.getInternalStorage().getInventory();
        var simulate = inventory.insert(ManaKey.KEY, maxTransfer, Actionable.SIMULATE, context.getActionSource());

        // take up to that much
        var extracted = receiver.extract(Ints.saturatedCast(simulate), Actionable.MODULATE);

        // insert to network
        var inserted = inventory.insert(ManaKey.KEY, extracted, Actionable.MODULATE, context.getActionSource());

        if (inserted < extracted) {
            // try to give back overflow
            var difference = extracted - inserted;
            difference -= receiver.insert(Ints.saturatedCast(difference), Actionable.MODULATE);

            if (difference != 0) {
                LOGGER.warn("Extracted {} Mana from adjacent storage but the ME network only accepted {}; rest was voided",
                        extracted, inserted);
            }
        }

        context.reduceOperationsRemaining((inserted + amountPerOperation - 1) / amountPerOperation);
        return false;
    }
}

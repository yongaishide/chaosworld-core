/*
 * Derived from Applied Botanics 1.6.0-alpha.3
 *   https://github.com/ramidzkh/Applied-Botanics
 * Copyright (C) ramidzkh and contributors, LGPL-3.0-or-later
 * Modified for Chaos World Core by chaosworld on 2026-10-26.
 * See THIRD_PARTY_NOTICES.md for details.
 */
package com.yongaishide.chaosworld.botania;

import vazkii.botania.api.mana.ManaCollector;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.spark.ManaSparkAttachable;

public class ManaHelper {

    public static int getCapacity(ManaReceiver receiver) {
        if (receiver instanceof ManaPool pool) {
            return pool.getMaxMana();
        } else if (receiver instanceof ManaCollector collector) {
            return collector.getMaxMana();
        } else if (receiver instanceof ManaSparkAttachable sparkAttachable) {
            return receiver.getCurrentMana() + sparkAttachable.getAvailableSpaceForMana();
        } else if (!receiver.isFull()) {
            return receiver.getCurrentMana() + 1000;
        }

        return 0;
    }
}

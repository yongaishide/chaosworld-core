package com.yongaishide.chaosworld.compat.ftbquests.client;

import dev.ftb.mods.ftblibrary.config.LongConfig;

import java.util.function.Consumer;

public class DecimalLongConfig extends LongConfig {
    public DecimalLongConfig(long min, long max) {
        super(min, max);
    }

    @Override
    public boolean parse(Consumer<Long> consumer, String text) {
        if (text == null || text.isEmpty()) {
            return okValue(consumer, 0L);
        }

        try {
            long value = Long.parseLong(text.trim().replace(",", "").replace("_", ""));
            if (value >= min.longValue() && value <= max.longValue()) {
                return okValue(consumer, value);
            }
        } catch (NumberFormatException ignored) {
        }
        return false;
    }
}

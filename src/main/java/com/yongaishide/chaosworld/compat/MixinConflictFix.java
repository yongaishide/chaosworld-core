package com.yongaishide.chaosworld.compat;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.Config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class MixinConflictFix {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Set<String> DISABLED_MIXINS = Set.of(
            "com.raishxn.ufo.mixin.MixinCPUSelectionList"
    );

    private static boolean done;
    private static int attempts;

    private MixinConflictFix() {
    }

    public static void apply() {
        if (done) {
            return;
        }

        if (!ModList.get().isLoaded("data_energistics") || !ModList.get().isLoaded("ufo")) {
            done = true;
            return;
        }

        if (++attempts > 2400) {
            done = true;
            LOGGER.warn("Mixin conflict fix gave up after {} attempts", attempts);
            return;
        }

        boolean inspected = false;
        int removed = 0;
        try {
            for (IMixinConfig mixinConfig : collectConfigs()) {
                Set<String> targets = mixinConfig.getTargets();
                if (targets.isEmpty()) {
                    continue;
                }
                inspected = true;

                Method getMixinsFor = mixinConfig.getClass().getMethod("getMixinsFor", String.class);
                getMixinsFor.setAccessible(true);
                for (String target : new ArrayList<>(targets)) {
                    Object result = getMixinsFor.invoke(mixinConfig, target);
                    if (!(result instanceof List<?> mixins)) {
                        continue;
                    }
                    for (Iterator<?> iterator = mixins.iterator(); iterator.hasNext(); ) {
                        Object next = iterator.next();
                        if (next instanceof IMixinInfo info && DISABLED_MIXINS.contains(info.getClassName())) {
                            iterator.remove();
                            removed++;
                            LOGGER.info("Disabled conflicting mixin {} (config {}, target {})",
                                    info.getClassName(), mixinConfig.getName(), target);
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
            LOGGER.warn("Mixin conflict fix failed", throwable);
            done = true;
            return;
        }

        if (inspected) {
            done = true;
            LOGGER.info("Mixin conflict fix finished (removed {})", removed);
        }
    }

    private static List<IMixinConfig> collectConfigs() {
        List<IMixinConfig> configs = new ArrayList<>();

        for (Config handle : Mixins.getConfigs()) {
            configs.add(handle.getConfig());
        }
        int globalCount = configs.size();

        Object transformer = null;
        Object processor = null;
        int pendingCount = -1;
        try {
            transformer = MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
            if (transformer != null) {
                processor = readField(transformer, "processor");
                if (processor != null) {
                    Object prepared = readField(processor, "configs");
                    if (prepared instanceof List<?> list) {
                        pendingCount = list.size();
                        for (Object entry : list) {
                            if (entry instanceof IMixinConfig mixinConfig) {
                                configs.add(mixinConfig);
                            }
                        }
                    }
                    Object pending = readField(processor, "pendingConfigs");
                    if (pending instanceof List<?> list) {
                        for (Object entry : list) {
                            if (entry instanceof IMixinConfig mixinConfig && !configs.contains(mixinConfig)) {
                                configs.add(mixinConfig);
                            }
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
            LOGGER.warn("Mixin conflict fix: reflection failed (transformer={}, processor={})",
                    transformer, processor, throwable);
        }

        if (attempts <= 1) {
            LOGGER.info("Mixin conflict fix: global={}, transformer={}, pending={}, total={}",
                    globalCount, transformer != null, pendingCount, configs.size());
        }

        return configs;
    }

    private static Object readField(Object target, String name) throws Exception {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        return null;
    }
}

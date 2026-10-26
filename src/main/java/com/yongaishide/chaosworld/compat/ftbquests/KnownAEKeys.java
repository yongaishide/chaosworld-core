package com.yongaishide.chaosworld.compat.ftbquests;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.moakiee.ae2lt.me.key.LightningKey;
import com.raishxn.ufo.compat.mekanism.UfoMekanismKey;
import com.raishxn.ufo.compat.mekanism.UfoMekanismKeyType;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public final class KnownAEKeys {
    private static final String DATA_ENERGISTICS_KEYS = "com.fish_dan_.data_energistics.ae2.DEAE2Keys";
    private static final String ASTRAL_LUMEN_CLASS = "hellfirepvp.astralsorcery.common.lumen.Lumen";
    private static final String ASTRAL_REGISTRIES_CLASS = "hellfirepvp.astralsorcery.common.lib.RegistriesAS";
    private static final String APPLIED_ASTRAL_KEY_CLASS = "com.appliedastralsorcery.lumen.LumenKey";
    private static final String APPLIED_PNEUMATICS_AIR_KEY_CLASS = "com.wintercogs.appliedpneumatics.common.me.keys.AirKey";

    /**
     * 全局共享的已知 key 池：所有存档、所有世界共用同一份缓存，这是刻意设计。
     *
     * <p>不要按「跨存档泄漏」把它改成按存档隔离 —— 合并成一份正是为了跨档不漏 key。</p>
     */
    private static List<AEKey> customKeys;
    private static List<AEKey> chemicalKeys;

    private KnownAEKeys() {
    }

    public static synchronized List<AEKey> all() {
        if (customKeys == null) {
            List<AEKey> keys = new ArrayList<>();
            collectLightningKeys(keys);
            collectFluxKeys(keys);
            collectDataEnergisticsKeys(keys);
            collectManaKeys(keys);
            collectLumenKeys(keys);
            collectAirKeys(keys);
            customKeys = List.copyOf(keys);
        }
        return customKeys;
    }

    public static synchronized List<AEKey> chemicals() {
        if (chemicalKeys == null) {
            List<AEKey> keys = new ArrayList<>();
            for (Chemical chemical : MekanismAPI.CHEMICAL_REGISTRY) {
                if (chemical.isEmptyType()) {
                    continue;
                }
                keys.add(UfoMekanismKey.of(new ChemicalStack(chemical, 1)));
            }
            chemicalKeys = List.copyOf(keys);
        }
        return chemicalKeys;
    }

    public static ResourceLocation chemicalTypeId() {
        return UfoMekanismKeyType.TYPE.getId();
    }

    public static synchronized void register(AEKey key) {
        if (key == null) {
            return;
        }
        all();
        for (AEKey existing : customKeys) {
            if (existing.equals(key)) {
                return;
            }
        }
        List<AEKey> keys = new ArrayList<>(customKeys);
        keys.add(key);
        customKeys = List.copyOf(keys);
    }

    public static AEKey resolve(AEKeyRef ref) {
        if (ref == null || ref.keyType() == null || ref.resource() == null) {
            return null;
        }

        if (ref.keyType().equals(AEKeyType.items().getId())) {
            Item item = BuiltInRegistries.ITEM.get(ref.resource());
            if (item == null || item == Items.AIR) {
                return null;
            }
            ItemStack stack = new ItemStack(item);
            if (ref.components() != null && !ref.components().isEmpty()) {
                stack.applyComponents(ref.components());
            }
            return AEItemKey.of(stack);
        }
        if (ref.keyType().equals(AEKeyType.fluids().getId())) {
            Fluid fluid = BuiltInRegistries.FLUID.get(ref.resource());
            return fluid == null || fluid == Fluids.EMPTY ? null : AEFluidKey.of(fluid);
        }

        if (ref.keyType().equals(chemicalTypeId())) {
            for (AEKey key : chemicals()) {
                if (key.getId().equals(ref.resource())) {
                    return key;
                }
            }
            return null;
        }

        for (AEKey key : all()) {
            if (key.getType().getId().equals(ref.keyType()) && key.getId().equals(ref.resource())) {
                return key;
            }
        }
        return null;
    }

    private static void collectLightningKeys(List<AEKey> out) {
        out.add(LightningKey.HIGH_VOLTAGE);
        out.add(LightningKey.EXTREME_HIGH_VOLTAGE);
    }

    private static void collectFluxKeys(List<AEKey> out) {
        try {
            Class<?> keyClass = Class.forName("com.glodblock.github.appflux.common.me.key.FluxKey");
            Class<?> energyClass = Class.forName("com.glodblock.github.appflux.common.me.key.type.EnergyType");
            Object fe = energyClass.getField("FE").get(null);
            Object key = keyClass.getMethod("of", energyClass).invoke(null, fe);
            if (key instanceof AEKey aeKey) {
                out.add(aeKey);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void collectDataEnergisticsKeys(List<AEKey> out) {
        try {
            Class<?> clazz = Class.forName(DATA_ENERGISTICS_KEYS);
            Object result = clazz.getMethod("keys").invoke(null);
            if (result instanceof List<?> keys) {
                for (Object key : keys) {
                    if (key instanceof AEKey aeKey) {
                        out.add(aeKey);
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * Botania 的 mana 键（chaosworld_core:mana）。用反射取，保证没装 Botania 时这个兼容层依然能加载。
     */
    private static void collectManaKeys(List<AEKey> out) {
        if (!net.neoforged.fml.ModList.get().isLoaded("botania")) {
            return;
        }
        try {
            Class<?> keyClass = Class.forName("com.yongaishide.chaosworld.botania.ManaKey");
            Object key = keyClass.getField("KEY").get(null);
            if (key instanceof AEKey aeKey) {
                out.add(aeKey);
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * 应用星辉（appliedas）的流明（lumen）键。
     *
     * <p>流明键没有固定的静态实例，只能拿星辉本体的流明注册表（RegistriesAS.REGISTRY_LUMEN）逐个造：
     * 每个已注册的流明对应一个 {@code appliedas:lumen} 类型的 key，id 就是流明的注册名。
     * 空流明（LumenAS.NONE）会在 LumenKey 构造时抛异常，这里逐条 try/catch 跳过。</p>
     *
     * <p>全部走反射，没装应用星辉时不影响加载。</p>
     */
    private static void collectLumenKeys(List<AEKey> out) {
        if (!net.neoforged.fml.ModList.get().isLoaded("appliedas")
                || !net.neoforged.fml.ModList.get().isLoaded("astralsorcery")) {
            return;
        }
        try {
            Class<?> lumenClass = Class.forName(ASTRAL_LUMEN_CLASS);
            Class<?> keyClass = Class.forName(APPLIED_ASTRAL_KEY_CLASS);
            Object registry = Class.forName(ASTRAL_REGISTRIES_CLASS).getField("REGISTRY_LUMEN").get(null);
            if (!(registry instanceof Registry<?> lumenRegistry)) {
                return;
            }
            for (ResourceLocation id : lumenRegistry.keySet()) {
                Object lumen = lumenRegistry.get(id);
                if (lumen == null) {
                    continue;
                }
                try {
                    Object key = keyClass.getMethod("of", lumenClass).invoke(null, lumen);
                    if (key instanceof AEKey aeKey) {
                        out.add(aeKey);
                    }
                } catch (Throwable ignored) {
                    // 空流明之类无法建键的条目直接跳过
                }
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * 应用气动的压缩空气键（{@code appliedpneumatics:air}）。
     *
     * <p>只有空气一种资源，模组里是个单例 {@code AirKey.INSTANCE}，直接取字段即可。</p>
     */
    private static void collectAirKeys(List<AEKey> out) {
        if (!net.neoforged.fml.ModList.get().isLoaded("appliedpneumatics")) {
            return;
        }
        try {
            Object key = Class.forName(APPLIED_PNEUMATICS_AIR_KEY_CLASS).getField("INSTANCE").get(null);
            if (key instanceof AEKey aeKey) {
                out.add(aeKey);
            }
        } catch (Throwable ignored) {
        }
    }
}

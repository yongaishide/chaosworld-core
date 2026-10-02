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
        all();
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
}

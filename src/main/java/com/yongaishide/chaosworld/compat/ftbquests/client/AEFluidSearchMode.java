package com.yongaishide.chaosworld.compat.ftbquests.client;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import com.yongaishide.chaosworld.compat.ftbquests.AEKeyRef;
import dev.ftb.mods.ftblibrary.config.ui.resource.ResourceSearchMode;
import dev.ftb.mods.ftblibrary.config.ui.resource.SelectableResource;
import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AEFluidSearchMode implements ResourceSearchMode<AEKeyRef> {
    private static final Icon ICON = Icon.getIcon("minecraft:item/water_bucket");

    private List<SelectableResource<AEKeyRef>> cache;

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("chaosworld_core.resource.fluids");
    }

    @Override
    public synchronized Collection<? extends SelectableResource<AEKeyRef>> getAllResources() {
        if (cache == null) {
            List<SelectableResource<AEKeyRef>> list = new ArrayList<>();
            Set<AEKeyRef> seen = new HashSet<>();
            for (Fluid fluid : BuiltInRegistries.FLUID) {
                if (fluid != Fluids.EMPTY && fluid.isSource(fluid.defaultFluidState())) {
                    add(list, seen, AEFluidKey.of(fluid));
                }
            }
            cache = list;
        }
        return cache;
    }

    @Override
    public synchronized void clearCache() {
        cache = null;
    }

    private static void add(List<SelectableResource<AEKeyRef>> list, Set<AEKeyRef> seen, AEKey key) {
        AEKeyRef ref = AEKeyRef.of(key);
        if (seen.add(ref)) {
            list.add(new AEKeySelectableResource(key, ref));
        }
    }
}

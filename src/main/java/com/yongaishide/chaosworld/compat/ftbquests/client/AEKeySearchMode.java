package com.yongaishide.chaosworld.compat.ftbquests.client;

import appeng.api.stacks.AEKey;
import com.yongaishide.chaosworld.compat.ftbquests.AEKeyRef;
import com.yongaishide.chaosworld.compat.ftbquests.KnownAEKeys;
import dev.ftb.mods.ftblibrary.config.ui.resource.ResourceSearchMode;
import dev.ftb.mods.ftblibrary.config.ui.resource.SelectableResource;
import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AEKeySearchMode implements ResourceSearchMode<AEKeyRef> {
    private static final Icon ICON = Icon.getIcon("ae2:item/wireless_terminal");

    private List<SelectableResource<AEKeyRef>> cache;

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("chaosworld_core.ae_resource.search_mode");
    }

    @Override
    public synchronized Collection<? extends SelectableResource<AEKeyRef>> getAllResources() {
        if (cache == null) {
            List<SelectableResource<AEKeyRef>> list = new ArrayList<>();
            Set<AEKeyRef> seen = new HashSet<>();

            for (AEKey key : KnownAEKeys.all()) {
                AEKeyRef ref = AEKeyRef.of(key);
                if (seen.add(ref)) {
                    list.add(new AEKeySelectableResource(key, ref));
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
}

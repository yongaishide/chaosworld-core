package com.yongaishide.chaosworld.compat.ftbquests.client;

import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.AEKey;
import com.yongaishide.chaosworld.compat.ftbquests.AEKeyRef;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ResourceConfigValue;
import dev.ftb.mods.ftblibrary.config.ui.resource.ResourceSelectorScreen;
import dev.ftb.mods.ftblibrary.config.ui.resource.SelectableResource;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.util.SearchTerms;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class AbstractAEKeySelectScreen extends ResourceSelectorScreen<AEKeyRef> {
    protected AbstractAEKeySelectScreen(ResourceConfigValue<AEKeyRef> config, ConfigCallback callback) {
        super(config, callback);
    }

    @Override
    protected ResourceSelectorScreen<AEKeyRef>.ResourceButton makeResourceButton(Panel panel, SelectableResource<AEKeyRef> resource) {
        return new AEKeyButton(panel, resource == null ? AEKeySelectableResource.EMPTY : resource);
    }

    private class AEKeyButton extends ResourceButton {
        private AEKeyButton(Panel panel, SelectableResource<AEKeyRef> resource) {
            super(panel, resource);
        }

        @Override
        public boolean shouldAdd(SearchTerms searchTerms) {
            AEKey key = selectable instanceof AEKeySelectableResource selectableResource ? selectableResource.key() : null;
            if (key == null) {
                return true;
            }
            return searchTerms.match(key.getId(), key.getDisplayName().getString(), id -> false);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            AEKey key = selectable instanceof AEKeySelectableResource selectableResource ? selectableResource.key() : null;
            if (key == null) {
                return;
            }

            List<Component> tooltip = AEKeyRendering.get(key.getType()) != null
                    ? AEKeyRendering.getTooltip(key)
                    : List.of(key.getDisplayName());
            if (tooltip.isEmpty()) {
                list.add(key.getDisplayName());
            } else {
                tooltip.forEach(list::add);
            }
        }
    }
}

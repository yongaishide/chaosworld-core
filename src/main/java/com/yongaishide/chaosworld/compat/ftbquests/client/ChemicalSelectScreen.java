package com.yongaishide.chaosworld.compat.ftbquests.client;

import com.yongaishide.chaosworld.compat.ftbquests.AEKeyRef;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ResourceConfigValue;
import dev.ftb.mods.ftblibrary.config.ui.resource.ResourceSearchMode;
import dev.ftb.mods.ftblibrary.config.ui.resource.SearchModeIndex;

public class ChemicalSelectScreen extends AbstractAEKeySelectScreen {
    private static final SearchModeIndex<ResourceSearchMode<AEKeyRef>> MODES = new SearchModeIndex<>();

    static {
        MODES.appendMode(new ChemicalSearchMode());
    }

    public ChemicalSelectScreen(ResourceConfigValue<AEKeyRef> config, ConfigCallback callback) {
        super(config, callback);
    }

    @Override
    protected SearchModeIndex<ResourceSearchMode<AEKeyRef>> getSearchModeIndex() {
        return MODES;
    }
}

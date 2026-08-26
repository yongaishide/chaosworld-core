package com.yongaishide.chaosworld.block.entity;

import java.util.List;

public interface IUniversalMultiblockController {
    boolean isGuiAssembled();

    boolean isGuiRunning();

    int getGuiProgress();

    int getGuiMaxProgress();

    int getGuiTemperature();

    int getGuiMaxTemperature();

    default int getGuiOverloadTimer() {
        return -1;
    }

    int getGuiMachineTier();

    long getGuiStoredEnergy();

    long getGuiMaxEnergy();

    int getGuiActiveParallels();

    int getGuiMaxParallels();

    boolean isGuiSafeMode();

    boolean isGuiOverclocked();

    default boolean isGuiAeConnected() {
        return false;
    }

    void toggleSafeMode();

    void toggleOverclock();

    List<UniversalDisplayedRecipe> getDisplayedRecipes();
}

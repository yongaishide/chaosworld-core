package com.yongaishide.chaosworld.mixin;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Only applies ProjectE / ProjectExpansion related mixins when those mods are present.
 * Without this plugin the required mixin config would crash on startup when the
 * targeted classes do not exist.
 */
public class ChaosWorldMixinPlugin implements IMixinConfigPlugin {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROJECTEXPANSION_MIXIN_PACKAGE = "com.yongaishide.chaosworld.mixin.projectexpansion";
    private static final String FORGE_EXTERNAL_STRATEGY_MIXIN = "com.yongaishide.chaosworld.mixin.ae2.ForgeExternalStorageStrategyMixin";
    private static final String TWILIGHT_BOSS_CHEST_MIXIN = "com.yongaishide.chaosworld.mixin.twilightforest.BossRewardChestMixin";
    private static final String TWILIGHT_BOSS_LOOT_MIXIN = "com.yongaishide.chaosworld.mixin.twilightforest.BossLootBufferMixin";
    private static final String TWILIGHT_MIXIN_PACKAGE = "com.yongaishide.chaosworld.mixin.twilightforest";
    private static final String PROJECTE_INTEGRATION_MIXIN_PACKAGE = "com.yongaishide.chaosworld.mixin.projecteintegration";
    private static final String FTBQUESTS_MIXIN_PACKAGE = "com.yongaishide.chaosworld.mixin.ftbquests";

    /**
     * Mixin plugins run before all mods have finished loading, so {@link ModList#isLoaded(String)}
     * can report {@code false} even for mods that are present. The loading mod list is populated
     * from the mods directory scan at startup and is reliable at this point.
     */
    private static boolean isModLoaded(String modid) {
        ModList modList = ModList.get();
        if (modList != null) {
            return modList.isLoaded(modid);
        }
        ModFileInfo fileInfo = FMLLoader.getLoadingModList().getModFileById(modid);
        return fileInfo != null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(PROJECTEXPANSION_MIXIN_PACKAGE)
                || mixinClassName.equals(FORGE_EXTERNAL_STRATEGY_MIXIN)) {
            return isModLoaded("projectexpansion");
        }
        if (mixinClassName.equals(TWILIGHT_BOSS_CHEST_MIXIN)) {
            boolean twilight = isModLoaded("twilightforest");
            boolean avaritia = isModLoaded("avaritia");
            LOGGER.info("[ChaosWorld] BossRewardChestMixin apply check: twilightforest={}, avaritia={}", twilight, avaritia);
            return twilight && avaritia;
        }
        if (mixinClassName.startsWith(PROJECTE_INTEGRATION_MIXIN_PACKAGE)) {
            return isModLoaded("projecteintegration");
        }
        if (mixinClassName.startsWith(TWILIGHT_MIXIN_PACKAGE)) {
            return isModLoaded("twilightforest");
        }
        if (mixinClassName.startsWith(FTBQUESTS_MIXIN_PACKAGE)) {
            return isModLoaded("ftbquests");
        }
        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

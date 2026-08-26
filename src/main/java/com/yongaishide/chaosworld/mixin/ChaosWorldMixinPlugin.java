package com.yongaishide.chaosworld.mixin;

import net.neoforged.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
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

    private static final String PROJECTEXPANSION_MIXIN_PACKAGE = "com.yongaishide.chaosworld.mixin.projectexpansion";
    private static final String FORGE_EXTERNAL_STRATEGY_MIXIN = "com.yongaishide.chaosworld.mixin.ae2.ForgeExternalStorageStrategyMixin";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(PROJECTEXPANSION_MIXIN_PACKAGE)
                || mixinClassName.equals(FORGE_EXTERNAL_STRATEGY_MIXIN)) {
            return ModList.get() != null && ModList.get().isLoaded("projectexpansion");
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

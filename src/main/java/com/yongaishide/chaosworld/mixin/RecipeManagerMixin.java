package com.yongaishide.chaosworld.mixin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;
import com.yongaishide.chaosworld.patch.ufo.DynamicQmfMirrors;
import com.yongaishide.chaosworld.patch.ufo.PatchRecipes;
import com.yongaishide.chaosworld.patch.ufo.UfoContentRemoval;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Patch layer: drops the recipes of UFO Future's removed infinite / mega content
 * before they are parsed, and rewrites the multiblock mappings so the machines
 * and JEI always follow data-pack edits:
 * <ul>
 *   <li>every single-output DMA recipe gets a dynamically scaled x64 QMF mirror
 *       (replacing the matching {@code universal/qmf/bulk|*_batch|*_massive} entry
 *       or adding {@code universal/qmf/bulk/<name>}); multi-output DMA recipes are removed</li>
 *   <li>AE2LT overload recipes are registered as {@code universal/quantum_processor_assembler/overload/*}</li>
 *   <li>ExtendedAE circuit cutter recipes are registered as {@code universal/quantum_slicer/circuit_cutter/*}</li>
 *   <li>the Quantum Processing Factory keeps only the AE2LT overload mappings</li>
 * </ul>
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Accessor("byType")
    protected abstract Multimap<RecipeType<?>, RecipeHolder<?>> chaosworld$getByType();

    @Accessor("byType")
    protected abstract void chaosworld$setByType(Multimap<RecipeType<?>, RecipeHolder<?>> byType);

    @Accessor("byName")
    protected abstract Map<ResourceLocation, RecipeHolder<?>> chaosworld$getByName();

    @Accessor("byName")
    protected abstract void chaosworld$setByName(Map<ResourceLocation, RecipeHolder<?>> byName);

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void chaosworld$removeUfoContent(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager,
            ProfilerFiller profiler, CallbackInfo ci) {
        map.entrySet().removeIf(entry -> UfoContentRemoval.isRemovedRecipe(entry.getKey(), entry.getValue()));
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void chaosworld$dynamicMirrorsAfterLoad(Map<ResourceLocation, JsonElement> map,
            ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        chaosworld$rebuildDynamicMappings();
    }

    @Inject(method = "replaceRecipes", at = @At("TAIL"))
    private void chaosworld$dynamicMirrorsAfterSync(Iterable<RecipeHolder<?>> recipes, CallbackInfo ci) {
        chaosworld$rebuildDynamicMappings();
    }

    @Unique
    private void chaosworld$rebuildDynamicMappings() {
        try {
            Multimap<RecipeType<?>, RecipeHolder<?>> byType = this.chaosworld$getByType();
            if (byType == null || byType.isEmpty()) {
                return;
            }
            Map<ResourceLocation, RecipeHolder<?>> byName = this.chaosworld$getByName();
            RecipeType<?> universalType = com.raishxn.ufo.init.ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get();
            RecipeType<?> dmaType = com.raishxn.ufo.init.ModRecipes.DMA_RECIPE_TYPE.get();
            if (universalType == null || dmaType == null) {
                return;
            }

            // 1. Collect single-output DMA recipes; multi-output ones are removed.
            Map<ResourceLocation, DimensionalMatterAssemblerRecipe> dmaById = new LinkedHashMap<>();
            Set<ResourceLocation> removedDmaIds = new HashSet<>();
            for (RecipeHolder<?> holder : byType.get(dmaType)) {
                if (holder.value() instanceof DimensionalMatterAssemblerRecipe dma) {
                    if (DynamicQmfMirrors.isMultiOutput(dma)) {
                        removedDmaIds.add(holder.id());
                    } else {
                        dmaById.put(holder.id(), dma);
                    }
                }
            }

            // 2. Existing QMF recipe paths, used to reuse hand-written ids.
            Set<String> existingQmfPaths = new HashSet<>();
            for (RecipeHolder<?> holder : byType.get(universalType)) {
                if (holder.value() instanceof UniversalMultiblockRecipe universal
                        && universal.getMachine() == UniversalMultiblockMachineKind.QMF) {
                    existingQmfPaths.add(holder.id().getPath());
                }
            }

            // 3. Target mirror id per DMA recipe.
            Map<ResourceLocation, DimensionalMatterAssemblerRecipe> targets = new LinkedHashMap<>();
            Set<ResourceLocation> newTargets = new HashSet<>();
            for (Map.Entry<ResourceLocation, DimensionalMatterAssemblerRecipe> entry : dmaById.entrySet()) {
                ResourceLocation dmaId = entry.getKey();
                if (!"ufo".equals(dmaId.getNamespace()) || !dmaId.getPath().startsWith("dma/")) {
                    continue;
                }
                String shortName = dmaId.getPath().substring("dma/".length());
                ResourceLocation target = null;
                for (String candidate : List.of(
                        DynamicQmfMirrors.BULK_PREFIX + shortName,
                        "universal/qmf/" + shortName + "_batch",
                        "universal/qmf/" + shortName + "_massive")) {
                    if (existingQmfPaths.contains(candidate)) {
                        target = DynamicQmfMirrors.recipeId(candidate);
                        break;
                    }
                }
                if (target == null) {
                    target = DynamicQmfMirrors.recipeId(DynamicQmfMirrors.BULK_PREFIX + shortName);
                    newTargets.add(target);
                }
                targets.put(target, entry.getValue());
            }

            Map<ResourceLocation, RecipeHolder<?>> additions = new HashMap<>();
            chaosworld$collectOverloadMappings(byType, byName, additions);
            chaosworld$collectCircuitCutterMappings(byType, byName, additions);

            ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> newByType = ImmutableMultimap.builder();
            ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> newByName = ImmutableMap.builder();
            boolean changed = !removedDmaIds.isEmpty() || !newTargets.isEmpty();
            for (Map.Entry<RecipeType<?>, RecipeHolder<?>> entry : byType.entries()) {
                RecipeHolder<?> holder = entry.getValue();
                if (entry.getKey() == dmaType && removedDmaIds.contains(holder.id())) {
                    changed = true;
                    continue;
                }
                if (entry.getKey() == universalType && chaosworld$isRemovedProcessorRecipe(holder)) {
                    changed = true;
                    continue;
                }
                if (entry.getKey() == universalType && targets.containsKey(holder.id())) {
                    RecipeHolder<?> replacement = DynamicQmfMirrors.buildMirror(holder.id(), targets.get(holder.id()));
                    if (replacement != null) {
                        holder = replacement;
                        changed = true;
                    }
                }
                newByType.put(entry.getKey(), holder);
                newByName.put(holder.id(), holder);
            }

            for (Map.Entry<ResourceLocation, RecipeHolder<?>> entry : additions.entrySet()) {
                newByType.put(universalType, entry.getValue());
                newByName.put(entry.getKey(), entry.getValue());
            }
            for (ResourceLocation target : newTargets) {
                RecipeHolder<?> mirror = DynamicQmfMirrors.buildMirror(target, targets.get(target));
                if (mirror != null) {
                    newByType.put(universalType, mirror);
                    newByName.put(target, mirror);
                }
            }

            if (changed) {
                var builtByType = newByType.build();
                var builtByName = newByName.build();
                this.chaosworld$setByType(builtByType);
                this.chaosworld$setByName(builtByName);
            }
        } catch (Throwable ignored) {
            // Never break recipe loading because of the mapping rewrite.
        }
    }

    @Unique
    private static boolean chaosworld$isRemovedProcessorRecipe(RecipeHolder<?> holder) {
        if (!(holder.value() instanceof UniversalMultiblockRecipe universal)) {
            return false;
        }
        if (universal.getMachine() != UniversalMultiblockMachineKind.QUANTUM_PROCESSOR_ASSEMBLER) {
            return false;
        }
        return !holder.id().getPath().startsWith("universal/quantum_processor_assembler/overload/");
    }

    @Unique
    private void chaosworld$collectOverloadMappings(Multimap<RecipeType<?>, RecipeHolder<?>> byType,
            Map<ResourceLocation, RecipeHolder<?>> byName, Map<ResourceLocation, RecipeHolder<?>> additions) {
        if (!ModList.get().isLoaded("ae2lt")) {
            return;
        }
        try {
            RecipeType<?> overloadType = com.moakiee.ae2lt.registry.ModRecipeTypes.OVERLOAD_PROCESSING_TYPE.get();
            for (RecipeHolder<?> holder : byType.get(overloadType)) {
                if (holder.value() instanceof com.moakiee.ae2lt.machine.overloadfactory.recipe.OverloadProcessingRecipe overload
                        && !overload.isIncomplete()) {
                    ResourceLocation dynamicId = DynamicQmfMirrors.recipeId(
                            "universal/quantum_processor_assembler/overload/" + holder.id().getPath());
                    if (!byName.containsKey(dynamicId) && !additions.containsKey(dynamicId)) {
                        additions.put(dynamicId, new RecipeHolder<>(dynamicId,
                                PatchRecipes.universalFromOverload(dynamicId, overload)));
                    }
                }
            }
        } catch (Throwable ignored) {
            // AE2LT missing or API mismatch: keep the rest of the mappings working.
        }
    }

    @Unique
    private void chaosworld$collectCircuitCutterMappings(Multimap<RecipeType<?>, RecipeHolder<?>> byType,
            Map<ResourceLocation, RecipeHolder<?>> byName, Map<ResourceLocation, RecipeHolder<?>> additions) {
        if (!ModList.get().isLoaded("extendedae")) {
            return;
        }
        try {
            RecipeType<?> cutterType = com.glodblock.github.extendedae.recipe.CircuitCutterRecipe.TYPE;
            for (RecipeHolder<?> holder : byType.get(cutterType)) {
                if (holder.value() instanceof com.glodblock.github.extendedae.recipe.CircuitCutterRecipe cutter) {
                    ResourceLocation dynamicId = DynamicQmfMirrors.recipeId(
                            "universal/quantum_slicer/circuit_cutter/" + holder.id().getPath());
                    if (!byName.containsKey(dynamicId) && !additions.containsKey(dynamicId)) {
                        additions.put(dynamicId, new RecipeHolder<>(dynamicId,
                                PatchRecipes.universalFromCircuitCutter(dynamicId, cutter)));
                    }
                }
            }
        } catch (Throwable ignored) {
            // ExtendedAE missing or API mismatch: keep the rest of the mappings working.
        }
    }
}

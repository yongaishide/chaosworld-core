package com.yongaishide.chaosworld.compat.ftbquests;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;

public record AEKeyRef(ResourceLocation keyType, ResourceLocation resource, DataComponentPatch components) {
    public AEKeyRef(ResourceLocation keyType, ResourceLocation resource) {
        this(keyType, resource, null);
    }

    public static AEKeyRef of(AEKey key) {
        DataComponentPatch components = null;
        if (key instanceof AEItemKey itemKey) {
            DataComponentPatch patch = itemKey.getReadOnlyStack().getComponentsPatch();
            if (!patch.isEmpty()) {
                components = patch;
            }
        }
        return new AEKeyRef(key.getType().getId(), key.getId(), components);
    }
}

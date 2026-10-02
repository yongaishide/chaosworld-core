package com.yongaishide.chaosworld.compat.ftbquests.client;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.yongaishide.chaosworld.compat.ftbquests.AEKeyRef;
import dev.ftb.mods.ftblibrary.config.ui.resource.SelectableResource;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.util.client.ClientUtils;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class AEKeySelectableResource implements SelectableResource<AEKeyRef>, LongCountResource {
    public static final AEKeySelectableResource EMPTY = new AEKeySelectableResource(null, null, 0L);

    private AEKey key;
    private AEKeyRef ref;
    private long count;

    public AEKeySelectableResource(AEKey key, AEKeyRef ref) {
        this(key, ref, key == null ? 0L : 1L);
    }

    public AEKeySelectableResource(AEKey key, AEKeyRef ref, long count) {
        this.key = key;
        this.ref = ref;
        this.count = count;
    }

    public AEKey key() {
        return key;
    }

    @Override
    public AEKeyRef resource() {
        return ref;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {
        this.count = Math.max(1, count);
    }

    @Override
    public void setCountLong(long count) {
        this.count = Math.max(1L, count);
    }

    @Override
    public boolean isEmpty() {
        return key == null;
    }

    @Override
    public Component getName() {
        return key == null ? Component.translatable("chaosworld_core.ae_resource.none") : key.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return key == null ? Icon.empty() : new AEKeyIcon(key);
    }

    @Override
    public SelectableResource<AEKeyRef> copyWithCount(long count) {
        return new AEKeySelectableResource(key, ref, count);
    }

    @Override
    public CompoundTag getComponentsTag() {
        if (!(key instanceof AEItemKey itemKey)) {
            return null;
        }
        DataComponentPatch patch = itemKey.getReadOnlyStack().getComponentsPatch();
        if (patch.isEmpty()) {
            return new CompoundTag();
        }
        try {
            return DataComponentPatch.CODEC.encodeStart(ClientUtils.registryAccess().createSerializationContext(NbtOps.INSTANCE), patch)
                    .result().filter(tag -> tag instanceof CompoundTag).map(tag -> (CompoundTag) tag).orElse(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public void applyComponentsTag(CompoundTag tag) {
        if (!(key instanceof AEItemKey) || ref == null) {
            return;
        }
        try {
            DataComponentPatch patch = tag == null || tag.isEmpty() ? DataComponentPatch.EMPTY
                    : DataComponentPatch.CODEC.parse(ClientUtils.registryAccess().createSerializationContext(NbtOps.INSTANCE), tag)
                            .result().orElse(DataComponentPatch.EMPTY);
            Item item = BuiltInRegistries.ITEM.get(ref.resource());
            if (item == null || item == Items.AIR) {
                return;
            }
            ItemStack stack = new ItemStack(item);
            stack.applyComponents(patch);
            key = AEItemKey.of(stack);
            ref = new AEKeyRef(ref.keyType(), ref.resource(), patch.isEmpty() ? null : patch);
        } catch (Throwable ignored) {
        }
    }
}

package com.yongaishide.chaosworld.compat.ftbquests.client;

import appeng.api.stacks.AEKey;
import com.yongaishide.chaosworld.compat.ftbquests.AEKeyRef;
import com.yongaishide.chaosworld.compat.ftbquests.KnownAEKeys;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ResourceConfigValue;
import dev.ftb.mods.ftblibrary.config.ui.resource.SelectableResource;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;

import java.util.OptionalLong;

public class AEKeyConfig extends ResourceConfigValue<AEKeyRef> {
    private long amount;

    public AEKeyConfig() {
        this(1L);
    }

    public AEKeyConfig(long amount) {
        this.amount = Math.max(1L, amount);
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public OptionalLong fixedResourceSize() {
        return OptionalLong.empty();
    }

    @Override
    public boolean isEmpty() {
        return value == null || value.keyType() == null || value.resource() == null;
    }

    @Override
    public SelectableResource<AEKeyRef> getResource() {
        AEKeyRef ref = getValue();
        if (ref == null || ref.resource() == null) {
            return new AEKeySelectableResource(null, null, amount);
        }
        AEKey key = KnownAEKeys.resolve(ref);
        return key == null ? new AEKeySelectableResource(null, null, amount) : new AEKeySelectableResource(key, ref, amount);
    }

    @Override
    public boolean setResource(SelectableResource<AEKeyRef> selectable) {
        amount = Math.max(1L, selectable.getCount());
        return setCurrentValue(selectable.resource());
    }

    @Override
    public void onClicked(Widget clicked, MouseButton button, ConfigCallback callback) {
        new AEKeySelectScreen(this, callback).withGridSize(9, 6).openGui();
    }

    @Override
    public Component getStringForGUI(AEKeyRef v) {
        if (v == null || v.resource() == null) {
            return Component.translatable("chaosworld_core.ae_resource.any");
        }
        AEKey key = KnownAEKeys.resolve(v);
        return key != null ? key.getDisplayName() : Component.literal(v.resource().toString());
    }
}

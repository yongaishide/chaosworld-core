package com.yongaishide.chaosworld.compat.ftbquests.client;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.yongaishide.chaosworld.compat.ftbquests.AEKeyRef;
import dev.ftb.mods.ftblibrary.config.ui.resource.ResourceSearchMode;
import dev.ftb.mods.ftblibrary.config.ui.resource.SelectableResource;
import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AEInventorySearchMode implements ResourceSearchMode<AEKeyRef> {
    private static final Icon ICON = Icon.getIcon("minecraft:item/bundle");

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("chaosworld_core.resource.inventory");
    }

    @Override
    public Collection<? extends SelectableResource<AEKeyRef>> getAllResources() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return List.of();
        }

        List<SelectableResource<AEKeyRef>> list = new ArrayList<>();
        Set<AEKeyRef> seen = new HashSet<>();
        Inventory inventory = minecraft.player.getInventory();
        add(list, seen, inventory.items);
        add(list, seen, inventory.offhand);
        add(list, seen, inventory.armor);
        return list;
    }

    private static void add(List<SelectableResource<AEKeyRef>> list, Set<AEKeyRef> seen, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            AEKey key = AEItemKey.of(stack);
            AEKeyRef ref = AEKeyRef.of(key);
            if (seen.add(ref)) {
                list.add(new AEKeySelectableResource(key, ref));
            }
        }
    }
}

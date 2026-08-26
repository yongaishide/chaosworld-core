package com.yongaishide.chaosworld.mixin.neoecoae;

import appeng.util.inv.AppEngInternalInventory;
import cn.dancingsnow.neoecoae.api.storage.IECOStorageCell;
import cn.dancingsnow.neoecoae.blocks.entity.storage.ECODriveBlockEntity;
import cn.dancingsnow.neoecoae.blocks.entity.storage.ECOStorageSystemBlockEntity;
import cn.dancingsnow.neoecoae.gui.common.HostElements;
import cn.dancingsnow.neoecoae.gui.storage.StorageHostPanelUI;
import cn.dancingsnow.neoecoae.multiblock.cluster.NEStorageCluster;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import dev.vfyjxf.taffy.style.FlexDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Appends an "Energy Consumption" row below the "Energy Storage" row in the
 * storage subsystem host GUI's left panel. The value mirrors the actual
 * network idle consumption: controller + drives, zeroed while the creative
 * energy cell sits in the infinite component slot.
 */
@Mixin(value = ECOStorageSystemBlockEntity.class, remap = false)
public class StorageHostConsumptionRowMixin {

    private static final ResourceLocation CREATIVE_ENERGY_CELL_ID =
            ResourceLocation.fromNamespaceAndPath("ae2", "creative_energy_cell");
    private static final int ACCENT_COLOR = 0xD6D4E0;
    private static final int VALUE_COLOR = 0xFFAA00;

    /** Insert right after the energy label, performance label and energy storage row. */
    private static final int INSERT_INDEX = 3;

    @Redirect(method = "createUI",
            at = @At(value = "INVOKE",
                    target = "Lcn/dancingsnow/neoecoae/gui/storage/StorageHostPanelUI;createLeftPanel(Lcn/dancingsnow/neoecoae/gui/storage/StorageHostPanelUI$Config;)Lcom/lowdragmc/lowdraglib2/gui/ui/UIElement;",
                    remap = false))
    private UIElement chaosworld$appendConsumptionRow(StorageHostPanelUI.Config config) {
        UIElement panel = StorageHostPanelUI.createLeftPanel(config);
        ECOStorageSystemBlockEntity self = (ECOStorageSystemBlockEntity) (Object) this;
        chaosworld$addConsumptionRow(panel, self);
        return panel;
    }

    private static void chaosworld$addConsumptionRow(UIElement element, ECOStorageSystemBlockEntity self) {
        for (UIElement child : element.getChildren()) {
            if (child instanceof ScrollerView scroller) {
                scroller.addScrollViewChildAt(chaosworld$consumptionRow(self), INSERT_INDEX);
            } else {
                chaosworld$addConsumptionRow(child, self);
            }
        }
    }

    private static UIElement chaosworld$consumptionRow(ECOStorageSystemBlockEntity self) {
        UIElement row = new UIElement();
        row.layout(style -> {
            style.gapAll(2.0F);
            style.flexDirection(FlexDirection.COLUMN);
        });
        row.addChild(HostElements.sectionLabel(
                () -> Component.translatable("chaosworld_core.storage.energy_consumption").append(":"),
                () -> ACCENT_COLOR));
        row.addChild(HostElements.textSegment(
                () -> Component.literal(chaosworld$formatConsumption(chaosworld$totalConsumption(self))),
                () -> VALUE_COLOR));
        return row;
    }

    private static String chaosworld$formatConsumption(double consumption) {
        return String.format(Locale.ROOT, consumption == 0.0 ? "0 AE/t" : "%,.2f AE/t", consumption);
    }

    private static double chaosworld$totalConsumption(ECOStorageSystemBlockEntity self) {
        if (chaosworld$hasEnhancementCore(self)) {
            return 0.0;
        }
        double total = 256.0 + (1 << (1 + 4 * self.getTier().getTier()));
        Field clusterField = chaosworld$findField(ECOStorageSystemBlockEntity.class, "cluster");
        if (clusterField != null) {
            try {
                Object cluster = clusterField.get(self);
                if (cluster instanceof NEStorageCluster storageCluster) {
                    for (ECODriveBlockEntity drive : storageCluster.getDrives()) {
                        double driveIdle = 256.0;
                        IECOStorageCell cell = drive.getCellInventory();
                        if (cell != null && self.getTier().compareTo(cell.getTier()) >= 0) {
                            driveIdle += cell.getIdleDrain();
                        }
                        total += driveIdle;
                    }
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return total;
    }

    private static boolean chaosworld$hasEnhancementCore(ECOStorageSystemBlockEntity self) {
        try {
            Field compField = ECOStorageSystemBlockEntity.class.getDeclaredField("infiniteComponentInventory");
            compField.setAccessible(true);
            ItemStack stack = ((AppEngInternalInventory) compField.get(self)).getStackInSlot(0);
            return stack.is(BuiltInRegistries.ITEM.get(CREATIVE_ENERGY_CELL_ID));
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Field chaosworld$findField(Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }
}

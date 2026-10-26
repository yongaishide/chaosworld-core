package com.yongaishide.chaosworld.compat.neoecoae;

import appeng.util.inv.AppEngInternalInventory;
import cn.dancingsnow.neoecoae.api.power.ECOPowerProfiles;
import cn.dancingsnow.neoecoae.api.power.ECOPowerRole;
import cn.dancingsnow.neoecoae.blocks.entity.storage.ECODriveBlockEntity;
import cn.dancingsnow.neoecoae.blocks.entity.storage.ECOStorageSystemBlockEntity;
import com.mojang.logging.LogUtils;
import com.yongaishide.chaosworld.ChaosWorld;
import com.yongaishide.chaosworld.mixin.neoecoae.ECOStorageSystemAccess;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

/**
 * NeoEcoAE 电力档案：子系统无限元件槽 0 装着创造能量元件时，控制器与存储驱动器的待机耗电归零。
 *
 * <p>NeoEcoAE 21.2 把待机耗电统一交给 {@code NEBlockEntity#applyIdlePower} 解析，并开放了
 * {@link ECOPowerProfiles} 扩展点。旧实现用 mixin 直接改写网格节点的待机耗电，既依赖反射，
 * 也会被 {@code refreshIdlePower()} 覆盖；现在只注册档案（返回 {@code null} 表示“不表态”），
 * 控制器和驱动器自己会在 {@code onReady}/{@code updateState} 时来问。</p>
 *
 * <p>控制器在 {@code onChangeInventory} 里不会重算待机耗电，所以还需要
 * {@code ECOStorageSystemBlockEntityMixin} 在槽位变化后让我们刷新一次；
 * 刷新用的是 21.2.10 的公开方法 {@code refreshIdlePower()}。</p>
 */
public final class CreativeCellIdlePowerProfile {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation CREATIVE_ENERGY_CELL_ID =
            ResourceLocation.fromNamespaceAndPath("ae2", "creative_energy_cell");

    private static final String ACTIVATED_KEY = "chaosworld_core.storage.enhancement_core.activated";
    private static final String REMOVED_KEY = "chaosworld_core.storage.enhancement_core.removed";

    /** 高于 NeoEcoAE 自带档案的优先级。 */
    private static final int PRIORITY = 1000;

    /** 服务器侧控制器上一次的“是否装有创造能量元件”，只用来判断要不要广播。 */
    private static final Map<ECOStorageSystemBlockEntity, Boolean> LAST_ACTIVE =
            Collections.synchronizedMap(new WeakHashMap<>());

    private static boolean registered;
    private static Item creativeEnergyCell;
    private static boolean readFailureLogged;

    private CreativeCellIdlePowerProfile() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        try {
            ECOPowerProfiles.register(ChaosWorld.id("creative_cell_idle_power"), PRIORITY,
                    CreativeCellIdlePowerProfile::idlePower);
            registered = true;
            LOGGER.info("[chaosworld] 已注册 NeoEcoAE 电力档案：创造能量元件供能时子系统零待机耗电");
        } catch (Throwable t) {
            LOGGER.warn("[chaosworld] 注册 NeoEcoAE 电力档案失败，待机耗电保持原样", t);
        }
    }

    /**
     * 档案入口。控制器和驱动器都会来问：只有无限元件槽 0 里是创造能量元件时返回 0，
     * 其余情况返回 {@code null}，交回 NeoEcoAE 的默认计算。
     */
    private static Double idlePower(ECOPowerProfiles.Context context) {
        try {
            ECOStorageSystemBlockEntity controller = controllerOf(context);
            if (controller != null && hasCreativeCell(controller)) {
                return 0.0D;
            }
        } catch (Throwable t) {
            if (!readFailureLogged) {
                readFailureLogged = true;
                LOGGER.warn("[chaosworld] NeoEcoAE 电力档案计算失败，本次按默认待机耗电处理", t);
            }
        }
        return null;
    }

    private static ECOStorageSystemBlockEntity controllerOf(ECOPowerProfiles.Context context) {
        if (context.role() == ECOPowerRole.STORAGE_CONTROLLER
                && context.host() instanceof ECOStorageSystemBlockEntity controller) {
            return controller;
        }
        if (context.role() == ECOPowerRole.STORAGE_DRIVE
                && context.host() instanceof ECODriveBlockEntity drive) {
            return drive.getStorageController();
        }
        return null;
    }

    /** 控制器刚加载时记录初始状态，避免登录/区块加载时刷“已激活”提示。 */
    public static synchronized void onControllerReady(ECOStorageSystemBlockEntity controller) {
        LAST_ACTIVE.put(controller, hasCreativeCell(controller));
    }

    /** 无限元件槽 0 发生变化：重算控制器和簇内驱动器的待机耗电，状态真的变了才广播。 */
    public static synchronized void onComponentsChanged(ECOStorageSystemBlockEntity controller) {
        boolean active = hasCreativeCell(controller);
        Boolean previous = LAST_ACTIVE.put(controller, active);
        if (previous == null || previous != active) {
            controller.refreshIdlePower();
            for (ECODriveBlockEntity drive : controller.getStorageDrivesForIntegration()) {
                drive.refreshIdlePower();
            }
        }
        if (previous != null && previous != active) {
            broadcast(active);
        }
    }

    private static boolean hasCreativeCell(ECOStorageSystemBlockEntity controller) {
        try {
            AppEngInternalInventory components =
                    ((ECOStorageSystemAccess) (Object) controller).chaosworld$componentInventory();
            if (components == null || components.size() < 1) {
                return false;
            }
            ItemStack stack = components.getStackInSlot(0);
            Item cell = creativeEnergyCell();
            return cell != null && !stack.isEmpty() && stack.is(cell);
        } catch (Throwable t) {
            if (!readFailureLogged) {
                readFailureLogged = true;
                LOGGER.warn("[chaosworld] 读不到 NeoEcoAE 无限元件槽，创造能量元件不减免待机耗电", t);
            }
            return false;
        }
    }

    private static Item creativeEnergyCell() {
        if (creativeEnergyCell == null) {
            creativeEnergyCell = BuiltInRegistries.ITEM.get(CREATIVE_ENERGY_CELL_ID);
        }
        return creativeEnergyCell;
    }

    private static void broadcast(boolean active) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        Component message = Component.translatable(active ? ACTIVATED_KEY : REMOVED_KEY);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
    }
}

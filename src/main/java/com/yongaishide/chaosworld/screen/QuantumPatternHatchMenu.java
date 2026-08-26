package com.yongaishide.chaosworld.screen;

import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.helpers.patternprovider.PatternProviderReturnInventory;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.RestrictedInputSlot;
import com.extendedae_plus.api.config.EAPSettings;
import com.extendedae_plus.api.smartDoubling.ISmartDoublingHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class QuantumPatternHatchMenu extends AEBaseMenu {
    public static final String ACTION_SET_PER_PROVIDER_SCALING_LIMIT = "setPerProviderScalingLimit";

    protected final PatternProviderLogic logic;
    private final ToolboxMenu toolbox;

    @GuiSync(3)
    public YesNo blockingMode = YesNo.NO;
    @GuiSync(4)
    public YesNo showInAccessTerminal = YesNo.YES;
    @GuiSync(5)
    public LockCraftingMode lockCraftingMode = LockCraftingMode.NONE;
    @GuiSync(6)
    public LockCraftingMode craftingLockedReason = LockCraftingMode.NONE;
    @GuiSync(7)
    public GenericStack unlockStack = null;
    @GuiSync(8)
    public YesNo advancedBlocking = YesNo.NO;
    @GuiSync(9)
    public YesNo smartDoubling = YesNo.NO;
    @GuiSync(10)
    public int perProviderScalingLimit = 0;

    public QuantumPatternHatchMenu(MenuType<? extends QuantumPatternHatchMenu> menuType, int id, Inventory playerInventory,
            PatternProviderLogicHost host) {
        super(menuType, id, playerInventory, host);
        this.toolbox = new ToolboxMenu(this);
        this.createPlayerInventorySlots(playerInventory);
        this.logic = host.getLogic();

        registerClientAction(ACTION_SET_PER_PROVIDER_SCALING_LIMIT, Integer.class, this::handleSetScalingLimit);

        var patternInv = logic.getPatternInv();
        for (int slot = 0; slot < patternInv.size(); slot++) {
            this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.PROVIDER_PATTERN, patternInv, slot),
                    SlotSemantics.ENCODED_PATTERN);
        }

        var returnInv = logic.getReturnInv().createMenuWrapper();
        for (int slot = 0; slot < PatternProviderReturnInventory.NUMBER_OF_SLOTS; slot++) {
            if (slot < returnInv.size()) {
                this.addSlot(new AppEngSlot(returnInv, slot), SlotSemantics.STORAGE);
            }
        }
    }

    public QuantumPatternHatchMenu(int id, Inventory playerInventory, PatternProviderLogicHost host) {
        this(com.yongaishide.chaosworld.init.ModMenus.QUANTUM_PATTERN_HATCH_MENU, id, playerInventory, host);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            blockingMode = logic.getConfigManager().getSetting(Settings.BLOCKING_MODE);
            showInAccessTerminal = logic.getConfigManager().getSetting(Settings.PATTERN_ACCESS_TERMINAL);
            lockCraftingMode = logic.getConfigManager().getSetting(Settings.LOCK_CRAFTING_MODE);
            craftingLockedReason = logic.getCraftingLockedReason();
            unlockStack = logic.getUnlockStack();
            advancedBlocking = logic.getConfigManager().getSetting(EAPSettings.ADVANCED_BLOCKING);
            smartDoubling = logic.getConfigManager().getSetting(EAPSettings.SMART_DOUBLING);
            perProviderScalingLimit = logic instanceof ISmartDoublingHolder holder
                    ? holder.eap$getProviderSmartDoublingLimit()
                    : 0;
        }
        this.toolbox.tick();
        super.broadcastChanges();
    }

    public GenericStackInv getReturnInv() {
        return logic.getReturnInv();
    }

    public YesNo getBlockingMode() {
        return blockingMode;
    }

    public LockCraftingMode getLockCraftingMode() {
        return lockCraftingMode;
    }

    public LockCraftingMode getCraftingLockedReason() {
        return craftingLockedReason;
    }

    public GenericStack getUnlockStack() {
        return unlockStack;
    }

    public YesNo getShowInAccessTerminal() {
        return showInAccessTerminal;
    }

    public YesNo getAdvancedBlocking() {
        return advancedBlocking;
    }

    public YesNo getSmartDoubling() {
        return smartDoubling;
    }

    public int getPerProviderScalingLimit() {
        return perProviderScalingLimit;
    }

    public void sendScalingLimitFromClient(int limit) {
        if (isClientSide()) {
            sendClientAction(ACTION_SET_PER_PROVIDER_SCALING_LIMIT, limit);
        }
    }

    private void handleSetScalingLimit(int limit) {
        if (logic instanceof ISmartDoublingHolder holder) {
            holder.eap$setProviderSmartDoublingLimit(limit);
            logic.saveChanges();
        }
    }

    public ToolboxMenu getToolbox() {
        return toolbox;
    }
}

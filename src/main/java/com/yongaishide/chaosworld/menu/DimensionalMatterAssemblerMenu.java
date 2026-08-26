package com.yongaishide.chaosworld.menu;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import net.pedroksl.ae2addonlib.api.IFluidTankHandler;
import net.pedroksl.ae2addonlib.gui.OutputDirectionMenu;
import net.pedroksl.ae2addonlib.network.clientPacket.FluidTankStackUpdatePacket;

import com.yongaishide.chaosworld.block.entity.DimensionalMatterAssemblerBlockEntity;
import com.yongaishide.chaosworld.init.ModMenus;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;

public class DimensionalMatterAssemblerMenu extends UpgradeableMenu<DimensionalMatterAssemblerBlockEntity>
        implements IProgressProvider, IFluidTankHandler {

    @GuiSync(2)
    public int maxProcessingTime = -1;

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(7)
    public YesNo autoExport = YesNo.NO;

    @GuiSync(8)
    public boolean showWarning = false;

    @GuiSync(9)
    public int currentPower = 0;

    @GuiSync(10)
    public int temperature = 0;

    @GuiSync(11)
    public int maxTemperature = 10000;

    @GuiSync(12)
    public int overloadTimer = -1;

    public final int INPUT_FLUID_SIZE = 16;
    public final int OUTPUT_FLUID_SIZE = 16;

    private static final String CONFIGURE_OUTPUT = "configureOutput";
    private static final String CLEAR_TANK = "clearTank";
    private static final String FILL_DRAIN_TANK = "fillDrainTank";

    private final List<Slot> inputs = new ArrayList<>(9);

    public DimensionalMatterAssemblerMenu(int id, Inventory ip, DimensionalMatterAssemblerBlockEntity host) {
        super(ModMenus.DIMENSIONAL_MATTER_ASSEMBLER, id, ip, host);

        var inputsInv = host.getInput();

        for (var x = 0; x < inputsInv.size(); x++) {
            this.inputs.add(x, this.addSlot(new AppEngSlot(inputsInv, x), SlotSemantics.MACHINE_INPUT));
        }

        var outputsInv = host.getOutput();
        // Use separate semantics for each output slot to allow independent
        // positioning in the screen JSON (non-standard 28px vertical spacing)
        this.addSlot(new OutputSlot(outputsInv, 0, null), SlotSemantics.MACHINE_OUTPUT);
        if (outputsInv.size() > 1) {
            this.addSlot(new OutputSlot(outputsInv, 1, null), UFOSlotSemantics.MACHINE_OUTPUT_2);
        }

        registerClientAction(CONFIGURE_OUTPUT, this::configureOutput);
        registerClientAction(CLEAR_TANK, Integer.class, this::clearTank);
        registerClientAction(FILL_DRAIN_TANK, Integer.class, this::fillOrDrainTank);
    }

    protected void loadSettingsFromHost(IConfigManager cm) {
        this.autoExport = this.getHost().getConfigManager().getSetting(Settings.AUTO_EXPORT);
    }

    @Override
    protected void standardDetectAndSendChanges() {
        if (isServerSide()) {
            this.maxProcessingTime = getHost().getMaxProcessingTime();
            this.processingTime = getHost().getProcessingTime();
            this.showWarning = getHost().showWarning();
            this.currentPower = (int) getHost().getAECurrentPower();
            this.temperature = getHost().getTemperature();
            this.maxTemperature = getHost().getMaxTemperature();
            this.overloadTimer = getHost().getOverloadTimer();

            // Sincronizar tanques 0, 1 (Outputs) e 2, 3 (Inputs)
            for (int i = 0; i < 4; i++) {
                var genFluid = this.getHost().getTank().getStack(i);
                FluidStack fluidStack = FluidStack.EMPTY;
                if (genFluid != null && genFluid.what() != null) {
                    fluidStack = ((AEFluidKey) genFluid.what()).toStack(((int) genFluid.amount()));
                }
                sendPacketToClient(new FluidTankStackUpdatePacket(i, fluidStack));
            }
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public boolean isValidForSlot(Slot s, ItemStack is) {
        if (this.inputs.contains(s)) {
            return true;
        }
        return true;
    }

    @Override
    public int getCurrentProgress() {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress() {
        return this.maxProcessingTime;
    }

    public YesNo getAutoExport() {
        return autoExport;
    }

    public boolean getShowWarning() {
        return this.showWarning;
    }

    public void configureOutput() {
        if (isClientSide()) {
            sendClientAction(CONFIGURE_OUTPUT);
            return;
        }

        var locator = getLocator();
        if (locator != null && isServerSide()) {
            OutputDirectionMenu.open(
                    ((ServerPlayer) this.getPlayer()),
                    getLocator(),
                    this.getHost().getAllowedOutputs());
        }
    }

    /**
     * Clears a specific tank slot. Triggered by GUI clear buttons.
     */
    public void clearTank(int slot) {
        if (isClientSide()) {
            sendClientAction(CLEAR_TANK, slot);
            return;
        }
        getHost().clearTank(slot);
    }

    /**
     * Fills or drains a tank slot using the player's held bucket/container.
     * Called when player right-clicks a tank in the GUI.
     */
    public void fillOrDrainTank(int tankIndex) {
        if (isClientSide()) {
            sendClientAction(FILL_DRAIN_TANK, tankIndex);
            return;
        }

        var player = getPlayer();
        if (player == null) return;

        ItemStack held = getCarried();
        if (held.isEmpty()) return;

        var tank = getHost().getTank();

        // Try to drain from player's held item into the tank (input tanks only: 2, 3)
        if (tankIndex == 2 || tankIndex == 3) {
            var fluidCap = held.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM);
            if (fluidCap != null) {
                FluidStack simulated = fluidCap.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
                if (!simulated.isEmpty()) {
                    AEFluidKey fluidKey = AEFluidKey.of(simulated.getFluid());
                    var currentStack = tank.getStack(tankIndex);

                    // Check if slot is empty or has the same fluid
                    if (currentStack == null || currentStack.what().equals(fluidKey)) {
                        long currentAmount = currentStack != null ? currentStack.amount() : 0;
                        long maxCapacity = tank.getMaxAmount(fluidKey);
                        long space = maxCapacity - currentAmount;

                        if (space > 0) {
                            int toDrain = (int) Math.min(simulated.getAmount(), space);
                            FluidStack drained = fluidCap.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
                            if (!drained.isEmpty()) {
                                long newAmount = currentAmount + drained.getAmount();
                                tank.setStack(tankIndex, new GenericStack(fluidKey, newAmount));
                                setCarried(fluidCap.getContainer());
                                getHost().onChangeTank();
                                getHost().saveChanges();
                            }
                        }
                    }
                    return;
                }
            }
        }

        // Try to fill player's held item from the tank (output tanks: 0, 1 or any tank)
        if (tankIndex == 0 || tankIndex == 1 || tankIndex == 2 || tankIndex == 3) {
            var currentStack = tank.getStack(tankIndex);
            if (currentStack != null && currentStack.what() instanceof AEFluidKey fluidKey) {
                var fluidCap = held.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM);
                if (fluidCap != null) {
                    FluidStack toFill = fluidKey.toStack((int) Math.min(currentStack.amount(), Integer.MAX_VALUE));
                    int filled = fluidCap.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
                    if (filled > 0) {
                        long remaining = currentStack.amount() - filled;
                        if (remaining > 0) {
                            tank.setStack(tankIndex, new GenericStack(fluidKey, remaining));
                        } else {
                            tank.setStack(tankIndex, null);
                        }
                        setCarried(fluidCap.getContainer());
                        getHost().onChangeTank();
                        getHost().saveChanges();
                    }
                }
            }
        }
    }

    @Override
    public ServerPlayer getServerPlayer() {
        if (isClientSide()) {
            return null;
        }
        return ((ServerPlayer) getPlayer());
    }

    @Override
    public ItemStack getCarriedItem() {
        return getCarried();
    }

    @Override
    public void setCarriedItem(ItemStack stack) {
        setCarried(stack);
    }

    @Override
    public GenericStackInv getTank() {
        return this.getHost().getTank();
    }

    @Override
    public boolean canExtractFromTank(int index) {
        return index == 0 || index == 1;
    }

    @Override
    public boolean canInsertInto(int index) {
        return index == 2 || index == 3;
    }
}

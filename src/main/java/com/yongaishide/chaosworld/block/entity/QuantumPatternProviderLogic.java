package com.yongaishide.chaosworld.block.entity;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.helpers.patternprovider.PatternProviderLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Custom provider logic for the Quantum Pattern Hatch.
 * If this hatch is linked to a multiblock controller that accepts AE2 crafting plans,
 * forward the pattern directly to that controller.
 */
public class QuantumPatternProviderLogic extends PatternProviderLogic {

    private final QuantumPatternHatchBE hatch;

    public QuantumPatternProviderLogic(QuantumPatternHatchBE hatch, int patternInventorySize) {
        super(hatch.getMainNode(), hatch, patternInventorySize);
        this.hatch = hatch;
        this.getConfigManager().putSetting(Settings.PATTERN_ACCESS_TERMINAL, YesNo.NO);
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, appeng.api.stacks.KeyCounter[] inputHolder) {
        @Nullable Level level = hatch.getLevel();
        @Nullable BlockPos controllerPos = hatch.getControllerPos();

        if (level != null && controllerPos != null) {
            var controllerBe = level.getBlockEntity(controllerPos);
            if (controllerBe instanceof ICraftingMachine machine && machine.acceptsPlans()) {
                Direction direction = hatch.getPushDirectionForController();
                return machine.pushPattern(patternDetails, inputHolder, direction);
            }

            // If the hatch is linked to a multiblock controller, do not fall back to the
            // default adjacent-inventory behavior. The controller is the only valid target.
            return false;
        }

        return super.pushPattern(patternDetails, inputHolder);
    }
}

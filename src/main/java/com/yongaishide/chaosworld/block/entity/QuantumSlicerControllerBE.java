package com.yongaishide.chaosworld.block.entity;

import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import com.yongaishide.chaosworld.block.entity.pattern.QuantumSlicerPatternFactory;
import com.yongaishide.chaosworld.init.ModBlockEntities;
import com.yongaishide.chaosworld.screen.QuantumSlicerControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class QuantumSlicerControllerBE extends AbstractParallelMultiblockControllerBE {

    private static MultiblockPattern PATTERN;

    public QuantumSlicerControllerBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_SLICER_CONTROLLER_BE.get(), pos, state);
    }

    @Override
    protected MultiblockPattern getControllerPattern() {
        if (PATTERN == null) {
            PATTERN = QuantumSlicerPatternFactory.getPattern();
        }
        return PATTERN;
    }

    @Override
    protected String getControllerTranslationKey() {
        return "block.chaosworld_core.quantum_slicer_controller";
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new QuantumSlicerControllerMenu(id, playerInventory, this);
    }

    @Override
    protected java.util.List<com.yongaishide.chaosworld.block.entity.processing.MultiblockProcessingRecipe> getAvailableRecipes() {
        if (this.level == null) {
            return java.util.List.of();
        }
        java.util.List<com.yongaishide.chaosworld.block.entity.processing.MultiblockProcessingRecipe> recipes = new java.util.ArrayList<>();
        if (net.neoforged.fml.ModList.get().isLoaded("extendedae")) {
            try {
                for (var holder : this.level.getRecipeManager().getAllRecipesFor(
                        com.glodblock.github.extendedae.recipe.CircuitCutterRecipe.TYPE)) {
                    recipes.add(com.yongaishide.chaosworld.block.entity.processing.MultiblockProcessingRecipe
                            .fromCircuitCutter(holder.id(), holder.value()));
                }
            } catch (Throwable ignored) {
                // The slicer maps the ExtendedAE Circuit Cutter recipes; never break ticking on compat issues
            }
        }
        return recipes;
    }
}

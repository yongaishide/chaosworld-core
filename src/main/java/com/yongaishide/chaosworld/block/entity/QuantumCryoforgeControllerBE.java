package com.yongaishide.chaosworld.block.entity;

import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import com.yongaishide.chaosworld.block.entity.pattern.QuantumCryoforgePatternFactory;
import com.yongaishide.chaosworld.block.entity.processing.MultiblockProcessingRecipe;
import com.yongaishide.chaosworld.init.ModBlockEntities;
import com.yongaishide.chaosworld.init.ModRecipes;
import com.yongaishide.chaosworld.recipe.UniversalMultiblockMachineKind;
import com.yongaishide.chaosworld.screen.QuantumCryoforgeControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuantumCryoforgeControllerBE extends AbstractParallelMultiblockControllerBE {

    private static MultiblockPattern PATTERN;

    public QuantumCryoforgeControllerBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_CRYOFORGE_CONTROLLER_BE.get(), pos, state);
    }

    @Override
    protected MultiblockPattern getControllerPattern() {
        if (PATTERN == null) {
            PATTERN = QuantumCryoforgePatternFactory.getPattern();
        }
        return PATTERN;
    }

    @Override
    protected String getControllerTranslationKey() {
        return "block.chaosworld_core.quantum_cryoforge_controller";
    }

    @Override
    protected double getHeatGenerationMultiplier() {
        return 0.5D;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new QuantumCryoforgeControllerMenu(id, playerInventory, this);
    }

    @Override
    protected List<MultiblockProcessingRecipe> getAvailableRecipes() {
        if (this.level == null) {
            return List.of();
        }

        List<MultiblockProcessingRecipe> recipes = new ArrayList<>();
        for (var holder : this.level.getRecipeManager().getAllRecipesFor(ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get())) {
            var recipe = holder.value();
            if (recipe.getMachine() == UniversalMultiblockMachineKind.QUANTUM_CRYOFORGE) {
                recipes.add(MultiblockProcessingRecipe.fromUniversal(holder.id(), recipe));
            }
        }
        return recipes;
    }
}

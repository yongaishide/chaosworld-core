package com.yongaishide.chaosworld.block.entity;

import com.moakiee.ae2lt.registry.ModRecipeTypes;
import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import com.yongaishide.chaosworld.block.entity.pattern.QpaPatternFactory;
import com.yongaishide.chaosworld.block.entity.processing.MultiblockProcessingRecipe;
import com.yongaishide.chaosworld.init.ModBlockEntities;
import com.yongaishide.chaosworld.init.ModRecipes;
import com.yongaishide.chaosworld.recipe.UniversalMultiblockMachineKind;
import com.yongaishide.chaosworld.screen.QuantumProcessorAssemblerControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuantumProcessorAssemblerControllerBE extends AbstractParallelMultiblockControllerBE {

    private static MultiblockPattern PATTERN;

    public QuantumProcessorAssemblerControllerBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_BE.get(), pos, state);
    }

    @Override
    protected MultiblockPattern getControllerPattern() {
        if (PATTERN == null) {
            PATTERN = QpaPatternFactory.getPattern();
        }
        return PATTERN;
    }

    @Override
    protected String getControllerTranslationKey() {
        return "block.chaosworld_core.quantum_processing_factory_controller";
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new QuantumProcessorAssemblerControllerMenu(id, playerInventory, this);
    }

    @Override
    protected List<MultiblockProcessingRecipe> getAvailableRecipes() {
        if (this.level == null) {
            return List.of();
        }
        List<MultiblockProcessingRecipe> recipes = new ArrayList<>();
        var recipeManager = this.level.getRecipeManager();

        for (var holder : recipeManager.getAllRecipesFor(ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get())) {
            var recipe = holder.value();
            if (recipe.getMachine() == UniversalMultiblockMachineKind.QUANTUM_PROCESSOR_ASSEMBLER) {
                recipes.add(MultiblockProcessingRecipe.fromUniversal(holder.id(), recipe));
            }
        }

        for (var holder : recipeManager.getAllRecipesFor(ModRecipeTypes.OVERLOAD_PROCESSING_TYPE.get())) {
            var recipe = holder.value();
            if (!recipe.isIncomplete()) {
                recipes.add(MultiblockProcessingRecipe.fromOverload(holder.id(), recipe));
            }
        }

        return recipes;
    }
}

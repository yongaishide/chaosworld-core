package com.yongaishide.chaosworld.block.entity;

import com.yongaishide.chaosworld.api.multiblock.MultiblockPattern;
import com.yongaishide.chaosworld.block.entity.pattern.QmfPatternFactory;
import com.yongaishide.chaosworld.block.entity.processing.MultiblockProcessingRecipe;
import com.yongaishide.chaosworld.init.ModBlockEntities;
import com.yongaishide.chaosworld.init.ModRecipes;
import com.yongaishide.chaosworld.recipe.UniversalMultiblockMachineKind;
import com.yongaishide.chaosworld.screen.QmfControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QmfControllerBE extends AbstractParallelMultiblockControllerBE {

    private static MultiblockPattern PATTERN;

    public QmfControllerBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QMF_CONTROLLER.get(), pos, state);
    }

    @Override
    protected MultiblockPattern getControllerPattern() {
        if (PATTERN == null) {
            PATTERN = QmfPatternFactory.getPattern();
        }
        return PATTERN;
    }

    @Override
    protected String getControllerTranslationKey() {
        return "block.chaosworld_core.quantum_matter_fabricator_controller";
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new QmfControllerMenu(id, playerInventory, this);
    }

    @Override
    protected List<MultiblockProcessingRecipe> getAvailableRecipes() {
        if (this.level == null) {
            return List.of();
        }

        List<MultiblockProcessingRecipe> recipes = new ArrayList<>();
        for (RecipeHolder<?> holder : this.level.getRecipeManager().getAllRecipesFor(ModRecipes.QMF_TYPE.get())) {
            recipes.add(MultiblockProcessingRecipe.fromQmf(holder.id(), (com.yongaishide.chaosworld.recipe.QMFRecipe) holder.value()));
        }
        for (RecipeHolder<?> holder : this.level.getRecipeManager().getAllRecipesFor(ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get())) {
            var recipe = (com.yongaishide.chaosworld.recipe.UniversalMultiblockRecipe) holder.value();
            if (recipe.getMachine() == UniversalMultiblockMachineKind.QMF) {
                recipes.add(MultiblockProcessingRecipe.fromUniversal(holder.id(), recipe));
            }
        }
        return recipes;
    }
}

package com.yongaishide.chaosworld.block.entity;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import com.yongaishide.chaosworld.api.multiblock.MultiblockTierScaling;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinitions;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.block.entity.processing.MultiblockProcessingRecipe;
import com.yongaishide.chaosworld.block.entity.processing.ParallelProcessState;
import com.yongaishide.chaosworld.compat.mekanism.MekanismChemicalStorage;
import com.yongaishide.chaosworld.compat.mekanism.UfoMekanismKey;
import com.yongaishide.chaosworld.fluid.ModFluids;
import com.yongaishide.chaosworld.init.ModSounds;
import com.yongaishide.chaosworld.item.custom.BaseCatalystItem;
import com.yongaishide.chaosworld.item.custom.DimensionalCatalystItem;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractParallelMultiblockControllerBE extends AbstractSimpleMultiblockControllerBE implements ICraftingMachine {
    protected static final int MAX_PARALLEL_THREADS = 256;
    protected static final int OVERCLOCK_SPEED_MULTIPLIER = 5;
    private static final int RECIPE_CACHE_REFRESH_TICKS = 100;
    private static final int THERMAL_MAX = 10000;
    private static final int[] THERMAL_MAX_BY_TIER = {0, 10000, 30000, 100000};
    private static final int OVERLOAD_TICKS = 100;
    private static final float THERMAL_EXPLOSION_POWER = 30.0F;
    protected final List<ParallelProcessState> processStates = new ArrayList<>();
    private long lastClientSyncTick = Long.MIN_VALUE;
    private int lastClientSyncHash = Integer.MIN_VALUE;
    private int thermalTicker = 0;
    private int overloadTimer = -1;
    @Nullable
    private PatternContainerGroup cachedCraftingMachineInfo;
    private int cachedCraftingMachineTier = Integer.MIN_VALUE;
    @Nullable
    private List<MultiblockProcessingRecipe> cachedAvailableRecipes;
    @Nullable
    private Map<ResourceLocation, MultiblockProcessingRecipe> cachedRecipeIndex;
    private long lastRecipeCacheRefreshTick = Long.MIN_VALUE;
    private final Map<ResourceLocation, Long> pendingCrafts = new HashMap<>();

    protected AbstractParallelMultiblockControllerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.maxTemperature = THERMAL_MAX;
        for (int i = 0; i < MAX_PARALLEL_THREADS; i++) {
            this.processStates.add(new ParallelProcessState());
        }
    }

    @Override
    protected void machineTick() {
        if (!this.assembled || this.level == null) {
            this.running = false;
            this.progress = 0;
            this.maxProgress = 0;
            this.storedEnergy = 0L;
            this.maxStoredEnergy = 0L;
            this.displayedRecipes.clear();
            this.aeConnected = false;
            updateTemperature(0, null, null, CatalystProfile.DEFAULT);
            syncClientState(false);
            return;
        }

        RecipeSnapshot recipes = getRecipeSnapshot();
        List<MultiblockProcessingRecipe> availableRecipes = recipes.recipes();
        Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex = recipes.index();

        AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
        this.aeConnected = nodeBE != null;
        if (nodeBE == null || nodeBE.getActionableNode() == null) {
            clearProcessStates();
            refreshProcessStates(recipeIndex);
            this.running = false;
            this.progress = 0;
            this.maxProgress = 0;
            this.storedEnergy = 0L;
            this.maxStoredEnergy = 0L;
            rebuildDisplayedRecipes(recipeIndex);
            updateTemperature(0, null, null, CatalystProfile.DEFAULT);
            syncClientState(false);
            return;
        }

        IGridNode node = nodeBE.getActionableNode();
        IGrid grid = node.getGrid();
        if (grid == null) {
            clearProcessStates();
            refreshProcessStates(recipeIndex);
            this.running = false;
            this.progress = 0;
            this.maxProgress = 0;
            this.storedEnergy = 0L;
            this.maxStoredEnergy = 0L;
            rebuildDisplayedRecipes(recipeIndex);
            updateTemperature(0, null, null, CatalystProfile.DEFAULT);
            syncClientState(false);
            return;
        }

        IEnergyService energyService = grid.getEnergyService();
        IStorageService storageService = grid.getStorageService();
        MEStorage inventory = storageService.getInventory();
        IActionSource src = IActionSource.ofMachine(nodeBE);
        refreshProcessStates(recipeIndex);
        fillPendingCrafts();
        CatalystProfile catalystProfile = getCatalystProfile();

        boolean anyRunning = false;
        int hottestMaxProgress = 0;
        int hottestProgress = 0;
        int runningThreads = 0;
        boolean thermalLocked = this.safeMode && this.temperature >= this.maxTemperature;
        int parallelLimit = getParallelThreadLimit();

        for (ParallelProcessState processState : this.processStates) {
            if (!processState.isActive()) {
                continue;
            }

            MultiblockProcessingRecipe recipe = recipeIndex.get(processState.getRecipeId());
            if (recipe == null) {
                processState.clear();
                continue;
            }

            int scaledMaxProgress = getAdjustedProcessingTime(recipe, catalystProfile);
            if (scaledMaxProgress > hottestMaxProgress) {
                hottestMaxProgress = scaledMaxProgress;
                hottestProgress = processState.getProgress();
            }

            if (!MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier()) || thermalLocked) {
                continue;
            }

            if (runningThreads >= parallelLimit) {
                continue;
            }

            processState.resizeBuffers(recipe.itemInputs().size(), recipe.fluidInputs().size(), recipe.chemicalInputs().size());
            long scaledEnergy = getAdjustedEnergyCost(recipe, catalystProfile);
            chargeEnergy(processState, energyService, scaledEnergy);
            boolean materialsFulfilled = pullIngredients(processState, recipe, inventory, src);
            if (!materialsFulfilled || processState.getEnergyBuffer() < scaledEnergy) {
                continue;
            }

            runningThreads++;
            anyRunning = true;
            processState.setProgress(processState.getProgress() + getProgressPerTick());
            if (processState.getProgress() >= scaledMaxProgress) {
                finishRecipe(processState, recipe, inventory, src);
            }
        }

        this.running = anyRunning;
        this.maxProgress = hottestMaxProgress;
        this.progress = hottestProgress;
        updateDisplayedEnergy(recipeIndex, catalystProfile);
        rebuildDisplayedRecipes(recipeIndex, catalystProfile);
        updateTemperature(runningThreads, inventory, src, catalystProfile);
        this.setChanged();
        syncClientState(true);
    }

    private Map<ResourceLocation, MultiblockProcessingRecipe> indexRecipes(List<MultiblockProcessingRecipe> availableRecipes) {
        Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex = new HashMap<>(availableRecipes.size());
        for (MultiblockProcessingRecipe recipe : availableRecipes) {
            recipeIndex.put(recipe.id(), recipe);
        }
        return recipeIndex;
    }

    private RecipeSnapshot getRecipeSnapshot() {
        long gameTime = this.level != null ? this.level.getGameTime() : 0L;
        if (this.cachedAvailableRecipes == null
                || this.cachedRecipeIndex == null
                || this.lastRecipeCacheRefreshTick == Long.MIN_VALUE
                || gameTime - this.lastRecipeCacheRefreshTick >= RECIPE_CACHE_REFRESH_TICKS) {
            refreshRecipeCache(gameTime);
        }
        return new RecipeSnapshot(this.cachedAvailableRecipes, this.cachedRecipeIndex);
    }

    private void refreshRecipeCache(long gameTime) {
        List<MultiblockProcessingRecipe> recipes = List.copyOf(getAvailableRecipes());
        this.cachedAvailableRecipes = recipes;
        this.cachedRecipeIndex = indexRecipes(recipes);
        this.lastRecipeCacheRefreshTick = gameTime;
    }

    private void invalidateRecipeCache() {
        this.cachedAvailableRecipes = null;
        this.cachedRecipeIndex = null;
        this.lastRecipeCacheRefreshTick = Long.MIN_VALUE;
    }

    private void refreshProcessStates(Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex) {
        for (ParallelProcessState state : this.processStates) {
            if (!state.isActive()) {
                continue;
            }

            MultiblockProcessingRecipe recipe = recipeIndex.get(state.getRecipeId());
            if (recipe == null || !MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier())) {
                state.clear();
                continue;
            }

            state.resizeBuffers(recipe.itemInputs().size(), recipe.fluidInputs().size(), recipe.chemicalInputs().size());
            if (!state.isPatternPushed() && !state.hasBufferedWork()) {
                state.clear();
            }
        }
    }

    private void clearProcessStates() {
        for (ParallelProcessState state : this.processStates) {
            state.clear();
        }
    }

    protected int getParallelThreadLimit() {
        int base = switch (this.machineTier) {
            case 3 -> 64;
            case 2 -> 16;
            default -> 4;
        };
        int limit = this.safeMode ? base : base * 4;
        return Math.min(limit, MAX_PARALLEL_THREADS);
    }

    @Override
    public int getGuiOverloadTimer() {
        return this.overloadTimer;
    }

    protected int getActiveProcessCount() {
        int count = 0;
        for (ParallelProcessState state : this.processStates) {
            if (state.isActive()) {
                count++;
            }
        }
        return count;
    }

    protected int getProgressPerTick() {
        return this.overclocked ? OVERCLOCK_SPEED_MULTIPLIER : 1;
    }

    protected double getHeatGenerationMultiplier() {
        return 1.0D;
    }

    private void chargeEnergy(ParallelProcessState state, IEnergyService energyService, long targetEnergy) {
        if (state.getEnergyBuffer() >= targetEnergy) {
            return;
        }
        long needed = targetEnergy - state.getEnergyBuffer();
        double extracted = energyService.extractAEPower(needed, Actionable.MODULATE, PowerMultiplier.CONFIG);
        state.setEnergyBuffer(state.getEnergyBuffer() + (long) extracted);
    }

    private boolean pullIngredients(ParallelProcessState state, MultiblockProcessingRecipe recipe, MEStorage inventory, IActionSource src) {
        boolean materialsFulfilled = true;

        for (int i = 0; i < recipe.itemInputs().size(); i++) {
            var requirement = recipe.itemInputs().get(i);
            if (state.getItemBuffers()[i] >= requirement.amount()) {
                continue;
            }
            long needed = requirement.amount() - state.getItemBuffers()[i];
            long toExtract = Math.min(needed, 100_000L);
            for (ItemStack match : requirement.ingredient().getItems()) {
                long extracted = inventory.extract(AEItemKey.of(match), toExtract, Actionable.MODULATE, src);
                state.getItemBuffers()[i] += extracted;
                toExtract -= extracted;
                if (toExtract <= 0) {
                    break;
                }
            }
            if (state.getItemBuffers()[i] < requirement.amount()) {
                materialsFulfilled = false;
            }
        }

        for (int i = 0; i < recipe.fluidInputs().size(); i++) {
            var requirement = recipe.fluidInputs().get(i);
            if (state.getFluidBuffers()[i] >= requirement.amount()) {
                continue;
            }
            long needed = requirement.amount() - state.getFluidBuffers()[i];
            long extracted = inventory.extract(AEFluidKey.of(requirement.fluid().getFluid()), Math.min(needed, 1_000_000L), Actionable.MODULATE, src);
            state.getFluidBuffers()[i] += extracted;
            if (state.getFluidBuffers()[i] < requirement.amount()) {
                materialsFulfilled = false;
            }
        }

        for (int i = 0; i < recipe.chemicalInputs().size(); i++) {
            var requirement = recipe.chemicalInputs().get(i);
            if (state.getChemicalBuffers()[i] >= requirement.amount()) {
                continue;
            }
            long needed = requirement.amount() - state.getChemicalBuffers()[i];
            long extracted = extractChemicalFromHatches(requirement.chemicalId(), Math.min(needed, 1_000_000L));
            state.getChemicalBuffers()[i] += extracted;
            if (state.getChemicalBuffers()[i] < requirement.amount()) {
                materialsFulfilled = false;
            }
        }

        return materialsFulfilled;
    }

    private long extractChemicalFromHatches(ResourceLocation chemicalId, long amount) {
        if (this.level == null || amount <= 0L) {
            return 0L;
        }

        long remaining = amount;
        long extractedTotal = 0L;
        for (BlockPos partPos : this.parts) {
            if (!(this.level.getBlockEntity(partPos) instanceof MekanismChemicalStorage storage) || !storage.supportsChemicalIO()) {
                continue;
            }

            ResourceLocation storedId = storage.getStoredChemicalId();
            if (storedId == null || !storedId.equals(chemicalId)) {
                continue;
            }

            long extracted = Math.min(remaining, storage.getStoredChemicalAmount());
            if (extracted <= 0L) {
                continue;
            }

            storage.setStoredChemical(storedId, storage.getStoredChemicalAmount() - extracted);
            extractedTotal += extracted;
            remaining -= extracted;
            if (remaining <= 0L) {
                break;
            }
        }
        return extractedTotal;
    }

    private void finishRecipe(ParallelProcessState state, MultiblockProcessingRecipe recipe, MEStorage inventory, IActionSource src) {
        CatalystProfile catalystProfile = getCatalystProfile();
        for (var output : recipe.outputs()) {
            if (!output.item().isEmpty()) {
                inventory.insert(AEItemKey.of(output.item()), getAdjustedItemOutputAmount(output.amount(), catalystProfile), Actionable.MODULATE, src);
            }
            if (!output.fluid().isEmpty()) {
                inventory.insert(AEFluidKey.of(output.fluid().getFluid()), getAdjustedItemOutputAmount(output.amount(), catalystProfile), Actionable.MODULATE, src);
            }
        }

        state.clear();
    }

    private void rebuildDisplayedRecipes(Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex) {
        rebuildDisplayedRecipes(recipeIndex, getCatalystProfile());
    }

    private void rebuildDisplayedRecipes(Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex, CatalystProfile catalystProfile) {
        this.displayedRecipes.clear();
        for (ParallelProcessState processState : this.processStates) {
            if (!processState.isActive()) {
                continue;
            }
            MultiblockProcessingRecipe recipe = recipeIndex.get(processState.getRecipeId());
            if (recipe == null) {
                continue;
            }
            var primaryOutput = recipe.primaryOutput();
            int scaledMaxProgress = getAdjustedProcessingTime(recipe, catalystProfile);
            int displayedMaxProgress = getDisplayedTicks(scaledMaxProgress);
            int displayedProgress = Math.min(displayedMaxProgress, getDisplayedTicks(processState.getProgress()));
            Component label = primaryOutput.item().isEmpty()
                    ? (primaryOutput.fluid().isEmpty() ? Component.literal(recipe.name()) : primaryOutput.fluid().getHoverName())
                    : primaryOutput.item().getHoverName();
            if (!MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier())) {
                label = label.copy().append(Component.literal(" [Locked: MK" + recipe.requiredTier() + "]"));
            }
            this.displayedRecipes.add(new UniversalDisplayedRecipe(
                    primaryOutput.item(),
                    primaryOutput.fluid(),
                    label,
                    primaryOutput.item().isEmpty() ? primaryOutput.amount() : getMaximumAdjustedItemOutputAmount(primaryOutput.amount(), catalystProfile),
                    displayedProgress,
                    displayedMaxProgress));
        }
    }

    protected int getDisplayedTicks(int rawTicks) {
        int divisor = getProgressPerTick();
        return Math.max(0, (rawTicks + divisor - 1) / divisor);
    }

    private void updateDisplayedEnergy(Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex, CatalystProfile catalystProfile) {
        long bufferedEnergy = 0L;
        long targetEnergy = 0L;
        for (ParallelProcessState processState : this.processStates) {
            if (!processState.isActive()) {
                continue;
            }

            bufferedEnergy += Math.max(0L, processState.getEnergyBuffer());
            MultiblockProcessingRecipe recipe = recipeIndex.get(processState.getRecipeId());
            if (recipe != null) {
                targetEnergy += Math.max(0L, getAdjustedEnergyCost(recipe, catalystProfile));
            }
        }

        this.storedEnergy = bufferedEnergy;
        this.maxStoredEnergy = targetEnergy;
    }

    private void updateTemperature(int activeThreads, @Nullable MEStorage inventory, @Nullable IActionSource src, CatalystProfile catalystProfile) {
        this.thermalTicker++;

        if (catalystProfile.creative()) {
            if (this.temperature > 0 && inventory != null && src != null) {
                this.temperature -= consumeCoolant(inventory, src);
            }
        } else if (activeThreads > 0) {
            if (this.thermalTicker % 2 == 0) {
                int baseHeat = Math.max(1, activeThreads) * (this.overclocked ? 5 : 1);
                int heatToAdd = Math.max(0, (int) Math.ceil(baseHeat * getHeatGenerationMultiplier() * catalystProfile.heatMultiplier()));
                this.temperature = Math.min(this.maxTemperature, this.temperature + heatToAdd);
            }
        } else if (this.temperature > 0 && this.thermalTicker % 40 == 0) {
            this.temperature -= 1;
        }

        if (this.temperature > 0 && inventory != null && src != null) {
            this.temperature -= consumeCoolant(inventory, src);
        }

        if (this.temperature < 0) {
            this.temperature = 0;
        }

        if (this.safeMode) {
            this.overloadTimer = -1;
            return;
        }

        if (this.temperature >= this.maxTemperature) {
            if (this.overloadTimer == -1) {
                this.overloadTimer = OVERLOAD_TICKS;
            }
        } else {
            this.overloadTimer = -1;
        }

        if (this.overloadTimer > 0) {
            if (this.level != null && this.overloadTimer % 20 == 0) {
                this.level.playSound(null, this.worldPosition, ModSounds.DMA_ALARM.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.6f, 0.8f);
            }

            this.overloadTimer--;
            if (this.overloadTimer == 0) {
                triggerThermalExplosion();
            }
        }
    }

    private int consumeCoolant(MEStorage inventory, IActionSource src) {
        for (AEFluidKey coolantKey : getCoolantPriority()) {
            if (coolantKey == null || coolantKey.getFluid() == Fluids.EMPTY) {
                continue;
            }

            CoolantProfile profile = getCoolantProfile(coolantKey.getFluid());
            long simulatedAvailable = inventory.extract(coolantKey, profile.maxConsumePerTick(), Actionable.SIMULATE, src);
            if (simulatedAvailable <= 0L) {
                continue;
            }

            long amountToConsume;
            long heatCooled;
            if (profile.millibucketsPerHeat() > 0) {
                amountToConsume = Math.min(simulatedAvailable, profile.maxConsumePerTick());
                long possibleHeat = amountToConsume / profile.millibucketsPerHeat();
                heatCooled = Math.min(this.temperature, possibleHeat);
                amountToConsume = heatCooled * profile.millibucketsPerHeat();
            } else {
                amountToConsume = Math.min(simulatedAvailable, profile.maxConsumePerTick());
                long possibleHeat = amountToConsume * profile.heatPerMillibucket();
                if (this.temperature < possibleHeat) {
                    amountToConsume = Math.max(1L,
                            (long) Math.ceil(this.temperature / (double) profile.heatPerMillibucket()));
                }
                heatCooled = Math.min(this.temperature, amountToConsume * profile.heatPerMillibucket());
            }

            if (amountToConsume <= 0L || heatCooled <= 0L) {
                continue;
            }

            long extracted = inventory.extract(coolantKey, amountToConsume, Actionable.MODULATE, src);
            if (extracted <= 0L) {
                continue;
            }

            if (profile.millibucketsPerHeat() > 0) {
                return (int) Math.min(this.temperature, extracted / profile.millibucketsPerHeat());
            }

            return (int) Math.min(this.temperature, extracted * profile.heatPerMillibucket());
        }

        return 0;
    }

    private AEFluidKey[] getCoolantPriority() {
        AEFluidKey tier1 = AEFluidKey.of(ModFluids.SOURCE_GELID_CRYOTHEUM.get());
        AEFluidKey tier2 = AEFluidKey.of(ModFluids.SOURCE_STABLE_COOLANT.get());
        AEFluidKey tier3 = AEFluidKey.of(ModFluids.SOURCE_TEMPORAL_FLUID.get());
        return switch (this.machineTier) {
            case 3 -> new AEFluidKey[]{tier3, tier2, tier1};
            case 2 -> new AEFluidKey[]{tier2, tier3, tier1};
            default -> new AEFluidKey[]{tier1, tier2, tier3};
        };
    }

    private CoolantProfile getCoolantProfile(Fluid fluid) {
        if (fluid == ModFluids.SOURCE_TEMPORAL_FLUID.get() || fluid == ModFluids.FLOWING_TEMPORAL_FLUID.get()) {
            return new CoolantProfile(100, 0, 100);
        }
        if (fluid == ModFluids.SOURCE_STABLE_COOLANT.get() || fluid == ModFluids.FLOWING_STABLE_COOLANT.get()) {
            return new CoolantProfile(50, 0, 100);
        }
        if (fluid == ModFluids.SOURCE_GELID_CRYOTHEUM.get() || fluid == ModFluids.FLOWING_GELID_CRYOTHEUM.get()) {
            return new CoolantProfile(0, 120, 10_000);
        }
        return new CoolantProfile(15, 0, 100);
    }

    private void triggerThermalExplosion() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        Level level = this.level;
        level.explode(null,
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5,
                THERMAL_EXPLOSION_POWER,
                Level.ExplosionInteraction.BLOCK);
        onControllerBroken();
        removeControllerBlockAfterExplosion();
        this.temperature = 0;
        this.overloadTimer = -1;
        this.running = false;
        this.progress = 0;
        this.maxProgress = 0;
        clearProcessStates();
        updateDisplayedEnergy(Map.of(), CatalystProfile.DEFAULT);
        this.displayedRecipes.clear();
        saveChanges();
    }

    private void removeControllerBlockAfterExplosion() {
        if (this.level == null) {
            return;
        }

        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return;
        }

        this.level.removeBlock(this.worldPosition, false);
    }

    private AENetworkedBlockEntity getConnectedNetworkNode() {
        if (this.level == null) {
            return null;
        }
        for (BlockPos position : this.parts) {
            if (this.level.getBlockEntity(position) instanceof AENetworkedBlockEntity nodeBE) {
                if (nodeBE.getActionableNode() != null
                        && nodeBE.getActionableNode().getGrid() != null
                        && nodeBE.getActionableNode().isActive()) {
                    return nodeBE;
                }
            }
        }
        return null;
    }

    private CatalystProfile getCatalystProfile() {
        double heatMultiplier = 1.0D;
        double speedMultiplier = 1.0D;
        double energyMultiplier = 1.0D;
        double bonusDropChance = 0.0D;
        boolean creative = false;
        int identicalCount = 0;
        int catalystCount = 0;
        BaseCatalystItem firstCatalyst = null;
        boolean synergyPossible = true;

        for (int i = 0; i < this.upgrades.size(); i++) {
            ItemStack upgradeStack = this.upgrades.getStackInSlot(i);
            if (upgradeStack.isEmpty()) {
                synergyPossible = false;
                continue;
            }

            if (upgradeStack.getItem() instanceof DimensionalCatalystItem) {
                creative = true;
                synergyPossible = false;
                continue;
            }

            if (upgradeStack.getItem() instanceof BaseCatalystItem catalyst) {
                catalystCount++;
                if (firstCatalyst == null) {
                    firstCatalyst = catalyst;
                    identicalCount++;
                } else if (firstCatalyst == catalyst) {
                    identicalCount++;
                } else {
                    synergyPossible = false;
                }
                continue;
            }

            synergyPossible = false;
        }

        // Stacking coefficient (shared by heat and effects): 2 -> x1.75, 3 -> x2.25, 4 -> x3
        double coefficient = switch (catalystCount) {
            case 2 -> 1.75D;
            case 3 -> 2.25D;
            case 4 -> 3.0D;
            default -> 1.0D;
        };

        if (firstCatalyst != null) {
            // Single catalyst heat multiplier, scaled by the stacking coefficient
            heatMultiplier = (1.0D + firstCatalyst.getStaticHeat() / 100.0D) * coefficient;
            // Single catalyst effect, scaled by the stacking coefficient
            speedMultiplier = firstCatalyst.getSpeedMultiplier() * coefficient;
            double power = firstCatalyst.getPowerMultiplier();
            if (power < 1.0D) {
                // Energy is a reduction: apply the coefficient to the reduction amount,
                // but never reduce the cost below 10% of the base.
                energyMultiplier = Math.max(0.1D, 1.0D - (1.0D - power) * coefficient);
            } else {
                energyMultiplier = power;
            }
            bonusDropChance = firstCatalyst.getBonusDropChance() * coefficient;
        }

        if (synergyPossible && identicalCount == 4 && firstCatalyst != null) {
            if ("chrono".equals(firstCatalyst.getFamily())) {
                speedMultiplier *= 2.0D;
            } else if ("matterflow".equals(firstCatalyst.getFamily())) {
                energyMultiplier *= 0.5D;
            } else if ("quantum".equals(firstCatalyst.getFamily())) {
                bonusDropChance += 0.5D;
            }
        }

        if (creative) {
            return CatalystProfile.CREATIVE;
        }

        return new CatalystProfile(
                false,
                Math.max(0.0D, heatMultiplier),
                Math.max(0.01D, speedMultiplier),
                Math.max(0.0D, energyMultiplier),
                Math.max(0.0D, bonusDropChance));
    }

    private int getAdjustedProcessingTime(MultiblockProcessingRecipe recipe, CatalystProfile catalystProfile) {
        if (catalystProfile.creative()) {
            return 1;
        }
        int tierAdjustedTime = MultiblockTierScaling.adjustedTime(recipe.time(), this.machineTier, recipe.requiredTier());
        return Math.max(1, (int) Math.ceil(tierAdjustedTime / catalystProfile.speedMultiplier()));
    }

    private long getAdjustedEnergyCost(MultiblockProcessingRecipe recipe, CatalystProfile catalystProfile) {
        if (catalystProfile.creative()) {
            return 0L;
        }
        long tierAdjustedEnergy = MultiblockTierScaling.adjustedEnergy(recipe.energy(), this.machineTier, recipe.requiredTier());
        return Math.max(1L, (long) Math.ceil(tierAdjustedEnergy * catalystProfile.energyMultiplier()));
    }

    private long getAdjustedItemOutputAmount(long baseAmount, CatalystProfile catalystProfile) {
        if (baseAmount <= 0L) {
            return 0L;
        }

        double bonusChance = Math.max(0.0D, catalystProfile.bonusDropChance());
        long bonusRolls = (long) bonusChance;
        double fractionalBonusRoll = bonusChance - bonusRolls;
        if (fractionalBonusRoll > 0.0D && this.level != null && this.level.random.nextDouble() < fractionalBonusRoll) {
            bonusRolls++;
        }

        return saturatedMultiply(baseAmount, 1L + bonusRolls);
    }

    private long getMaximumAdjustedItemOutputAmount(long baseAmount, CatalystProfile catalystProfile) {
        if (baseAmount <= 0L) {
            return 0L;
        }
        return saturatedMultiply(baseAmount, 1L + (long) Math.ceil(Math.max(0.0D, catalystProfile.bonusDropChance())));
    }

    private long saturatedMultiply(long value, long multiplier) {
        if (value <= 0L || multiplier <= 0L) {
            return 0L;
        }
        if (value > Long.MAX_VALUE / multiplier) {
            return Long.MAX_VALUE;
        }
        return value * multiplier;
    }

    protected abstract List<MultiblockProcessingRecipe> getAvailableRecipes();

    protected MultiblockProcessingRecipe findRecipe(List<MultiblockProcessingRecipe> availableRecipes, ResourceLocation recipeId) {
        for (MultiblockProcessingRecipe recipe : availableRecipes) {
            if (recipe.id().equals(recipeId)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public PatternContainerGroup getCraftingMachineInfo() {
        if (this.cachedCraftingMachineInfo == null || this.cachedCraftingMachineTier != this.machineTier) {
            this.cachedCraftingMachineTier = this.machineTier;
            this.cachedCraftingMachineInfo = new PatternContainerGroup(
                    AEItemKey.of(this.getBlockState().getBlock().asItem()),
                    Component.translatable(getControllerTranslationKey()),
                    List.of(Component.literal("MK" + this.machineTier)));
        }
        return this.cachedCraftingMachineInfo;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputs, net.minecraft.core.Direction ejectionDirection) {
        if (!this.assembled) {
            return false;
        }

        MultiblockProcessingRecipe recipe = resolvePatternRecipe(patternDetails, inputs);
        if (recipe == null || !MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier())) {
            return false;
        }

        long scale = computePatternScale(patternDetails, recipe);
        this.pendingCrafts.merge(recipe.id(), scale, Long::sum);

        for (KeyCounter input : inputs) {
            input.clear();
        }

        rebuildDisplayedRecipes(getRecipeSnapshot().index());
        saveChanges();
        return true;
    }

    @Override
    public boolean acceptsPlans() {
        return this.assembled;
    }

    private void fillPendingCrafts() {
        if (this.pendingCrafts.isEmpty()) {
            return;
        }
        int parallelLimit = getParallelThreadLimit();
        int activeCount = getActiveProcessCount();
        if (activeCount >= parallelLimit) {
            return;
        }
        var recipeIndex = getRecipeSnapshot().index();
        for (ParallelProcessState state : this.processStates) {
            if (state.isActive()) {
                continue;
            }
            if (activeCount >= parallelLimit) {
                break;
            }
            var iterator = this.pendingCrafts.entrySet().iterator();
            if (!iterator.hasNext()) {
                break;
            }
            var entry = iterator.next();
            ResourceLocation recipeId = entry.getKey();
            MultiblockProcessingRecipe recipe = recipeIndex.get(recipeId);
            if (recipe == null) {
                iterator.remove();
                continue;
            }
            state.clear();
            state.setRecipeId(recipeId);
            state.setPatternPushed(true);
            state.resizeBuffers(recipe.itemInputs().size(), recipe.fluidInputs().size(), recipe.chemicalInputs().size());
            state.setEnergyBuffer(0L);
            state.setProgress(0);
            activeCount++;
            if (entry.getValue() <= 1L) {
                iterator.remove();
            } else {
                entry.setValue(entry.getValue() - 1L);
            }
        }
    }

    private MultiblockProcessingRecipe resolvePatternRecipe(IPatternDetails patternDetails, KeyCounter[] inputs) {
        List<MultiblockProcessingRecipe> outputMatches = new ArrayList<>();
        for (MultiblockProcessingRecipe recipe : getRecipeSnapshot().recipes()) {
            if (MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier())
                    && patternMatchesOutputs(patternDetails.getOutputs(), recipe.outputs())) {
                outputMatches.add(recipe);
            }
        }

        if (outputMatches.isEmpty()) {
            return null;
        }

        if (outputMatches.size() == 1) {
            return outputMatches.getFirst();
        }

        for (MultiblockProcessingRecipe recipe : outputMatches) {
            if (patternMatchesInputs(inputs, recipe)) {
                return recipe;
            }
        }

        return null;
    }

    private long computePatternScale(IPatternDetails patternDetails, MultiblockProcessingRecipe recipe) {
        var primaryOutput = recipe.primaryOutput();
        if (primaryOutput.amount() <= 0L) {
            return 1L;
        }
        AEKey recipeKey = primaryOutput.item().isEmpty()
                ? AEFluidKey.of(primaryOutput.fluid().getFluid())
                : AEItemKey.of(primaryOutput.item());
        if (recipeKey == null) {
            return 1L;
        }
        for (GenericStack patternOutput : patternDetails.getOutputs()) {
            if (recipeKey.equals(patternOutput.what()) && patternOutput.amount() > 0L) {
                return Math.max(1L, patternOutput.amount() / primaryOutput.amount());
            }
        }
        return 1L;
    }

    private boolean patternMatchesInputs(KeyCounter[] inputs, MultiblockProcessingRecipe recipe) {
        List<PatternStack> availableStacks = flattenInputs(inputs);
        if (availableStacks.isEmpty() && (!recipe.itemInputs().isEmpty() || !recipe.fluidInputs().isEmpty())) {
            return false;
        }

        List<PatternStack> remaining = new ArrayList<>(availableStacks);
        for (var requirement : recipe.itemInputs()) {
            if (!removeMatchingItemRequirement(remaining, requirement)) {
                return false;
            }
        }
        for (var requirement : recipe.fluidInputs()) {
            if (!removeMatchingFluidRequirement(remaining, requirement)) {
                return false;
            }
        }
        for (var requirement : recipe.chemicalInputs()) {
            if (!removeMatchingChemicalRequirement(remaining, requirement)) {
                return false;
            }
        }
        return remaining.isEmpty();
    }

    private boolean patternMatchesOutputs(List<GenericStack> outputs, List<MultiblockProcessingRecipe.OutputStack> recipeOutputs) {
        if (outputs.size() != recipeOutputs.size()) {
            return false;
        }

        List<PatternStack> remaining = new ArrayList<>();
        for (GenericStack output : outputs) {
            remaining.add(new PatternStack(output.what(), output.amount()));
        }

        for (var output : recipeOutputs) {
            AEKey expectedKey = !output.item().isEmpty()
                    ? AEItemKey.of(output.item())
                    : AEFluidKey.of(output.fluid().getFluid());
            if (expectedKey == null) {
                return false;
            }

            boolean matched = false;
            for (int i = 0; i < remaining.size(); i++) {
                PatternStack candidate = remaining.get(i);
                if (candidate.key.equals(expectedKey)) {
                    remaining.remove(i);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return false;
            }
        }

        return remaining.isEmpty();
    }

    private List<PatternStack> flattenInputs(KeyCounter[] inputs) {
        List<PatternStack> stacks = new ArrayList<>();
        for (KeyCounter counter : inputs) {
            for (var entry : counter) {
                stacks.add(new PatternStack(entry.getKey(), entry.getLongValue()));
            }
        }
        return stacks;
    }

    private boolean removeMatchingItemRequirement(List<PatternStack> remaining, MultiblockProcessingRecipe.ItemRequirement requirement) {
        for (int i = 0; i < remaining.size(); i++) {
            PatternStack stack = remaining.get(i);
            if (stack.key instanceof AEItemKey itemKey
                    && stack.amount > 0L
                    && requirement.ingredient().test(itemKey.toStack((int) Math.max(1, stack.amount)))) {
                remaining.remove(i);
                return true;
            }
        }
        return false;
    }

    private boolean removeMatchingFluidRequirement(List<PatternStack> remaining, MultiblockProcessingRecipe.FluidRequirement requirement) {
        for (int i = 0; i < remaining.size(); i++) {
            PatternStack stack = remaining.get(i);
            if (stack.key instanceof AEFluidKey fluidKey
                    && stack.amount > 0L
                    && fluidKey.getFluid() == requirement.fluid().getFluid()) {
                remaining.remove(i);
                return true;
            }
        }
        return false;
    }

    private boolean removeMatchingChemicalRequirement(List<PatternStack> remaining, MultiblockProcessingRecipe.ChemicalRequirement requirement) {
        for (int i = 0; i < remaining.size(); i++) {
            PatternStack stack = remaining.get(i);
            if (stack.key instanceof UfoMekanismKey chemicalKey
                    && stack.amount > 0L
                    && chemicalKey.getId().equals(requirement.chemicalId())) {
                remaining.remove(i);
                return true;
            }
        }
        return false;
    }

    private record PatternStack(AEKey key, long amount) {
    }

    private record RecipeSnapshot(
            List<MultiblockProcessingRecipe> recipes,
            Map<ResourceLocation, MultiblockProcessingRecipe> index) {
    }

    private record CoolantProfile(int heatPerMillibucket, int millibucketsPerHeat, long maxConsumePerTick) {
    }

    private record CatalystProfile(
            boolean creative,
            double heatMultiplier,
            double speedMultiplier,
            double energyMultiplier,
            double bonusDropChance) {
        private static final CatalystProfile DEFAULT = new CatalystProfile(false, 1.0D, 1.0D, 1.0D, 0.0D);
        private static final CatalystProfile CREATIVE = new CatalystProfile(true, 0.0D, 1000.0D, 0.0D, 1.0D);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag processTags = new ListTag();
        for (ParallelProcessState state : this.processStates) {
            processTags.add(state.save(registries));
        }
        tag.put("processStates", processTags);
        tag.putInt("thermalTicker", this.thermalTicker);
        tag.putInt("overloadTimer", this.overloadTimer);
        ListTag pendingTags = new ListTag();
        for (var entry : this.pendingCrafts.entrySet()) {
            CompoundTag pendingTag = new CompoundTag();
            pendingTag.putString("recipe", entry.getKey().toString());
            pendingTag.putLong("count", entry.getValue());
            pendingTags.add(pendingTag);
        }
        tag.put("pendingCrafts", pendingTags);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("processStates", Tag.TAG_LIST)) {
            ListTag processTags = tag.getList("processStates", Tag.TAG_COMPOUND);
            for (int i = 0; i < Math.min(processTags.size(), this.processStates.size()); i++) {
                this.processStates.get(i).load(processTags.getCompound(i), registries);
            }
        }
        this.thermalTicker = tag.getInt("thermalTicker");
        this.overloadTimer = tag.contains("overloadTimer") ? tag.getInt("overloadTimer") : -1;
        this.maxTemperature = THERMAL_MAX_BY_TIER[this.machineTier];
        this.pendingCrafts.clear();
        if (tag.contains("pendingCrafts", Tag.TAG_LIST)) {
            ListTag pendingTags = tag.getList("pendingCrafts", Tag.TAG_COMPOUND);
            for (int i = 0; i < pendingTags.size(); i++) {
                CompoundTag pendingTag = pendingTags.getCompound(i);
                ResourceLocation recipeId = ResourceLocation.tryParse(pendingTag.getString("recipe"));
                if (recipeId != null) {
                    this.pendingCrafts.put(recipeId, pendingTag.getLong("count"));
                }
            }
        }
    }

    @Override
    public void onControllerBroken() {
        super.onControllerBroken();
        invalidateRecipeCache();
        for (ParallelProcessState processState : this.processStates) {
            processState.clear();
        }
        this.pendingCrafts.clear();
        this.storedEnergy = 0L;
        this.maxStoredEnergy = 0L;
        this.overloadTimer = -1;
    }

    @Override
    protected int resolveMachineTier(com.yongaishide.chaosworld.api.multiblock.MultiblockPattern.MatchResult result) {
        if (this.level == null) {
            return com.yongaishide.chaosworld.api.multiblock.MultiblockMachineTier.MK1.level();
        }

        BlockState controllerState = this.level.getBlockState(this.worldPosition);
        Direction facing = MultiblockControllerDefinitions.getPatternFacing(this, controllerState);

        int resolvedTier = com.yongaishide.chaosworld.api.multiblock.MultiblockMachineTier.MK3.level();
        boolean foundField = false;
        for (BlockPos fieldPos : getControllerPattern().getExpectedPositions(this.worldPosition, facing, 'F')) {
            BlockState fieldState = this.level.getBlockState(fieldPos);
            if (fieldState.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get())) {
                resolvedTier = Math.min(resolvedTier, 1);
                foundField = true;
            } else if (fieldState.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get())) {
                resolvedTier = Math.min(resolvedTier, 2);
                foundField = true;
            } else if (fieldState.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get())) {
                foundField = true;
            }
        }

        int tier = foundField ? resolvedTier : com.yongaishide.chaosworld.api.multiblock.MultiblockMachineTier.MK1.level();
        this.maxTemperature = THERMAL_MAX_BY_TIER[tier];
        return tier;
    }

    @Override
    protected boolean hasOngoingWork() {
        for (ParallelProcessState state : this.processStates) {
            if (state.isActive()) {
                return true;
            }
        }
        return super.hasOngoingWork();
    }

    @Override
    public int getGuiActiveParallels() {
        return getActiveProcessCount();
    }

    @Override
    public int getGuiMaxParallels() {
        return getParallelThreadLimit();
    }

    private void syncClientState(boolean throttle) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        int syncHash = computeClientSyncHash();
        if (syncHash == this.lastClientSyncHash) {
            return;
        }

        long gameTime = this.level.getGameTime();
        if (throttle && this.lastClientSyncTick != Long.MIN_VALUE && gameTime - this.lastClientSyncTick < 5L) {
            return;
        }

        this.lastClientSyncTick = gameTime;
        this.lastClientSyncHash = syncHash;
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
    }

    private int computeClientSyncHash() {
        int hash = Boolean.hashCode(this.assembled);
        hash = 31 * hash + Boolean.hashCode(this.running);
        hash = 31 * hash + this.progress;
        hash = 31 * hash + this.maxProgress;
        hash = 31 * hash + this.temperature;
        hash = 31 * hash + this.maxTemperature;
        hash = 31 * hash + this.machineTier;
        hash = 31 * hash + Boolean.hashCode(this.safeMode);
        hash = 31 * hash + Boolean.hashCode(this.overclocked);
        hash = 31 * hash + Boolean.hashCode(this.aeConnected);
        hash = 31 * hash + Long.hashCode(this.storedEnergy);
        hash = 31 * hash + Long.hashCode(this.maxStoredEnergy);
        hash = 31 * hash + getActiveProcessCount();
        hash = 31 * hash + computeDisplayedRecipesHash();
        return hash;
    }

    private int computeDisplayedRecipesHash() {
        int hash = 1;
        for (UniversalDisplayedRecipe recipe : this.displayedRecipes) {
            hash = 31 * hash + recipe.label().getString().hashCode();
            hash = 31 * hash + recipe.progress();
            hash = 31 * hash + recipe.maxProgress();
            hash = 31 * hash + Long.hashCode(recipe.outputAmount());
            hash = 31 * hash + java.util.Objects.hashCode(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(recipe.itemIcon().getItem()));
            hash = 31 * hash + java.util.Objects.hashCode(net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(recipe.fluidIcon().getFluid()));
        }
        return hash;
    }
}

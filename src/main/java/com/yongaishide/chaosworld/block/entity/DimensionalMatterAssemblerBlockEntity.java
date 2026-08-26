package com.yongaishide.chaosworld.block.entity;

import java.util.*;
import java.util.Comparator;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import com.yongaishide.chaosworld.block.DimensionalMatterAssemblerBlock;
import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.recipe.DimensionalMatterAssemblerRecipe;
import com.yongaishide.chaosworld.init.ModRecipes;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.*;
import appeng.api.config.Actionable;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;

import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.me.storage.CompositeStorage;
import appeng.menu.ISubMenu;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.SettingsFrom;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;
import net.pedroksl.ae2addonlib.api.IDirectionalOutputHost;

public class DimensionalMatterAssemblerBlockEntity extends AENetworkedPoweredBlockEntity
        implements IGridTickable, IUpgradeableObject, IConfigurableObject, IDirectionalOutputHost {
    private static final int MAX_INPUT_SLOTS = 9;
    private static final int MAX_OUTPUT_SLOTS = 2;
    private static final int MAX_POWER_STORAGE = 500000;
    private static final int MAX_TANK_CAPACITY = 64000;

    private static List<RecipeHolder<DimensionalMatterAssemblerRecipe>> sortedRecipeCache = null;
    private static RecipeManager cachedRecipeManagerRef = null;

    private final IUpgradeInventory upgrades;
    private final IConfigManager configManager;

    private final AppEngInternalInventory inputInv = new AppEngInternalInventory(this, MAX_INPUT_SLOTS, 64);
    private final AppEngInternalInventory outputInv = new AppEngInternalInventory(this, MAX_OUTPUT_SLOTS, 64);
    private final InternalInventory inv = new CombinedInternalInventory(this.inputInv, this.outputInv);

    private final FilteredInternalInventory inputExposed = new FilteredInternalInventory(this.inputInv,
            AEItemFilters.INSERT_ONLY);
    private final FilteredInternalInventory outputExposed = new FilteredInternalInventory(this.outputInv,
            AEItemFilters.EXTRACT_ONLY);
    private final InternalInventory invExposed = new CombinedInternalInventory(this.inputExposed, this.outputExposed);

    private final CustomGenericInv fluidInv = new CustomGenericInv(Set.of(AEKeyType.fluids()), this::onChangeTank,
            GenericStackInv.Mode.STORAGE, 4);

    private boolean working = false;
    private int processingTime = 0;
    private boolean dirty = false;

    private DimensionalMatterAssemblerRecipe cachedTask = null;

    private EnumSet<RelativeSide> allowedOutputs = EnumSet.noneOf(RelativeSide.class);

    private final HashMap<Direction, Map<AEKeyType, ExternalStorageStrategy>> exportStrategies = new HashMap<>();

    private boolean showWarning = false;

    // --- Thermal & Risk Mechanics ---
    private int temperature = 0;
    private int maxTemperature = 10000;
    private int overloadTimer = -1;
    private int localMaxPower = MAX_POWER_STORAGE;
    private int thermalTicker = 0; // Tick counter for smooth heat/cool transitions
    private int productiveThermalTicks = 0;
    private int productiveHeatRemainder = 0;

    public DimensionalMatterAssemblerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        this.getMainNode().setFlags().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.setInternalMaxPower(this.localMaxPower);

        this.fluidInv.setCapacity(AEKeyType.fluids(), MAX_TANK_CAPACITY);

        // Resolve the machine item from the block state — this is valid even during
        // BlockEntityType construction because the block is already registered.
        // BuiltInRegistries.ITEM.get() returns AIR during BET registration because
        // deferred items haven't been resolved yet.
        this.upgrades = appeng.api.upgrades.UpgradeInventories.forMachine(
                blockState.getBlock().asItem(), 4, this::saveChanges);

        this.configManager = appeng.api.util.IConfigManager.builder(this::onConfigChanged)
                .registerSetting(appeng.api.config.Settings.AUTO_EXPORT, appeng.api.config.YesNo.NO)
                .build();

        this.setPowerSides(getGridConnectableSides(getOrientation()));
    }

    public boolean isWorking() {
        return this.working;
    }

    public void setWorking(boolean working) {
        if (working != this.working) {
            if (this.level != null && !this.level.isClientSide()) {
                updateBlockState(working);
                this.markForUpdate();
            }
        }
        this.working = working;
    }

    @Override
    public void onReady() {
        super.onReady();
        recalculateUpgrades();
    }

    public int getTemperature() {
        return this.temperature;
    }

    public int getMaxTemperature() {
        return this.maxTemperature;
    }

    public int getOverloadTimer() {
        return this.overloadTimer;
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide())
            return;

        // Auto-wake the AE2 TickManager if we have power but are asleep when we
        // shouldn't be
        if (this.dirty) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        handleThermalLogic();
    }

    /**
     * Executes the thermal simulation loop, regulating machine heat based on production.
     * <p><b>Thermal Engine Lifecycle:</b></p>
     * <ul>
     *      <li><b>Generation</b>: While working, block adds heat dynamically based on its upgrades (+1 HU/4ticks).</li>
     *      <li><b>Passive Cooling</b>: While idle, ambient block surfaces slowly radiate heat back to the environment (-1 HU/40ticks).</li>
     *      <li><b>Active Synergies</b>: Fluids injected into the Cooling Tank are instantly consumed to suppress excess heat.
     *          Each fluid class (e.g., Starlight vs Gelid Cryotheum) is balanced with specific HU/mB ratios.</li>
     * </ul>
     */
    private void handleThermalLogic() {
        if (this.level == null || this.level.isClientSide())
            return;

        this.thermalTicker++;

        if (this.hasCreativeCatalyst) {
            this.temperature = 0;
            this.overloadTimer = -1;
            return;
        }

        // 1. Heat Generation: only productive crafting ticks create heat.
        // External block tick accelerators should not heat the DMA unless the AE2
        // crafting tick also advanced recipe progress.
        if (this.productiveThermalTicks > 0) {
            this.productiveHeatRemainder += this.productiveThermalTicks;
            this.productiveThermalTicks = 0;
            while (this.productiveHeatRemainder >= 4) {
                int generationAmount = (int) Math.max(0, Math.round(this.currentHeatMultiplier));
                this.temperature += generationAmount;
                this.productiveHeatRemainder -= 4;
            }
        } else {
            // 2. Passive Cooling: -1 HU every 40 ticks when idle (= -0.5 HU/s)
            if (!this.isWorking() && this.temperature > 0 && this.thermalTicker % 40 == 0) {
                this.temperature -= 1;
            }
        }

        // 3. Coolant Cooling (player-placed, independent of recipes)
        if (this.temperature > 0) {
            GenericStack coolantStack = this.fluidInv.getStack(2); // Input Coolant tank
            if (coolantStack != null && coolantStack.what() instanceof AEFluidKey fluidKey
                    && coolantStack.amount() > 0) {
                // Determine coolant strength
                String fluidId = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluidKey.getFluid())
                        .toString();
                int mBPerHeat = 0;
                int heatPerMB = 0;

                if (fluidId.contains("temporal")) {
                    heatPerMB = 100; // extreme endgame coolant
                } else if (fluidId.contains("stable_coolant")) {
                    heatPerMB = 50; // intended mid-tier progression coolant
                } else if (fluidId.contains("starlight")) {
                    heatPerMB = 30; // good utility coolant, but below stable coolant
                } else if (fluidId.contains("gelid_cryotheum")) {
                    mBPerHeat = 24; // starter coolant should sustain a single basic DMA
                } else {
                    heatPerMB = 15; // default fallback for generic fluids
                }

                long amountToConsume = 0;
                long heatCooled = 0;

                if (mBPerHeat > 0) {
                    amountToConsume = Math.min(coolantStack.amount(), 1000); // max 1 bucket per tick
                    long possibleHeat = amountToConsume / mBPerHeat;
                    heatCooled = Math.min(this.temperature, possibleHeat);
                    amountToConsume = heatCooled * mBPerHeat;
                } else if (heatPerMB > 0) {
                    amountToConsume = Math.min(10, coolantStack.amount());
                    long possibleHeat = amountToConsume * heatPerMB;
                    if (this.temperature < possibleHeat) {
                        amountToConsume = Math.max(1, (this.temperature / heatPerMB));
                    }
                    heatCooled = amountToConsume * heatPerMB;
                }

                if (amountToConsume > 0) {
                    this.fluidInv.extractInternal(2, fluidKey, amountToConsume, Actionable.MODULATE);
                    this.temperature -= (int) heatCooled;
                }
            }
        }

        if (this.temperature < 0)
            this.temperature = 0;

        // --- Sound Handling (Work Loop) ---
        if (this.isWorking() && this.level.getGameTime() % 40 == 0 && this.temperature > 0) {
            this.level.playSound(null, this.worldPosition, com.yongaishide.chaosworld.init.ModSounds.DMA_WORK.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.3f, 1.0f);
        }

        // 2. Hazard Area
        double heatRatio = (double) this.temperature / Math.max(1, this.maxTemperature);
        if (heatRatio >= 0.5) { // Threshold reduced to 50%
            // Emit rotating flame particles
            if (this.level instanceof ServerLevel sLevel) {
                // Creates a spinning ring effect using GameTime
                double baseTime = sLevel.getGameTime() / 10.0;
                double[] radii = { 6.0, 7.0, 8.0 };
                double[] speeds = { 1.5, 1.0, 0.5 };

                for (int ring = 0; ring < 3; ring++) {
                    double time = baseTime * speeds[ring];
                    double r = radii[ring];
                    for (int i = 0; i < 12; i++) { // 6 particles per ring (total 18)
                        double angle = time + (i * ((Math.PI * 2) / 6));
                        double px = this.worldPosition.getX() + 0.5 + r * Math.cos(angle);
                        double py = this.worldPosition.getY() + 0.5;
                        double pz = this.worldPosition.getZ() + 0.5 + r * Math.sin(angle);
                        sLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME, px, py, pz, 1, 0, 0, 0,
                                0.0);
                    }
                }
            }

            // Damage players without proper armor
            net.minecraft.world.phys.AABB hazardArea = new net.minecraft.world.phys.AABB(this.worldPosition).inflate(7);
            List<Player> players = this.level.getEntitiesOfClass(Player.class, hazardArea);
            for (Player player : players) {
                if (this.level.getGameTime() % 20 == 0) {
                    if (!com.yongaishide.chaosworld.event.HazardHandler.hasThermalProtection(player)) {
                        player.hurt(this.level.damageSources().onFire(), 4.0f);
                        player.setRemainingFireTicks(60);
                    }
                }
            }
        }

        // 3. Overload & Explosion
        if (this.temperature >= this.maxTemperature) {
            if (this.overloadTimer == -1) {
                this.overloadTimer = 100; // 5 seconds (20 ticks * 5)
            }
        } else {
            this.overloadTimer = -1; // clear
        }

        if (this.overloadTimer > 0) {
            // Play Alarm sound every second during overload
            if (this.overloadTimer % 20 == 0) {
                this.level.playSound(null, this.worldPosition, com.yongaishide.chaosworld.init.ModSounds.DMA_ALARM.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.6f, 1.0f);

                int seconds = this.overloadTimer / 20;
                net.minecraft.world.phys.AABB hazardArea = new net.minecraft.world.phys.AABB(this.worldPosition)
                        .inflate(15);
                for (Player player : this.level.getEntitiesOfClass(Player.class, hazardArea)) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component
                                    .literal("CRITICAL OVERLOAD IN " + seconds + " SECONDS!")
                                    .withStyle(net.minecraft.ChatFormatting.RED, net.minecraft.ChatFormatting.BOLD),
                            true // true renders over hotbar (Title/Actionbar)
                    );
                }
            }
            this.overloadTimer--;

            if (this.overloadTimer == 0) {
                net.minecraft.server.MinecraftServer server = this.level.getServer();
                if (server != null) {
                    java.lang.String msg = "[⚠ THERMAL ALERT] Dimensional Matter Assembler exploded catastrophically at [X: "
                            +
                            this.worldPosition.getX() + ", Y: " + this.worldPosition.getY() + ", Z: " +
                            this.worldPosition.getZ() + "]!";
                    server.getPlayerList().broadcastSystemMessage(
                            net.minecraft.network.chat.Component.literal(msg).withStyle(
                                    net.minecraft.ChatFormatting.DARK_RED, net.minecraft.ChatFormatting.BOLD),
                            false);
                }

                this.level.explode(null, this.worldPosition.getX(), this.worldPosition.getY(),
                        this.worldPosition.getZ(),
                        10.0f, net.minecraft.world.level.Level.ExplosionInteraction.BLOCK); // Powerful block breaking
                                                                                             // explosion
                removeBlockAfterCatastrophicExplosion();
                this.overloadTimer = -1;
            }
        }
    }

    private void removeBlockAfterCatastrophicExplosion() {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return;
        }

        this.level.removeBlock(this.worldPosition, false);
    }

    private void updateBlockState(boolean working) {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return;
        }

        final BlockState current = this.level.getBlockState(this.worldPosition);
        if (current.getBlock() instanceof DimensionalMatterAssemblerBlock) {
            final BlockState newState = current.setValue(DimensionalMatterAssemblerBlock.WORKING, working);

            if (current != newState) {
                this.level.setBlock(this.worldPosition, newState, Block.UPDATE_CLIENTS);
            }
        }
    }

    public int getMaxProcessingTime() {
        return this.cachedTask != null ? this.cachedTask.getTime() : 200;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    private void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    @Override
    protected void saveVisualState(CompoundTag data) {
        super.saveVisualState(data);
        data.putBoolean("working", isWorking());
        data.putInt("temperature", this.temperature);
        data.putInt("maxTemperature", this.maxTemperature);
        data.putInt("overloadTimer", this.overloadTimer);
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);
        this.working = data.getBoolean("working");
        if (data.contains("temperature"))
            this.temperature = data.getInt("temperature");
        if (data.contains("maxTemperature"))
            this.maxTemperature = data.getInt("maxTemperature");
        if (data.contains("overloadTimer"))
            this.overloadTimer = data.getInt("overloadTimer");
    }

    public void saveChanges() {
        recalculateUpgrades();
        this.setChanged();
    }

    private void persistChangesQuietly() {
        this.setChanged();
    }

    // New thermal and synergy stat fields
    private double currentHeatMultiplier = 1.0;
    private double currentSpeedMultiplier = 1.0;
    private double currentPowerMultiplier = 1.0;
    private double currentBonusDropChance = 0.0;
    private boolean hasCreativeCatalyst = false;

    private void recalculateUpgrades() {
        long newMaxPower = MAX_POWER_STORAGE;
        double heatMult = 1.0;
        double speedMult = 1.0;
        double powerMult = 1.0;
        double bonusDropChance = 0.0;

        int identicalCount = 0;
        com.yongaishide.chaosworld.item.custom.BaseCatalystItem firstCatalyst = null;
        boolean synergyPossible = true;

        boolean foundCreative = false;

        for (int i = 0; i < this.upgrades.size(); i++) {
            ItemStack upgradeStack = this.upgrades.getStackInSlot(i);
            if (!upgradeStack.isEmpty()) {
                if (upgradeStack.getItem() instanceof com.yongaishide.chaosworld.item.custom.DimensionalCatalystItem) {
                    foundCreative = true;
                } else if (upgradeStack.getItem() instanceof com.yongaishide.chaosworld.item.custom.BaseCatalystItem catalyst) {
                    newMaxPower *= catalyst.getBufferMultiplier();

                    // Thermal now multiplies heat generation (e.g. +400 static heat = 4x
                    // multiplier)
                    // We map getStaticHeat -> Multiplier: (e.g. 100 heat = 1.0 extra multiplier)
                    heatMult += Math.max(0, catalyst.getStaticHeat() / 100.0);
                    speedMult *= catalyst.getSpeedMultiplier();
                    powerMult *= catalyst.getPowerMultiplier();
                    bonusDropChance += catalyst.getBonusDropChance();

                    if (firstCatalyst == null) {
                        firstCatalyst = catalyst;
                        identicalCount++;
                    } else if (firstCatalyst == catalyst) {
                        identicalCount++;
                    } else {
                        synergyPossible = false;
                    }
                } else {
                    synergyPossible = false; // Missing or non-catalyst prevents synergy
                }
            } else {
                synergyPossible = false;
            }
        }

        // Apply 4-stack synergy combo!
        if (synergyPossible && identicalCount == 4 && firstCatalyst != null) {
            heatMult *= 1.5; // 50% more heat generation as debuff
            if ("chrono".equals(firstCatalyst.getFamily())) {
                speedMult *= 2.0; // Huge speed synergy
            } else if ("matterflow".equals(firstCatalyst.getFamily())) {
                powerMult *= 0.5; // Huge power discount
            } else if ("quantum".equals(firstCatalyst.getFamily())) {
                bonusDropChance += 0.5;
            }
        }

        if (foundCreative) {
             heatMult = 0.0;
             speedMult = 1000.0;
             powerMult = 0.0;
             bonusDropChance = 1.0;
             newMaxPower = MAX_POWER_STORAGE;
        }

        this.hasCreativeCatalyst = foundCreative;
        this.currentHeatMultiplier = heatMult;
        this.currentSpeedMultiplier = speedMult;
        this.currentPowerMultiplier = powerMult;
        this.currentBonusDropChance = bonusDropChance;
        this.localMaxPower = (int) Math.min(Integer.MAX_VALUE, newMaxPower);
        this.maxTemperature = 10000; // Reset max capacity; heat is now a multiplier payload
        this.setInternalMaxPower(this.localMaxPower);
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.allOf(Direction.class);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    public InternalInventory getInput() {
        return this.inputInv;
    }

    public InternalInventory getOutput() {
        return this.outputInv;
    }

    public GenericStackInv getTank() {
        return this.fluidInv;
    }

    public void setShowWarning(boolean show) {
        this.showWarning = show;
    }

    public boolean showWarning() {
        return this.showWarning;
    }

    @Override
    public EnumSet<RelativeSide> getAllowedOutputs() {
        return this.allowedOutputs;
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.STORAGE)) {
            return this.getInternalInventory();
        } else if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        }
        return super.getSubInventory(id);
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(Direction facing) {
        return this.invExposed;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    private void onChangeInventory() {
        this.dirty = true;
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        onChangeInventory();
    }

    public void onChangeTank() {
        onChangeInventory();
        if (this.level != null && !this.level.isClientSide()) {
            this.markForUpdate();
        }
    }

    private boolean hasAutoExportWork() {
        return (!this.outputInv.getStackInSlot(0).isEmpty() || !this.outputInv.getStackInSlot(1).isEmpty()
                || this.fluidInv.getStack(0) != null
                || this.fluidInv.getAmount(0) > 0
                || this.fluidInv.getAmount(1) > 0)
                && configManager.getSetting(Settings.AUTO_EXPORT) == YesNo.YES;
    }

    private boolean hasCraftWork() {
        var task = this.getTask();
        if (task != null) {
            // Check outputs fit
            boolean outputsFit = true;
            for (int i = 0; i < task.getItemOutputs().size(); i++) {
                var outStack = task.getItemOutputs().get(i);
                if (outStack.what() instanceof AEItemKey itemKey) {
                    int outputAmount = getOutputAmountWithMaxBonus(outStack.amount());
                    var stack = itemKey.toStack(outputAmount);
                    if (!this.outputInv.insertItem(i, stack, true).isEmpty()) {
                        outputsFit = false;
                        break;
                    }
                }
            }
            if (!outputsFit)
                return false;

            for (int i = 0; i < task.getFluidOutputs().size(); i++) {
                var outStack = task.getFluidOutputs().get(i);
                if (outStack.what() instanceof AEFluidKey fluidKey) {
                    int outputAmount = getOutputAmountWithMaxBonus(outStack.amount());
                    if (!this.fluidInv.canAdd(i, fluidKey, outputAmount)) {
                        return false;
                    }
                }
            }
            return true;
        }

        this.setProcessingTime(0);
        return this.isWorking();
    }

    @Nullable
    public DimensionalMatterAssemblerRecipe getTask() {
        if (this.cachedTask == null && level != null) {
            this.cachedTask = findRecipe(level);
        }
        return this.cachedTask;
    }

    private int getOutputAmountWithMaxBonus(long baseAmount) {
        long multiplier = 1L + (long) Math.ceil(Math.max(0.0, this.currentBonusDropChance));
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, baseAmount) * multiplier);
    }

    private int getOutputAmountWithRolledBonus(long baseAmount) {
        long safeBaseAmount = Math.max(0L, baseAmount);
        double bonusChance = Math.max(0.0, this.currentBonusDropChance);
        long guaranteedBonusRolls = (long) bonusChance;
        double fractionalBonusRoll = bonusChance - guaranteedBonusRolls;
        long bonusRolls = guaranteedBonusRolls;

        if (fractionalBonusRoll > 0.0 && this.level != null && this.level.random.nextDouble() < fractionalBonusRoll) {
            bonusRolls++;
        }

        return (int) Math.min(Integer.MAX_VALUE, safeBaseAmount * (1L + bonusRolls));
    }

    private static List<RecipeHolder<DimensionalMatterAssemblerRecipe>> getSortedRecipes(Level level) {
        RecipeManager manager = level.getRecipeManager();
        if (manager != cachedRecipeManagerRef || sortedRecipeCache == null) {
            sortedRecipeCache = manager.getAllRecipesFor(ModRecipes.DMA_RECIPE_TYPE.get()).stream()
                    .sorted(Comparator
                            .comparingInt((RecipeHolder<DimensionalMatterAssemblerRecipe> holder) ->
                                    (int) holder.value().getItemInputs().stream()
                                            .filter(r -> r != null && !r.isEmpty()).count())
                            .thenComparingLong((RecipeHolder<DimensionalMatterAssemblerRecipe> holder) ->
                                    holder.value().getFluidInputs().stream()
                                            .filter(f -> f != null && !f.isEmpty())
                                            .mapToLong(f -> f.getAmount()).sum())
                            .reversed())
                    .toList();
            cachedRecipeManagerRef = manager;
        }
        return sortedRecipeCache;
    }

    private DimensionalMatterAssemblerRecipe findRecipe(Level level) {
        var possibleRecipes = getSortedRecipes(level);
        for (var recipeHolder : possibleRecipes) {
            var recipe = recipeHolder.value();
            boolean matches = true;

            // Shapeless Logic to check input grids
            List<ItemStack> availableInputs = new java.util.ArrayList<>();
            for (int i = 0; i < this.inputInv.size(); i++) {
                var stack = this.inputInv.getStackInSlot(i);
                if (!stack.isEmpty())
                    availableInputs.add(stack.copy());
            }

            for (var req : recipe.getItemInputs()) {
                if (req == null || req.isEmpty())
                    continue;
                int amountNeeded = (int) req.getAmount();
                for (var stack : availableInputs) {
                    if (req.getIngredient().test(stack)) {
                        int toTake = Math.min(stack.getCount(), amountNeeded);
                        stack.shrink(toTake);
                        amountNeeded -= toTake;
                    }
                    if (amountNeeded <= 0)
                        break;
                }
                if (amountNeeded > 0) {
                    matches = false;
                    break;
                }
            }

            if (!matches)
                continue;

            // Match recipe fluid inputs against slot 3 (base fluid input).
            // Slot 2 (coolant) is player-managed and NOT part of recipes.
            for (int i = 0; i < recipe.getFluidInputs().size(); i++) {
                var fluidInSlot = this.fluidInv.getStack(3); // Always use slot 3 (base fluid input)
                if (recipe.getFluidInputs().get(i) != null && !recipe.getFluidInputs().get(i).isEmpty()) {
                    if (fluidInSlot == null || fluidInSlot.amount() < recipe.getFluidInputs().get(i).getAmount()) {
                        matches = false;
                        break;
                    }
                    if (!(fluidInSlot.what() instanceof AEFluidKey fluidKey)) {
                        matches = false;
                        break;
                    }
                    FluidStack fluidStack = fluidKey.toStack((int) fluidInSlot.amount());
                    if (!recipe.getFluidInputs().get(i).getIngredient().test(fluidStack)) {
                        matches = false;
                        break;
                    }
                }
            }

            if (matches)
                return recipe;
        }
        return null;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 20, !hasAutoExportWork() && !this.hasCraftWork());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int ticksSinceLastCall) {
        if (this.dirty) {
            // Check if running recipe is still valid
            if (level != null) {
                var recipe = findRecipe(level);
                if (recipe == null) {
                    this.setProcessingTime(0);
                    this.setWorking(false);
                    this.cachedTask = null;
                } else if (recipe != this.cachedTask) {
                    this.setProcessingTime(0);
                    this.cachedTask = recipe;
                }
            }
            this.setChanged();
            this.dirty = false;
        }

        if (this.hasCraftWork()) {
            boolean[] didWork = { false };
            getMainNode().ifPresent(grid -> {
                IEnergyService eg = grid.getEnergyService();
                IEnergySource src = this;

                int baseSpeedFactor = switch (this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD)) {
                    default -> 2; // 100 ticks
                    case 1 -> 3; // 66 ticks
                    case 2 -> 5; // 40 ticks
                    case 3 -> 10; // 20 ticks
                    case 4 -> 50; // 4 ticks
                };

                int recipeTime = this.cachedTask != null ? this.cachedTask.getTime() : 200;

                final int speedFactor = this.hasCreativeCatalyst
                        ? Math.max(1, recipeTime)
                        : Math.min(Math.max(1, recipeTime), Math.max(1, Mth.ceil((float) (baseSpeedFactor * this.currentSpeedMultiplier))));
                final int progressReq = recipeTime - this.getProcessingTime();
                final float powerRatio = progressReq < speedFactor ? (float) progressReq / speedFactor : 1;
                final int requiredTicks = Mth.ceil((float) recipeTime / speedFactor);

                int basePowerConsumption = Mth.floor(((float) getTask().getEnergy() / requiredTicks) * powerRatio);
                final int powerConsumption = this.hasCreativeCatalyst
                        ? 0
                        : Math.max(1, (int) (basePowerConsumption * this.currentPowerMultiplier));
                final double powerThreshold = powerConsumption - 0.01;

                if (this.hasCreativeCatalyst) {
                    this.setProcessingTime(this.getProcessingTime() + speedFactor);
                    setShowWarning(false);
                    didWork[0] = true;
                    return;
                }

                // Try to recharge from AppFlux FE cells in the AE2 network
                try {
                    if (net.neoforged.fml.ModList.get().isLoaded("appflux")) {
                        com.yongaishide.chaosworld.compat.appflux.AppliedFluxPlugin.rechargeEnergyStorage(
                                grid,
                                Integer.MAX_VALUE,
                                IActionSource.ofMachine(this),
                                this.getEnergyStorage(Direction.UP));
                    }
                } catch (Throwable ignored) {
                    // NO-OP if AppFlux is not available
                }

                double powerReq = this.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (powerReq <= powerThreshold) {
                    src = eg;
                    var oldPowerReq = powerReq;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                    if (oldPowerReq > powerReq) {
                        src = this;
                        powerReq = oldPowerReq;
                    }
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    this.setProcessingTime(this.getProcessingTime() + speedFactor);
                    setShowWarning(false);
                    didWork[0] = true;
                } else if (powerReq != 0) {
                    var progressRatio = src == this
                            ? powerReq / powerConsumption
                            : (powerReq - 10 * eg.getIdlePowerUsage()) / powerConsumption;
                    var factor = Mth.floor(progressRatio * speedFactor);

                    if (factor >= 1) {
                        var extracted = src.extractAEPower(
                                (double) (powerConsumption * factor) / speedFactor,
                                Actionable.MODULATE,
                                PowerMultiplier.CONFIG);
                        var actualFactor = (int) Math.floor(extracted / powerConsumption * speedFactor);
                        this.setProcessingTime(this.getProcessingTime() + actualFactor);
                        didWork[0] = true;
                    }
                    setShowWarning(true);
                }
            });
            this.setWorking(didWork[0]);
            if (didWork[0]) {
                this.productiveThermalTicks++;
            }

            if (this.getProcessingTime() >= this.getMaxProcessingTime()) {
                this.setProcessingTime(0);
                final DimensionalMatterAssemblerRecipe out = this.getTask();
                if (out != null) {
                    // [DEBUG] Craft Done => decrease fluid & consume items

                    // Insert out items
                    for (int i = 0; i < out.getItemOutputs().size(); i++) {
                        if (out.getItemOutputs().get(i) != null
                                && out.getItemOutputs().get(i).what() instanceof AEItemKey itemKey) {
                            int outAmount = getOutputAmountWithRolledBonus(out.getItemOutputs().get(i).amount());
                            var toIns = itemKey.toStack(outAmount);
                            this.outputInv.insertItem(i, toIns, false);
                        }
                    }

                    // Insert fluids out
                    for (int i = 0; i < out.getFluidOutputs().size(); i++) {
                        if (out.getFluidOutputs().get(i) != null
                                && out.getFluidOutputs().get(i).what() instanceof AEFluidKey fluidKey) {
                            int outAmount = getOutputAmountWithRolledBonus(out.getFluidOutputs().get(i).amount());
                            this.fluidInv.add(i, fluidKey, outAmount);
                        }
                    }

                    // Consume inputs shapelessly
                    for (var req : out.getItemInputs()) {
                        if (req != null && !req.isEmpty()) {
                            int amountNeeded = (int) req.getAmount();
                            for (int i = 0; i < this.inputInv.size() && amountNeeded > 0; i++) {
                                var currentStack = this.inputInv.getStackInSlot(i);
                                if (req.getIngredient().test(currentStack)) {
                                    int toTake = Math.min(currentStack.getCount(), amountNeeded);
                                    currentStack.shrink(toTake);
                                    this.inputInv.setItemDirect(i, currentStack);
                                    amountNeeded -= toTake;
                                }
                            }
                        }
                    }

                    // Consume fluids (always from slot 3 = base fluid input)
                    for (int i = 0; i < out.getFluidInputs().size(); i++) {
                        if (out.getFluidInputs().get(i) != null && !out.getFluidInputs().get(i).isEmpty()) {
                            var currentStack = this.fluidInv.getStack(3); // Always slot 3 (base fluid)
                            if (currentStack != null) {
                                var key = currentStack.what();
                                long remaining = currentStack.amount() - out.getFluidInputs().get(i).getAmount();
                                if (remaining > 0) {
                                    this.fluidInv.setStack(3, new GenericStack(key, remaining));
                                } else {
                                    this.fluidInv.setStack(3, null);
                                }
                            }
                        }
                    }
                }
                this.persistChangesQuietly();
                this.cachedTask = null;
                this.setWorking(false);
            }
        } else {
            setShowWarning(false);
        }

        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }

        return this.hasCraftWork()
                ? TickRateModulation.URGENT
                : this.hasAutoExportWork() ? TickRateModulation.SLOWER : TickRateModulation.SLEEP;
    }

    private boolean pushOutResult() {
        if (!this.hasAutoExportWork()) {
            return false;
        }

        var orientation = this.getOrientation();

        for (var side : allowedOutputs) {
            var dir = orientation.getSide(side);
            var target = getTarget(dir);

            if (target != null) {
                var source = IActionSource.ofMachine(this);
                var movedStacks = false;

                // Push Items out
                for (int i = 0; i < this.outputInv.size(); i++) {
                    var genStack = GenericStack.fromItemStack(this.outputInv.getStackInSlot(i));
                    if (genStack != null && genStack.what() != null) {
                        var extractedStack = this.outputInv.extractItem(i, 64, false);
                        var inserted = target.insert(genStack.what(), extractedStack.getCount(), Actionable.MODULATE,
                                source);
                        extractedStack.setCount(extractedStack.getCount() - (int) inserted);
                        this.outputInv.insertItem(i, extractedStack, false);
                        movedStacks |= inserted > 0;
                    }
                }

                // Push fluids out
                for (int i = 0; i < 2; i++) {
                    var outFluid = this.fluidInv.getStack(i);
                    if (outFluid != null && outFluid.what() != null) {
                        var extracted = this.fluidInv.extract(i, outFluid.what(), outFluid.amount(),
                                Actionable.MODULATE);
                        var inserted = target.insert(outFluid.what(), extracted, Actionable.MODULATE, source);
                        this.fluidInv.add(i, ((AEFluidKey) outFluid.what()), (int) (extracted - inserted));

                        if (this.fluidInv.getAmount(i) == 0)
                            this.fluidInv.clear(i);
                        movedStacks |= inserted > 0;
                    }
                }

                if (movedStacks) {
                    return true;
                }
            }
        }

        return false;
    }

    private CompositeStorage getTarget(Direction dir) {
        if (this.exportStrategies.get(dir) == null) {
            var be = this.getBlockEntity();
            this.exportStrategies.put(
                    dir,
                    StackWorldBehaviors.createExternalStorageStrategies(
                            (ServerLevel) be.getLevel(), be.getBlockPos().relative(dir), dir.getOpposite()));
        }

        var externalStorages = new IdentityHashMap<AEKeyType, MEStorage>(2);
        for (var entry : exportStrategies.get(dir).entrySet()) {
            var wrapper = entry.getValue().createWrapper(false, () -> {
            });
            if (wrapper != null) {
                externalStorages.put(entry.getKey(), wrapper);
            }
        }

        if (!externalStorages.isEmpty()) {
            return new CompositeStorage(externalStorages);
        }
        return null;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.fluidInv.writeToChildTag(data, "tank", registries);

        ListTag outputTags = new ListTag();
        for (var side : this.allowedOutputs) {
            outputTags.add(StringTag.valueOf(side.name()));
        }
        data.put("outputs", outputTags);

        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.configManager.writeToNBT(data, registries);

        // Persist thermal state
        data.putInt("temperature", this.temperature);
        data.putInt("overloadTimer", this.overloadTimer);
        data.putInt("productiveHeatRemainder", this.productiveHeatRemainder);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.fluidInv.readFromChildTag(data, "tank", registries);

        this.allowedOutputs.clear();
        ListTag outputTags = data.getList("outputs", Tag.TAG_STRING);
        if (!outputTags.isEmpty()) {
            for (var x = 0; x < outputTags.size(); x++) {
                RelativeSide side = Enum.valueOf(RelativeSide.class, outputTags.getString(x));
                this.allowedOutputs.add(side);
            }
        }

        this.upgrades.readFromNBT(data, "upgrades", registries);
        this.configManager.readFromNBT(data, registries);

        // Restore thermal state
        if (data.contains("temperature"))
            this.temperature = data.getInt("temperature");
        if (data.contains("overloadTimer"))
            this.overloadTimer = data.getInt("overloadTimer");
        if (data.contains("productiveHeatRemainder"))
            this.productiveHeatRemainder = data.getInt("productiveHeatRemainder");

        // Recalculate upgrade bonuses (maxTemperature, maxPower) from loaded upgrades
        recalculateUpgrades();
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var c = super.readFromStream(data);

        var oldWorking = isWorking();
        var newWorking = data.readBoolean();

        if (oldWorking != newWorking) {
            this.working = newWorking;
        }

        for (int i = 0; i < this.inv.size(); i++) {
            this.inv.setItemDirect(i, ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
        }

        this.fluidInv.setStack(0, GenericStack.readBuffer(data));
        this.fluidInv.setStack(1, GenericStack.readBuffer(data));
        this.fluidInv.setStack(2, GenericStack.readBuffer(data));
        this.fluidInv.setStack(3, GenericStack.readBuffer(data));
        this.cachedTask = null;

        // Read thermal state from server
        this.temperature = data.readInt();
        this.maxTemperature = data.readInt();
        this.overloadTimer = data.readInt();

        return c;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);

        data.writeBoolean(isWorking());
        for (int i = 0; i < this.inv.size(); i++) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(data, this.inv.getStackInSlot(i));
        }

        GenericStack.writeBuffer(this.fluidInv.getStack(0), data);
        GenericStack.writeBuffer(this.fluidInv.getStack(1), data);
        GenericStack.writeBuffer(this.fluidInv.getStack(2), data);
        GenericStack.writeBuffer(this.fluidInv.getStack(3), data);

        // Sync thermal state to client
        data.writeInt(this.temperature);
        data.writeInt(this.maxTemperature);
        data.writeInt(this.overloadTimer);
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder builder, @Nullable Player player) {
        super.exportSettings(mode, builder, player);
        builder.set(ModDataComponents.DMA_ALLOWED_OUTPUTS.get(), encodeAllowedOutputs());
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        Integer exportedOutputs = input.get(ModDataComponents.DMA_ALLOWED_OUTPUTS.get());
        if (exportedOutputs != null) {
            updateOutputSides(decodeAllowedOutputs(exportedOutputs));
        }
    }

    private int encodeAllowedOutputs() {
        int mask = 0;
        for (RelativeSide side : this.allowedOutputs) {
            mask |= 1 << side.ordinal();
        }
        return mask;
    }

    private static EnumSet<RelativeSide> decodeAllowedOutputs(int mask) {
        EnumSet<RelativeSide> outputs = EnumSet.noneOf(RelativeSide.class);
        for (RelativeSide side : RelativeSide.values()) {
            if ((mask & (1 << side.ordinal())) != 0) {
                outputs.add(side);
            }
        }
        return outputs;
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {
        if (setting == Settings.AUTO_EXPORT) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }
        saveChanges();
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);

        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }

        for (var i = 0; i < this.fluidInv.size(); i++) {
            var fluid = this.fluidInv.getStack(i);
            if (fluid != null) {
                fluid.what().addDrops(fluid.amount(), drops, level, pos);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.fluidInv.clear();
        this.upgrades.clear();
    }

    /**
     * Clears a specific tank slot. Used by GUI clear buttons.
     */
    public void clearTank(int slot) {
        if (slot >= 0 && slot < this.fluidInv.size()) {
            this.fluidInv.setStack(slot, null);
            this.onChangeTank();
            this.saveChanges();
        }
    }

    @Override
    public void updateOutputSides(EnumSet<RelativeSide> allowedOutputs) {
        this.allowedOutputs = allowedOutputs;
        saveChanges();
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        appeng.menu.MenuOpener.returnTo(com.yongaishide.chaosworld.init.ModMenus.DIMENSIONAL_MATTER_ASSEMBLER, player,
                iSubMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(this.getBlockState().getBlock().asItem());
    }

    public static class CustomGenericInv extends GenericStackInv {
        public CustomGenericInv(Set<AEKeyType> supportedKeyTypes, @Nullable Runnable listener, Mode mode, int size) {
            super(supportedKeyTypes, listener, mode, size);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean isAllowedIn(int slot, AEKey what) {
            // Slots 0, 1 are output-only (no external insertion allowed)
            if (slot == 0 || slot == 1)
                return false;

            if (what instanceof AEFluidKey fluidKey) {
                boolean isCoolant = fluidKey.getFluid().builtInRegistryHolder()
                        .is(com.yongaishide.chaosworld.util.ModTags.Fluids.COOLANT)
                        || fluidKey.getFluid().builtInRegistryHolder().is(com.yongaishide.chaosworld.util.ModTags.Fluids.COOLANTS);

                // Slot 2 is exclusive for Coolants
                if (slot == 2 && !isCoolant) {
                    return false;
                }

                // Slot 3 is exclusive for basic recipe fluids (NO coolants allowed)
                if (slot == 3 && isCoolant) {
                    return false;
                }
            }

            return super.isAllowedIn(slot, what);
        }

        /**
         * Custom insertion logic for the DMA's fluid tanks.
         * Default GenericStackInv merges all identical fluids into the first matching slot.
         * The DMA uses slot 2 for coolants and slot 3 for base recipe fluids, so they MUST be kept separate.
         * This code dynamically routes the fluid into the correct slot depending on its type while still
         * properly tracking merging / remainder math when a slot is partially full.
         */
        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (!(what instanceof AEFluidKey)) {
                return super.insert(what, amount, mode, source);
            }

            long remaining = amount;

            // Try input slots 2 and 3 in order
            for (int slot = 2; slot <= 3 && remaining > 0; slot++) {
                if (!isAllowedIn(slot, what))
                    continue;

                var stack = this.getStack(slot);
                if (stack == null) {
                    // Empty slot — fill it as much as possible
                    long toInsert = Math.min(remaining, this.getMaxAmount(what));
                    if (mode == Actionable.MODULATE) {
                        this.setStack(slot, new GenericStack(what, toInsert));
                    }
                    remaining -= toInsert;
                } else if (stack.what().equals(what)) {
                    // Partially full matching slot — merge into it
                    long space = this.getMaxAmount(what) - stack.amount();
                    if (space > 0) {
                        long toInsert = Math.min(remaining, space);
                        if (mode == Actionable.MODULATE) {
                            this.setStack(slot, new GenericStack(what, stack.amount() + toInsert));
                        }
                        remaining -= toInsert;
                    }
                }
                // If it's a completely different fluid, we simply bypass and check the next slot (if any).
            }

            return amount - remaining;
        }

        @Override
        public long extract(int slot, AEKey what, long amount, Actionable mode) {
            // Slots 2, 3 are input-only (no external extraction allowed)
            if (slot == 2 || slot == 3)
                return 0L;
            return super.extract(slot, what, amount, mode);
        }

        /**
         * Internal extraction that bypasses the external-only protection.
         * Used by the machine's own thermal logic to consume coolant.
         */
        public long extractInternal(int slot, AEKey what, long amount, Actionable mode) {
            return super.extract(slot, what, amount, mode);
        }

        public boolean canAdd(int slot, AEFluidKey key, int amount) {
            var stack = this.getStack(slot);
            if (stack == null)
                return true;
            if (!stack.what().equals(key))
                return false;
            return stack.amount() + amount <= this.getMaxAmount(key);
        }

        public int add(int slot, AEFluidKey key, int amount) {
            if (!canAdd(slot, key, amount))
                return 0;

            var stack = this.getStack(slot);
            var newAmount = amount;
            if (stack != null)
                newAmount += (int) stack.amount();
            this.setStack(slot, new GenericStack(key, newAmount));
            return amount;
        }

        public void clear(int slot) {
            boolean changed = this.getStack(slot) != null;
            this.setStack(slot, null);
            if (changed) {
                onChange();
            }
        }
    }
}

package com.yongaishide.chaosworld.compat.mekanism;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public final class MekanismChemicalCompat {
    private MekanismChemicalCompat() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event, net.minecraft.world.level.block.entity.BlockEntityType<? extends MekanismChemicalStorage> type) {
        event.registerBlockEntity(
                Capabilities.CHEMICAL.block(),
                type,
                (be, side) -> be.supportsChemicalIO() ? new HatchChemicalHandler(be) : null
        );
    }

    public static ChemicalStack createStack(ResourceLocation chemicalId, long amount) {
        if (amount <= 0L) {
            return ChemicalStack.EMPTY;
        }
        var chemical = MekanismAPI.CHEMICAL_REGISTRY.get(chemicalId);
        if (chemical == null || chemical.isEmptyType()) {
            return ChemicalStack.EMPTY;
        }
        return new ChemicalStack(chemical.getAsHolder(), amount);
    }

    public static ResourceLocation getChemicalId(ChemicalStack stack) {
        return ResourceLocation.parse(stack.getChemicalHolder().getRegisteredName());
    }

    private static final class HatchChemicalHandler implements IChemicalHandler {
        private final MekanismChemicalStorage storage;

        private HatchChemicalHandler(MekanismChemicalStorage storage) {
            this.storage = storage;
        }

        @Override
        public int getChemicalTanks() {
            return 1;
        }

        @Override
        public ChemicalStack getChemicalInTank(int tank) {
            if (tank != 0) {
                return ChemicalStack.EMPTY;
            }
            ResourceLocation chemicalId = this.storage.getStoredChemicalId();
            if (chemicalId == null) {
                return ChemicalStack.EMPTY;
            }
            return createStack(chemicalId, this.storage.getStoredChemicalAmount());
        }

        @Override
        public void setChemicalInTank(int tank, ChemicalStack stack) {
            if (tank != 0) {
                return;
            }
            if (stack.isEmpty()) {
                this.storage.setStoredChemical(null, 0L);
            } else {
                this.storage.setStoredChemical(getChemicalId(stack), stack.getAmount());
            }
        }

        @Override
        public long getChemicalTankCapacity(int tank) {
            return tank == 0 ? this.storage.getChemicalCapacity() : 0L;
        }

        @Override
        public boolean isValid(int tank, ChemicalStack stack) {
            return tank == 0 && !stack.isEmpty();
        }

        @Override
        public ChemicalStack insertChemical(int tank, ChemicalStack stack, Action action) {
            if (tank != 0 || stack.isEmpty()) {
                return stack;
            }
            ResourceLocation currentId = this.storage.getStoredChemicalId();
            ResourceLocation incomingId = getChemicalId(stack);
            if (currentId != null && !currentId.equals(incomingId)) {
                return stack;
            }

            long stored = this.storage.getStoredChemicalAmount();
            long inserted = Math.min(stack.getAmount(), Math.max(0L, this.storage.getChemicalCapacity() - stored));
            if (inserted <= 0L) {
                return stack;
            }

            if (action.execute()) {
                this.storage.setStoredChemical(incomingId, stored + inserted);
            }
            return stack.getAmount() == inserted ? ChemicalStack.EMPTY : stack.copyWithAmount(stack.getAmount() - inserted);
        }

        @Override
        public ChemicalStack extractChemical(int tank, long amount, Action action) {
            if (tank != 0 || amount <= 0L) {
                return ChemicalStack.EMPTY;
            }
            ResourceLocation currentId = this.storage.getStoredChemicalId();
            long stored = this.storage.getStoredChemicalAmount();
            if (currentId == null || stored <= 0L) {
                return ChemicalStack.EMPTY;
            }

            long extracted = Math.min(amount, stored);
            ChemicalStack result = createStack(currentId, extracted);
            if (action.execute()) {
                long remaining = stored - extracted;
                this.storage.setStoredChemical(remaining > 0L ? currentId : null, remaining);
            }
            return result;
        }
    }
}

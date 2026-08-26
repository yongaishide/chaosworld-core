package com.yongaishide.chaosworld.util;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.EnergyStorage;

/**
 * Uma classe de armazenamento de energia customizada que persiste (salva) seu valor
 * em um Data Component do ItemStack sempre que a energia é alterada.
 */
public class UfoPersistentEnergyStorage extends EnergyStorage {
    private final ItemStack parent;
    private final DataComponentType<Integer> componentType;

    public UfoPersistentEnergyStorage(ItemStack parent, DataComponentType<Integer> componentType, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.parent = parent;
        this.componentType = componentType;

        // Carrega o valor inicial do Data Component quando o objeto é criado
        this.energy = this.parent.getOrDefault(this.componentType, 0);
    }

    // Um método para salvar o valor atual no Data Component
    private void save() {
        this.parent.set(this.componentType, this.energy);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && received > 0) {
            save(); // Salva após receber energia
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && extracted > 0) {
            save(); // Salva após extrair energia
        }
        return extracted;
    }

    // Usado para definir a energia diretamente (como na transformação)
    public void setEnergy(int energy) {
        this.energy = energy;
        if (this.energy > capacity) {
            this.energy = capacity;
        } else if (this.energy < 0) {
            this.energy = 0;
        }
        save(); // Salva após definir a energia
    }
}
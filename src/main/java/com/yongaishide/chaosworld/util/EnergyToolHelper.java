package com.yongaishide.chaosworld.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Optional;

public class EnergyToolHelper {

    /**
     * Extrai energia de um ItemStack. É a maneira segura de fazer isso.
     * @param stack O item do qual extrair energia.
     * @param amount A quantidade a extrair.
     * @param simulate Se true, a energia não será realmente removida.
     * @return A quantidade de energia que foi efetivamente extraída.
     */
    public static int extractEnergy(ItemStack stack, int amount, boolean simulate) {
        if (stack.isEmpty()) {
            return 0;
        }

        // Usar Optional é mais seguro para evitar NullPointerException
        Optional<IEnergyStorage> energyStorageOpt = Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM));
        return energyStorageOpt.map(storage -> storage.extractEnergy(amount, simulate)).orElse(0);
    }

    /**
     * Verifica se o item tem uma quantidade de energia suficiente.
     * @param stack O item a ser verificado.
     * @param amount A quantidade de energia necessária.
     * @param simulate Se a verificação deve apenas simular a extração.
     * @return true se houver energia suficiente, false caso contrário.
     */
    public static boolean hasEnoughEnergy(ItemStack stack, int amount, boolean simulate) {
        return extractEnergy(stack, amount, simulate) >= amount;
    }

    /**
     * Controla a visibilidade da barra de energia no HUD.
     */
    public static boolean isBarVisible(ItemStack stack) {
        Optional<IEnergyStorage> energyStorageOpt = Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM));
        return energyStorageOpt.map(storage -> storage.getMaxEnergyStored() > 0).orElse(false);
    }

    /**
     * Calcula a largura da barra de energia.
     */
    public static int getBarWidth(ItemStack stack) {
        Optional<IEnergyStorage> energyStorageOpt = Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM));
        return energyStorageOpt.map(storage -> {
            if (storage.getMaxEnergyStored() == 0) return 0;
            return (int) Math.round(13.0 * storage.getEnergyStored() / storage.getMaxEnergyStored());
        }).orElse(0);
    }

    /**
     * Define a cor da barra de energia.
     */
    public static int getBarColor(ItemStack stack) {
        return 0x0066FF; // Um tom de azul.
    }
}
package com.yongaishide.chaosworld.item.custom.cell;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.CellState;
import appeng.api.upgrades.IUpgradeableItem;
import appeng.items.storage.StorageTier;
import com.yongaishide.chaosworld.datagen.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Define algumas interfaces de obtenção de informações e alguns métodos para definir/obter rapidamente informações para o ItemStack
 * <p>
 * A principal diferença do IAEUniversalCell é que ele fornece um caminho e métodos simplificados para a interface necessária para o BigInteger.
 *
 * @author Frostbite
 */
public interface IAEBigIntegerCell extends IUpgradeableItem {

    double getIdleDrain();

    // Novos métodos adicionados
    StorageTier getTier();
    AEKeyType getKeyType();

    long getMaxBytes(ItemStack stack);
    int getMaxTypes(ItemStack stack);
    int getBytesPerType(ItemStack stack);

    default boolean isBlackListed(ItemStack stack, AEKey requestedAddition) {
        return false;
    }

    // === Métodos auxiliares a seguir: migrados de NBT para Componentes de Dados, o comportamento permanece o mesmo ===
    static BigInteger getUsedBytes(ItemStack stack) {
        BigInteger v = stack.get(ModDataComponents.CELL_BYTE_USAGE_BIG.get());
        return v == null ? BigInteger.ZERO : v;
    }

    static void setUsedBytes(ItemStack stack, BigInteger usedBytes) {
        stack.set(ModDataComponents.CELL_BYTE_USAGE_BIG.get(), usedBytes);
    }

    static long getUsedTypes(ItemStack stack) {
        Long v = stack.get(ModDataComponents.CELL_TYPES_USAGE.get());
        return v == null ? 0L : v;
    }

    static void setUsedTypes(ItemStack stack, long usedTypes) {
        stack.set(ModDataComponents.CELL_TYPES_USAGE.get(), usedTypes);
    }

    static CellState getCellState(ItemStack stack) {
        String s = stack.get(ModDataComponents.CELL_STATE.get());
        if (s == null) return CellState.EMPTY;
        try {
            return CellState.valueOf(s);
        } catch (IllegalArgumentException ex) {
            return CellState.EMPTY;
        }
    }

    static void setCellState(ItemStack stack, CellState newState) {
        stack.set(ModDataComponents.CELL_STATE.get(), newState.name());
    }

    static List<GenericStack> getTooltipShowStacks(ItemStack stack) {
        List<GenericStack> raw = stack.get(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get());
        if (raw == null || raw.isEmpty()) return List.of();
        // Retorna uma cópia imutável para manter a semântica de "visualização somente leitura" da lógica original
        return Collections.unmodifiableList(new ArrayList<>(raw));
    }

    static void setTooltipShowStacks(ItemStack stack, List<GenericStack> showStacks) {
        if (showStacks == null || showStacks.isEmpty()) {
            stack.remove(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get());
            return;
        }
        // Filtra possíveis nulos para manter a semântica de "pular entradas ruins" da implementação antiga
        List<GenericStack> cleaned = new ArrayList<>(showStacks.size());
        for (GenericStack gs : showStacks) {
            if (gs != null) cleaned.add(gs);
        }
        if (cleaned.isEmpty()) {
            stack.remove(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get());
        } else {
            stack.set(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get(), cleaned);
        }
    }
}

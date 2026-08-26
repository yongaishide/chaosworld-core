// Conteúdo de src/main/java/com/raishxn/ufo/item/custom/cell/ModCellItem.java

package com.yongaishide.chaosworld.item.custom.cell;

import appeng.api.stacks.AEKeyType;
import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageTier;
import com.yongaishide.chaosworld.item.ModCellItems;
import net.minecraft.world.item.Item;

/**
 * Classe base para as novas células de armazenamento customizadas.
 */
public class ModCellItem extends BasicStorageCell {

    public ModCellItem(StorageTier tier, int maxTypes, AEKeyType keyType) {
        super(
                new Item.Properties().stacksTo(1),
                tier.idleDrain(),
                calculateKibiBytes(tier),
                calculateBytesPerType(tier),
                maxTypes,
                keyType
        );
    }

    private static int calculateKibiBytes(StorageTier tier) {
        // Para a tier infinita, usamos o maior valor de int possível.
        if (tier == ModCellItems.TIER_INFINITY) {
            return Integer.MAX_VALUE /  1024;
        }
        // Para as outras tiers, o valor cabe em um int, então fazemos a conversão.
        return (int) (tier.bytes() / 1024);
    }

    private static int calculateBytesPerType(StorageTier tier) {
        if (tier == ModCellItems.TIER_INFINITY) {
            // Manter o valor original para o overhead da célula infinita, pois sua lógica é separada.
            return 262144;
        }
        // CORREÇÃO PARA O PROBLEMA DO USO EXCESSIVO (5859376 bytes used)
        // A divisão original resultava em um número muito grande devido à alta capacidade do tier.
        // Devemos retornar o custo de overhead por tipo para um valor pequeno (1 byte para itens).
        return 1;
    }
}
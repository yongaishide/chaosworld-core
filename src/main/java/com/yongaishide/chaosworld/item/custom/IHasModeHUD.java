package com.yongaishide.chaosworld.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Uma interface para itens que devem exibir um HUD com seu modo atual.
 */
public interface IHasModeHUD {

    /**
     * Retorna o texto a ser exibido no HUD para o itemstack atual.
     * @param stack O ItemStack do item.
     * @return O Component (texto formatado) a ser desenhado na tela.
     */
    Component getModeHudComponent(ItemStack stack);
}
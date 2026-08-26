package com.yongaishide.chaosworld.part;

import appeng.api.parts.IPartItem;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.crafting.PatternProviderPart;
import com.yongaishide.chaosworld.block.entity.QuantumPatternHatchBE;
import com.yongaishide.chaosworld.init.ModMenus;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class QuantumPatternProviderPart extends PatternProviderPart {
    public QuantumPatternProviderPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected PatternProviderLogic createLogic() {
        return new PatternProviderLogic(this.getMainNode(), this, QuantumPatternHatchBE.PATTERN_CAPACITY);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ModMenus.QUANTUM_PATTERN_HATCH_MENU, player, subMenu.getLocator());
    }

    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!player.level().isClientSide()) {
            MenuOpener.open(ModMenus.QUANTUM_PATTERN_HATCH_MENU, player, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(getPartItem().asItem());
    }
}

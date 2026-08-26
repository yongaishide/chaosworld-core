package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UfoEnergyHoeItem extends HoeItem implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final String TAG_RANGE = "range";
    private static final int[] RANGES = {0, 1, 2, 3, 5}; // 0=1x1, 1=3x3...

    public UfoEnergyHoeItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    // --- LÓGICA DE AÇÃO EM ÁREA (CORRIGIDA) ---
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos originPos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();
        int range = getRange(stack);

        if (player == null || level.isClientSide()) {
            return InteractionResult.PASS;
        }

        boolean actionPerformed = false;
        // A lógica de getPositions é a mesma usada aqui
        for (BlockPos pos : getPositions(originPos, range)) {
            BlockState blockState = level.getBlockState(pos);

            BlockState modifiedState = level.getBlockState(pos).getToolModifiedState(
                    pContext,
                    net.neoforged.neoforge.common.ItemAbilities.HOE_TILL,
                    false
            );

            if (modifiedState != null) {
                if (consumeEnergy(stack)) {
                    level.setBlock(pos, modifiedState, 11);
                    actionPerformed = true;
                } else {
                    break;
                }
            }
        }

        if (actionPerformed) {
            level.playSound(player, originPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    // --- MÉTODOS DE TROCA DE MODO E VISUAIS ---

    @Override
    public void cycleMode(ItemStack stack, Player player) {
        int currentRange = getRange(stack);
        int currentIndex = 0;
        for (int i = 0; i < RANGES.length; i++) { if (RANGES[i] == currentRange) { currentIndex = i; break; } }
        int nextIndex = (currentIndex + 1) % RANGES.length;
        int newRange = RANGES[nextIndex];

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt(TAG_RANGE, newRange);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    // --- MÉTODO CORRIGIDO ---
    public static int getRange(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) return data.copyTag().getInt(TAG_RANGE);
        return RANGES[0];
    }

    // --- MÉTODO NOVO ADICIONADO ---
    public static List<BlockPos> getPositions(BlockPos origin, int range) {
        return StreamSupport.stream(
                        BlockPos.betweenClosed(origin.offset(-range, 0, -range), origin.offset(range, 0, range)).spliterator(), false)
                .map(BlockPos::immutable)
                .collect(Collectors.toList());
    }

    // Arquivo: UfoEnergyHoeItem.java

    @Override
    public Component getModeHudComponent(ItemStack stack) {
        int range = getRange(stack);
        int dimension = (range == 0) ? 1 : (range * 2) + 1;
        String areaText = dimension + "x" + dimension;

        // Define a cor baseada no range atual (com o range 5 da enxada)
        ChatFormatting color;
        switch (range) {
            case 1:  // 3x3
                color = ChatFormatting.GREEN;
                break;
            case 2:  // 5x5
                color = ChatFormatting.YELLOW;
                break;
            case 3:  // 7x7
                color = ChatFormatting.RED;
                break;
            case 5:  // 11x11
                color = ChatFormatting.AQUA; // Uma cor especial para o maior range
                break;
            default: // 1x1 (range 0)
                color = ChatFormatting.WHITE;
                break;
        }

        // Cria um componente para o texto da área e aplica a cor
        Component coloredArea = Component.literal(areaText).withStyle(color);

        // Retorna o componente final usando a nova chave de tradução
        return Component.translatable("tooltip.ufo.area_mode", coloredArea);
    }

    @Override
    public Component getName(ItemStack stack) { return IEnergyTool.super.getName(stack); }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(getModeHudComponent(pStack));
        IEnergyTool.super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    @Override public int getEnergyPerUse() { return 50; }
    @Override public boolean isBarVisible(ItemStack pStack) { return EnergyToolHelper.isBarVisible(pStack); }
    @Override public int getBarWidth(ItemStack pStack) { return EnergyToolHelper.getBarWidth(pStack); }
    @Override public int getBarColor(ItemStack pStack) { return EnergyToolHelper.getBarColor(pStack); }
}
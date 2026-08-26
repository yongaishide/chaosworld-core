package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.util.EnergyToolHelper; // Importante: Importar o Helper
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HammerItem extends DiggerItem implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final String TAG_RANGE = "range";
    private static final int[] RANGES = {0, 1, 2, 3}; // 0=1x1, 1=3x3, 2=5x5, 3=7x7

    public HammerItem(Tier pTier, Properties pProperties) {
        super(pTier, BlockTags.MINEABLE_WITH_PICKAXE, pProperties);
    }

    // --- PADRONIZAÇÃO COM IEnergyTool E EnergyToolHelper ---

    @Override
    public Component getName(ItemStack stack) {
        // Padrão RGB do IEnergyTool
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        // Delega para o Helper (Padrão)
        return EnergyToolHelper.isBarVisible(pStack);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        // Delega para o Helper (Padrão)
        return EnergyToolHelper.getBarWidth(pStack);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        // Delega para o Helper (Padrão)
        return EnergyToolHelper.getBarColor(pStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        // Adiciona informações específicas do Martelo (Modo de Área)
        pTooltipComponents.add(getModeHudComponent(pStack));

        // Chama o tooltip padrão de energia (Padrão)
        IEnergyTool.super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    @Override
    public int getEnergyPerUse() {
        return 50; // Custo por bloco
    }

    // --- LÓGICA DE MINERAÇÃO (MANTIDA) ---

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide && pEntityLiving instanceof ServerPlayer player) {
            int range = getRange(pStack);

            // Se for modo 1x1, apenas consome energia do bloco central (se for quebrável)
            if (range == 0) {
                // Nota: A lógica de consumir energia do bloco central geralmente fica no evento ou aqui se chamarmos super
                // Mas para seguir o padrão do UfoEnergyAxeItem, podemos verificar aqui:
                if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
                    consumeEnergy(pStack);
                }
                return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
            }

            List<BlockPos> blocksToBreak = getBlocksToBeDestroyed(range, pPos, player);
            boolean autoSmelt = pStack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false);

            // Consome energia para o bloco central também
            if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
                consumeEnergy(pStack);
            }

            for (BlockPos targetPos : blocksToBreak) {
                if (targetPos.equals(pPos)) continue;

                BlockState targetState = pLevel.getBlockState(targetPos);

                if (!targetState.isAir() && pStack.isCorrectToolForDrops(targetState)) {
                    // Tenta consumir energia para o bloco extra
                    if (consumeEnergy(pStack)) {
                        if (autoSmelt) {
                            smeltAndSpawn(pLevel, targetPos, targetState, pStack);
                            pLevel.destroyBlock(targetPos, false, player);
                        } else {
                            pLevel.destroyBlock(targetPos, true, player);
                        }
                    } else {
                        // Se faltar energia, para de quebrar a área, mas o bloco central já foi processado
                        break;
                    }
                }
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    // --- MÉTODOS AUXILIARES (ÁREA, AUTO-SMELT, MODOS) ---

    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initalBlockPos, Player player) {
        List<BlockPos> positions = new ArrayList<>();
        BlockHitResult traceResult = player.level().clip(new ClipContext(player.getEyePosition(1f),
                (player.getEyePosition(1f).add(player.getViewVector(1f).scale(6f))),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        if(traceResult.getType() == HitResult.Type.MISS) {
            return positions;
        }

        Direction face = traceResult.getDirection();
        if(face == Direction.DOWN || face == Direction.UP) {
            for(int x = -range; x <= range; x++) for(int y = -range; y <= range; y++) positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY(), initalBlockPos.getZ() + y));
        } else if(face == Direction.NORTH || face == Direction.SOUTH) {
            for(int x = -range; x <= range; x++) for(int y = -range; y <= range; y++) positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY() + y, initalBlockPos.getZ()));
        } else if(face == Direction.EAST || face == Direction.WEST) {
            for(int x = -range; x <= range; x++) for(int y = -range; y <= range; y++) positions.add(new BlockPos(initalBlockPos.getX(), initalBlockPos.getY() + y, initalBlockPos.getZ() + x));
        }
        return positions;
    }

    private void smeltAndSpawn(Level level, BlockPos pos, BlockState state, ItemStack tool) {
        if (level.isClientSide) return;
        List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, level.getBlockEntity(pos), null, tool);

        for (ItemStack drop : drops) {
            Optional<net.minecraft.world.item.crafting.RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(drop), level);

            if (recipe.isPresent()) {
                ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
                result.setCount(drop.getCount());
                spawnItem(level, pos, result);
            } else {
                spawnItem(level, pos, drop);
            }
        }
    }

    private void spawnItem(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
    }

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

        player.displayClientMessage(Component.translatable("tooltip.ufo.mode_changed", newRange), true);
    }

    public static int getRange(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.copyTag().contains(TAG_RANGE)) {
            return customData.copyTag().getInt(TAG_RANGE);
        }
        return RANGES[0];
    }

    @Override
    public Component getModeHudComponent(ItemStack stack) {
        int range = getRange(stack);
        int dimension = (range == 0) ? 1 : (range * 2) + 1;
        String areaText = dimension + "x" + dimension;
        ChatFormatting color;
        switch (range) {
            case 1: color = ChatFormatting.GREEN; break;
            case 2: color = ChatFormatting.YELLOW; break;
            case 3: color = ChatFormatting.RED; break;
            default: color = ChatFormatting.WHITE; break;
        }
        Component coloredArea = Component.literal(areaText).withStyle(color);
        return Component.translatable("tooltip.ufo.area_mode", coloredArea);
    }
}
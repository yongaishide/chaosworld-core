package com.yongaishide.chaosworld.item.custom;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AetherContainmentCapsuleItem extends Item {
    public static final int CAPACITY = 4000;

    public AetherContainmentCapsuleItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack accStack = player.getItemInHand(hand); // Renomeei a variável local para consistência
        if (level.isClientSide) return InteractionResultHolder.success(accStack);

        InteractionHand otherHand = (hand == InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherStack = player.getItemInHand(otherHand);

        if (otherStack.isEmpty()) return InteractionResultHolder.pass(accStack);

        IFluidHandlerItem accHandler = accStack.getCapability(Capabilities.FluidHandler.ITEM, null);
        IFluidHandlerItem otherHandler = otherStack.getCapability(Capabilities.FluidHandler.ITEM, null);

        if (accHandler != null && otherHandler != null) {
            if (player.isShiftKeyDown()) {
                // --- MODO ESVAZIAR (ACC -> Outro) ---
                FluidStack drained = accHandler.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    int filled = otherHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                    if (filled > 0) {
                        accHandler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                        player.setItemInHand(otherHand, otherHandler.getContainer());
                        player.setItemInHand(hand, accHandler.getContainer());

                        // MUDANÇA: CCA -> ACC
                        player.displayClientMessage(Component.translatable("message.ufo.acc.emptied").withStyle(ChatFormatting.YELLOW), true);
                        return InteractionResultHolder.consume(accHandler.getContainer());
                    }
                }
            } else {
                // --- MODO ENCHER (Outro -> ACC) ---
                FluidStack toDrain = otherHandler.drain(CAPACITY, IFluidHandler.FluidAction.SIMULATE);

                if (!toDrain.isEmpty()) {
                    if (toDrain.is(ModTags.Fluids.HAZARDOUS)) {
                        int filled = accHandler.fill(toDrain, IFluidHandler.FluidAction.EXECUTE);
                        if (filled > 0) {
                            otherHandler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                            player.setItemInHand(otherHand, otherHandler.getContainer());
                            ItemStack newAcc = accHandler.getContainer();
                            player.setItemInHand(hand, newAcc);

                            // MUDANÇA: CCA -> ACC
                            player.displayClientMessage(Component.translatable("message.ufo.acc.filled_prefix")
                                    .append(" (" + filled + "mB)")
                                    .withStyle(ChatFormatting.GREEN), true);
                            return InteractionResultHolder.consume(newAcc);
                        } else {
                            // MUDANÇA: CCA -> ACC
                            player.displayClientMessage(Component.translatable("message.ufo.acc.full").withStyle(ChatFormatting.RED), true);
                        }
                    } else {
                        // MUDANÇA: CCA -> ACC
                        player.displayClientMessage(Component.translatable("message.ufo.acc.wrong_fluid").withStyle(ChatFormatting.RED), true);
                    }
                }
            }
        }

        return InteractionResultHolder.fail(accStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        // MUDANÇA: Chave de tradução atualizada para .acc
        tooltipComponents.add(Component.translatable("tooltip.ufo.acc").withStyle(ChatFormatting.GRAY));
        FluidHandlerItemStack handler = new HazardousFluidHandler(stack, CAPACITY);
        FluidStack fluid = handler.getFluid();
        if (!fluid.isEmpty()) {
            // Já estava em inglês "Contains:", mantido.
            tooltipComponents.add(Component.translatable("gui.ufo.contains_prefix")
                    .append(fluid.getHoverName())
                    .append(" (" + fluid.getAmount() + "mB)")
                    .withStyle(ChatFormatting.AQUA));
        } else {
            // Já estava em inglês "Empty", mantido.
            tooltipComponents.add(Component.translatable("gui.ufo.empty").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static class HazardousFluidHandler extends FluidHandlerItemStack {
        public HazardousFluidHandler(@NotNull ItemStack container, int capacity) {
            super(ModDataComponents.FLUID_CONTENT, container, capacity);
        }
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return stack.is(ModTags.Fluids.HAZARDOUS);
        }
    }
}
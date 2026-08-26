package com.yongaishide.chaosworld.event;

import com.yongaishide.chaosworld.block.ModBlocks;
import com.yongaishide.chaosworld.block.MultiblockBlocks;
import com.yongaishide.chaosworld.api.multiblock.MultiblockControllerDefinitions;
import com.yongaishide.chaosworld.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = "chaosworld_core", value = Dist.CLIENT)
public class ModTooltipEventHandler {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.is(ModBlocks.QUANTUM_ENERGY_CELL.get().asItem())) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            boolean chargedPreview = customData != null
                    && customData.copyTag().getBoolean("ufoQuantumEnergyCellChargedPreview");
            event.getToolTip().add(Component.translatable(chargedPreview
                    ? "tooltip.ufo.quantum_cell.charged"
                    : "tooltip.ufo.quantum_cell.discharged").withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.stored_energy_infinite").withStyle(ChatFormatting.GRAY));
        }
        else if (stack.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get().asItem())) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.field_generator.tier1").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.field_generator.nexus_info").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get().asItem())) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.field_generator.tier2").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.field_generator.over_tier").withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.field_generator.nexus_info2").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get().asItem())) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.field_generator.tier3").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.field_generator.coolant_required").withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.field_generator.nexus_info3").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem())) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.cryoforge.coolant_requires_mk3").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.cryoforge.field_position_hint").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (isAeHatch(stack)) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.ae_hatch.connect_info").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.ae_hatch.storage_info").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(ModItems.STABLE_COOLANT_BUCKET.get())) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.stable_coolant.info").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.stable_coolant.craft_info").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(ModItems.GELID_CRYOTHEUM_BUCKET.get())) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.gelid_cryotheum.info").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.coolant.machine_hint").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(ModItems.TEMPORAL_FLUID_BUCKET.get())) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.temporal_fluid.info").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.coolant.machine_hint").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(net.minecraft.world.item.Items.WATER_BUCKET)) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.water_coolant.info").withStyle(ChatFormatting.GRAY));
        }
        else if (stack.is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ae2", "creative_energy_cell")))) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.creative_energy_cell.header").withStyle(ChatFormatting.LIGHT_PURPLE));
            event.getToolTip().add(Component.translatable("tooltip.ufo.creative_energy_cell.desc").withStyle(ChatFormatting.GRAY));
        }
        else if (stack.is(ModItems.NEUTRONITE_INGOT.get())) {
            event.getToolTip().add(Component.translatable("tooltip.ufo.neutronite.lore1").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.neutronite.lore2").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.neutronite.lore3").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    private static boolean isAeHatch(ItemStack stack) {
        return stack.is(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get().asItem())
                || stack.is(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get().asItem())
                || stack.is(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get().asItem())
                || stack.is(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get().asItem());
    }
}

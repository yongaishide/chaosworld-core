package com.yongaishide.chaosworld.block;

import appeng.block.AEBaseBlockItem;
import appeng.block.networking.CreativeEnergyCellBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.definitions.BlockDefinition;
import com.yongaishide.chaosworld.fluid.ModFluids;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import com.yongaishide.chaosworld.item.ModItems;
import com.yongaishide.chaosworld.item.custom.AnimatedNameBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;
import net.pedroksl.ae2addonlib.registry.BlockRegistry;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks extends BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks("chaosworld_core");

    public static final ModBlocks INSTANCE = new ModBlocks();

    ModBlocks() {
        super("chaosworld_core");
    }

    // Seus blocos base que chamam o m茅todo helper
    public static final DeferredBlock<Block> QUANTUM_LATTICE_FRAME = registerBlockWithAnimatedItem("quantum_lattice_frame",
            () -> new Block(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> GRAVITON_PLATED_CASING = registerBlockWithAnimatedItem("graviton_plated_casing",
            () -> new Block(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> WHITE_DWARF_FRAGMENT_BLOCK = registerBlockWithAnimatedItem("white_dwarf_fragment_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(6.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> PULSAR_FRAGMENT_BLOCK = PulsarBlockWithAnimatedItem("pulsar_fragment_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(6.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> NEUTRON_STAR_FRAGMENT_BLOCK = NeutronStarBlockWithAnimatedItem("neutron_star_fragment_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(6.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<LiquidBlock> NEUTRON_STAR_FRAGMENT_FLUID_BLOCK = BLOCKS.register("neutron_star_fragment_fluid_block",
            () -> new LiquidBlock((FlowingFluid) ModFluids.SOURCE_NEUTRON_STAR_FRAGMENT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));

    public static final DeferredBlock<LiquidBlock> PULSAR_FRAGMENT_FLUID_BLOCK = BLOCKS.register("pulsar_fragment_fluid_block",
            () -> new LiquidBlock((FlowingFluid) ModFluids.SOURCE_PULSAR_FRAGMENT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));

    public static final DeferredBlock<LiquidBlock> WHITE_DWARF_FRAGMENT_FLUID_BLOCK = BLOCKS.register("white_dwarf_fragment_fluid_block",
            () -> new LiquidBlock((FlowingFluid) ModFluids.SOURCE_WHITE_DWARF_FRAGMENT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));

    public static final DeferredBlock<LiquidBlock> LIQUID_STARLIGHT_FLUID_BLOCK = BLOCKS.register("liquid_starlight_fluid_block",
            () -> new LiquidBlock((FlowingFluid) ModFluids.SOURCE_LIQUID_STARLIGHT_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));

    public static final DeferredBlock<LiquidBlock> PRIMORDIAL_MATTER_FLUID_BLOCK = BLOCKS.register("primordial_matter_fluid_block",
            () -> new LiquidBlock((FlowingFluid) ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));

    public static final DeferredBlock<LiquidBlock> RAW_STAR_MATTER_PLASMA_FLUID_BLOCK = BLOCKS.register("raw_star_matter_plasma_fluid_block",
            () -> new LiquidBlock((FlowingFluid) ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));

    public static final DeferredBlock<LiquidBlock> TRANSCENDING_MATTER_FLUID_BLOCK = BLOCKS.register("transcending_matter_fluid_block",
            () -> new LiquidBlock((FlowingFluid) ModFluids.SOURCE_TRANSCENDING_MATTER_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));

    public static final DeferredBlock<LiquidBlock> UU_MATTER_FLUID_BLOCK = BLOCKS.register("uu_matter_fluid_block",
            () -> new LiquidBlock((FlowingFluid) ModFluids.SOURCE_UU_MATTER_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));

    public static final DeferredBlock<LiquidBlock> UU_AMPLIFIER_FLUID_BLOCK = BLOCKS.register("uu_amplifier_fluid_block",
            () -> new LiquidBlock((FlowingFluid) ModFluids.SOURCE_UU_AMPLIFIER_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final DeferredBlock<LiquidBlock> GELID_CRYOTHEUM_BLOCK = BLOCKS.register("gelid_cryotheum_block",
            () -> new LiquidBlock(ModFluids.SOURCE_GELID_CRYOTHEUM.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final DeferredBlock<LiquidBlock> STABLE_COOLANT_BLOCK = BLOCKS.register("stable_coolant_block",
            () -> new LiquidBlock(ModFluids.SOURCE_STABLE_COOLANT.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final DeferredBlock<LiquidBlock> TEMPORAL_FLUID_BLOCK = BLOCKS.register("temporal_fluid_block",
            () -> new LiquidBlock((FlowingFluid)ModFluids.SOURCE_TEMPORAL_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final DeferredBlock<LiquidBlock> SPATIAL_FLUID_BLOCK = BLOCKS.register("spatial_fluid_block",
            () -> new LiquidBlock((FlowingFluid)ModFluids.SOURCE_SPATIAL_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final DeferredBlock<com.yongaishide.chaosworld.block.DimensionalMatterAssemblerBlock> DIMENSIONAL_MATTER_ASSEMBLER_BLOCK =
            registerBlockWithStaticItem("dimensional_matter_assembler", com.yongaishide.chaosworld.block.DimensionalMatterAssemblerBlock::new);

    public static final DeferredBlock<EnergyCellBlock> UFO_ENERGY_CELL = BLOCKS.register("energy_cell",
            () -> new EnergyCellBlock(1_000_000_000D, 16_000, 1_000_000));

    public static final DeferredBlock<CreativeEnergyCellBlock> QUANTUM_ENERGY_CELL = BLOCKS.register("quantum_energy_cell",
            CreativeEnergyCellBlock::new);

    private static <T extends Block> DeferredBlock<T> registerBlockWithAnimatedItem(String name, java.util.function.Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new AnimatedNameBlockItem(block.get(), new Item.Properties(),
                ChatFormatting.WHITE,
                ChatFormatting.GRAY,
                ChatFormatting.DARK_GRAY,
                ChatFormatting.BLACK,
                ChatFormatting.DARK_GRAY,
                ChatFormatting.GRAY));
        return block;
    }
    private static <T extends Block> DeferredBlock<T> registerBlockWithStaticItem(String name, java.util.function.Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
    private static <T extends Block> DeferredBlock<T> PulsarBlockWithAnimatedItem(String name, java.util.function.Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new AnimatedNameBlockItem(block.get(), new Item.Properties(),
                ChatFormatting.WHITE,
                ChatFormatting.GREEN,
                ChatFormatting.DARK_GREEN));
        return block;
    }
    private static <T extends Block> DeferredBlock<T> NeutronStarBlockWithAnimatedItem(String name, java.util.function.Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new AnimatedNameBlockItem(block.get(), new Item.Properties(),
                ChatFormatting.WHITE,
                ChatFormatting.BLUE,
                ChatFormatting.DARK_BLUE,
                ChatFormatting.AQUA));
        return block;
    }
    public void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    protected static <T extends Block> BlockDefinition<T> block(
            String englishName, String id, Supplier<T> blockSupplier) {
        return block("chaosworld_core", englishName, id, blockSupplier, null);
    }
    protected static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        return block("chaosworld_core", englishName, id, blockSupplier, itemFactory);
    }
}

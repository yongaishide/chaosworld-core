package com.yongaishide.chaosworld.datagen;

import appeng.api.stacks.GenericStack;
import com.mojang.serialization.Codec;
import com.yongaishide.chaosworld.item.ModItems;
import com.yongaishide.chaosworld.item.custom.cell.BigIntegerCodec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs; // Importe este
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModDataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "chaosworld_core");

    // --- NOVOS COMPONENTES PARA AS C脡LULAS BIGINTEGER ---
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BigInteger>> CELL_BYTE_USAGE_BIG = register("cell_byte_usage_big", builder -> builder.persistent(BigIntegerCodec.INSTANCE).networkSynchronized(BigIntegerCodec.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> CELL_TYPES_USAGE =
            register("cell_types_usage", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CELL_STATE = register("cell_state", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));

    // LINHA CORRIGIDA ABAIXO
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GenericStack>>> CELL_SHOW_TOOLTIP_STACKS = register("cell_show_tooltip_stacks", builder -> builder.persistent(GenericStack.CODEC.listOf()).networkSynchronized(GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list())));

    // Para a Picareta
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_SMELT =
            register("auto_smelt", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PROGRESSIVE_FORTUNE =
            register("progressive_fortune", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    // Para a Espada
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> KILL_COUNT =
            register("kill_count", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COMBO_COUNT =
            register("combo_count", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LAST_HIT_TIME =
            register("last_hit_time", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

    // --- Seus Componentes Existentes (sem altera莽玫es) ---
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY =
            DATA_COMPONENTS.register("energy", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TOOL_MODE_INDEX =
            DATA_COMPONENTS.register("tool_mode_index", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> MODE =
            DATA_COMPONENTS.register("mode", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FAST_MODE =
            DATA_COMPONENTS.register("fast_mode", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BOW_FAST_MODE =
            DATA_COMPONENTS.register("bow_fast_mode", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DMA_ALLOWED_OUTPUTS =
            register("dma_allowed_outputs", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT = register("fluid_content",
            builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    // --- 结构终端设置 ---
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<net.minecraft.nbt.CompoundTag>> TERMINAL_SETTINGS =
            register("terminal_settings", builder -> builder
                    .persistent(net.minecraft.nbt.CompoundTag.CODEC)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.COMPOUND_TAG));

    // Corre莽茫o para SAVED_INVENTORY (caso tenhas adicionado o do CCM anteriormente com o nome errado)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> SAVED_INVENTORY = register("saved_inventory",
            builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));

    // --- Novo m茅todo helper para simplificar o registro ---
    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return DATA_COMPONENTS.register(name, () -> operator.apply(DataComponentType.builder()).build());
    }
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> CELL_UUID = register("cell_uuid", builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC));
    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}

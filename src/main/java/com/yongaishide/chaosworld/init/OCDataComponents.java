package com.yongaishide.chaosworld.init;

import appeng.api.stacks.GenericStack;
import com.yongaishide.chaosworld.ChaosWorld;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class OCDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ChaosWorld.MODID);

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String name, UnaryOperator<DataComponentType.Builder<T>> builder
    ) {
        return DATA_COMPONENTS.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> CELL_BYTES_USAGE =
            register("cell_bytes_usage", b -> b
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
                    .cacheEncoding()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BigInteger>> CELL_BYTE_USAGE_BIG =
            register("cell_byte_usage_big", b -> b
                    .persistent(Codec.STRING.comapFlatMap(
                            s -> {
                                try {
                                    return DataResult.success(new BigInteger(s));
                                } catch (NumberFormatException e) {
                                    return DataResult.error(() -> "Invalid BigInteger: " + s);
                                }
                            },
                            BigInteger::toString
                    ))
                    .networkSynchronized(StreamCodec.of(
                            (RegistryFriendlyByteBuf buf, BigInteger v) -> {
                                byte[] arr = v.toByteArray();
                                buf.writeByteArray(arr);
                            },
                            (RegistryFriendlyByteBuf buf) -> {
                                byte[] arr = buf.readByteArray();
                                return new BigInteger(arr);
                            }
                    ))
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CELL_TYPES_USAGE =
            register("cell_types_usage", b -> b
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .cacheEncoding()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CELL_STATE =
            register("cell_state", b -> b
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .cacheEncoding()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GenericStack>>> CELL_SHOW_TOOLTIP_STACKS =
            register("cell_show_tooltip_stacks", b -> b
                    .persistent(GenericStack.FAULT_TOLERANT_LIST_CODEC)
                    .networkSynchronized(
                            ByteBufCodecs.collection(
                                    ArrayList::new,
                                    GenericStack.STREAM_CODEC
                            )
                    )
                    .cacheEncoding()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> CELL_UUID =
            register("cell_uuid", b -> b
                    .persistent(UUIDUtil.CODEC)
                    .networkSynchronized(UUIDUtil.STREAM_CODEC)
                    .cacheEncoding()
            );

    public static void register(IEventBus eventBus)
    {
        DATA_COMPONENTS.register(eventBus);
    }
}

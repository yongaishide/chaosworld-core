package com.yongaishide.chaosworld.item.custom.cell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.math.BigInteger;

public class BigIntegerCodec {
    public static final Codec<BigInteger> INSTANCE = Codec.STRING.comapFlatMap(
            s -> {
                try {
                    return DataResult.success(new BigInteger(s));
                } catch (NumberFormatException e) {
                    return DataResult.error(() -> "Not a valid BigInteger: " + s);
                }
            },
            BigInteger::toString
    );

    public static final StreamCodec<ByteBuf, BigInteger> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
            BigInteger::new,
            BigInteger::toString
    );
}
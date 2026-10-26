/*
 * Derived from Applied Botanics 1.6.0-alpha.3
 *   https://github.com/ramidzkh/Applied-Botanics
 * Copyright (C) ramidzkh and contributors, LGPL-3.0-or-later
 * Modified for Chaos World Core by chaosworld on 2026-10-26.
 * See THIRD_PARTY_NOTICES.md for details.
 */
package com.yongaishide.chaosworld.botania;

import com.mojang.serialization.MapCodec;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;

import com.yongaishide.chaosworld.ChaosWorld;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;

public class ManaKeyType extends AEKeyType {

    public static final Component MANA = Component.translatable("gui." + ChaosWorld.MODID + ".mana");

    public static final AEKeyType TYPE = new ManaKeyType();
    private static final MapCodec<? extends AEKey> CODEC = MapCodec.unit(ManaKey.KEY);

    private ManaKeyType() {
        super(ChaosWorld.id("mana"), ManaKey.class, MANA);
    }

    @Override
    public MapCodec<? extends AEKey> codec() {
        return CODEC;
    }

    @Override
    public int getAmountPerOperation() {
        return 500;
    }

    @Override
    public int getAmountPerByte() {
        return 500;
    }

    @Override
    public @Nullable AEKey readFromPacket(RegistryFriendlyByteBuf input) {
        return ManaKey.KEY;
    }

    @Override
    public @Nullable AEKey loadKeyFromTag(HolderLookup.Provider registries, CompoundTag tag) {
        return ManaKey.KEY;
    }

    @Override
    public int getAmountPerUnit() {
        return 1;
    }

    @Override
    public @Nullable String getUnitSymbol() {
        return "mana";
    }
}

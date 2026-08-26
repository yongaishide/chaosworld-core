package com.yongaishide.chaosworld.compat.mekanism;

import net.minecraft.resources.ResourceLocation;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.MapCodec;
import mekanism.api.MekanismAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.TagKey;

import java.util.Objects;
import java.util.stream.Stream;

public final class UfoMekanismKeyType extends AEKeyType {
    public static final AEKeyType TYPE = new UfoMekanismKeyType();

    private UfoMekanismKeyType() {
        super(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("chaosworld_core", "chemical"), UfoMekanismKey.class, net.minecraft.network.chat.Component.translatable("item.chaosworld_core.pulsar_cell"));
    }

    @Override
    public MapCodec<? extends AEKey> codec() {
        return UfoMekanismKey.MAP_CODEC;
    }

    @Override
    public int getAmountPerOperation() {
        return AEFluidKey.AMOUNT_BUCKET * 125 / 1000;
    }

    @Override
    public int getAmountPerByte() {
        return 8 * AEFluidKey.AMOUNT_BUCKET;
    }

    @Override
    public UfoMekanismKey readFromPacket(RegistryFriendlyByteBuf input) {
        Objects.requireNonNull(input);
        return UfoMekanismKey.fromPacket(input);
    }

    @Override
    public AEKey loadKeyFromTag(HolderLookup.Provider registries, CompoundTag tag) {
        return UfoMekanismKey.fromTag(registries, tag);
    }

    @Override
    public int getAmountPerUnit() {
        return AEFluidKey.AMOUNT_BUCKET;
    }

    @Override
    public Stream<TagKey<?>> getTagNames() {
        return MekanismAPI.CHEMICAL_REGISTRY.getTagNames().map(tag -> tag);
    }

    @Override
    public String getUnitSymbol() {
        return "B";
    }
}

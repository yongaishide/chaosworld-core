package com.yongaishide.chaosworld.compat.mekanism;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.core.AELog;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.radiation.IRadiationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class UfoMekanismKey extends AEKey {
    public static final MapCodec<UfoMekanismKey> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Chemical.HOLDER_CODEC.fieldOf("id").forGetter(key -> key.getStack().getChemicalHolder()))
            .apply(instance, chemical -> UfoMekanismKey.of(new ChemicalStack(chemical, 1))));
    public static final Codec<UfoMekanismKey> CODEC = MAP_CODEC.codec();

    private final ChemicalStack stack;

    private UfoMekanismKey(ChemicalStack stack) {
        this.stack = stack;
    }

    @Nullable
    public static UfoMekanismKey of(ChemicalStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return new UfoMekanismKey(stack.copy());
    }

    public ChemicalStack getStack() {
        return stack;
    }

    public ChemicalStack withAmount(long amount) {
        return stack.copyWithAmount(amount);
    }

    @Override
    public AEKeyType getType() {
        return UfoMekanismKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Nullable
    public static UfoMekanismKey fromTag(HolderLookup.Provider registries, CompoundTag tag) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        try {
            return CODEC.decode(ops, tag).getOrThrow().getFirst();
        } catch (Exception e) {
            AELog.debug("Tried to load an invalid UFO Mekanism chemical key from NBT: %s", tag, e);
            return null;
        }
    }

    @Override
    public CompoundTag toTag(HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        return (CompoundTag) CODEC.encodeStart(ops, this).getOrThrow();
    }

    @Override
    public Object getPrimaryKey() {
        return stack.getChemical();
    }

    @Override
    public ResourceLocation getId() {
        return stack.getChemicalHolder().getKey().location();
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
        IRadiationManager.INSTANCE.dumpRadiation(GlobalPos.of(level.dimension(), pos), withAmount(amount));
    }

    @Override
    protected Component computeDisplayName() {
        return stack.getChemical().getTextComponent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isTagged(TagKey<?> tag) {
        return stack.is((TagKey<Chemical>) tag);
    }

    @Override
    public <T> @Nullable T get(DataComponentType<T> type) {
        return null;
    }

    @Override
    public boolean hasComponents() {
        return false;
    }

    @Override
    public void writeToPacket(RegistryFriendlyByteBuf data) {
        ChemicalStack.STREAM_CODEC.encode(data, stack);
    }

    public static UfoMekanismKey fromPacket(RegistryFriendlyByteBuf data) {
        return new UfoMekanismKey(ChemicalStack.STREAM_CODEC.decode(data));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (UfoMekanismKey) o;
        return Objects.equals(stack.getChemical(), that.stack.getChemical());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack.getChemical());
    }

    @Override
    public String toString() {
        return "UfoMekanismKey{" + "stack=" + stack.getChemical() + '}';
    }
}

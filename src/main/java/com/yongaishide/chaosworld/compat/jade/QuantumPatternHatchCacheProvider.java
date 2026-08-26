package com.yongaishide.chaosworld.compat.jade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.IElementHelper;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;

import com.yongaishide.chaosworld.block.entity.QuantumPatternHatchBE;

/**
 * Server-side data provider + client-side tooltip renderer for the Quantum Pattern Hatch.
 * <p>
 * The hatch is an AE2 Pattern Provider. Its internal return inventory buffers items and
 * fluids that could not be pushed back into the ME network. This provider serializes
 * those buffered stacks on the server and renders them as an item/fluid list on the client.
 */
public final class QuantumPatternHatchCacheProvider
        implements IServerDataProvider<BlockAccessor>, IBlockComponentProvider {

    public static final QuantumPatternHatchCacheProvider INSTANCE = new QuantumPatternHatchCacheProvider();

    private static final String NBT_CACHED = "cached";
    private static final String NBT_PATTERNS = "patterns";

    private QuantumPatternHatchCacheProvider() {
    }

    @Override
    public ResourceLocation getUid() {
        return ChaosWorldJadePlugin.QUANTUM_PATTERN_HATCH_CACHE;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof QuantumPatternHatchBE hatch)) {
            return;
        }

        var logic = hatch.getLogic();
        HolderLookup.Provider registries = accessor.getLevel().registryAccess();

        ListTag cached = new ListTag();
        var returnInv = logic.getReturnInv();
        for (int i = 0; i < returnInv.size(); i++) {
            GenericStack stack = returnInv.getStack(i);
            if (stack != null && stack.amount() > 0) {
                cached.add(GenericStack.writeTag(registries, stack));
            }
        }
        data.put(NBT_CACHED, cached);

        int patterns = 0;
        var patternInv = logic.getPatternInv();
        for (int i = 0; i < patternInv.size(); i++) {
            if (!patternInv.getStackInSlot(i).isEmpty()) {
                patterns++;
            }
        }
        data.putInt(NBT_PATTERNS, patterns);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        HolderLookup.Provider registries = accessor.getLevel().registryAccess();

        int patterns = data.getInt(NBT_PATTERNS);
        if (patterns > 0) {
            tooltip.add(Component.translatable("jade.ufo.quantum_pattern_hatch.patterns", patterns));
        }

        ListTag cached = data.getList(NBT_CACHED, Tag.TAG_COMPOUND);
        if (cached.isEmpty()) {
            return;
        }

        tooltip.add(Component.translatable("jade.ufo.quantum_pattern_hatch.cached"));

        List<GenericStack> stacks = new ArrayList<>(cached.size());
        for (Tag tag : cached) {
            GenericStack stack = GenericStack.readTag(registries, (CompoundTag) tag);
            if (stack != null && stack.amount() > 0) {
                stacks.add(stack);
            }
        }

        for (GenericStack stack : stacks) {
            AEKey key = stack.what();
            long amount = stack.amount();

            IElementHelper helper = IElementHelper.get();
            if (AEItemKey.is(key)) {
                AEItemKey itemKey = (AEItemKey) key;
                int displayCount = (int) Math.min(amount, itemKey.getMaxStackSize());
                if (displayCount <= 0) {
                    displayCount = 1;
                }
                ItemStack itemStack = itemKey.toStack(displayCount);
                tooltip.add(helper.item(itemStack));
            } else if (AEFluidKey.is(key)) {
                AEFluidKey fluidKey = (AEFluidKey) key;
                tooltip.add(helper.fluid(JadeFluidObject.of(fluidKey.getFluid(), amount)));
            } else {
                tooltip.add(Component.literal(key.getId().toString()));
            }

            tooltip.append(Component.translatable(
                    "jade.ufo.quantum_pattern_hatch.stack",
                    key.getDisplayName(),
                    key.formatAmount(amount, AmountFormat.FULL)));
        }
    }
}

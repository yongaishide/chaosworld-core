package com.yongaishide.chaosworld.compat.ftbquests;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.raishxn.ufo.compat.mekanism.UfoMekanismKey;
import com.yongaishide.chaosworld.compat.ftbquests.client.AEKeyConfig;
import com.yongaishide.chaosworld.compat.ftbquests.client.AEKeyIcon;
import com.yongaishide.chaosworld.compat.ftbquests.client.DecimalLongConfig;
import dev.architectury.fluid.FluidStack;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AEResourceTask extends Task {
    private static final String KEY_TYPE_TAG = "key_type";
    private static final String RESOURCE_TAG = "resource";
    private static final String COMPONENTS_TAG = "components";
    private static final String AMOUNT_TAG = "amount";
    private static final ResourceLocation FLUX_TYPE_ID = ResourceLocation.fromNamespaceAndPath("appflux", "flux");

    private ResourceLocation keyTypeId;
    private ResourceLocation resourceId;
    private DataComponentPatch components;
    private long amount;

    public AEResourceTask(long id, Quest quest) {
        super(id, quest);
        amount = 1L;
    }

    @Override
    public TaskType getType() {
        return FTBQuestsIntegration.AE_RESOURCE;
    }

    public ResourceLocation getKeyTypeId() {
        return keyTypeId;
    }

    public ResourceLocation getResourceId() {
        return resourceId;
    }

    public void setResource(ResourceLocation keyTypeId, ResourceLocation resourceId) {
        this.keyTypeId = keyTypeId;
        this.resourceId = resourceId;
    }

    public void setAmount(long amount) {
        this.amount = Math.max(1L, amount);
    }

    private AEKeyRef ref() {
        return new AEKeyRef(keyTypeId, resourceId, components);
    }

    @Override
    public long getMaxProgress() {
        return amount;
    }

    @Override
    public String formatMaxProgress() {
        return formatValue(amount);
    }

    @Override
    public String formatProgress(TeamData teamData, long progress) {
        return formatValue(progress);
    }

    private String formatValue(long value) {
        if (isFluxResource()) {
            return StringUtils.formatDouble(value, true) + " FE";
        }
        return isVolumeResource() ? ChemicalTask.getVolumeString(value) : StringUtils.formatDouble(value, true);
    }

    private boolean isVolumeResource() {
        if (resourceId == null || keyTypeId == null) {
            return false;
        }
        return keyTypeId.equals(AEKeyType.fluids().getId()) || keyTypeId.equals(KnownAEKeys.chemicalTypeId());
    }

    private boolean isFluxResource() {
        return FLUX_TYPE_ID.equals(keyTypeId);
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putString(KEY_TYPE_TAG, keyTypeId == null ? "" : keyTypeId.toString());
        nbt.putString(RESOURCE_TAG, resourceId == null ? "" : resourceId.toString());
        if (components != null && !components.isEmpty()) {
            DataComponentPatch.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), components)
                    .result().ifPresent(tag -> nbt.put(COMPONENTS_TAG, tag));
        }
        nbt.putLong(AMOUNT_TAG, amount);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        keyTypeId = parseId(nbt.getString(KEY_TYPE_TAG));
        resourceId = parseId(nbt.getString(RESOURCE_TAG));
        components = null;
        if (nbt.contains(COMPONENTS_TAG, Tag.TAG_COMPOUND)) {
            components = DataComponentPatch.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), nbt.getCompound(COMPONENTS_TAG))
                    .result().orElse(null);
        }
        amount = Math.max(1L, nbt.getLong(AMOUNT_TAG));
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(keyTypeId == null ? "" : keyTypeId.toString());
        buffer.writeUtf(resourceId == null ? "" : resourceId.toString());
        DataComponentPatch.STREAM_CODEC.encode(buffer, components == null ? DataComponentPatch.EMPTY : components);
        buffer.writeVarLong(amount);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        keyTypeId = parseId(buffer.readUtf());
        resourceId = parseId(buffer.readUtf());
        DataComponentPatch patch = DataComponentPatch.STREAM_CODEC.decode(buffer);
        components = patch.isEmpty() ? null : patch;
        amount = Math.max(1L, buffer.readVarLong());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        long initialAmount = amount;
        AEKeyConfig keyConfig = new AEKeyConfig(amount);
        DecimalLongConfig amountConfig = new DecimalLongConfig(1L, Long.MAX_VALUE);
        config.add("resource", keyConfig, ref(), ref -> {
            if (ref == null || ref.keyType() == null || ref.resource() == null) {
                keyTypeId = null;
                resourceId = null;
                components = null;
            } else {
                keyTypeId = ref.keyType();
                resourceId = ref.resource();
                components = ref.components();
            }
            long picked = keyConfig.getAmount();
            if (picked != initialAmount) {
                amount = picked;
                amountConfig.setValue(picked);
            }
        }, null).setNameKey("chaosworld_core.ae_resource.resource");
        config.add("amount", amountConfig, amount, v -> amount = Math.max(1L, v), initialAmount)
                .setNameKey("chaosworld_core.ae_resource.amount");
    }

    @Override
    public MutableComponent getAltTitle() {
        if (isFluxResource()) {
            return Component.literal(formatValue(amount));
        }
        return isVolumeResource()
                ? Component.literal(ChemicalTask.getVolumeString(amount) + " of ").append(resourceName())
                : Component.literal(amount + "x ").append(resourceName());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        AEKey key = KnownAEKeys.resolve(ref());
        return key != null ? new AEKeyIcon(key) : super.getAltIcon();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onButtonClicked(Button button, boolean canClick) {
        AEKey key = KnownAEKeys.resolve(ref());
        boolean ftbHelper = FTBQuests.getRecipeModHelper().isRecipeModAvailable();
        if (key instanceof AEItemKey itemKey) {
            ItemStack stack = itemKey.getReadOnlyStack();
            if (!stack.isEmpty()) {
                if (com.yongaishide.chaosworld.compat.ftbquests.client.JeiRecipes.show(stack)) {
                    return;
                }
                if (ftbHelper) {
                    FTBQuests.getRecipeModHelper().showRecipes(stack);
                    return;
                }
            }
        } else if (key instanceof AEFluidKey fluidKey) {
            if (com.yongaishide.chaosworld.compat.ftbquests.client.JeiRecipes.show(
                    new net.neoforged.neoforge.fluids.FluidStack(fluidKey.getFluid(), 1000))) {
                return;
            }
            if (ftbHelper) {
                FTBQuests.getRecipeModHelper().showRecipes(FluidStack.create(fluidKey.getFluid(), 1000L));
                return;
            }
        } else if (key instanceof UfoMekanismKey chemicalKey
                && com.yongaishide.chaosworld.compat.ftbquests.client.JeiRecipes.show(chemicalKey.getStack())) {
            return;
        } else if (key != null
                && com.yongaishide.chaosworld.compat.ftbquests.client.JeiRecipes.show(key)) {
            return;
        }
        super.onButtonClicked(button, canClick);
    }

    @Override
    public int autoSubmitOnPlayerTick() {
        return 20;
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        if (teamData.isCompleted(this) || !checkTaskSequence(teamData)) {
            return;
        }

        AEKey expected = KnownAEKeys.resolve(ref());
        boolean precise = expected != null && components != null && !components.isEmpty();
        long count = AENetworkScan.count(player, key -> precise ? expected.equals(key) : matches(key), amount);
        if (count > teamData.getProgress(this)) {
            teamData.setProgress(this, Math.min(count, amount));
        }
    }

    private boolean matches(AEKey key) {
        if (keyTypeId != null && !keyTypeId.equals(key.getType().getId())) {
            return false;
        }
        return resourceId == null || resourceId.equals(key.getId());
    }

    private Component resourceName() {
        AEKey key = KnownAEKeys.resolve(ref());
        if (key != null) {
            return key.getDisplayName();
        }
        if (resourceId != null) {
            return Component.translatableWithFallback("key." + resourceId.getNamespace() + "." + resourceId.getPath(),
                    resourceId.toString());
        }
        if (keyTypeId != null) {
            return Component.translatableWithFallback("key_type." + keyTypeId.getNamespace() + "." + keyTypeId.getPath(),
                    keyTypeId.toString());
        }
        return Component.translatable("chaosworld_core.ae_resource.any");
    }

    private static ResourceLocation parseId(String value) {
        return value == null || value.isEmpty() ? null : ResourceLocation.tryParse(value);
    }
}

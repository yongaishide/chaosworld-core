package com.yongaishide.chaosworld.compat.ftbquests;

import appeng.api.stacks.AEKey;
import com.raishxn.ufo.compat.mekanism.UfoMekanismKey;
import com.yongaishide.chaosworld.compat.ftbquests.client.AEKeyIcon;
import com.yongaishide.chaosworld.compat.ftbquests.client.ChemicalConfig;
import com.yongaishide.chaosworld.compat.ftbquests.client.DecimalLongConfig;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ChemicalTask extends Task {
    private static final String RESOURCE_TAG = "chemical";
    private static final String AMOUNT_TAG = "amount";

    private ResourceLocation chemicalId;
    private long amount;

    public ChemicalTask(long id, Quest quest) {
        super(id, quest);
        amount = 1000L;
    }

    @Override
    public TaskType getType() {
        return FTBQuestsIntegration.CHEMICAL;
    }

    public ResourceLocation getChemicalId() {
        return chemicalId;
    }

    public void setChemical(ResourceLocation chemicalId) {
        this.chemicalId = chemicalId;
    }

    public void setAmount(long amount) {
        this.amount = Math.max(1L, amount);
    }

    @Override
    public long getMaxProgress() {
        return amount;
    }

    @Override
    public String formatMaxProgress() {
        return getVolumeString(amount);
    }

    @Override
    public String formatProgress(TeamData teamData, long progress) {
        return getVolumeString(Math.min(progress, Integer.MAX_VALUE));
    }

    public static String getVolumeString(long amount) {
        StringBuilder builder = new StringBuilder();
        if (amount >= 1000L) {
            if (amount % 1000L != 0L) {
                builder.append(StringUtils.formatDouble(amount / 1000.0));
            } else {
                builder.append(amount / 1000L);
            }
            builder.append(" B");
        } else {
            builder.append(amount).append(" mB");
        }
        return builder.toString();
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putString(RESOURCE_TAG, chemicalId == null ? "" : chemicalId.toString());
        nbt.putLong(AMOUNT_TAG, amount);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        chemicalId = parseId(nbt.getString(RESOURCE_TAG));
        amount = Math.max(1L, nbt.getLong(AMOUNT_TAG));
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(chemicalId == null ? "" : chemicalId.toString());
        buffer.writeVarLong(amount);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        chemicalId = parseId(buffer.readUtf());
        amount = Math.max(1L, buffer.readVarLong());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        long initialAmount = amount;
        ChemicalConfig chemicalConfig = new ChemicalConfig(amount);
        DecimalLongConfig amountConfig = new DecimalLongConfig(1L, Long.MAX_VALUE);
        config.add("chemical", chemicalConfig, new AEKeyRef(KnownAEKeys.chemicalTypeId(), chemicalId), ref -> {
            chemicalId = ref == null || ref.resource() == null ? null : ref.resource();
            long picked = chemicalConfig.getAmount();
            if (picked != initialAmount) {
                amount = picked;
                amountConfig.setValue(picked);
            }
        }, null).setNameKey("chaosworld_core.chemical.resource");
        config.add("amount", amountConfig, amount, v -> amount = Math.max(1L, v), initialAmount)
                .setNameKey("chaosworld_core.chemical.amount");
    }

    @Override
    public MutableComponent getAltTitle() {
        return Component.literal(getVolumeString(amount) + " of ").append(chemicalName());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        AEKey key = KnownAEKeys.resolve(new AEKeyRef(KnownAEKeys.chemicalTypeId(), chemicalId));
        return key != null ? new AEKeyIcon(key) : super.getAltIcon();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onButtonClicked(Button button, boolean canClick) {
        AEKey key = KnownAEKeys.resolve(new AEKeyRef(KnownAEKeys.chemicalTypeId(), chemicalId));
        if (key instanceof UfoMekanismKey chemicalKey
                && com.yongaishide.chaosworld.compat.ftbquests.client.JeiRecipes.show(chemicalKey.getStack())) {
            return;
        }
        super.onButtonClicked(button, canClick);
    }

    private Component chemicalName() {
        AEKey key = KnownAEKeys.resolve(new AEKeyRef(KnownAEKeys.chemicalTypeId(), chemicalId));
        if (key != null) {
            return key.getDisplayName();
        }
        if (chemicalId != null) {
            return Component.literal(chemicalId.toString());
        }
        return Component.translatable("chaosworld_core.chemical.any");
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

        long count = AENetworkScan.count(player, key -> {
            if (!KnownAEKeys.chemicalTypeId().equals(key.getType().getId())) {
                return false;
            }
            return chemicalId == null || chemicalId.equals(key.getId());
        }, amount);
        if (count > teamData.getProgress(this)) {
            teamData.setProgress(this, Math.min(count, amount));
        }
    }

    private static ResourceLocation parseId(String value) {
        return value == null || value.isEmpty() ? null : ResourceLocation.tryParse(value);
    }
}

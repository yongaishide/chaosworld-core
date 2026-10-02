package com.yongaishide.chaosworld.compat.ftbquests;

import appeng.api.upgrades.Upgrades;
import appeng.items.tools.powered.WirelessTerminalItem;
import com.yongaishide.chaosworld.ChaosWorld;
import com.yongaishide.chaosworld.compat.ftbquests.client.AEKeyConfig;
import com.yongaishide.chaosworld.compat.ftbquests.client.AEKeySelectScreen;
import com.yongaishide.chaosworld.compat.ftbquests.client.ChemicalConfig;
import com.yongaishide.chaosworld.compat.ftbquests.client.ChemicalSelectScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class FTBQuestsIntegration {
    public static final ResourceLocation AE_RESOURCE_ID = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "ae_resource");
    public static final ResourceLocation CHEMICAL_ID = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "chemical");

    public static DeferredHolder<Item, QuestDetectionCardItem> QUEST_DETECTION_CARD;
    public static TaskType AE_RESOURCE;
    public static TaskType CHEMICAL;

    private FTBQuestsIntegration() {
    }

    public static void init() {
        QUEST_DETECTION_CARD = ChaosWorld.ITEMS.register("quest_detection_card",
                () -> new QuestDetectionCardItem(new Item.Properties()));

        AE_RESOURCE = TaskTypes.register(AE_RESOURCE_ID, AEResourceTask::new,
                () -> Icon.getIcon("ae2:item/wireless_terminal"));

        CHEMICAL = TaskTypes.register(CHEMICAL_ID, ChemicalTask::new,
                () -> Icon.getIcon("mekanism:gui/chemicals.png"));

        if (FMLEnvironment.dist == Dist.CLIENT) {
            setupGuiProviders();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void setupGuiProviders() {
        AE_RESOURCE.setGuiProvider((panel, quest, callback) -> {
            AEKeyConfig config = new AEKeyConfig();
            new AEKeySelectScreen(config, accepted -> {
                panel.run();
                if (accepted) {
                    AEResourceTask task = new AEResourceTask(0L, quest);
                    AEKeyRef ref = config.getValue();
                    if (ref != null) {
                        task.setResource(ref.keyType(), ref.resource());
                    }
                    task.setAmount(config.getAmount());
                    callback.accept(task);
                }
            }).openGui();
        });

        CHEMICAL.setGuiProvider((panel, quest, callback) -> {
            ChemicalConfig config = new ChemicalConfig();
            new ChemicalSelectScreen(config, accepted -> {
                panel.run();
                if (accepted) {
                    ChemicalTask task = new ChemicalTask(0L, quest);
                    AEKeyRef ref = config.getValue();
                    task.setChemical(ref == null ? null : ref.resource());
                    task.setAmount(config.getAmount());
                    callback.accept(task);
                }
            }).openGui();
        });
    }

    public static void setup() {
        Item card = QUEST_DETECTION_CARD.get();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof WirelessTerminalItem) {
                Upgrades.add(card, item, 1);
            }
        }
    }

    public static boolean hasDetectionCard(ItemStack terminalStack) {
        return QUEST_DETECTION_CARD != null
                && terminalStack.getItem() instanceof WirelessTerminalItem terminal
                && terminal.getUpgrades(terminalStack).getInstalledUpgrades(QUEST_DETECTION_CARD.get()) > 0;
    }
}

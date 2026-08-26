package com.yongaishide.chaosworld.item.custom.cell;

import appeng.api.stacks.AEKey;
import com.yongaishide.chaosworld.init.OCDataComponents;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/** 基于AEUniversalCellData的BigInteger版本，无其他逻辑更变，
 *  所有调试信息版本也仍使用AEUniversalCellData统一通知。
 *
 * @author Frostbite
 */
public class AEBigIntegerCellData extends SavedData
{
    public static final String INV_SAVED_TAG = "inventory";
    private static final String ENTRIES_TAG = "entries";
    private static final String ERROR_ENTRIES_TAG = "error_entries";
    private static final String ENTRY_KEY_TAG = "key";
    private static final String ENTRY_AMOUNT_TAG = "amount";
    private static final String SAVED_FOLDER_NAME = "ae_universal_cell_data";
    private final Object2ObjectMap<AEKey, BigInteger> storage;
    private final ObjectArrayList<CompoundTag> pendingReadErrors;

    public AEBigIntegerCellData(@NotNull Object2ObjectMap<AEKey, BigInteger> storage)
    {
        this(storage, new ObjectArrayList<>());
    }

    private AEBigIntegerCellData(@NotNull Object2ObjectMap<AEKey, BigInteger> storage,
                                @NotNull ObjectArrayList<CompoundTag> pendingReadErrors)
    {
        this.storage = storage;
        this.storage.defaultReturnValue(BigInteger.ZERO);
        this.pendingReadErrors = pendingReadErrors;
    }
    public static final SavedData.Factory<AEBigIntegerCellData> FACTORY =
            new SavedData.Factory<>(
                    () -> {
                        Object2ObjectOpenHashMap<AEKey, BigInteger> s = new Object2ObjectOpenHashMap<>();
                        s.defaultReturnValue(BigInteger.ZERO);
                        return new AEBigIntegerCellData(s, new ObjectArrayList<>());
                    },
                    AEBigIntegerCellData::load
            );
    public @NotNull Object2ObjectMap<AEKey, BigInteger> getOriginalStorage()
    {
        return storage;
    }
    public static @Nullable AEBigIntegerCellData getCellDataByUUID(@NotNull UUID uuid)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;

        ensureSaveDirExists(server);

        final String key = makeKey(uuid);
        return server.overworld().getDataStorage().get(FACTORY, key);
    }
    public static @Nullable AEBigIntegerCellData computeIfAbsentCellDataForItemStack(@NotNull ItemStack itemStack)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;

        ensureSaveDirExists(server);

        final var dataStorage = server.overworld().getDataStorage();
        UUID existing = itemStack.get(OCDataComponents.CELL_UUID.get());
        if (existing != null) {
            AEBigIntegerCellData data = getCellDataByUUID(existing);
            if (data != null) {
                return data;
            }
        }
        UUID fresh;
        do {
            fresh = UUID.randomUUID();
        } while (getCellDataByUUID(fresh) != null);

        itemStack.set(OCDataComponents.CELL_UUID.get(), fresh);
        Object2ObjectOpenHashMap<AEKey, BigInteger> s = new Object2ObjectOpenHashMap<>();
        s.defaultReturnValue(BigInteger.ZERO);
        AEBigIntegerCellData newData = new AEBigIntegerCellData(s);
        dataStorage.set(makeKey(fresh), newData);
        return newData;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
    {
        CompoundTag invTag = new CompoundTag();


        ListTag entriesList = new ListTag();
        for (Object2ObjectMap.Entry<AEKey, BigInteger> e : storage.object2ObjectEntrySet())
        {
            AEKey key = e.getKey();
            BigInteger amount = e.getValue();

            if (key == null)
            {
                System.err.println("[AEUniversalCellData] Skip null key during serialization.");
                continue;
            }

            try
            {
                CompoundTag entry = new CompoundTag();
                entry.put(ENTRY_KEY_TAG, key.toTagGeneric(registries));
                entry.putByteArray(ENTRY_AMOUNT_TAG, amount.toByteArray());
                entriesList.add(entry);
            }
            catch(Throwable ex)
            {
                System.err.println("[AEUniversalCellData] Failed to serialize entry: key=" + key
                        + ", amount=" + amount + " ; cause=" + ex);
            }
        }
        invTag.put(ENTRIES_TAG, entriesList);

        ListTag errorList = new ListTag();
        for (CompoundTag bad : pendingReadErrors)
        {
            errorList.add(bad.copy());
        }
        invTag.put(ERROR_ENTRIES_TAG, errorList);

        tag.put(INV_SAVED_TAG, invTag);
        return tag;
    }

    public static AEBigIntegerCellData load(CompoundTag tag, HolderLookup.Provider registries)
    {
        Object2ObjectMap<AEKey, BigInteger> storage = new Object2ObjectOpenHashMap<>();
        storage.defaultReturnValue(BigInteger.ZERO);
        ObjectArrayList<CompoundTag> errorQueue = new ObjectArrayList<>();
        CompoundTag invTag = tag.getCompound(INV_SAVED_TAG);
        ListTag entries = invTag.getList(ENTRIES_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++)
        {
            CompoundTag entry = entries.getCompound(i);
            try
            {
                CompoundTag keyTag = entry.getCompound(ENTRY_KEY_TAG);
                AEKey key = AEKey.fromTagGeneric(registries, keyTag);
                if(key == null)
                {
                    errorQueue.add(entry.copy());
                    System.err.println("[AEUniversalCellData] Failed to deserialize entry (null key). Entry=" + entry);
                    continue;
                }
                BigInteger amount = new BigInteger(entry.getByteArray(ENTRY_AMOUNT_TAG));
                addTo(storage, key, amount);
            }
            catch(Throwable ex)
            {
                errorQueue.add(entry.copy());
                System.err.println("[AEUniversalCellData] Failed to deserialize entry: " + entry + " ; cause=" + ex);
            }
        }
        ListTag oldErrors = invTag.getList(ERROR_ENTRIES_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < oldErrors.size(); i++)
        {
            CompoundTag badEntry = oldErrors.getCompound(i);
            boolean recovered = false;
            try
            {
                CompoundTag keyTag = badEntry.getCompound(ENTRY_KEY_TAG);
                AEKey key = AEKey.fromTagGeneric(registries, keyTag);
                if(key != null)
                {
                    BigInteger amount = new BigInteger(badEntry.getByteArray(ENTRY_AMOUNT_TAG));
                    addTo(storage, key, amount);
                    recovered = true;
                }
            }
            catch(Throwable ignored)
            {
                recovered = false;
            }

            if (recovered)
            {
                System.err.println("[AEUniversalCellData] Recovered previously failed entry: " + badEntry);
            }
            else
            {
                errorQueue.add(badEntry.copy());
            }
        }
        return new AEBigIntegerCellData(storage, errorQueue);
    }

    private static String makeKey(@NotNull UUID uuid)
    {
        return SAVED_FOLDER_NAME + "/" + uuid;
    }

    private static void ensureSaveDirExists(@NotNull MinecraftServer server)
    {
        Path dir = server.getWorldPath(LevelResource.ROOT)
                .resolve("data")
                .resolve(SAVED_FOLDER_NAME);
        try
        {
            Files.createDirectories(dir);
        }
        catch(IOException e)
        {
            System.err.println("[AEUniversalCellData] Failed to create save directory: " + dir + " : " + e);
        }
    }
    private static void addTo(Object2ObjectMap<AEKey, BigInteger> map, AEKey key, BigInteger delta)
    {
        if (delta == null) return;
        if (delta.signum() <= 0) return;
        BigInteger prev = map.getOrDefault(key, BigInteger.ZERO);
        BigInteger now = prev.add(delta);
        if (now.signum() == 0)
        {
            map.remove(key);
        }
        else
        {
            map.put(key, now);
        }
    }
}

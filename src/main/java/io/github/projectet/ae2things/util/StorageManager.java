package io.github.projectet.ae2things.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class StorageManager extends SavedData {

    private static final String DISKUUID = "diskuuid";
    private static final String MANAGER_NAME = "ae2things_disk_manager";
    private static final String DISKDATA = "diskdata";
    private static final String DISKLIST = "disklist";

    public final HolderLookup.Provider provider;
    private final Map<UUID, DataStorage> disks;

    public StorageManager(HolderLookup.Provider provider) {
        Objects.requireNonNull(provider, "Argument cannot be null");

        disks = new HashMap<>();
        this.provider = provider;
    }

    private StorageManager(Map<UUID, DataStorage> disks, HolderLookup.Provider provider) {
        Objects.requireNonNull(provider, "'provider' cannot be null");
        Objects.requireNonNull(disks, "'disks' cannot be null");

        this.disks = disks;
        this.provider = provider;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        ListTag diskList = new ListTag();
        for (Map.Entry<UUID, DataStorage> entry : disks.entrySet()) {
            CompoundTag disk = new CompoundTag();

            disk.putUUID(DISKUUID, entry.getKey());
            disk.put(DISKDATA, entry.getValue().toNbt());
            diskList.add(disk);
        }

        nbt.put(DISKLIST, diskList);
        return nbt;
    }

    public static StorageManager readNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        Map<UUID, DataStorage> disks = new HashMap<>();
        ListTag diskList = nbt.getList(DISKLIST, CompoundTag.TAG_COMPOUND);
        for(int i = 0; i < diskList.size(); i++) {
            CompoundTag disk = diskList.getCompound(i);
            disks.put(disk.getUUID(DISKUUID), DataStorage.fromNbt(disk.getCompound(DISKDATA)));
        }
        return new StorageManager(disks, provider);
    }

    public void updateDisk(UUID uuid, DataStorage dataStorage) {
        disks.put(uuid, dataStorage);
        setDirty();
    }

    public void removeDisk(UUID uuid) {
        disks.remove(uuid);
        setDirty();
    }

    public boolean hasUUID(UUID uuid) {
        return disks.containsKey(uuid);
    }

    public DataStorage getOrCreateDisk(UUID uuid) {
        if(!disks.containsKey(uuid)) {
            updateDisk(uuid, new DataStorage());
        }
        return disks.get(uuid);
    }

    public void modifyDisk(UUID diskID, ListTag stackKeys, long[] stackAmounts, long itemCount) {
        DataStorage diskToModify = getOrCreateDisk(diskID);
        if(stackKeys != null && stackAmounts != null) {
            diskToModify.stackKeys = stackKeys;
            diskToModify.stackAmounts = stackAmounts;
        }
        diskToModify.itemCount = itemCount;

        updateDisk(diskID, diskToModify);
    }

    @Nullable
    public static StorageManager getInstance() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !server.isSameThread()) {
            return null;
        }
        ServerLevel world = server.overworld();
        return world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
                () -> new StorageManager(world.registryAccess()), StorageManager::readNbt),
                MANAGER_NAME);
    }
}

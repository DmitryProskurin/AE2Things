package io.github.projectet.ae2things.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageManager extends SavedData {

    private static final String DISKUUID = "diskuuid";
    private static final String MANAGER_NAME = "disk_manager";
    private static final String DISKDATA = "diskdata";
    private static final String DISKLIST = "disklist";

    public HolderLookup.Provider Provider;
    private final Map<UUID, DataStorage> disks;

    public StorageManager() {
        disks = new HashMap<>();
        this.setDirty();
    }

    private StorageManager(Map<UUID, DataStorage> disks, HolderLookup.Provider provider) {
        this.disks = disks;
        this.Provider = provider;
        this.setDirty();
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
//https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/8d4e1aecc4cc3f1c6ae334c746f684761c843313/src/main/java/appeng/core/AppEngBase.java#L258
    public static StorageManager getInstance(MinecraftServer server) {
        //what if over is not existed yet? Game is started in nether?
        ServerLevel world = server.getLevel(ServerLevel.OVERWORLD);
        if (world == null) { return null; }
        return world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
                StorageManager::new, StorageManager::readNbt),
                MANAGER_NAME);
    }
}

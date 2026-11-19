package io.github.projectet.ae2things.definitions;

import appeng.api.client.StorageCellModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;
import appeng.items.storage.StorageTier;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.item.DISKDrive;
import io.github.projectet.ae2things.item.PortableDISKItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class AE2TItems {
    public static final DeferredRegister.Items DR = DeferredRegister.createItems(AE2Things.MOD_ID);

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();
    private static final List<ItemDefinition<?>> STORAGE_ITEMS = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    @SuppressWarnings("unchecked")
    static final List<Tuple<String, ResourceLocation>> CAPACITY_MODEL_MATCH = List.of(
        new Tuple("_1k",  AE2Things.id("model/drive/cells/disk_1k")),
        new Tuple("_4k",  AE2Things.id("model/drive/cells/disk_4k")),
        new Tuple("_16k", AE2Things.id("model/drive/cells/disk_16k")),
        new Tuple("_64k", AE2Things.id("model/drive/cells/disk_64k")),
        new Tuple("_256k", AE2Things.id("model/drive/cells/disk_256k"))
    );

    private final static Item.Properties ONE_ITEM_PROPERTIES = new Item.Properties()
            .stacksTo(1)
            .fireResistant();

    private final static Item.Properties STACK_ITEM_PROPERTIES = new Item.Properties()
            .stacksTo(64)
            .fireResistant();

    public static final ItemDefinition<Item> DISK_HOUSING = item("AE2T disk housing", "disk_housing", p -> new Item(STACK_ITEM_PROPERTIES));
    public static final ItemDefinition<DISKDrive> DISK_DRIVE_1K = storageItem("Disk Drive 1k", "disk_drive_1k", p->new DISKDrive(AEItems.CELL_COMPONENT_1K, 1, 0.5f, STACK_ITEM_PROPERTIES));
    public static final ItemDefinition<DISKDrive> DISK_DRIVE_4K = storageItem("Disk Drive 4k", "disk_drive_4k", p -> new DISKDrive(AEItems.CELL_COMPONENT_4K, 4, 1.0f, STACK_ITEM_PROPERTIES));
    public static final ItemDefinition<DISKDrive> DISK_DRIVE_16K = storageItem("Disk Drive 16k", "disk_drive_16k", p -> new DISKDrive(AEItems.CELL_COMPONENT_16K, 16, 1.5f, STACK_ITEM_PROPERTIES));
    public static final ItemDefinition<DISKDrive> DISK_DRIVE_64K = storageItem("Disk Drive 64k", "disk_drive_64k", p -> new DISKDrive(AEItems.CELL_COMPONENT_64K, 64, 2.0f, STACK_ITEM_PROPERTIES));
    public static final ItemDefinition<DISKDrive> DISK_DRIVE_256K = storageItem("Disk Drive 256k", "disk_drive_256k", p -> new DISKDrive(AEItems.CELL_COMPONENT_256K, 256, 2.5f, STACK_ITEM_PROPERTIES));

    public static final ItemDefinition<Item> PORTABLE_DISK_1K = storageItem("Portable Disk 1k", "portable_disk_1k", p -> new PortableDISKItem(StorageTier.SIZE_1K, ONE_ITEM_PROPERTIES));
    public static final ItemDefinition<Item> PORTABLE_DISK_4K = storageItem("Portable Disk 4k", "portable_disk_4k", p -> new PortableDISKItem(StorageTier.SIZE_4K, ONE_ITEM_PROPERTIES));
    public static final ItemDefinition<Item> PORTABLE_DISK_16K = storageItem("Portable Disk 16k", "portable_disk_16k", p -> new PortableDISKItem(StorageTier.SIZE_16K, ONE_ITEM_PROPERTIES));
    public static final ItemDefinition<Item> PORTABLE_DISK_64K = storageItem("Portable Disk 64k", "portable_disk_64k", p -> new PortableDISKItem(StorageTier.SIZE_64K, ONE_ITEM_PROPERTIES));
    public static final ItemDefinition<Item> PORTABLE_DISK_256K = storageItem("Portable Disk 256k","portable_disk_256k", p -> new PortableDISKItem(StorageTier.SIZE_256K, ONE_ITEM_PROPERTIES));

    public static void initUpgrades(){
        for(var itemDefinition : Collections.unmodifiableList(STORAGE_ITEMS)) {
            var itemId = itemDefinition.id().getPath();
            Upgrades.add(AEItems.FUZZY_CARD, itemDefinition, 1, "gui.ae2things.upgrade.disk");
            Upgrades.add(AEItems.VOID_CARD, itemDefinition, 1, "gui.ae2things.upgrade.disk");
            Upgrades.add(AEItems.INVERTER_CARD, itemDefinition, 1, "gui.ae2things.upgrade.disk");
            if (itemId.startsWith("portable_disk")) {
                Upgrades.add(AEItems.ENERGY_CARD, itemDefinition, 2, "gui.ae2things.upgrade.portabledisk");
            }

            //register model
            for (var tuple : CAPACITY_MODEL_MATCH) {
                if (itemId.endsWith(tuple.getA())) {
                    StorageCellModels.registerModel(itemDefinition, tuple.getB());
                    break;
                }
            }
        }
    }

    private static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, DR.registerItem(id, factory));
        ITEMS.add(definition);
        return definition;
    }

    private static <T extends Item> ItemDefinition<T> storageItem(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = item(englishName, id, factory);
        STORAGE_ITEMS.add(definition);
        return definition;
    }
}

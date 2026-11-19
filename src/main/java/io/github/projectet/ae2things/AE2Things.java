package io.github.projectet.ae2things;

import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import io.github.projectet.ae2things.definitions.*;
import io.github.projectet.ae2things.recipe.CrystalGrowthRecipeStatic;
import io.github.projectet.ae2things.storage.DISKCellHandler;
import io.github.projectet.ae2things.util.StorageManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@Mod(AE2Things.MOD_ID)
public class AE2Things {

    public static final String MOD_ID = "ae2things";

    public static StorageManager STORAGE_INSTANCE = new StorageManager();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID , path);
    }

    public AE2Things(IEventBus modBus) {
        CrystalGrowthRecipeStatic.init();

        AE2TComponents.DR.register(modBus);
        AE2TRecipeTypes.DR.register(modBus);
        AE2TRecipeSerializer.DR.register(modBus);
        AE2TItems.DR.register(modBus);
        AE2TBlocks.DR.register(modBus);
        AE2TBlockEntities.DR.register(modBus);
        AE2TMenus.DR.register(modBus);
        AE2TCreativeTab.DR.register(modBus);

        modBus.addListener(AE2Things::initUpgrades);
        modBus.addListener(AE2Things::initStorageCells);

        NeoForge.EVENT_BUS.addListener(this::onServerLevelTickStart);
    }

    private static void initUpgrades(FMLCommonSetupEvent event) {
        event.enqueueWork(()-> {
            AE2TItems.initUpgrades();
            Upgrades.add(AEItems.SPEED_CARD, AE2TBlocks.ADVANCED_INSCRIBER, 5);
            Upgrades.add(AEItems.SPEED_CARD, AE2TBlocks.CRYSTAL_GROWTH, 3);
        });
    }

    private static void initStorageCells(FMLCommonSetupEvent event) {
        event.enqueueWork(()->{
            StorageCells.addCellHandler(DISKCellHandler.INSTANCE);
        });
    }

    private void onServerLevelTickStart(LevelTickEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        STORAGE_INSTANCE = StorageManager.getInstance(level.getServer());
    }
}

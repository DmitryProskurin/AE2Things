package io.github.projectet.ae2things.client;

import appeng.init.client.InitScreens;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.gui.advancedInscriber.AdvancedInscriberMenu;
import io.github.projectet.ae2things.gui.advancedInscriber.AdvancedInscriberRootPanel;
import io.github.projectet.ae2things.gui.crystalGrowth.CrystalGrowthMenu;
import io.github.projectet.ae2things.gui.crystalGrowth.CrystalGrowthRootPanel;
import io.github.projectet.ae2things.item.DISKDrive;
import io.github.projectet.ae2things.definitions.AE2TItems;
import io.github.projectet.ae2things.item.PortableDISKItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = AE2Things.MOD_ID, value = Dist.CLIENT)
public class AE2ThingsClient {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(event, AdvancedInscriberMenu.ADVANCED_INSCRIBER_SHT, AdvancedInscriberRootPanel::new, "/screens/advanced_inscriber.json");
        InitScreens.register(event, CrystalGrowthMenu.CRYSTAL_GROWTH_SHT, CrystalGrowthRootPanel::new, "/screens/crystal_growth.json");
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(DISKDrive::getColor, AE2TItems.DISK_DRIVE_1K, AE2TItems.DISK_DRIVE_4K, AE2TItems.DISK_DRIVE_16K, AE2TItems.DISK_DRIVE_64K, AE2TItems.DISK_DRIVE_256K);
        event.register(PortableDISKItem::getColor, AE2TItems.PORTABLE_DISK_1K, AE2TItems.PORTABLE_DISK_4K, AE2TItems.PORTABLE_DISK_16K, AE2TItems.PORTABLE_DISK_64K, AE2TItems.PORTABLE_DISK_256K);
    }
}

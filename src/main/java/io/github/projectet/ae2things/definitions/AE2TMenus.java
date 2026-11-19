package io.github.projectet.ae2things.definitions;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.gui.advancedInscriber.AdvancedInscriberMenu;
import io.github.projectet.ae2things.gui.crystalGrowth.CrystalGrowthMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AE2TMenus {
    public static final DeferredRegister<MenuType<?>> DR =
            DeferredRegister.create(Registries.MENU, AE2Things.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<AdvancedInscriberMenu>> ADVANCED_INSCRIBER_MENU =
            DR.register(AdvancedInscriberMenu.ID.getPath(), () -> AdvancedInscriberMenu.ADVANCED_INSCRIBER_SHT);

    public static final DeferredHolder<MenuType<?>, MenuType<CrystalGrowthMenu>> CRYSTAL_GROWTH_MENU =
            DR.register(CrystalGrowthMenu.ID.getPath(), () -> CrystalGrowthMenu.CRYSTAL_GROWTH_SHT);
}

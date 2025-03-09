package io.github.projectet.ae2things.compat;

import appeng.core.AppEng;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.gui.advancedInscriber.AdvancedInscriberRootPanel;
import io.github.projectet.ae2things.recipe.CrystalGrowthRecipe;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.ButtonArea;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class REI implements REIClientPlugin {

    CategoryIdentifier<?> ID = CategoryIdentifier.of(AppEng.makeId("ae2.inscriber"));
    public static final CategoryIdentifier<CrystalGrowthRecipeDisplay> CRYSTAL_GROWTH = CategoryIdentifier.of(CrystalGrowthRecipe.TYPE_ID);

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(
                new Rectangle(82, 39, 26, 16),
                AdvancedInscriberRootPanel.class,
                ID);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(CrystalGrowthRecipe.class, CrystalGrowthRecipe.TYPE, CrystalGrowthRecipeDisplay::new);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        ItemStack inscriber = new ItemStack(Registry.ITEM.get(AE2Things.id("advanced_inscriber")));
        registry.addWorkstations(ID, EntryStacks.of(inscriber));
        registry.setPlusButtonArea(ID, ButtonArea.defaultArea());

        registry.add(new CrystalGrowthRecipeCategory());
        registry.addWorkstations(CRYSTAL_GROWTH, EntryStacks.of(AE2Things.CRYSTAL_GROWTH));
    }
}

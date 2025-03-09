package io.github.projectet.ae2things.compat;

import appeng.core.AppEng;
import io.github.projectet.ae2things.AE2Things;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CrystalGrowthRecipeCategory implements DisplayCategory<CrystalGrowthRecipeDisplay>  {

    @Override
    public CategoryIdentifier<? extends CrystalGrowthRecipeDisplay> getCategoryIdentifier() {
        return REI.CRYSTAL_GROWTH;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.ae2things.crystal_growth");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AE2Things.CRYSTAL_GROWTH);
    }

    @Override
    public List<Widget> setupDisplay(CrystalGrowthRecipeDisplay display, Rectangle bounds) {
        List<Widget> result = new ArrayList();
        var crystalGrowthUiResource = AppEng.makeId("textures/guis/crystal_growth_rei.png");
        var textureWidget = Widgets.createTexturedWidget(crystalGrowthUiResource, new Rectangle(bounds.getLocation().x, bounds.getLocation().y, 176, 66), 0,0);
        result.add(textureWidget);

        var ingredientEntries = display.getInputEntries();
        var output = display.getOutputEntries();
        for(int row = 0; row < 3; row++) {
            for(int column = 0; column < 4; column++) {
                int x = bounds.x + 9 + (column * 36);
                int y = bounds.y + 7 + (row * 18);
                var shouldDisplayIngredient = false;
                switch (column) {
                    case 0:
                        shouldDisplayIngredient = display.getRecipe().isFromFlawlessOrFlawed();
                        break;
                    case 1:
                        shouldDisplayIngredient = display.getRecipe().isFromChipped();
                        break;
                    case 2:
                        shouldDisplayIngredient = display.getRecipe().isFromDamaged();
                        break;
                    case 3:
                        result.add(Widgets.createSlot(new Point(x,y)).entries(output.get(0)).markOutput().disableBackground());
                        break;
                }
                if (shouldDisplayIngredient) {
                    result.add(Widgets.createSlot(new Point(x,y)).entries(ingredientEntries.get(0)).markInput().disableBackground());
                }
            }
        }
        return result;
    }
}

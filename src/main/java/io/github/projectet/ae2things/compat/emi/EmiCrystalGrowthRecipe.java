package io.github.projectet.ae2things.compat.emi;

import appeng.core.AppEng;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.recipe.CrystalGrowthRecipe;
import io.github.projectet.ae2things.definitions.AE2TBlocks;
import io.github.projectet.ae2things.definitions.AE2TRecipeTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class EmiCrystalGrowthRecipe extends BasicEmiRecipe {

    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategoryWithNameOverride(
            AE2Things.id(AE2TRecipeTypes.CRYSTAL_GROWTH_RECIPE_ID),
            EmiStack.of(AE2TBlocks.CRYSTAL_GROWTH),
            Component.translatable("gui.ae2things.crystal_growth"));

    private final CrystalGrowthRecipe recipe;

    public EmiCrystalGrowthRecipe(RecipeHolder<CrystalGrowthRecipe> holder) {
        super(CATEGORY, holder.id(), 145, 66);

        this.recipe = holder.value();
        inputs.add(EmiIngredient.of(recipe.getIngredient()));
        outputs.add(EmiStack.of(recipe.getResultItem()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var crystalGrowthUiResource = AppEng.makeId("textures/guis/crystal_growth.png");
        widgets.addTexture(crystalGrowthUiResource, 0,0, 145, 66, 19,10);
        var progressTexture = new EmiTexture(crystalGrowthUiResource, 176,0, 6,18);

        widgets.addAnimatedTexture(progressTexture, 133,6, 2000, false, true, false);
        widgets.addAnimatedTexture(progressTexture, 133,24, 2000, false, true, false);
        widgets.addAnimatedTexture(progressTexture, 133,42, 2000, false, true, false);

        for(int row = 0; row < 3; row++) {
            for(int column = 0; column < 4; column++) {
                int x = 6 + (column * 36);
                int y = 6 + (row * 18);
                var shouldDisplayIngredient = false;
                switch (column) {
                    case 0:
                        shouldDisplayIngredient = this.recipe.isFromFlawlessOrFlawed();
                        break;
                    case 1:
                        shouldDisplayIngredient = this.recipe.isFromChipped();
                        break;
                    case 2:
                        shouldDisplayIngredient = this.recipe.isFromDamaged();
                        break;
                    case 3:
                        widgets.addSlot(EmiStack.of(recipe.getResultItem()), x,y).drawBack(false);
                        break;
                }
                if (shouldDisplayIngredient) {
                    widgets.addSlot(EmiIngredient.of(recipe.getIngredient()), x, y).drawBack(false);
                }
            }
        }
    }
}

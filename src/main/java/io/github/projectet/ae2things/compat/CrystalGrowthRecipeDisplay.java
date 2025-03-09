package io.github.projectet.ae2things.compat;

import io.github.projectet.ae2things.recipe.CrystalGrowthRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.List;

public class CrystalGrowthRecipeDisplay extends BasicDisplay {
    private final CrystalGrowthRecipe recipe;
    public CrystalGrowthRecipeDisplay(CrystalGrowthRecipe recipe) {
        super(EntryIngredients.ofIngredients(recipe.getIngredients()), List.of(EntryIngredients.of(recipe.getResultItem())));
        this.recipe = recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() { return REI.CRYSTAL_GROWTH; }

    public CrystalGrowthRecipe getRecipe() { return recipe; }
}

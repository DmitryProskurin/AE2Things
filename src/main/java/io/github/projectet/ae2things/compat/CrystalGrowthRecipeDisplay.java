package io.github.projectet.ae2things.compat;

import io.github.projectet.ae2things.recipe.CrystalGrowthRecipe;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class CrystalGrowthRecipeDisplay extends BasicDisplay {
    private final RecipeHolder<CrystalGrowthRecipe> recipeHolder;
    public CrystalGrowthRecipeDisplay(RecipeHolder<CrystalGrowthRecipe> holder) {
        super(EntryIngredients.ofIngredients(holder.value().getIngredients()), List.of(EntryIngredients.of(holder.value().getResultItem())));
        this.recipeHolder = holder;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() { return REI.CRYSTAL_GROWTH; }

    public CrystalGrowthRecipe getRecipe() { return recipeHolder.value(); }
}

package io.github.projectet.ae2things.recipe;

import java.util.List;

import io.github.projectet.ae2things.definitions.AE2TRecipeSerializer;
import io.github.projectet.ae2things.definitions.AE2TRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;


public class CrystalGrowthRecipe implements Recipe<RecipeInput> {

    public final static RecipeType<CrystalGrowthRecipe> TYPE = AE2TRecipeTypes.CRYSTAL_GROWTH_RECIPE;

    private final ItemStack outputIngredient;
    private final Ingredient inputIngredient;

    public CrystalGrowthRecipe(Ingredient inputIngredient, ItemStack outputIngredient) {
        this.inputIngredient = inputIngredient;
        this.outputIngredient = outputIngredient;
    }

    public static List<RecipeHolder<CrystalGrowthRecipe>> getRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(CrystalGrowthRecipe.TYPE);
    }

    public static CrystalGrowthRecipe getRecipefromStack(Level level, ItemStack item) {
        CrystalGrowthRecipe matchedRecipe = null;
        var recipes = getRecipes(level);
        for (RecipeHolder<CrystalGrowthRecipe> recipeHolder : recipes) {
            for(Ingredient ingredient : recipeHolder.value().getIngredients()) {
                if(ingredient.test(item)) {
                    matchedRecipe = recipeHolder.value();
                    break;
                }
            }
            if(matchedRecipe != null)
                break;
        }
        return matchedRecipe;
    }

    public Ingredient getIngredient() {
        return inputIngredient;
    }

    public ItemStack getResultItem() {
        return outputIngredient;
    }

    public boolean isFromFlawlessOrFlawed(ItemStack testStack) {
        return isFromFlawless(testStack) || isFromFlawed(testStack);
    }

    public boolean isFromFlawless(ItemStack testStack) {
        return testInternal(testStack, CrystalGrowthRecipeStatic.FLAWLESS_BUDDING_STACK.get());
    }

    public boolean isFromFlawed(ItemStack testStack) {
        return testInternal(testStack, CrystalGrowthRecipeStatic.FLAWED_BUDDING_STACK.get());
    }

    public boolean isFromChipped(ItemStack testStack) {
        return testInternal(testStack, CrystalGrowthRecipeStatic.CHIPPED_BUDDING_STACK.get());
    }

    public boolean isFromDamaged(ItemStack testStack) {
        return testInternal(testStack, CrystalGrowthRecipeStatic.DAMAGED_BUDDING_STACK.get());
    }

    public boolean isFromFlawlessOrFlawed() {
        return inputIngredient.test(CrystalGrowthRecipeStatic.FLAWLESS_BUDDING_STACK.get()) || inputIngredient.test(CrystalGrowthRecipeStatic.FLAWED_BUDDING_STACK.get());
    }

    public boolean isFromChipped() {
        return inputIngredient.test(CrystalGrowthRecipeStatic.CHIPPED_BUDDING_STACK.get());
    }

    public boolean isFromDamaged() {
        return inputIngredient.test(CrystalGrowthRecipeStatic.DAMAGED_BUDDING_STACK.get());
    }

    private boolean testInternal(ItemStack testStack, ItemStack requiredStack) {
        return inputIngredient.test(testStack) && inputIngredient.test(requiredStack);
    }

    public Item nextStage(ItemStack item) {
        if(isFromFlawless(item))
            return Items.AIR;
        else if(isFromFlawed(item))
            return CrystalGrowthRecipeStatic.CHIPPED_BUDDING_ITEM.get();
        else if(isFromChipped(item))
            return CrystalGrowthRecipeStatic.DAMAGED_BUDDING_ITEM.get();;
        return Items.AIR;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) { return false; }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) { return outputIngredient.copy(); }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return outputIngredient;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.inputIngredient);
        return ingredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AE2TRecipeSerializer.CRYSTAL_GROWTH_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}

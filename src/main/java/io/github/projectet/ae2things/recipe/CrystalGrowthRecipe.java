package io.github.projectet.ae2things.recipe;

import io.github.projectet.ae2things.AE2Things;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;


public class CrystalGrowthRecipe implements Recipe<Container> {

    public final static ResourceLocation TYPE_ID = AE2Things.id("crystal_growth_chamber");

    public final static RecipeType<CrystalGrowthRecipe> TYPE = RecipeType.register(TYPE_ID.toString());

    private final ResourceLocation id;

    private final ItemStack outputIngredient;
    private final Ingredient inputIngredient;

    public CrystalGrowthRecipe(ResourceLocation id, Ingredient inputIngredient, ItemStack outputIngredient) {
        this.id = id;
        this.inputIngredient = inputIngredient;
        this.outputIngredient = outputIngredient;
    }

    public static Iterable<CrystalGrowthRecipe> getRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(CrystalGrowthRecipe.TYPE);
    }

    public static CrystalGrowthRecipe getRecipefromStack(Level level, ItemStack item) {
        CrystalGrowthRecipe matchedRecipe = null;
        for (CrystalGrowthRecipe recipe : getRecipes(level)) {
            for(Ingredient ingredient : recipe.getIngredients()) {
                if(ingredient.test(item)) {
                    matchedRecipe = recipe;
                    break;
                }
            }
            if(matchedRecipe != null)
                break;
        }
        return matchedRecipe;
    }

    public boolean isFromFlawlessOrFlawed(ItemStack testStack) {
        return isFromFlawless(testStack) || isFromFlawed(testStack);
    }

    public boolean isFromFlawless(ItemStack testStack) {
        return testInternal(testStack, CrystalGrowthRecipeStatic.FLAWLESS_BUDDING_STACK);
    }

    public boolean isFromFlawed(ItemStack testStack) {
        return testInternal(testStack, CrystalGrowthRecipeStatic.FLAWED_BUDDING_STACK);
    }

    public boolean isFromChipped(ItemStack testStack) {
        return testInternal(testStack, CrystalGrowthRecipeStatic.CHIPPED_BUDDING_STACK);
    }

    public boolean isFromDamaged(ItemStack testStack) {
        return testInternal(testStack, CrystalGrowthRecipeStatic.DAMAGED_BUDDING_STACK);
    }

    public boolean isFromFlawlessOrFlawed() {
        return inputIngredient.test(CrystalGrowthRecipeStatic.FLAWLESS_BUDDING_STACK) || inputIngredient.test(CrystalGrowthRecipeStatic.FLAWED_BUDDING_STACK);
    }

    public boolean isFromChipped() {
        return inputIngredient.test(CrystalGrowthRecipeStatic.CHIPPED_BUDDING_STACK);
    }

    public boolean isFromDamaged() {
        return inputIngredient.test(CrystalGrowthRecipeStatic.DAMAGED_BUDDING_STACK);
    }

    private boolean testInternal(ItemStack testStack, ItemStack requiredStack) {
        return inputIngredient.test(testStack) && inputIngredient.test(requiredStack);
    }

    public Item nextStage(ItemStack item) {
        if(isFromFlawless(item))
            return Items.AIR;
        else if(isFromFlawed(item))
            return CrystalGrowthRecipeStatic.CHIPPED_BUDDING_ITEM;
        else if(isFromChipped(item))
            return CrystalGrowthRecipeStatic.DAMAGED_BUDDING_ITEM;;
        return Items.AIR;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container) {
        return outputIngredient.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return outputIngredient;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.inputIngredient);
        return ingredients;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CrystalGrowthRecipeSerializer.INSTANCE;
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

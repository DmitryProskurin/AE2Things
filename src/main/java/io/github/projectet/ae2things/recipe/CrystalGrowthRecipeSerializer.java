package io.github.projectet.ae2things.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public class CrystalGrowthRecipeSerializer implements RecipeSerializer<CrystalGrowthRecipe> {

    public static final CrystalGrowthRecipeSerializer INSTANCE = new CrystalGrowthRecipeSerializer();

    private CrystalGrowthRecipeSerializer() {}

    @Override
    public CrystalGrowthRecipe fromJson(@NotNull ResourceLocation resourceLocation, @NotNull JsonObject jsonObject) {
        ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
        JsonObject ingredients = GsonHelper.getAsJsonObject(jsonObject, "ingredients");
        Ingredient input = Ingredient.fromJson(ingredients);
        return new CrystalGrowthRecipe(resourceLocation, input, output);
    }

    @Override
    public CrystalGrowthRecipe fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        ItemStack output = friendlyByteBuf.readItem();
        Ingredient input = Ingredient.fromNetwork(friendlyByteBuf);
        return new CrystalGrowthRecipe(resourceLocation, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, CrystalGrowthRecipe recipe) {
        friendlyByteBuf.writeItem(recipe.getResultItem());
        recipe.getIngredients().get(0).toNetwork(friendlyByteBuf);
    }
}

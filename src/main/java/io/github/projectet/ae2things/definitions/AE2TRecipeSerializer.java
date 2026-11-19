package io.github.projectet.ae2things.definitions;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.recipe.CrystalGrowthRecipe;
import io.github.projectet.ae2things.recipe.CrystalGrowthRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AE2TRecipeSerializer {

    public static final DeferredRegister<RecipeSerializer<?>> DR =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, AE2Things.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystalGrowthRecipe>> CRYSTAL_GROWTH_RECIPE_SERIALIZER =
            DR.register(AE2TRecipeTypes.CRYSTAL_GROWTH_RECIPE_ID, CrystalGrowthRecipeSerializer::new);
}

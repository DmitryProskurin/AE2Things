package io.github.projectet.ae2things.definitions;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.recipe.CrystalGrowthRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AE2TRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> DR = DeferredRegister
            .create(Registries.RECIPE_TYPE, AE2Things.MOD_ID);

    public static String CRYSTAL_GROWTH_RECIPE_ID = "crystal_growth_chamber";
    public static final RecipeType<CrystalGrowthRecipe> CRYSTAL_GROWTH_RECIPE = register(CRYSTAL_GROWTH_RECIPE_ID);

    private static <T extends Recipe<?>> RecipeType<T> register(String id) {
        RecipeType<T> type = RecipeType.simple(AE2Things.id(id));
        DR.register(id, () -> type);
        return type;
    }
}

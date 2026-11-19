package io.github.projectet.ae2things.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CrystalGrowthRecipeSerializer implements RecipeSerializer<CrystalGrowthRecipe> {
    @Override
    public MapCodec<CrystalGrowthRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CrystalGrowthRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    public static final MapCodec<CrystalGrowthRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            Ingredient.CODEC.fieldOf("ingredients").forGetter(CrystalGrowthRecipe::getIngredient),
                            ItemStack.CODEC.fieldOf("result").forGetter(CrystalGrowthRecipe::getResultItem))
                    .apply(builder, CrystalGrowthRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrystalGrowthRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            CrystalGrowthRecipe::getIngredient,
            ItemStack.STREAM_CODEC,
            CrystalGrowthRecipe::getResultItem,
            CrystalGrowthRecipe::new);
}

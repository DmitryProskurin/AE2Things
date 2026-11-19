package io.github.projectet.ae2things.recipe;

import appeng.core.definitions.AEBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CrystalGrowthRecipeStatic {

    public static Supplier<ItemStack> FLAWLESS_BUDDING_STACK;
    public static Supplier<ItemStack> FLAWED_BUDDING_STACK;
    public static Supplier<ItemStack> CHIPPED_BUDDING_STACK;
    public static Supplier<ItemStack> DAMAGED_BUDDING_STACK;

    public static Supplier<Item> CHIPPED_BUDDING_ITEM;
    public static Supplier<Item> DAMAGED_BUDDING_ITEM;


    public static void init() {
        FLAWLESS_BUDDING_STACK = ()->AEBlocks.FLAWLESS_BUDDING_QUARTZ.stack();
        FLAWED_BUDDING_STACK = ()->AEBlocks.FLAWED_BUDDING_QUARTZ.stack();
        CHIPPED_BUDDING_STACK = ()->AEBlocks.CHIPPED_BUDDING_QUARTZ.stack();
        DAMAGED_BUDDING_STACK = ()->AEBlocks.DAMAGED_BUDDING_QUARTZ.stack();

        CHIPPED_BUDDING_ITEM = ()->AEBlocks.CHIPPED_BUDDING_QUARTZ.asItem();
        DAMAGED_BUDDING_ITEM = ()->AEBlocks.DAMAGED_BUDDING_QUARTZ.asItem();
    }
}

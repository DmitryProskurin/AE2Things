package io.github.projectet.ae2things.recipe;

import appeng.core.definitions.AEBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CrystalGrowthRecipeStatic {

    public static ItemStack FLAWLESS_BUDDING_STACK;
    public static ItemStack FLAWED_BUDDING_STACK;
    public static ItemStack CHIPPED_BUDDING_STACK;
    public static ItemStack DAMAGED_BUDDING_STACK;

    public static Item CHIPPED_BUDDING_ITEM;
    public static Item DAMAGED_BUDDING_ITEM;


    public static void init() {
        FLAWLESS_BUDDING_STACK = AEBlocks.FLAWLESS_BUDDING_QUARTZ.stack();
        FLAWED_BUDDING_STACK = AEBlocks.FLAWED_BUDDING_QUARTZ.stack();
        CHIPPED_BUDDING_STACK = AEBlocks.CHIPPED_BUDDING_QUARTZ.stack();
        DAMAGED_BUDDING_STACK = AEBlocks.DAMAGED_BUDDING_QUARTZ.stack();

        CHIPPED_BUDDING_ITEM = AEBlocks.CHIPPED_BUDDING_QUARTZ.asItem();
        DAMAGED_BUDDING_ITEM = AEBlocks.DAMAGED_BUDDING_QUARTZ.asItem();
    }
}

package io.github.projectet.ae2things;

import appeng.api.IAEAddonEntrypoint;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import io.github.projectet.ae2things.block.BlockAdvancedInscriber;
import io.github.projectet.ae2things.block.BlockCrystalGrowth;
import io.github.projectet.ae2things.block.entity.BEAdvancedInscriber;
import io.github.projectet.ae2things.block.entity.BECrystalGrowth;
import io.github.projectet.ae2things.command.Command;
import io.github.projectet.ae2things.gui.advancedInscriber.AdvancedInscriberMenu;
import io.github.projectet.ae2things.gui.cell.DISKItemCellGuiHandler;
import io.github.projectet.ae2things.gui.crystalGrowth.CrystalGrowthMenu;
import io.github.projectet.ae2things.item.AETItems;
import io.github.projectet.ae2things.recipe.CrystalGrowthRecipe;
import io.github.projectet.ae2things.recipe.CrystalGrowthRecipeSerializer;
import io.github.projectet.ae2things.storage.DISKCellHandler;
import io.github.projectet.ae2things.util.StorageManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;

public class AE2Things implements IAEAddonEntrypoint {

    public static final String MOD_ID = "ae2things";

    public static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder(id("item_group")).icon(() -> new ItemStack(AETItems.DISK_HOUSING)).build();

    public static StorageManager STORAGE_INSTANCE = new StorageManager();

    public static SlotSemantic CG_SEMANTIC = SlotSemantics.register("AE2THINGS_CRYSTAL_GROWTH", false);

    public static final Block ADVANCED_INSCRIBER = new BlockAdvancedInscriber(FabricBlockSettings.of(Material.METAL).destroyTime(4f));
    public static BlockEntityType<BEAdvancedInscriber> ADVANCED_INSCRIBER_BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("advanced_inscriber_be"), FabricBlockEntityTypeBuilder.create(BEAdvancedInscriber::new, ADVANCED_INSCRIBER).build());


    public static final Block CRYSTAL_GROWTH = new BlockCrystalGrowth(FabricBlockSettings.of(Material.METAL).destroyTime(4f));
    public static BlockEntityType<BECrystalGrowth> CRYSTAL_GROWTH_BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("crystal_growth_be"), FabricBlockEntityTypeBuilder.create(BECrystalGrowth::new, CRYSTAL_GROWTH).build());

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID , path);
    }

    private void registerBlockwithItem(String path, Block block) {
        AETItems.item(new AEBaseBlockItem(block, new Item.Properties()), path);
        Registry.register(BuiltInRegistries.BLOCK, id(path), block);
    }

    @Override
    public void onAe2Initialized() {
        Command.init();

        StorageCells.addCellHandler(DISKCellHandler.INSTANCE);
        StorageCells.addCellGuiHandler(new DISKItemCellGuiHandler());



        registerBlockwithItem("advanced_inscriber", ADVANCED_INSCRIBER);

        registerBlockwithItem("crystal_growth", CRYSTAL_GROWTH);

        AETItems.init();

        Upgrades.add(AEItems.SPEED_CARD, ADVANCED_INSCRIBER, 5);
        Upgrades.add(AEItems.SPEED_CARD, CRYSTAL_GROWTH, 3);

        ServerTickEvents.START_WORLD_TICK.register((world ->
            STORAGE_INSTANCE = StorageManager.getInstance(world.getServer())));

        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("advanced_inscriber"),AdvancedInscriberMenu.ADVANCED_INSCRIBER_SHT);
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("crystal_growth"),CrystalGrowthMenu.CRYSTAL_GROWTH_SHT);

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CrystalGrowthRecipe.TYPE_ID, CrystalGrowthRecipeSerializer.INSTANCE);
    }
}

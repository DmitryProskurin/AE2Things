package io.github.projectet.ae2things.definitions;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.block.BlockAdvancedInscriber;
import io.github.projectet.ae2things.block.BlockCrystalGrowth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class AE2TBlocks {

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    private final static BlockBehaviour.Properties COMMON_BLOCK_PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .sound(SoundType.METAL)
            .destroyTime(4f)
            .requiresCorrectToolForDrops();

    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(AE2Things.MOD_ID);

    public static final BlockDefinition<BlockAdvancedInscriber> ADVANCED_INSCRIBER =
        block(
                "Advanced inscriber",
                AE2TBlockIds.ADVANCED_INSCRIBER,
                () -> new BlockAdvancedInscriber(COMMON_BLOCK_PROPERTIES),
                null );

    public static final BlockDefinition<BlockCrystalGrowth> CRYSTAL_GROWTH =
            block("Crystal growth", AE2TBlockIds.CRYSTAL_GROWTH, () -> new BlockCrystalGrowth(COMMON_BLOCK_PROPERTIES), null);

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            ResourceLocation id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {

        // Create block and matching item
        var deferredBlock = DR.register(id.getPath(), blockSupplier);
        var deferredItem = AE2TItems.DR.register(id.getPath(), () -> {
            var block = deferredBlock.get();
            var itemProperties = new Item.Properties();
            if (itemFactory != null) {
                var item = itemFactory.apply(block, itemProperties);
                if (item == null) {
                    throw new IllegalArgumentException("BlockItem factory for " + id + " returned null");
                }
                return item;
            } else if (block instanceof AEBaseBlock) {
                return new AEBaseBlockItem(block, itemProperties);
            } else {
                return new BlockItem(block, itemProperties);
            }
        });

        var itemDef = new ItemDefinition<>(englishName, deferredItem);
        BlockDefinition<T> definition = new BlockDefinition<>(englishName, deferredBlock, itemDef);

        BLOCKS.add(definition);

        return definition;

    }
}

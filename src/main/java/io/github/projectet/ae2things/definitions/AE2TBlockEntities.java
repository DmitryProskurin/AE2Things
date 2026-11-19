package io.github.projectet.ae2things.definitions;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.BlockDefinition;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.block.entity.BEAdvancedInscriber;
import io.github.projectet.ae2things.block.entity.BECrystalGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AE2TBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> DR = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, AE2Things.MOD_ID);

    public static final Supplier<BlockEntityType<BEAdvancedInscriber>> ADVANCED_INSCRIBER_BE =
            create("advanced_inscriber",
                    BEAdvancedInscriber.class,
                    BEAdvancedInscriber::new,
                    AE2TBlocks.ADVANCED_INSCRIBER);

    public static final Supplier<BlockEntityType<BECrystalGrowth>> CRYSTAL_GROWTH_BE =
            create("crystal_growth_be",
                    BECrystalGrowth.class,
                    BECrystalGrowth::new,
                    AE2TBlocks.CRYSTAL_GROWTH);

    @SuppressWarnings("unchecked")
    @SafeVarargs
    private static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(String shortId,
                                                                                   Class<T> entityClass,
                                                                                   BlockEntityFactory<T> factory,
                                                                                   BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefinitions) {
        if (blockDefinitions.length == 0) {
            throw new IllegalArgumentException();
        }

        return DR.register(shortId, () -> {
            var blocks = Arrays.stream(blockDefinitions).map(BlockDefinition::block).toArray(AEBaseEntityBlock[]::new);

            var typeHolder = new AtomicReference<BlockEntityType<T>>();
            var type = BlockEntityType.Builder.of((pos, state) -> factory.create(typeHolder.get(), pos, state), blocks)
                    .build(null);
            typeHolder.set(type);

            AEBaseBlockEntity.registerBlockEntityItem(type, blockDefinitions[0].asItem());

            for (var block : blocks) {
                block.setBlockEntity(entityClass, type, null, null);
            }

            return type;
        });
    }

    @FunctionalInterface
    interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }
}

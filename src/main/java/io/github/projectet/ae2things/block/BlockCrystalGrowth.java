package io.github.projectet.ae2things.block;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import io.github.projectet.ae2things.block.entity.BECrystalGrowth;
import io.github.projectet.ae2things.gui.crystalGrowth.CrystalGrowthMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockCrystalGrowth extends AEBaseEntityBlock<BECrystalGrowth> {

    public BlockCrystalGrowth(Properties settings) {
        super(settings);
        settings.requiresCorrectToolForDrops();
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, BECrystalGrowth be) {
        return currentState.setValue(WORKING, be.isWorking());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WORKING);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                           BlockHitResult hitResult) {
        if (!InteractionUtil.isInAlternateUseMode(player)) {
            final BECrystalGrowth cg = (BECrystalGrowth) level.getBlockEntity(pos);
            if (cg != null) {
                if (!level.isClientSide()) {
                    hitResult.getDirection();
                    MenuOpener.open(CrystalGrowthMenu.CRYSTAL_GROWTH_SHT, player,
                            MenuLocators.forBlockEntity(cg));
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }
}

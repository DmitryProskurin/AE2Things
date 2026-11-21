package io.github.projectet.ae2things.item;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.hooks.AEToolItem;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;
import appeng.util.InteractionUtil;
import io.github.projectet.ae2things.definitions.AE2TItems;
import io.github.projectet.ae2things.storage.IDISKCellItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static appeng.api.storage.StorageCells.getCellInventory;

public class DISKDrive extends Item implements IDISKCellItem, AEToolItem {

    private final int bytes;
    private final double idleDrain;
    private final ItemLike coreItem;

    public DISKDrive(ItemLike coreItem, int kilobytes, double idleDrain, Properties props) {
        super(props);
        this.bytes = kilobytes * 1000;
        this.coreItem = coreItem;
        this.idleDrain = idleDrain;
    }

    @Override
    public AEKeyType getKeyType() {
        return AEKeyType.items();
    }

    @Override
    public int getBytes(ItemStack cellItem) {
        return bytes;
    }

    @Override
    public double getIdleDrain() {
        return idleDrain;
    }

    @Override
    public boolean isEditable(ItemStack is) {
        return true;
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        //type number restriction inside
        return CellConfig.create(Set.of(getKeyType()), is);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        return is.getOrDefault(AEComponents.STORAGE_CELL_FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
        is.set(AEComponents.STORAGE_CELL_FUZZY_MODE, fzMode);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        this.disassembleDrive(player.getItemInHand(hand), level, player);

        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()),
                player.getItemInHand(hand));
    }

    @Nullable
    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 2);
    }

    private boolean disassembleDrive(final ItemStack stack, final Level level, final Player player) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            if (level.isClientSide()) {
                return false;
            }

            final Inventory playerInventory = player.getInventory();
            var inv = getCellInventory(stack, null);
            if (inv != null && playerInventory.getSelected() == stack) {
                var list = inv.getAvailableStacks();
                if (list.isEmpty()) {
                    playerInventory.setItem(playerInventory.selected, ItemStack.EMPTY);

                    // drop core
                    playerInventory.placeItemBackInInventory(new ItemStack(coreItem));

                    // drop upgrades
                    for (ItemStack upgrade : this.getUpgrades(stack)) {
                        playerInventory.placeItemBackInInventory(upgrade);
                    }

                    // drop empty storage cell case
                    playerInventory.placeItemBackInInventory(new ItemStack(AE2TItems.DISK_HOUSING));

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return this.disassembleDrive(stack, context.getLevel(), context.getPlayer())
                ? InteractionResult.sidedSuccess(context.getLevel().isClientSide())
                : InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("gui.ae2things.disk_abbr").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        addCellInformationToTooltip(stack, tooltipComponents);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return buildCellTooltipImage(stack, this.getUpgrades(stack));
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            return IDISKCellItem.getLedColorByStoredItems(stack);
        } else {
            // White
            return 0xFFFFFFFF;
        }
    }
}

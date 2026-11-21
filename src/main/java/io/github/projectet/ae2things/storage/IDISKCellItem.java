package io.github.projectet.ae2things.storage;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.me.cells.BasicCellHandler;
import appeng.util.ConfigInventory;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.projectet.ae2things.definitions.AE2TComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public interface IDISKCellItem extends ICellWorkbenchItem {

    AEKeyType getKeyType();

    /**
     * The number of bytes that can be stored on this type of storage cell.
     * <p/>
     * It wont work if the return is not a multiple of 8. The limit is ({@link Integer#MAX_VALUE} + 1) / 8.
     *
     * @param cellItem item
     * @return number of bytes
     */
    int getBytes(ItemStack cellItem);

    /**
     * Allows you to fine tune which items are allowed on a given cell, if you don't care, just return false; As the
     * handler for this type of cell is still the default cells, the normal AE black list is also applied.
     *
     * @param cellItem          item
     * @param requestedAddition requested addition
     * @return true to preventAdditionOfItem
     */
    default boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        if(((AEItemKey) requestedAddition).getItem() instanceof IBasicCellItem) {
            return BasicCellHandler.INSTANCE.getCellInventory(((AEItemKey) requestedAddition).toStack(), null).getUsedBytes() > 0;
        }
        return false;
    }

    /**
     * Allows you to specify if this storage cell can be stored inside other storage cells, only set this for special
     * items like the matter cannon that are not general purpose storage.
     *
     * @return true if the storage cell can be stored inside other storage cells, this is generally false, except for
     *         certain situations such as the matter cannon.
     */
    default boolean storableInStorageCell() {
        return false;
    }

    /**
     * Allows an item to selectively enable or disable its status as a storage cell.
     *
     * @param i item
     * @return if the ItemStack should currently be usable as a storage cell.
     */
    default boolean isStorageCell(ItemStack i) {
        return true;
    }

    /**
     * @return drain in ae/t this storage cell will use.
     */
    double getIdleDrain();

    ConfigInventory getConfigInventory(ItemStack is);
    /**
     * Convenient helper to append useful tooltip information.
     */
    default void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        Preconditions.checkArgument(is.getItem() == this);
        DISKCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
    }

    default Optional<TooltipComponent> buildCellTooltipImage(ItemStack stack, IUpgradeInventory upgrades) {
        List<GenericStack> rawStacks = stack.getOrDefault(AE2TComponents.TOOLTIP_STACKS, List.of());
        boolean hasMoreContent = rawStacks.size() > 5;
        List<GenericStack> stacks = hasMoreContent ? rawStacks.subList(0, 5) : rawStacks;


        List<ItemStack> upgradeStacks = new ArrayList<>();
        for (ItemStack upgrade : upgrades) {
            if (!upgrade.isEmpty()) {
                upgradeStacks.add(upgrade);
            }
        }

        if (stacks.isEmpty() && upgradeStacks.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new StorageCellTooltipComponent(upgradeStacks, stacks, hasMoreContent, true));
    }

    static int getLedColorByStoredItems(ItemStack stack) {
        CellState cellState = CellState.EMPTY;
        var cellInfo = stack.getOrDefault(AE2TComponents.DISK_CELL_INFO, DiskCellInfo.DEFAULT);
        if (cellInfo != DiskCellInfo.DEFAULT && stack.getItem() instanceof IDISKCellItem cellItem) {
            if (cellInfo.storedItemsCount() > 0 && cellInfo.storedItemsCount() != cellItem.getBytes(stack)) {
                cellState = CellState.NOT_EMPTY;
            } else {
                cellState = CellState.FULL;
            }
        }

        return FastColor.ARGB32.opaque(cellState.getStateColor());
    }
}

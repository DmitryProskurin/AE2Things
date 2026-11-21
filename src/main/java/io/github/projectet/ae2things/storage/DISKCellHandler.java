package io.github.projectet.ae2things.storage;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.localization.Tooltips;
import io.github.projectet.ae2things.definitions.AE2TComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DISKCellHandler implements ICellHandler {

    public static final DISKCellHandler INSTANCE = new DISKCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return is.getItem() instanceof IDISKCellItem;
    }

    @Override
    public DISKCellInventory getCellInventory(ItemStack is, ISaveProvider container) {
        return DISKCellInventory.createInventory(is, container);
    }
    //AE2 Things shows the tooltip based on stack data components.
    public void addCellInformationToTooltip(ItemStack stack, List<Component> lines) {
        var cellInfo = stack.getOrDefault(AE2TComponents.DISK_CELL_INFO, DiskCellInfo.DEFAULT);
        if (cellInfo == DiskCellInfo.DEFAULT) { return; }

        lines.add(Component.literal("Disk UUID: ").withStyle(ChatFormatting.GRAY).append(Component.literal(cellInfo.uuid()).withStyle(ChatFormatting.AQUA)));
        if (stack.getItem() instanceof IDISKCellItem cellItem) {
            lines.add(Tooltips.bytesUsed(cellInfo.storedItemsCount(), cellItem.getBytes(stack)));
        }
    }
}

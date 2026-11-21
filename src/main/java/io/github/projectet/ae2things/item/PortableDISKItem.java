package io.github.projectet.ae2things.item;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.hooks.AEToolItem;
import appeng.items.contents.CellConfig;
import appeng.items.contents.PortableCellMenuHost;
import appeng.items.tools.powered.AbstractPortableCell;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.me.common.MEStorageMenu;
import appeng.util.ConfigInventory;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.definitions.AE2TComponents;
import io.github.projectet.ae2things.definitions.AE2TItems;
import io.github.projectet.ae2things.storage.DiskCellInfo;
import io.github.projectet.ae2things.storage.IDISKCellItem;
import io.github.projectet.ae2things.util.StorageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class PortableDISKItem extends AbstractPortableCell implements IDISKCellItem, AEToolItem {

    private final int bytes;
    private final ItemLike coreItem;
    private final AEKeyType keyType = AEKeyType.items();

    public PortableDISKItem(ItemLike coreItem, int kilobytes, Properties props) {
        super(MEStorageMenu.PORTABLE_ITEM_CELL_TYPE, props, 0xFF80caff);
        this.bytes = kilobytes * 1000;
        this.coreItem = coreItem;
    }

    public ResourceLocation getRecipeId() {
        return AE2Things.id("portable/" + Objects.requireNonNull(getRegistryName()).getPath());
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return 80d + 80d * getUpgrades(stack).getInstalledUpgrades(AEItems.ENERGY_CARD);
    }

    @Override
    public @Nullable PortableCellMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        //return new PortableDISKMenuHost(player, inventorySlot, this, stack, (p, sm) -> openFromInventory(p, inventorySlot));
        return new PortableCellMenuHost<>(this, player, locator,
                (p, sm) -> openFromInventory(p, locator, true));
    }

    @Override
    public AEKeyType getKeyType() {
        return AEKeyType.items();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines,
                                TooltipFlag advancedTooltips) {
        addCellInformationToTooltip(stack, lines);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return buildCellTooltipImage(stack, this.getUpgrades(stack));
    }

    @Override
    public int getBytes(ItemStack cellItem) { return this.bytes; }

    @Override
    public double getIdleDrain() {
        return 0.5;
    }

    @Override
    public boolean isEditable(ItemStack is) {
        return true;
    }

    @Override
    public void onUpgradesChanged(ItemStack stack, IUpgradeInventory upgrades) {
        var energyCards = upgrades.getInstalledUpgrades(AEItems.ENERGY_CARD);
        // The energy card is crafted with a dense energy cell, while the portable cell just uses a normal energy cell
        // Since the dense cells capacity is 8x the normal capacity, the result should be 9x normal.
        setAEMaxPowerMultiplier(stack, 1 + energyCards * 8);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 2, this::onUpgradesChanged);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(Set.of(keyType), is);
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown() && disassembleDrive(player.getItemInHand(hand), level, player)) {
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isShiftKeyDown() && disassembleDrive(stack, context.getLevel(), player)) {
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }
        return super.onItemUseFirst(stack, context);
    }

    private boolean disassembleDrive(ItemStack stack, Level level, Player player) {
        if (level.isClientSide()) {
            var info = stack.getOrDefault(AE2TComponents.DISK_CELL_INFO, DiskCellInfo.DEFAULT);
            if (info != DiskCellInfo.DEFAULT && info.storedItemsCount() > 0) {
                return false;
            }
            return true;
        }

        var cellInventory = StorageCells.getCellInventory(stack, null);
        if (cellInventory == null || !cellInventory.getAvailableStacks().isEmpty()) {
            return false;
        }

        var info = stack.getOrDefault(AE2TComponents.DISK_CELL_INFO, DiskCellInfo.DEFAULT);
        if (!info.isDefault()) {
            var manager = StorageManager.getInstance();
            if (manager != null) {
                manager.removeDisk(info.uuidAsUUID());
            }
        }

        Inventory playerInventory = player.getInventory();
        if (playerInventory.getSelected() != stack) {
            return false;
        }

        playerInventory.setItem(playerInventory.selected, ItemStack.EMPTY);

        playerInventory.placeItemBackInInventory(new ItemStack(this.coreItem));
        playerInventory.placeItemBackInInventory(new ItemStack(AEBlocks.ME_CHEST));
        playerInventory.placeItemBackInInventory(new ItemStack(AEBlocks.ENERGY_CELL));
        playerInventory.placeItemBackInInventory(new ItemStack(AE2TItems.DISK_HOUSING));
        for (ItemStack upgrade : this.getUpgrades(stack)) {
            playerInventory.placeItemBackInInventory(upgrade);
        }

        return true;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1 && stack.getItem() instanceof AbstractPortableCell portableCell) {
            // If the cell is out of power, always display empty
            if (portableCell.getAECurrentPower(stack) <= 0) {
                return CellState.ABSENT.getStateColor();
            }

            return IDISKCellItem.getLedColorByStoredItems(stack);
        } else if (tintIndex == 2 && stack.getItem() instanceof AbstractPortableCell portableCell) {
            return portableCell.getColor(stack);
        } else {
            // White
            return 0xFFFFFFFF;
        }
    }
}

package io.github.projectet.ae2things.item;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;
import appeng.hooks.AEToolItem;
import appeng.items.contents.CellConfig;
import appeng.items.contents.PortableCellMenuHost;
import appeng.items.materials.StorageComponentItem;
import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.AbstractPortableCell;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.me.common.MEStorageMenu;
import appeng.util.ConfigInventory;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.storage.IDISKCellItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PortableDISKItem extends AbstractPortableCell implements IDISKCellItem, AEToolItem {

    private final StorageTier tier;
    private final AEKeyType keyType = AEKeyType.items();

//    private final MenuType<?> menuType = MEStorageMenu.PORTABLE_ITEM_CELL_TYPE;

//    public PortableDISKItem(StorageComponentItem item, MenuType<?> menuType, Properties props, int defaultColor) {
//        super(menuType, props, defaultColor);
//        this.item = item;
//    }

    public PortableDISKItem(StorageTier tier, Properties props) {
        super(MEStorageMenu.PORTABLE_ITEM_CELL_TYPE, props, 0xFF80caff);
        this.tier = tier;
    }

    public ResourceLocation getRecipeId() {
        return AE2Things.id("portable/" + Objects.requireNonNull(getRegistryName()).getPath());
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return 80d + 80d * getUpgrades(stack).getInstalledUpgrades(AEItems.ENERGY_CARD);
    }

//    @Override
//    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
//        return context.isSecondaryUseActive()
//                && this.disassembleDrive(stack, context.getLevel(), context.getPlayer())
//                ? InteractionResult.sidedSuccess(context.getLevel().isClientSide())
//                : InteractionResult.PASS;
//    }

    /*@Nullable
    @Override
    public PortableDISKMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        return new PortableDISKMenuHost(player, inventorySlot, this, stack, (p, sm) -> openFromInventory(p, inventorySlot));
    }*/

    @Override
    public @Nullable PortableCellMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        //return new PortableDISKMenuHost(player, inventorySlot, this, stack, (p, sm) -> openFromInventory(p, inventorySlot));
        return new PortableCellMenuHost<>(this, player, locator,
                (p, sm) -> openFromInventory(p, locator, true));
    }

    /*private boolean disassembleDrive(ItemStack stack, Level level, Player player) {
        if (!AEConfig.instance().isPortableCellDisassemblyEnabled()) {
            return false;
        }

        // We refund the crafting recipe ingredients (the first one each)
        var recipeHolder = level.getRecipeManager().byKey(getRecipeId()).orElse(null);
        if (recipeHolder == null) {
            AELog.debug("Cannot disassemble portable cell because it's crafting recipe is null: %s",
                    getRecipeId());
            return false;
        }
        if (!(recipeHolder.value() instanceof CraftingRecipe craftingRecipe)) {
            AELog.debug("Cannot disassemble portable cell because it's crafting recipe doesn't exist: %s",
                    getRecipeId());
            return false;
        }

        if (level.isClientSide()) {
            return true;
        }

        var playerInventory = player.getInventory();
        if (playerInventory.getSelected() != stack) {
            return false;
        }

        var inv = DISKCellHandler.INSTANCE.getCellInventory(stack, null);
        if (inv == null) {
            return false;
        }

        if (inv.getAvailableStacks().isEmpty()) {
            playerInventory.setItem(playerInventory.selected, ItemStack.EMPTY);

            var remainingEnergy = getAECurrentPower(stack);
            for (var ingredient : craftingRecipe.getIngredients()) {
                var ingredientStack = ingredient.getItems()[0].copy();

                // Dump remaining energy into whatever can accept it
                if (remainingEnergy > 0 && ingredientStack.getItem() instanceof EnergyCellBlockItem chargeable) {
                    remainingEnergy = chargeable.injectAEPower(ingredientStack, remainingEnergy, Actionable.MODULATE);
                }

                playerInventory.placeItemBackInInventory(ingredientStack);
            }

            // Drop upgrades
            for (var upgrade : getUpgrades(stack)) {
                playerInventory.placeItemBackInInventory(upgrade);
            }
        } else {
            player.sendSystemMessage(PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text());
        }

        return true;
    }*/

    @Override
    public AEKeyType getKeyType() {
        return AEKeyType.items();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines,
                                TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, context, lines, advancedTooltips);
        addCellInformationToTooltip(stack, lines);
    }

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
//        if (!InteractionUtil.isInAlternateUseMode(player)
//                || !disassembleDrive(player.getItemInHand(hand), level, player)) {
//            if (!level.isClientSide()) {
//                MenuOpener.open(getMenuType(), player, MenuLocators.forHand(player, hand));
//            }
//        }
//        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()),
//                player.getItemInHand(hand));
//    }

    @Override
    public int getBytes(ItemStack cellItem) {
        return this.tier.bytes() / 2;
    }

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

    //overrideStackedOnOther and insert from base class instead of own

//    public MenuType<?> getMenuType() {
//        return menuType;
//    }

    public static int getColor(ItemStack stack, int tintIndex) {
        int color = AbstractPortableCell.getColor(stack, tintIndex);
        return FastColor.ARGB32.opaque(color);
    }
}

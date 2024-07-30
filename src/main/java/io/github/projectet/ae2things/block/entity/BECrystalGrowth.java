package io.github.projectet.ae2things.block.entity;

import appeng.api.config.*;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkPowerBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.item.AETItems;
import io.github.projectet.ae2things.recipe.CrystalGrowthRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BECrystalGrowth extends AENetworkPowerBlockEntity implements IGridTickable, IUpgradeableObject {
    private final InternalInventory extInventory;

    private final AppEngInternalInventory topRow = new AppEngInternalInventory(this, 4, 64);
    private final AppEngInternalInventory midRow = new AppEngInternalInventory(this, 4, 64);
    private final AppEngInternalInventory botRow = new AppEngInternalInventory(this, 4, 64);

    private final Random r = new Random();

    private final CombinedInternalInventory combInv = new CombinedInternalInventory(topRow, midRow, botRow);

    private final Map<InternalInventory, Integer> progress = new IdentityHashMap<>(Map.of(topRow, 0, midRow, 0, botRow, 0));
    private final Map<InternalInventory, CrystalGrowthRecipe> cachedRecipes = new IdentityHashMap<>();

    private final Set<InternalInventory> toRemove = new HashSet<>();

    private boolean hasInitialised = false;

    private IUpgradeInventory upgrades;

    private boolean isWorking;

    public BECrystalGrowth(BlockPos pos, BlockState state) {
        super(AE2Things.CRYSTAL_GROWTH_BE, pos, state);
        upgrades = UpgradeInventories.forMachine(AE2Things.CRYSTAL_GROWTH, 3, this::saveChanges);
        var filter = new FilteredInventory();
        this.extInventory = new FilteredInternalInventory(combInv, filter);

        this.getMainNode()
                .setExposedOnSides(EnumSet.allOf(Direction.class))
                .setIdlePowerUsage(0)
                .addService(IGridTickable.class, this);
        this.setInternalMaxPower(1600);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 20, this.combInv.isEmpty() || !this.hasWork(), true);
    }

    public int getTopProg() {
        return progress.get(topRow);
    }

    public int getMidProg() {
        return progress.get(midRow);
    }

    public int getBotProg() {
        return progress.get(botRow);
    }

    public boolean isWorking() {
        return isWorking;
    }

    public boolean hasWork() {
        if(!hasInitialised) {
            initCache();
            hasInitialised = true;
        }
        return !cachedRecipes.isEmpty();
    }
    private int howManyFull() {
        int full = 0;
        for (InternalInventory inv: progress.keySet()) {
            if(inv.getStackInSlot(3).getCount() == 64) {
                full++;
            }
        }
        return full;
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (isWorking()) {
            getMainNode().ifPresent(iGrid -> {
                int speedFactor = 1 + this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD);
                IEnergyService eg = iGrid.getEnergyService();
                IEnergySource src = this;
                final int powerConsumption = 10 * speedFactor * (cachedRecipes.size() - howManyFull());
                final double powerThreshold = powerConsumption - 0.01;
                double powerReq = this.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                if (powerReq <= powerThreshold) {
                    src = eg;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                }
                if(powerReq > powerThreshold) {
                    if(!isWorking()) {
                        isWorking = true;
                        markForUpdate();
                    }
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    cachedRecipes.forEach((inventory, recipe) -> {
                        if(recipe != null) {
                            int d = progress.get(inventory);
                            if(d >= 100) {
                                ItemStack resultItem = recipe.getResultItem(RegistryAccess.EMPTY);
                                if(inventory.insertItem(3, resultItem, true).getCount() != 0)
                                    return;
                                int i = 2;
                                while(inventory.getStackInSlot(i).getItem() == Items.AIR && i >= 1) {
                                    i--;
                                }
                                ItemStack stack = inventory.getStackInSlot(i);
                                if(stack.getItem() != Items.AIR) {
                                    Item item = recipe.nextStage(stack);
                                    if(r.nextInt(12) == 0 && !recipe.isFlawless(stack)) {
                                        inventory.getStackInSlot(i).shrink(1);
                                        if(item != Items.AIR && i != 2) {
                                            inventory.setItemDirect(i + 1, new ItemStack(item));
                                        }
                                        else
                                            inventory.getStackInSlot(i + 1).grow(1);
                                    }
                                }
                                inventory.insertItem(3, resultItem, false);
                                progress.put(inventory, 0);
                                saveChanges();
                            }
                            else {
                                if(inventory.getStackInSlot(3).getCount() != 64)
                                    progress.put(inventory, d + speedFactor);
                            }
                        }
                    });
                }
            });
        }
        clearEmptyCache();
        matchWork();
        return hasWork() ? TickRateModulation.URGENT : TickRateModulation.SLEEP;
    }

    @Override
    public void onReady() {
        this.getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
        super.onReady();
    }

    private void clearEmptyCache() {
        for (InternalInventory inv: toRemove) {
            cachedRecipes.remove(inv);
            progress.put(inv, 0);
        }
        toRemove.clear();
    }

    private void initCache() {
        for (InternalInventory inv: progress.keySet()) {
            if(hasIngredients(inv)) {
                for (ItemStack stack: inv) {
                    if(stack.isEmpty())
                        continue;
                    cachedRecipes.put(inv, CrystalGrowthRecipe.getRecipefromStack(getLevel(), stack));
                    break;
                }
            }
        }
    }

    @Override
    protected boolean readFromStream(FriendlyByteBuf data) {
        var c = super.readFromStream(data);

        var oldWorking = isWorking();
        var newWorking = data.readBoolean();

        if (oldWorking != newWorking && newWorking) {
            isWorking = true;
        }

        for (int i = 0; i < this.combInv.size(); i++) {
            this.combInv.setItemDirect(i, data.readItem());
        }

        return c;
    }

    @Override
    protected void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);

        data.writeBoolean(isWorking());

        for (int i = 0; i < this.combInv.size(); i++) {
            data.writeItem(combInv.getStackInSlot(i));
        }
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.STORAGE)) {
            return this.getInternalInventory();
        } else if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        }

        return super.getSubInventory(id);
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.upgrades.writeToNBT(data, "upgrades");
        data.putBoolean("working", isWorking);
        data.putIntArray("progress", new int[]{getTopProg(), getMidProg(), getBotProg()});
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.upgrades.readFromNBT(data, "upgrades");
        this.isWorking = data.getBoolean("working");
        int[] savedProgress = data.getIntArray("progress");
        this.progress.put(topRow, savedProgress[0]);
        this.progress.put(midRow, savedProgress[1]);
        this.progress.put(botRow, savedProgress[2]);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops, boolean remove) {
        super.addAdditionalDrops(level, pos, drops, remove);

        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public InternalInventory getInternalInventory() {
        return combInv;
    }

    @Override
    public InternalInventory getExposedInventoryForSide(Direction side) {
        return extInventory;
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        CrystalGrowthRecipe recipe = CrystalGrowthRecipe.getRecipefromStack(this.level, inv.getStackInSlot(slot));
        if(cachedRecipes.get(inv) != recipe && recipe != null) {
            cachedRecipes.put(inv, recipe);
            progress.put(inv, 0);
        }
        else if(!hasIngredients(inv)) {
            toRemove.add(inv);
        }

        if (!this.isWorking()) {
            this.markForUpdate();
        }

        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    public boolean hasIngredients(InternalInventory inv) {
        for(int i = 0; i <= 2; i++) {
            if(inv.getStackInSlot(i).getItem() != Items.AIR)
                return true;
        }
        return false;
    }

    public void matchWork() {
        if(isWorking() != hasWork()) {
            isWorking = hasWork();
            this.markForUpdate();
        }
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    public class FilteredInventory implements IAEItemFilter {
        @Override
        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return slot == 3;
        }

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            CrystalGrowthRecipe recipe = CrystalGrowthRecipe.getRecipefromStack(getLevel(), stack);
            if(recipe == null) {
                return false;
            }
            switch(slot) {
                case 0 -> {
                    return recipe.getFlawlessCrystal().test(stack) || recipe.getFlawedCrystal().test(stack);
                }
                case 1 -> {
                    return recipe.getChippedCrystal().test(stack);
                }
                case 2 -> {
                    return recipe.getDamagedCrystal().test(stack);
                }
                default -> {
                    return false;
                }
            }
        }
    }
}

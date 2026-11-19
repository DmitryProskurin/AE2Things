package io.github.projectet.ae2things.mixin;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.storage.DISKCellInventory;
import io.github.projectet.ae2things.storage.DiskCellInfo;
import io.github.projectet.ae2things.definitions.AE2TComponents;
import io.github.projectet.ae2things.util.DataStorage;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(AbstractContainerMenu.class)
public abstract class CursedInternalSlotMixin {

    @Final
    @Shadow
    public NonNullList<Slot> slots;

    @Inject(
            method = "doClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/inventory/Slot;hasItem()Z"
                    )
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    public void CLONE(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci) {
        Slot i = this.slots.get(slotIndex);
        if(DISKCellInventory.hasDiskUUID(i.getItem())) {
                var itemCellInfo = i.getItem().getOrDefault(AE2TComponents.DISK_CELL_INFO, DiskCellInfo.DEFAULT);
                if (itemCellInfo.isDefault() || AE2Things.STORAGE_INSTANCE == null)
                    return;

                DataStorage storage = AE2Things.STORAGE_INSTANCE.getOrCreateDisk(itemCellInfo.uuidAsUUID());
                ItemStack newStack = new ItemStack(i.getItem().getItem());
                //idk. id rematch?
                UUID id = UUID.randomUUID();
                newStack.set(AE2TComponents.DISK_CELL_INFO, new DiskCellInfo(id.toString(), storage.itemCount));
                AE2Things.STORAGE_INSTANCE.updateDisk(id, storage);

                newStack.setCount(newStack.getMaxStackSize());
                this.setCarried(newStack);
                ci.cancel();
        }
    }

    @Shadow
    public abstract void setCarried(ItemStack slot);
}

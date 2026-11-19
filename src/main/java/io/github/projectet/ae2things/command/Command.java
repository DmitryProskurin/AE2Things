package io.github.projectet.ae2things.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.definitions.AE2TItems;
import io.github.projectet.ae2things.storage.DiskCellInfo;
import io.github.projectet.ae2things.storage.IDISKCellItem;
import io.github.projectet.ae2things.definitions.AE2TComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.UUID;

@EventBusSubscriber(modid = AE2Things.MOD_ID)
public class Command {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().getRoot().addChild(commandRoot);
        event.getDispatcher().getRoot().addChild(recoverArg);
        event.getDispatcher().getRoot().addChild(copyUUID);
    }

    static LiteralCommandNode<CommandSourceStack> commandRoot = Commands.literal(AE2Things.MOD_ID).executes(Command::help).build();

    static LiteralCommandNode<CommandSourceStack> recoverArg = Commands.literal(AE2Things.MOD_ID)
            .then(Commands.literal("recover")
                    .then(Commands
                            .argument("uuid", UuidArgument.uuid())
                            .requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                            .executes(context -> spawnDrive(context, context.getArgument("uuid", UUID.class)))
                    )
            )
            .build();

    static LiteralCommandNode<CommandSourceStack> copyUUID = Commands.literal(AE2Things.MOD_ID)
            .then(Commands.literal("getuuid")
                    .executes(Command::getUUID)
            )
            .build();

    private static int help(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Available Argument(s): "), false);
        context.getSource().sendSuccess(() -> Component.literal("/ae2things recover <UUID> - Spawns a drive with the given UUID, if it doesn't exist, does not spawn any item."), false);
        context.getSource().sendSuccess(() -> Component.literal("/ae2things getuuid - Gets the UUID of the drive in the player's hand if it has a UUID. Returns the DISKS uuid."), false);
        return 0;
    }

    private static int spawnDrive(CommandContext<CommandSourceStack> context, UUID uuid) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        var storageInstance = AE2Things.STORAGE_INSTANCE;
        if (storageInstance == null) {
            context.getSource().sendFailure(Component.literal("STORAGE_INSTANCE is null"));
            return 1;
        }

        if(storageInstance.hasUUID(uuid)) {
            ItemStack stack = new ItemStack(AE2TItems.DISK_DRIVE_64K);
            CompoundTag nbt = new CompoundTag();

//            nbt.putUUID(Constants.DISKUUID, uuid);
//            nbt.putLong(DISKCellInventory.ITEM_COUNT_TAG, storageInstance.getOrCreateDisk(uuid).itemCount);
            stack.set(AE2TComponents.DISK_CELL_INFO, new DiskCellInfo(uuid.toString(), storageInstance.getOrCreateDisk(uuid).itemCount));

            player.addItem(stack);

            context.getSource().sendSuccess(()->Component.translatable("command.ae2things.recover_success", player.getDisplayName(), uuid), true);
            return 0;
        }
        else {
            context.getSource().sendFailure(Component.translatable("command.ae2things.recover_fail", uuid));
            return 1;
        }
    }

    private static int getUUID(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        ItemStack mainStack = player.getMainHandItem();
        if(mainStack.getItem() instanceof IDISKCellItem) {
            var cellInfo = mainStack.getOrDefault(AE2TComponents.DISK_CELL_INFO, DiskCellInfo.DEFAULT);
            if(!cellInfo.isDefault()) {
                Component text = copyToClipboard(cellInfo.uuid());
                context.getSource().sendSuccess(() -> Component.translatable("command.ae2things.getuuid_success", text), false);
                return 0;
            }
            else {
                context.getSource().sendFailure(Component.translatable("command.ae2things.getuuid_fail_nouuid"));
                return 1;
            }
        }
        context.getSource().sendFailure(Component.translatable("command.ae2things.getuuid_fail_notdisk"));
        return 1;
    }

    private static Component copyToClipboard(String string) {
        return Component.literal(string).withStyle(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, string))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))
                .withInsertion(string)
                .withColor(ChatFormatting.GREEN));
    }
}

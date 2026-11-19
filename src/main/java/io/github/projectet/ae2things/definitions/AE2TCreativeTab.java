package io.github.projectet.ae2things.definitions;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import io.github.projectet.ae2things.AE2Things;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;

public class AE2TCreativeTab {
    public static final DeferredRegister<CreativeModeTab> DR =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AE2Things.MOD_ID);

    static {
        DR.register("tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.ae2things.item_group"))
                .icon(AE2TItems.DISK_HOUSING::stack)
                .displayItems(AE2TCreativeTab::populateTab)
                .build());
    }

    private static void populateTab(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        var itemDefs = new ArrayList<ItemDefinition<?>>();
        itemDefs.addAll(AE2TItems.getItems());
        itemDefs.addAll(
                AE2TBlocks.getBlocks().stream().map(BlockDefinition::item).toList());

        for (var itemDef : itemDefs) {
            output.accept(itemDef);
        }
    }
}

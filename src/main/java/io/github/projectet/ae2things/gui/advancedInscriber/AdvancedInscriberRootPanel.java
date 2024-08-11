package io.github.projectet.ae2things.gui.advancedInscriber;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.client.gui.widgets.ToggleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AdvancedInscriberRootPanel extends UpgradeableScreen<AdvancedInscriberMenu> {

    private final ProgressBar pb;

    private final ToggleButton topLock;

    private final ToggleButton botLock;

    private final SettingToggleButton<YesNo> autoExportBtn;
    private final SettingToggleButton<YesNo> bufferSizeBtn;

    public AdvancedInscriberRootPanel(AdvancedInscriberMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", this.pb);
        this.topLock = new ToggleButton(Icon.LOCKED, Icon.UNLOCKED, a -> menu.toggleTopLock());
        this.botLock = new ToggleButton(Icon.LOCKED, Icon.UNLOCKED, a -> menu.toggleBotLock());
        widgets.add("topLock", topLock);
        widgets.add("botLock", botLock);

        this.autoExportBtn = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(autoExportBtn);

        this.bufferSizeBtn = new ServerSettingToggleButton<>(Settings.INSCRIBER_BUFFER_SIZE, YesNo.YES);
        this.addToLeftToolbar(bufferSizeBtn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        topLock.setState(menu.topLock);
        botLock.setState(menu.botLock);

        int progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        this.pb.setFullMsg(Component.literal(progress + "%"));

        this.autoExportBtn.set(getMenu().getAutoExport());
        this.bufferSizeBtn.set(getMenu().getBufferSize());
    }
}

package mekanism.client.gui.element.tab;

import javax.annotation.Nonnull;
import mekanism.client.SpecialColors;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiInsetElement;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.network.PacketGuiButtonPress;
import mekanism.common.network.PacketGuiButtonPress.ClickedTileButton;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;

public class GuiUpgradeTab extends GuiInsetElement<BlockEntity> {

    public GuiUpgradeTab(IGuiWrapper gui, BlockEntity tile) {
        super(MekanismUtils.getResource(ResourceType.GUI, "upgrade.png"), gui, tile, gui.getWidth(), 6, 26, 18, false);
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        displayTooltip(matrix, MekanismLang.UPGRADES.translate(), mouseX, mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedTileButton.UPGRADE_MANAGEMENT, tile));
    }

    @Override
    protected void colorTab() {
        MekanismRenderer.color(SpecialColors.TAB_UPGRADE.get());
    }
}
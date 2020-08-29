package mekanism.client.gui;

import javax.annotation.Nonnull;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.TileEntityPersonalChest;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GuiPersonalChestTile extends GuiMekanismTile<TileEntityPersonalChest, MekanismTileContainer<TileEntityPersonalChest>> {

    public GuiPersonalChestTile(MekanismTileContainer<TileEntityPersonalChest> container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        backgroundHeight += 64;
        dynamicSlots = true;
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiSecurityTab<>(this, tile));
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        renderTitleText(matrix);
        drawString(matrix, MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 2, titleTextColor());
        super.drawForegroundText(matrix, mouseX, mouseY);
    }
}
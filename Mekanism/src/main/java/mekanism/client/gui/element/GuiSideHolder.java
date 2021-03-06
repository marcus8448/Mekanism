package mekanism.client.gui.element;

import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GuiSideHolder extends GuiTexturedElement {

    private static final Identifier HOLDER_LEFT = MekanismUtils.getResource(ResourceType.GUI, "holder_left.png");
    private static final Identifier HOLDER_RIGHT = MekanismUtils.getResource(ResourceType.GUI, "holder_right.png");
    private static final int TEXTURE_WIDTH = 26;
    private static final int TEXTURE_HEIGHT = 9;

    protected final boolean left;

    public GuiSideHolder(IGuiWrapper gui, int x, int y, int height, boolean left) {
        super(left ? HOLDER_LEFT : HOLDER_RIGHT, gui, x, y, TEXTURE_WIDTH, height);
        this.left = left;
        active = false;
        setButtonBackground(ButtonBackground.DEFAULT);
    }

    protected void colorTab() {
        //Don't do any coloring by default
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        minecraft.textureManager.bindTexture(getResource());
        colorTab();
        //Top
        drawTexture(matrix, x, y, 0, 0, width, 4, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        //Middle
        int middleHeight = height - 8;
        if (middleHeight > 0) {
            drawTexture(matrix, x, y + 4, width, middleHeight, 0, 4, width, 1, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
        //Bottom
        drawTexture(matrix, x, y + 4 + middleHeight, 0, 5, width, 4, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        MekanismRenderer.resetColor();
    }
}
package mekanism.client.gui.element.progress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTexturedElement;
import mekanism.client.jei.interfaces.IJEIRecipeArea;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GuiProgress extends GuiTexturedElement implements IJEIRecipeArea<GuiProgress> {

    protected final IProgressInfoHandler handler;
    protected final ProgressType type;
    private Identifier[] recipeCategories;

    public GuiProgress(IProgressInfoHandler handler, ProgressType type, IGuiWrapper gui, int x, int y) {
        super(type.getTexture(), gui, x, y, type.getWidth(), type.getHeight());
        this.type = type;
        this.handler = handler;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        if (handler.isActive()) {
            minecraft.textureManager.bindTexture(getResource());
            drawTexture(matrix, x, y, 0, 0, width, height, type.getTextureWidth(), type.getTextureHeight());
            if (type.isVertical()) {
                int displayInt = (int) (handler.getProgress() * height);
                drawTexture(matrix, x, y, type.getOverlayX(), type.getOverlayY(), width, displayInt, type.getTextureWidth(), type.getTextureHeight());
            } else {
                int innerOffsetX = type == ProgressType.BAR ? 1 : 0;
                int displayInt = (int) (handler.getProgress() * (width - 2 * innerOffsetX));
                drawTexture(matrix, x + innerOffsetX, y, type.getOverlayX() + innerOffsetX, type.getOverlayY(), displayInt, height, type.getTextureWidth(), type.getTextureHeight());
            }
        }
    }

    @Override
    public boolean isActive() {
        return handler.isActive();
    }

    @Nonnull
    @Override
    public GuiProgress jeiCategories(@Nullable Identifier... recipeCategories) {
        this.recipeCategories = recipeCategories;
        return this;
    }

    @Nullable
    @Override
    public Identifier[] getRecipeCategories() {
        return recipeCategories;
    }
}
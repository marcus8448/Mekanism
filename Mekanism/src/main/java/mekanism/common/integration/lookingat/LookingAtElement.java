package mekanism.common.integration.lookingat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class LookingAtElement {

    private final int borderColor;
    private final int textColor;

    protected LookingAtElement(int borderColor, int textColor) {
        this.borderColor = borderColor;
        this.textColor = textColor;
    }

    public void render(@Nonnull MatrixStack matrix, int x, int y) {
        int width = getWidth();
        int height = getHeight();
        DrawableHelper.fill(matrix, x, y, x + width - 1, y + 1, borderColor);
        DrawableHelper.fill(matrix, x, y, x + 1, y + height - 1, borderColor);
        DrawableHelper.fill(matrix, x + width - 1, y, x + width, y + height - 1, borderColor);
        DrawableHelper.fill(matrix, x, y + height - 1, x + width, y + height, borderColor);
        Sprite icon = getIcon();
        if (icon != null) {
            int scale = getScaledLevel(width - 2);
            if (scale > 0) {
                boolean colored = applyRenderColor();
                GuiUtils.drawTiledSprite(matrix, x + 1, y + 1, height - 2, scale, height - 2, icon, 16, 16, 0);
                if (colored) {
                    MekanismRenderer.resetColor();
                }
            }
        }
        renderScaledText(MinecraftClient.getInstance(), matrix, x + 4, y + 3, textColor, getWidth() - 8, getText());
    }

    public int getWidth() {
        return 100;
    }

    public int getHeight() {
        return 13;
    }

    public abstract int getScaledLevel(int level);

    @Nullable
    public abstract Sprite getIcon();

    public abstract Text getText();

    protected boolean applyRenderColor() {
        return false;
    }

    public static void renderScaledText(MinecraftClient mc, @Nonnull MatrixStack matrix, int x, int y, int color, int maxWidth, Text component) {
        int length = mc.textRenderer.getWidth(component);
        if (length <= maxWidth) {
            mc.textRenderer.draw(matrix, component, x, y, color);
        } else {
            float scale = (float) maxWidth / length;
            float reverse = 1 / scale;
            float yAdd = 4 - (scale * 8) / 2F;
            matrix.push();
            matrix.scale(scale, scale, scale);
            mc.textRenderer.draw(matrix, component, (int) (x * reverse), (int) ((y * reverse) + yAdd), color);
            matrix.pop();
        }
        //Make sure the color does not leak from having drawn the string
        MekanismRenderer.resetColor();
    }
}
package mekanism.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

public class GuiUtils {

    // Note: Does not validate that the passed in dimensions are valid
    // this strategy starts with a small texture and will expand it (by scaling) to meet the size requirements. good for small widgets
    // where the background texture is a single color
    public static void renderExtendedTexture(MatrixStack matrix, Identifier resource, int sideWidth, int sideHeight, int left, int top, int width, int height) {
        int textureWidth = 2 * sideWidth + 1;
        int textureHeight = 2 * sideHeight + 1;
        int centerWidth = width - 2 * sideWidth;
        int centerHeight = height - 2 * sideHeight;
        int leftEdgeEnd = left + sideWidth;
        int rightEdgeStart = leftEdgeEnd + centerWidth;
        int topEdgeEnd = top + sideHeight;
        int bottomEdgeStart = topEdgeEnd + centerHeight;
        MekanismRenderer.bindTexture(resource);
        //Left Side
        //Top Left Corner
        DrawableHelper.drawTexture(matrix, left, top, 0, 0, sideWidth, sideHeight, textureWidth, textureHeight);
        //Left Middle
        if (centerHeight > 0) {
            DrawableHelper.drawTexture(matrix, left, topEdgeEnd, sideWidth, centerHeight, 0, sideHeight, sideWidth, 1, textureWidth, textureHeight);
        }
        //Bottom Left Corner
        DrawableHelper.drawTexture(matrix, left, bottomEdgeStart, 0, sideHeight + 1, sideWidth, sideHeight, textureWidth, textureHeight);

        //Middle
        if (centerWidth > 0) {
            //Top Middle
            DrawableHelper.drawTexture(matrix, leftEdgeEnd, top, centerWidth, sideHeight, sideWidth, 0, 1, sideHeight, textureWidth, textureHeight);
            if (centerHeight > 0) {
                //Center
                DrawableHelper.drawTexture(matrix, leftEdgeEnd, topEdgeEnd, centerWidth, centerHeight, sideWidth, sideHeight, 1, 1, textureWidth, textureHeight);
            }
            //Bottom Middle
            DrawableHelper.drawTexture(matrix, leftEdgeEnd, bottomEdgeStart, centerWidth, sideHeight, sideWidth, sideHeight + 1, 1, sideHeight, textureWidth, textureHeight);
        }

        //Right side
        //Top Right Corner
        DrawableHelper.drawTexture(matrix, rightEdgeStart, top, sideWidth + 1, 0, sideWidth, sideHeight, textureWidth, textureHeight);
        //Right Middle
        if (centerHeight > 0) {
            DrawableHelper.drawTexture(matrix, rightEdgeStart, topEdgeEnd, sideWidth, centerHeight, sideWidth + 1, sideHeight, sideWidth, 1, textureWidth, textureHeight);
        }
        //Bottom Right Corner
        DrawableHelper.drawTexture(matrix, rightEdgeStart, bottomEdgeStart, sideWidth + 1, sideHeight + 1, sideWidth, sideHeight, textureWidth, textureHeight);
    }

    // this strategy starts with a large texture and will scale it down or tile it if necessary. good for larger widgets, but requires a large texture; small textures will tank FPS due
    // to tiling
    public static void renderBackgroundTexture(MatrixStack matrix, Identifier resource, int texSideWidth, int texSideHeight, int left, int top, int width, int height, int textureWidth, int textureHeight) {
        // render as much side as we can, based on element dimensions
        int sideWidth = Math.min(texSideWidth, width / 2);
        int sideHeight = Math.min(texSideHeight, height / 2);

        // Adjustment for small odd-height and odd-width GUIs
        int leftWidth = sideWidth < texSideWidth ? sideWidth + (width % 2) : sideWidth;
        int topHeight = sideHeight < texSideHeight ? sideHeight + (height % 2) : sideHeight;

        int texCenterWidth = textureWidth - texSideWidth * 2, texCenterHeight = textureHeight - texSideHeight * 2;
        int centerWidth = width - leftWidth - sideWidth, centerHeight = height - topHeight - sideHeight;

        int leftEdgeEnd = left + leftWidth;
        int rightEdgeStart = leftEdgeEnd + centerWidth;
        int topEdgeEnd = top + topHeight;
        int bottomEdgeStart = topEdgeEnd + centerHeight;
        MekanismRenderer.bindTexture(resource);

        //Top Left Corner
        DrawableHelper.drawTexture(matrix, left, top, 0, 0, leftWidth, topHeight, textureWidth, textureHeight);
        //Bottom Left Corner
        DrawableHelper.drawTexture(matrix, left, bottomEdgeStart, 0, textureHeight - sideHeight, leftWidth, sideHeight, textureWidth, textureHeight);

        //Middle
        if (centerWidth > 0) {
            //Top Middle
            blitTiled(matrix, leftEdgeEnd, top, centerWidth, topHeight, texSideWidth, 0, texCenterWidth, texSideHeight, textureWidth, textureHeight);
            if (centerHeight > 0) {
                //Center
                blitTiled(matrix, leftEdgeEnd, topEdgeEnd, centerWidth, centerHeight, texSideWidth, texSideHeight, texCenterWidth, texCenterHeight, textureWidth, textureHeight);
            }
            //Bottom Middle
            blitTiled(matrix, leftEdgeEnd, bottomEdgeStart, centerWidth, sideHeight, texSideWidth, textureHeight - sideHeight, texCenterWidth, texSideHeight, textureWidth, textureHeight);
        }

        if (centerHeight > 0) {
            //Left Middle
            blitTiled(matrix, left, topEdgeEnd, leftWidth, centerHeight, 0, texSideHeight, texSideWidth, texCenterHeight, textureWidth, textureHeight);
            //Right Middle
            blitTiled(matrix, rightEdgeStart, topEdgeEnd, sideWidth, centerHeight, textureWidth - sideWidth, texSideHeight, texSideWidth, texCenterHeight, textureWidth, textureHeight);
        }

        //Top Right Corner
        DrawableHelper.drawTexture(matrix, rightEdgeStart, top, textureWidth - sideWidth, 0, sideWidth, topHeight, textureWidth, textureHeight);
        //Bottom Right Corner
        DrawableHelper.drawTexture(matrix, rightEdgeStart, bottomEdgeStart, textureWidth - sideWidth, textureHeight - sideHeight, sideWidth, sideHeight, textureWidth, textureHeight);
    }

    public static void blitTiled(MatrixStack matrix, int x, int y, int width, int height, int texX, int texY, int texDrawWidth, int texDrawHeight, int textureWidth, int textureHeight) {
        int xTiles = (int) Math.ceil((float) width / texDrawWidth), yTiles = (int) Math.ceil((float) height / texDrawHeight);

        int drawWidth = width, drawHeight = height;
        for (int tileX = 0; tileX < xTiles; tileX++) {
            for (int tileY = 0; tileY < yTiles; tileY++) {
                DrawableHelper.drawTexture(matrix, x + texDrawWidth * tileX, y + texDrawHeight * tileY, texX, texY, Math.min(drawWidth, texDrawWidth), Math.min(drawHeight, texDrawHeight), textureWidth, textureHeight);
                drawHeight -= texDrawHeight;
            }
            drawWidth -= texDrawWidth;
            drawHeight = height;
        }
    }

    public static void drawOutline(MatrixStack matrix, int x, int y, int width, int height, int color) {
        fill(matrix, x, y, width, 1, color);
        fill(matrix, x, y + height - 1, width, 1, color);
        if (height > 2) {
            fill(matrix, x, y + 1, 1, height - 2, color);
            fill(matrix, x + width - 1, y + 1, 1, height - 2, color);
        }
    }

    public static void fill(MatrixStack matrix, int x, int y, int width, int height, int color) {
        DrawableHelper.fill(matrix, x, y, x + width, y + height, color);
    }

    public static void drawSprite(MatrixStack matrix, int x, int y, int width, int height, int zLevel, Sprite sprite) {
        MekanismRenderer.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        BufferBuilder vertexBuffer = Tessellator.getInstance().getBuffer();
        vertexBuffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        Matrix4f matrix4f = matrix.peek().getModel();
        vertexBuffer.vertex(matrix4f, x, y + height, zLevel).texture(sprite.getMinU(), sprite.getMaxV()).next();
        vertexBuffer.vertex(matrix4f, x + width, y + height, zLevel).texture(sprite.getMaxU(), sprite.getMaxV()).next();
        vertexBuffer.vertex(matrix4f, x + width, y, zLevel).texture(sprite.getMaxU(), sprite.getMinV()).next();
        vertexBuffer.vertex(matrix4f, x, y, zLevel).texture(sprite.getMinU(), sprite.getMinV()).next();
        vertexBuffer.end();
        BufferRenderer.draw(vertexBuffer);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public static void drawTiledSprite(MatrixStack matrix, int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, Sprite sprite, int textureWidth,
          int textureHeight, int zLevel) {
        if (desiredWidth == 0 || desiredHeight == 0 || textureWidth == 0 || textureHeight == 0) {
            return;
        }
        MekanismRenderer.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        int xTileCount = desiredWidth / textureWidth;
        int xRemainder = desiredWidth - (xTileCount * textureWidth);
        int yTileCount = desiredHeight / textureHeight;
        int yRemainder = desiredHeight - (yTileCount * textureHeight);
        int yStart = yPosition + yOffset;
        float uMin = sprite.getMinU();
        float uMax = sprite.getMaxU();
        float vMin = sprite.getMinV();
        float vMax = sprite.getMaxV();
        float uDif = uMax - uMin;
        float vDif = vMax - vMin;
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        BufferBuilder vertexBuffer = Tessellator.getInstance().getBuffer();
        vertexBuffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        Matrix4f matrix4f = matrix.peek().getModel();
        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            int width = (xTile == xTileCount) ? xRemainder : textureWidth;
            if (width == 0) {
                break;
            }
            int x = xPosition + (xTile * textureWidth);
            int maskRight = textureWidth - width;
            int shiftedX = x + textureWidth - maskRight;
            float uMaxLocal = uMax - (uDif * maskRight / textureWidth);
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int height = (yTile == yTileCount) ? yRemainder : textureHeight;
                if (height == 0) {
                    //Note: We don't want to fully break out because our height will be zero if we are looking to
                    // draw the remainder, but there is no remainder as it divided evenly
                    break;
                }
                int y = yStart - ((yTile + 1) * textureHeight);
                int maskTop = textureHeight - height;
                float vMaxLocal = vMax - (vDif * maskTop / textureHeight);
                vertexBuffer.vertex(matrix4f, x, y + textureHeight, zLevel).texture(uMin, vMaxLocal).next();
                vertexBuffer.vertex(matrix4f, shiftedX, y + textureHeight, zLevel).texture(uMaxLocal, vMaxLocal).next();
                vertexBuffer.vertex(matrix4f, shiftedX, y + maskTop, zLevel).texture(uMaxLocal, vMin).next();
                vertexBuffer.vertex(matrix4f, x, y + maskTop, zLevel).texture(uMin, vMin).next();
            }
        }
        vertexBuffer.end();
        BufferRenderer.draw(vertexBuffer);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    // reverse-order iteration over children w/ built-in GuiElement check, runs a basic anyMatch with checker
    public static boolean checkChildren(List<? extends AbstractButtonWidget> children, Predicate<GuiElement> checker) {
        for (int i = children.size() - 1; i >= 0; i--) {
            Object obj = children.get(i);
            if (obj instanceof GuiElement && checker.test((GuiElement) obj)) {
                return true;
            }
        }
        return false;
    }

    public static void renderItem(MatrixStack matrix, ItemRenderer renderer, @Nonnull ItemStack stack, int xAxis, int yAxis, float scale, TextRenderer font,
          String text, boolean overlay) {
        if (!stack.isEmpty()) {
            try {
                matrix.push();
                RenderSystem.enableDepthTest();
                DiffuseLighting.enable();
                if (scale != 1) {
                    matrix.scale(scale, scale, scale);
                }
                //Apply our matrix stack to the render system and pass an unmodified one to the render methods
                // Vanilla still renders the items using render system transformations so this is required to
                // have things render in the correct order
                RenderSystem.pushMatrix();
                RenderSystem.multMatrix(matrix.peek().getModel());
                renderer.renderInGuiWithOverrides(stack, xAxis, yAxis);
                if (overlay) {
                    renderer.renderGuiItemOverlay(font, stack, xAxis, yAxis, text);
                }
                RenderSystem.popMatrix();
                DiffuseLighting.disable();
                RenderSystem.disableDepthTest();
                matrix.pop();
            } catch (Exception e) {
                Mekanism.logger.error("Failed to render stack into gui: " + stack, e);
            }
        }
    }
}
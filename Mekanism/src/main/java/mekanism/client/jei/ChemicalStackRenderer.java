package mekanism.client.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.math.MathUtils;
import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.client.jei.ChemicalStackRenderer.TooltipMode;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import net.minecraftforge.fluids.FluidAttributes;
import org.lwjgl.opengl.GL11;

public class ChemicalStackRenderer<STACK extends ChemicalStack<?>> implements IIngredientRenderer<STACK> {

    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    protected static final int TEX_WIDTH = 16;
    protected static final int TEX_HEIGHT = 16;
    private static final int MIN_CHEMICAL_HEIGHT = 1; // ensure tiny amounts of chemical are still visible

    private final long capacityMb;
    private final TooltipMode tooltipMode;
    private final int width;
    private final int height;
    @Nullable
    private final IDrawable overlay;

    public ChemicalStackRenderer() {
        this(FluidAttributes.BUCKET_VOLUME, TooltipMode.ITEM_LIST, TEX_WIDTH, TEX_HEIGHT, null);
    }

    public ChemicalStackRenderer(long capacityMb, int width, int height) {
        this(capacityMb, TooltipMode.SHOW_AMOUNT, width, height, null);
    }

    public ChemicalStackRenderer(long capacityMb, boolean showCapacity, int width, int height, @Nullable IDrawable overlay) {
        this(capacityMb, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height, overlay);
    }

    private ChemicalStackRenderer(long capacityMb, TooltipMode tooltipMode, int width, int height, @Nullable IDrawable overlay) {
        this.capacityMb = capacityMb;
        this.tooltipMode = tooltipMode;
        this.width = width;
        this.height = height;
        this.overlay = overlay;
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int xPosition, int yPosition, @Nullable STACK stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        drawChemical(matrix, xPosition, yPosition, stack);
        if (overlay != null) {
            matrix.push();
            matrix.translate(0, 0, 200);
            overlay.draw(matrix, xPosition, yPosition);
            matrix.pop();
        }
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    private void drawChemical(MatrixStack matrix, int xPosition, int yPosition, @Nonnull STACK stack) {
        int desiredHeight = MathUtils.clampToInt(height * (double) stack.getAmount() / capacityMb);
        if (desiredHeight < MIN_CHEMICAL_HEIGHT) {
            desiredHeight = MIN_CHEMICAL_HEIGHT;
        }
        if (desiredHeight > height) {
            desiredHeight = height;
        }
        Chemical<?> chemical = stack.getType();
        drawTiledSprite(matrix, xPosition, yPosition, width, desiredHeight, height, chemical);
    }

    private void drawTiledSprite(MatrixStack matrix, int xPosition, int yPosition, int desiredWidth, int desiredHeight, int yOffset, @Nonnull Chemical<?> chemical) {
        if (desiredWidth == 0 || desiredHeight == 0) {
            return;
        }
        Matrix4f matrix4f = matrix.peek().getModel();
        MekanismRenderer.color(chemical);
        Sprite sprite = MekanismRenderer.getSprite(chemical.getIcon());
        MekanismRenderer.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        int xTileCount = desiredWidth / TEX_WIDTH;
        int xRemainder = desiredWidth - (xTileCount * TEX_WIDTH);
        int yTileCount = desiredHeight / TEX_HEIGHT;
        int yRemainder = desiredHeight - (yTileCount * TEX_HEIGHT);
        int yStart = yPosition + yOffset;
        int zLevel = 100;
        float uMin = sprite.getMinU();
        float uMax = sprite.getMaxU();
        float vMin = sprite.getMinV();
        float vMax = sprite.getMaxV();
        float uDif = uMax - uMin;
        float vDif = vMax - vMin;
        BufferBuilder vertexBuffer = Tessellator.getInstance().getBuffer();
        vertexBuffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
            if (width == 0) {
                break;
            }
            int x = xPosition + (xTile * TEX_WIDTH);
            int maskRight = TEX_WIDTH - width;
            int shiftedX = x + TEX_WIDTH - maskRight;
            float uMaxLocal = uMax - (uDif * maskRight / TEX_WIDTH);
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
                if (height == 0) {
                    //Note: We don't want to fully break out because our height will be zero if we are looking to
                    // draw the remainder, but there is no remainder as it divided evenly
                    break;
                }
                int y = yStart - ((yTile + 1) * TEX_HEIGHT);
                int maskTop = TEX_HEIGHT - height;
                float vMaxLocal = vMax - (vDif * maskTop / TEX_HEIGHT);
                vertexBuffer.vertex(matrix4f, x, y + TEX_HEIGHT, zLevel).texture(uMin, vMaxLocal).next();
                vertexBuffer.vertex(matrix4f, shiftedX, y + TEX_HEIGHT, zLevel).texture(uMaxLocal, vMaxLocal).next();
                vertexBuffer.vertex(matrix4f, shiftedX, y + maskTop, zLevel).texture(uMaxLocal, vMin).next();
                vertexBuffer.vertex(matrix4f, x, y + maskTop, zLevel).texture(uMin, vMin).next();
            }
        }
        vertexBuffer.end();
        BufferRenderer.draw(vertexBuffer);
        MekanismRenderer.resetColor();
    }

    @Override
    public List<Text> getTooltip(@Nonnull STACK stack, TooltipContext tooltipFlag) {
        Chemical<?> chemical = stack.getType();
        if (chemical.isEmptyType()) {
            return Collections.emptyList();
        }
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(TextComponentUtil.build(chemical));
        Text component = null;
        if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
            component = MekanismLang.JEI_AMOUNT_WITH_CAPACITY.translateColored(EnumColor.GRAY, nf.format(stack.getAmount()), nf.format(capacityMb));
        } else if (tooltipMode == TooltipMode.SHOW_AMOUNT) {
            component = MekanismLang.GENERIC_MB.translateColored(EnumColor.GRAY, nf.format(stack.getAmount()));
        }
        if (component != null) {
            tooltip.add(component);
        }
        return tooltip;
    }

    @Override
    public TextRenderer getFontRenderer(MinecraftClient minecraft, @Nonnull STACK stack) {
        return minecraft.textRenderer;
    }

    enum TooltipMode {
        SHOW_AMOUNT,
        SHOW_AMOUNT_AND_CAPACITY,
        ITEM_LIST
    }
}
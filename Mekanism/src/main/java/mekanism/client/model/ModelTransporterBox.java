package mekanism.client.model;

import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ModelTransporterBox extends Model {

    private static final Identifier BOX_TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "transporter_box.png");
    private final RenderLayer RENDER_TYPE = getLayer(BOX_TEXTURE);
    private final ModelPart box;

    public ModelTransporterBox() {
        super(RenderLayer::getEntityCutoutNoCull);
        textureWidth = 64;
        textureHeight = 64;

        box = new ModelPart(this, 0, 0);
        box.addCuboid(0F, 0F, 0F, 7, 7, 7, false);
        box.setPivot(-3.5F, 0, -3.5F);
        box.setTextureSize(64, 64);
        box.mirror = true;
        setRotation(box, 0F, 0F, 0F);
    }

    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, float x, float y, float z, EnumColor color) {
        matrix.push();
        matrix.translate(x, y, z);
        render(matrix, renderer.getBuffer(RENDER_TYPE), MekanismRenderer.FULL_LIGHT, overlayLight, color.getColor(0), color.getColor(1), color.getColor(2), 1);
        matrix.pop();
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        box.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }

    private void setRotation(ModelPart model, float x, float y, float z) {
        model.pitch = x;
        model.yaw = y;
        model.roll = z;
    }
}
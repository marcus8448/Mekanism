package mekanism.generators.client.model;

import javax.annotation.Nonnull;
import mekanism.client.model.MekanismJavaModel;
import mekanism.generators.common.MekanismGenerators;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;

public class ModelTurbine extends MekanismJavaModel {

    private static final Identifier TURBINE_TEXTURE = MekanismGenerators.rl("render/turbine.png");
    private static final float BLADE_ROTATE = 0.418879F;

    private final RenderLayer RENDER_TYPE = getLayer(TURBINE_TEXTURE);

    private final ModelPart extension_north;
    private final ModelPart blade_north;
    private final ModelPart extension_south;
    private final ModelPart extension_west;
    private final ModelPart extension_east;
    private final ModelPart blade_south;
    private final ModelPart blade_east;
    private final ModelPart blade_west;

    public ModelTurbine() {
        super(RenderLayer::getEntitySolid);
        textureWidth = 64;
        textureHeight = 64;
        extension_south = new ModelPart(this, 0, 0);
        extension_south.setPivot(0.0F, 20.0F, 0.0F);
        extension_south.addCuboid(-1.0F, 0.0F, 1.0F, 2, 1, 3, 0.0F);
        setRotation(extension_south, 0.0F, 0.0F, -BLADE_ROTATE);
        extension_west = new ModelPart(this, 0, 4);
        extension_west.setPivot(0.0F, 20.0F, 0.0F);
        extension_west.addCuboid(-4.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F);
        setRotation(extension_west, BLADE_ROTATE, 0.0F, 0.0F);
        blade_east = new ModelPart(this, 10, 5);
        blade_east.setPivot(0.0F, 20.0F, 0.0F);
        blade_east.addCuboid(4.0F, 0.0F, -1.5F, 4, 1, 3, 0.0F);
        setRotation(blade_east, -BLADE_ROTATE, 0.0F, 0.0F);
        blade_north = new ModelPart(this, 10, 0);
        blade_north.setPivot(0.0F, 20.0F, 0.0F);
        blade_north.addCuboid(-1.5F, 0.0F, -8.0F, 3, 1, 4, 0.0F);
        setRotation(blade_north, 0.0F, 0.0F, BLADE_ROTATE);
        extension_east = new ModelPart(this, 0, 4);
        extension_east.setPivot(0.0F, 20.0F, 0.0F);
        extension_east.addCuboid(1.0F, 0.0F, -1.0F, 3, 1, 2, 0.0F);
        setRotation(extension_east, -BLADE_ROTATE, 0.0F, 0.0F);
        blade_south = new ModelPart(this, 10, 0);
        blade_south.setPivot(0.0F, 20.0F, 0.0F);
        blade_south.addCuboid(-1.5F, 0.0F, 4.0F, 3, 1, 4, 0.0F);
        setRotation(blade_south, 0.0F, 0.0F, -BLADE_ROTATE);
        extension_north = new ModelPart(this, 0, 0);
        extension_north.setPivot(0.0F, 20.0F, 0.0F);
        extension_north.addCuboid(-1.0F, 0.0F, -4.0F, 2, 1, 3, 0.0F);
        setRotation(extension_north, 0.0F, 0.0F, BLADE_ROTATE);
        blade_west = new ModelPart(this, 10, 5);
        blade_west.setPivot(0.0F, 20.0F, 0.0F);
        blade_west.addCuboid(-8.0F, 0.0F, -1.5F, 4, 1, 3, 0.0F);
        setRotation(blade_west, BLADE_ROTATE, 0.0F, 0.0F);
    }

    public VertexConsumer getBuffer(@Nonnull VertexConsumerProvider renderer) {
        return renderer.getBuffer(RENDER_TYPE);
    }

    public void render(@Nonnull MatrixStack matrix, VertexConsumer buffer, int light, int overlayLight, int index) {
        matrix.push();
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(index * 5));
        render(matrix, buffer, light, overlayLight, 1, 1, 1, 1);
        float scale = index * 0.5F;
        float widthDiv = 16;
        renderBlade(matrix, buffer, light, overlayLight, blade_west, scale, scale / widthDiv, -0.25, 0);
        renderBlade(matrix, buffer, light, overlayLight, blade_east, scale, scale / widthDiv, 0.25, 0);
        renderBlade(matrix, buffer, light, overlayLight, blade_north, scale / widthDiv, scale, 0, -0.25);
        renderBlade(matrix, buffer, light, overlayLight, blade_south, scale / widthDiv, scale, 0, 0.25);
        matrix.pop();
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue,
          float alpha) {
        extension_south.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        extension_west.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        extension_east.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        extension_north.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }

    private void renderBlade(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, ModelPart blade, float scaleX,
          float scaleZ, double transX, double transZ) {
        matrix.push();
        matrix.translate(transX, 0, transZ);
        matrix.scale(1.0F + scaleX, 1.0F, 1.0F + scaleZ);
        matrix.translate(-transX, 0, -transZ);
        blade.render(matrix, vertexBuilder, light, overlayLight, 1, 1, 1, 1);
        matrix.pop();
    }
}
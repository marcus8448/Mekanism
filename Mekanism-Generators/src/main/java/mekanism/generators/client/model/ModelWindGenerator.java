package mekanism.generators.client.model;

import javax.annotation.Nonnull;
import mekanism.client.model.ExtendedModelRenderer;
import mekanism.client.model.MekanismJavaModel;
import mekanism.client.render.MekanismRenderer;
import mekanism.generators.common.MekanismGenerators;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ModelWindGenerator extends MekanismJavaModel {

    private static final Identifier GENERATOR_TEXTURE = MekanismGenerators.rl("render/wind_generator.png");
    private final RenderLayer RENDER_TYPE = getLayer(GENERATOR_TEXTURE);

    private final ExtendedModelRenderer head;
    private final ExtendedModelRenderer plateConnector2;
    private final ExtendedModelRenderer plateConnector;
    private final ExtendedModelRenderer plate;
    private final ExtendedModelRenderer bladeCap;
    private final ExtendedModelRenderer bladeCenter;
    private final ExtendedModelRenderer baseRim;
    private final ExtendedModelRenderer base;
    private final ExtendedModelRenderer wire;
    private final ExtendedModelRenderer rearPlate1;
    private final ExtendedModelRenderer rearPlate2;
    private final ExtendedModelRenderer blade1a;
    private final ExtendedModelRenderer blade2a;
    private final ExtendedModelRenderer blade3a;
    private final ExtendedModelRenderer blade1b;
    private final ExtendedModelRenderer blade2b;
    private final ExtendedModelRenderer blade3b;
    private final ExtendedModelRenderer post1a;
    private final ExtendedModelRenderer post1b;
    private final ExtendedModelRenderer post1c;
    private final ExtendedModelRenderer post1d;

    public ModelWindGenerator() {
        super(RenderLayer::getEntitySolid);
        textureWidth = 128;
        textureHeight = 128;

        head = new ExtendedModelRenderer(this, 20, 0);
        head.addCuboid(-3.5F, -3.5F, 0F, 7, 7, 9, false);
        head.setPivot(0F, -48F, -4F);
        head.setTextureSize(128, 128);
        head.mirror = true;
        setRotation(head, 0F, 0F, 0F);
        plateConnector2 = new ExtendedModelRenderer(this, 42, 34);
        plateConnector2.addCuboid(0F, 0F, 0F, 6, 6, 10, false);
        plateConnector2.setPivot(-3F, 13F, -7F);
        plateConnector2.setTextureSize(128, 128);
        plateConnector2.mirror = true;
        setRotation(plateConnector2, 0F, 0F, 0F);
        plateConnector = new ExtendedModelRenderer(this, 0, 75);
        plateConnector.addCuboid(0F, 0F, 0F, 4, 2, 2, false);
        plateConnector.setPivot(-2F, 19F, -5.5F);
        plateConnector.setTextureSize(128, 128);
        plateConnector.mirror = true;
        setRotation(plateConnector, 0F, 0F, 0F);
        plate = new ExtendedModelRenderer(this, 42, 25);
        plate.addCuboid(0F, 0F, 0F, 8, 8, 1, false);
        plate.setPivot(-4F, 12F, -8F);
        plate.setTextureSize(128, 128);
        plate.mirror = true;
        setRotation(plate, 0F, 0F, 0F);
        bladeCap = new ExtendedModelRenderer(this, 22, 0);
        bladeCap.addCuboid(-1F, -1F, -8F, 2, 2, 1, false);
        bladeCap.setPivot(0F, -48F, 0F);
        bladeCap.setTextureSize(128, 128);
        bladeCap.mirror = true;
        setRotation(bladeCap, 0F, 0F, 0F);
        bladeCenter = new ExtendedModelRenderer(this, 20, 25);
        bladeCenter.addCuboid(-2F, -2F, -7F, 4, 4, 3, false);
        bladeCenter.setPivot(0F, -48F, 0F);
        bladeCenter.setTextureSize(128, 128);
        bladeCenter.mirror = true;
        setRotation(bladeCenter, 0F, 0F, 0F);
        baseRim = new ExtendedModelRenderer(this, 26, 50);
        baseRim.addCuboid(0F, 0F, 0F, 12, 2, 12, false);
        baseRim.setPivot(-6F, 21F, -6F);
        baseRim.setTextureSize(128, 128);
        baseRim.mirror = true;
        setRotation(baseRim, 0F, 0F, 0F);
        base = new ExtendedModelRenderer(this, 10, 64);
        base.addCuboid(0F, 0F, 0F, 16, 2, 16, false);
        base.setPivot(-8F, 22F, -8F);
        base.setTextureSize(128, 128);
        base.mirror = true;
        setRotation(base, 0F, 0F, 0F);
        wire = new ExtendedModelRenderer(this, 74, 0);
        wire.addCuboid(-1F, 0F, -1.1F, 2, 65, 2, false);
        wire.setPivot(0F, -46F, -1.5F);
        wire.setTextureSize(128, 128);
        wire.mirror = true;
        setRotation(wire, -0.0349066F, 0F, 0F);
        rearPlate1 = new ExtendedModelRenderer(this, 20, 16);
        rearPlate1.addCuboid(-2.5F, -6F, 0F, 5, 6, 3, false);
        rearPlate1.setPivot(0F, -44.5F, 4F);
        rearPlate1.setTextureSize(128, 128);
        rearPlate1.mirror = true;
        setRotation(rearPlate1, 0.122173F, 0F, 0F);
        rearPlate2 = new ExtendedModelRenderer(this, 36, 16);
        rearPlate2.addCuboid(-1.5F, -5F, -1F, 3, 5, 2, false);
        rearPlate2.setPivot(0F, -45F, 7F);
        rearPlate2.setTextureSize(128, 128);
        rearPlate2.mirror = true;
        setRotation(rearPlate2, 0.2094395F, 0F, 0F);
        blade1a = new ExtendedModelRenderer(this, 20, 32);
        blade1a.addCuboid(-1F, -32F, 0F, 2, 32, 1, false);
        blade1a.setPivot(0F, -48F, -5.99F);
        blade1a.setTextureSize(128, 128);
        blade1a.mirror = true;
        setRotation(blade1a, 0F, 0F, 0F);
        blade2a = new ExtendedModelRenderer(this, 20, 32);
        blade2a.addCuboid(-1F, 0F, 0F, 2, 32, 1, false);
        blade2a.setPivot(0F, -48F, -6F);
        blade2a.setTextureSize(128, 128);
        blade2a.mirror = true;
        setRotation(blade2a, 0F, 0F, 1.047198F);
        blade3a = new ExtendedModelRenderer(this, 20, 32);
        blade3a.addCuboid(-1F, 0F, 0F, 2, 32, 1, false);
        blade3a.setPivot(0F, -48F, -6F);
        blade3a.setTextureSize(128, 128);
        blade3a.mirror = true;
        setRotation(blade3a, 0F, 0F, -1.047198F);
        blade1b = new ExtendedModelRenderer(this, 26, 32);
        blade1b.addCuboid(-2F, -28F, 0F, 2, 28, 1, false);
        blade1b.setPivot(0F, -48F, -6F);
        blade1b.setTextureSize(128, 128);
        blade1b.mirror = true;
        setRotation(blade1b, 0F, 0F, 0.0349066F);
        blade2b = new ExtendedModelRenderer(this, 26, 32);
        blade2b.addCuboid(0F, 0F, 0F, 2, 28, 1, false);
        blade2b.setPivot(0F, -48F, -6.01F);
        blade2b.setTextureSize(128, 128);
        blade2b.mirror = true;
        setRotation(blade2b, 0F, 0F, 1.082104F);
        blade3b = new ExtendedModelRenderer(this, 26, 32);
        blade3b.addCuboid(0F, 0F, 0F, 2, 28, 1, false);
        blade3b.setPivot(0F, -48F, -6.01F);
        blade3b.setTextureSize(128, 128);
        blade3b.mirror = true;
        setRotation(blade3b, 0F, 0F, -1.012291F);
        post1a = new ExtendedModelRenderer(this, 0, 0);
        post1a.addCuboid(-2.5F, 0F, -2.5F, 5, 68, 5, false);
        post1a.setPivot(0F, -46F, 0F);
        post1a.setTextureSize(128, 128);
        post1a.mirror = true;
        setRotation(post1a, -0.0349066F, 0F, 0.0349066F);
        post1b = new ExtendedModelRenderer(this, 0, 0);
        post1b.addCuboid(-2.5F, 0F, -2.5F, 5, 68, 5, false);
        post1b.setPivot(0F, -46F, 0F);
        post1b.setTextureSize(128, 128);
        post1b.mirror = true;
        setRotation(post1b, 0.0349066F, 0F, -0.0349066F);
        post1c = new ExtendedModelRenderer(this, 0, 0);
        post1c.addCuboid(-2.5F, 0F, -2.5F, 5, 68, 5, false);
        post1c.setPivot(0F, -46F, 0F);
        post1c.setTextureSize(128, 128);
        post1c.mirror = true;
        setRotation(post1c, 0.0347321F, 0F, 0.0347321F);
        post1d = new ExtendedModelRenderer(this, 0, 0);
        post1d.addCuboid(-2.5F, 0F, -2.5F, 5, 68, 5, false);
        post1d.setPivot(0F, -46F, 0F);
        post1d.setTextureSize(128, 128);
        post1d.mirror = true;
        setRotation(post1d, -0.0347321F, 0F, -0.0347321F);
    }

    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, double angle, int light, int overlayLight, boolean hasEffect) {
        float baseRotation = getAbsoluteRotation(angle);
        setRotation(blade1a, 0F, 0F, baseRotation);
        setRotation(blade1b, 0F, 0F, 0.0349066F + baseRotation);

        float blade2Rotation = getAbsoluteRotation(angle - 60);
        setRotation(blade2a, 0F, 0F, blade2Rotation);
        setRotation(blade2b, 0F, 0F, 0.0349066F + blade2Rotation);

        float blade3Rotation = getAbsoluteRotation(angle + 60);
        setRotation(blade3a, 0F, 0F, blade3Rotation);
        setRotation(blade3b, 0F, 0F, 0.0349066F + blade3Rotation);

        setRotation(bladeCap, 0F, 0F, baseRotation);
        setRotation(bladeCenter, 0F, 0F, baseRotation);

        render(matrix, getVertexBuilder(renderer, RENDER_TYPE, hasEffect), light, overlayLight, 1, 1, 1, 1);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, false);
    }

    public void renderWireFrame(MatrixStack matrix, VertexConsumer vertexBuilder, double angle, float red, float green, float blue, float alpha) {
        float baseRotation = getAbsoluteRotation(angle);
        setRotation(blade1a, 0F, 0F, baseRotation);
        setRotation(blade1b, 0F, 0F, 0.0349066F + baseRotation);

        float blade2Rotation = getAbsoluteRotation(angle - 60);
        setRotation(blade2a, 0F, 0F, blade2Rotation);
        setRotation(blade2b, 0F, 0F, 0.0349066F + blade2Rotation);

        float blade3Rotation = getAbsoluteRotation(angle + 60);
        setRotation(blade3a, 0F, 0F, blade3Rotation);
        setRotation(blade3b, 0F, 0F, 0.0349066F + blade3Rotation);

        setRotation(bladeCap, 0F, 0F, baseRotation);
        setRotation(bladeCenter, 0F, 0F, baseRotation);
        render(matrix, vertexBuilder, MekanismRenderer.FULL_LIGHT, OverlayTexture.DEFAULT_UV, red, green, blue, alpha, true);
    }

    private void render(MatrixStack matrix, VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha, boolean wireFrame) {
        head.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        plateConnector2.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        plateConnector.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        plate.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        baseRim.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        base.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        wire.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        rearPlate1.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        rearPlate2.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        post1a.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        post1b.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        post1c.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        post1d.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);

        blade1a.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        blade2a.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        blade3a.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        blade1b.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        blade2b.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        blade3b.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);

        bladeCap.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
        bladeCenter.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, wireFrame);
    }

    private float getAbsoluteRotation(double angle) {
        return (float) Math.toRadians(angle % 360);
    }
}
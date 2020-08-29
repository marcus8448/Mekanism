package mekanism.client.model;

import javax.annotation.Nonnull;
import mekanism.client.render.MekanismRenderType;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ModelJetpack extends MekanismJavaModel {

    private static final Identifier JETPACK_TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "jetpack.png");
    private static final RenderLayer WING_RENDER_TYPE = MekanismRenderType.mekStandard(JETPACK_TEXTURE);
    private final RenderLayer frameRenderType;
    private final RenderLayer wingRenderType;

    private final ModelPart packTop;
    private final ModelPart packBottom;
    private final ModelPart thrusterLeft;
    private final ModelPart thrusterRight;
    private final ModelPart fuelTubeRight;
    private final ModelPart fuelTubeLeft;
    private final ModelPart packMid;
    private final ModelPart packCore;
    private final ModelPart wingSupportL;
    private final ModelPart wingSupportR;
    private final ModelPart packTopRear;
    private final ModelPart extendoSupportL;
    private final ModelPart extendoSupportR;
    private final ModelPart wingBladeL;
    private final ModelPart wingBladeR;
    private final ModelPart packDoodad2;
    private final ModelPart packDoodad3;
    private final ModelPart bottomThruster;
    private final ModelPart light1;
    private final ModelPart light2;
    private final ModelPart light3;

    public ModelJetpack() {
        this(JETPACK_TEXTURE, WING_RENDER_TYPE, -3);
    }

    /**
     * @param fuelZ Z offset for the Fuel Tubes, thrusters are offset by {@code fuelZ - 0.5}
     */
    protected ModelJetpack(Identifier texture, RenderLayer wingRenderType, float fuelZ) {
        super(RenderLayer::getEntitySolid);
        this.frameRenderType = getLayer(texture);
        this.wingRenderType = wingRenderType;
        textureWidth = 128;
        textureHeight = 64;

        packTop = new ModelPart(this, 92, 28);
        packTop.addCuboid(-4F, 0F, 4F, 8, 4, 1, false);
        packTop.setPivot(0F, 0F, 0F);
        packTop.setTextureSize(128, 64);
        packTop.mirror = true;
        setRotation(packTop, 0.2094395F, 0F, 0F);
        packBottom = new ModelPart(this, 92, 42);
        packBottom.addCuboid(-4F, 4.1F, 1.5F, 8, 4, 4, false);
        packBottom.setPivot(0F, 0F, 0F);
        packBottom.setTextureSize(128, 64);
        packBottom.mirror = true;
        setRotation(packBottom, -0.0872665F, 0F, 0F);
        thrusterLeft = new ModelPart(this, 69, 30);
        thrusterLeft.addCuboid(7.8F, 1.5F, fuelZ - 0.5F, 3, 3, 3, false);
        thrusterLeft.setPivot(0F, 0F, 0F);
        thrusterLeft.setTextureSize(128, 64);
        thrusterLeft.mirror = true;
        setRotation(thrusterLeft, 0.7853982F, -0.715585F, 0.3490659F);
        thrusterRight = new ModelPart(this, 69, 30);
        thrusterRight.addCuboid(-10.8F, 1.5F, fuelZ - 0.5F, 3, 3, 3, false);
        thrusterRight.setPivot(0F, 0F, 0F);
        thrusterRight.setTextureSize(128, 64);
        thrusterRight.mirror = true;
        setRotation(thrusterRight, 0.7853982F, 0.715585F, -0.3490659F);
        fuelTubeRight = new ModelPart(this, 92, 23);
        fuelTubeRight.addCuboid(-11.2F, 2F, fuelZ, 8, 2, 2, false);
        fuelTubeRight.setPivot(0F, 0F, 0F);
        fuelTubeRight.setTextureSize(128, 64);
        fuelTubeRight.mirror = true;
        setRotation(fuelTubeRight, 0.7853982F, 0.715585F, -0.3490659F);
        fuelTubeLeft = new ModelPart(this, 92, 23);
        fuelTubeLeft.addCuboid(3.2F, 2F, fuelZ, 8, 2, 2, false);
        fuelTubeLeft.setPivot(0F, 0F, 0F);
        fuelTubeLeft.setTextureSize(128, 64);
        fuelTubeLeft.mirror = true;
        setRotation(fuelTubeLeft, 0.7853982F, -0.715585F, 0.3490659F);
        packMid = new ModelPart(this, 92, 34);
        packMid.addCuboid(-4F, 3.3F, 1.5F, 8, 1, 4, false);
        packMid.setPivot(0F, 0F, 0F);
        packMid.setTextureSize(128, 64);
        packMid.mirror = true;
        setRotation(packMid, 0F, 0F, 0F);
        packCore = new ModelPart(this, 69, 2);
        packCore.addCuboid(-3.5F, 3F, 2F, 7, 1, 3, false);
        packCore.setPivot(0F, 0F, 0F);
        packCore.setTextureSize(128, 64);
        packCore.mirror = true;
        setRotation(packCore, 0F, 0F, 0F);
        wingSupportL = new ModelPart(this, 71, 55);
        wingSupportL.addCuboid(3F, -1F, 2.2F, 7, 2, 2, false);
        wingSupportL.setPivot(0F, 0F, 0F);
        wingSupportL.setTextureSize(128, 64);
        wingSupportL.mirror = true;
        setRotation(wingSupportL, 0F, 0F, 0.2792527F);
        wingSupportR = new ModelPart(this, 71, 55);
        wingSupportR.addCuboid(-10F, -1F, 2.2F, 7, 2, 2, false);
        wingSupportR.setPivot(0F, 0F, 0F);
        wingSupportR.setTextureSize(128, 64);
        wingSupportR.mirror = true;
        setRotation(wingSupportR, 0F, 0F, -0.2792527F);
        packTopRear = new ModelPart(this, 106, 28);
        packTopRear.addCuboid(-4F, 1F, 1F, 8, 3, 3, false);
        packTopRear.setPivot(0F, 0F, 0F);
        packTopRear.setTextureSize(128, 64);
        packTopRear.mirror = true;
        setRotation(packTopRear, 0.2094395F, 0F, 0F);
        extendoSupportL = new ModelPart(this, 94, 16);
        extendoSupportL.addCuboid(8F, -0.2F, 2.5F, 9, 1, 1, false);
        extendoSupportL.setPivot(0F, 0F, 0F);
        extendoSupportL.setTextureSize(128, 64);
        extendoSupportL.mirror = true;
        setRotation(extendoSupportL, 0F, 0F, 0.2792527F);
        extendoSupportR = new ModelPart(this, 94, 16);
        extendoSupportR.addCuboid(-17F, -0.2F, 2.5F, 9, 1, 1, false);
        extendoSupportR.setPivot(0F, 0F, 0F);
        extendoSupportR.setTextureSize(128, 64);
        extendoSupportR.mirror = true;
        setRotation(extendoSupportR, 0F, 0F, -0.2792527F);
        wingBladeL = new ModelPart(this, 62, 5);
        wingBladeL.addCuboid(3.3F, 1.1F, 3F, 14, 2, 0, false);
        wingBladeL.setPivot(0F, 0F, 0F);
        wingBladeL.setTextureSize(128, 64);
        wingBladeL.mirror = true;
        setRotation(wingBladeL, 0F, 0F, 0.2094395F);
        wingBladeR = new ModelPart(this, 62, 5);
        wingBladeR.addCuboid(-17.3F, 1.1F, 3F, 14, 2, 0, false);
        wingBladeR.setPivot(0F, 0F, 0F);
        wingBladeR.setTextureSize(128, 64);
        wingBladeR.mirror = true;
        setRotation(wingBladeR, 0F, 0F, -0.2094395F);
        packDoodad2 = new ModelPart(this, 116, 0);
        packDoodad2.addCuboid(1F, 0.5F, 4.2F, 2, 1, 1, false);
        packDoodad2.setPivot(0F, 0F, 0F);
        packDoodad2.setTextureSize(128, 64);
        packDoodad2.mirror = true;
        setRotation(packDoodad2, 0.2094395F, 0F, 0F);
        packDoodad3 = new ModelPart(this, 116, 0);
        packDoodad3.addCuboid(1F, 2F, 4.2F, 2, 1, 1, false);
        packDoodad3.setPivot(0F, 0F, 0F);
        packDoodad3.setTextureSize(128, 64);
        packDoodad3.mirror = true;
        setRotation(packDoodad3, 0.2094395F, 0F, 0F);
        bottomThruster = new ModelPart(this, 68, 26);
        bottomThruster.addCuboid(-3F, 8F, 2.333333F, 6, 1, 2, false);
        bottomThruster.setPivot(0F, 0F, 0F);
        bottomThruster.setTextureSize(128, 64);
        bottomThruster.mirror = true;
        setRotation(bottomThruster, 0F, 0F, 0F);
        light1 = new ModelPart(this, 55, 2);
        light1.addCuboid(2F, 6.55F, 4F, 1, 1, 1, false);
        light1.setPivot(0F, 0F, 0F);
        light1.setTextureSize(128, 64);
        light1.mirror = true;
        setRotation(light1, 0F, 0F, 0F);
        light2 = new ModelPart(this, 55, 2);
        light2.addCuboid(0F, 6.55F, 4F, 1, 1, 1, false);
        light2.setPivot(0F, 0F, 0F);
        light2.setTextureSize(128, 64);
        light2.mirror = true;
        setRotation(light2, 0F, 0F, 0F);
        light3 = new ModelPart(this, 55, 2);
        light3.addCuboid(-3F, 6.55F, 4F, 1, 1, 1, false);
        light3.setPivot(0F, 0F, 0F);
        light3.setTextureSize(128, 64);
        light3.mirror = true;
        setRotation(light3, 0F, 0F, 0F);
    }

    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, boolean hasEffect) {
        render(matrix, getVertexBuilder(renderer, frameRenderType, hasEffect), light, overlayLight, 1, 1, 1, 1);
        renderWings(matrix, getVertexBuilder(renderer, wingRenderType, hasEffect), MekanismRenderer.FULL_LIGHT, overlayLight, 1, 1, 1, 0.2F);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        packTop.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        packBottom.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        thrusterLeft.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        thrusterRight.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        fuelTubeRight.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        fuelTubeLeft.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        packMid.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        wingSupportL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        wingSupportR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        packTopRear.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        extendoSupportL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        extendoSupportR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        packDoodad2.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        packDoodad3.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        bottomThruster.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);

        //Stuff below here uses full bright for the lighting
        light = MekanismRenderer.FULL_LIGHT;
        light1.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        light2.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        light3.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        packCore.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }

    public void renderWings(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        wingBladeL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        wingBladeR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }
}
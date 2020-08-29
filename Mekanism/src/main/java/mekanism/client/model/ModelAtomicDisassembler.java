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

public class ModelAtomicDisassembler extends MekanismJavaModel {

    private static final Identifier DISASSEMBLER_TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "atomic_disassembler.png");
    private static final RenderLayer BLADE_RENDER_TYPE = MekanismRenderType.bladeRender(DISASSEMBLER_TEXTURE);
    private final RenderLayer RENDER_TYPE = getLayer(DISASSEMBLER_TEXTURE);

    private final ModelPart handle;
    private final ModelPart handleTop;
    private final ModelPart bladeBack;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart bladeFrontUpper;
    private final ModelPart bladeFrontLower;
    private final ModelPart neckAngled;
    private final ModelPart bladeFrontConnector;
    private final ModelPart bladeHolderBack;
    private final ModelPart bladeHolderMain;
    private final ModelPart bladeHolderFront;
    private final ModelPart rearBar;
    private final ModelPart bladeBackSmall;
    private final ModelPart handleBase;
    private final ModelPart handleTopBack;

    public ModelAtomicDisassembler() {
        super(RenderLayer::getEntitySolid);
        textureWidth = 64;
        textureHeight = 32;

        handle = new ModelPart(this, 0, 10);
        handle.addCuboid(0F, -1F, -3F, 1, 16, 1, false);
        handle.setPivot(0F, 0F, 0F);
        handle.setTextureSize(64, 32);
        handle.mirror = true;
        setRotation(handle, 0F, 0F, 0F);
        handleTop = new ModelPart(this, 34, 9);
        handleTop.addCuboid(-0.5F, -3.5F, -3.5F, 2, 5, 2, false);
        handleTop.setPivot(0F, 0F, 0F);
        handleTop.setTextureSize(64, 32);
        handleTop.mirror = true;
        setRotation(handleTop, 0F, 0F, 0F);
        bladeBack = new ModelPart(this, 42, 0);
        bladeBack.addCuboid(0F, -4F, -4F, 1, 2, 10, false);
        bladeBack.setPivot(0F, 0F, 0F);
        bladeBack.setTextureSize(64, 32);
        bladeBack.mirror = true;
        setRotation(bladeBack, 0F, 0F, 0F);
        head = new ModelPart(this, 24, 0);
        head.addCuboid(-5F, -5.7F, -5.5F, 3, 3, 6, false);
        head.setPivot(0F, 0F, 0F);
        head.setTextureSize(64, 32);
        head.mirror = true;
        setRotation(head, 0F, 0F, 0.7853982F);
        neck = new ModelPart(this, 0, 0);
        neck.addCuboid(-0.5F, -6F, -7F, 2, 2, 8, false);
        neck.setPivot(0F, 0F, 0F);
        neck.setTextureSize(64, 32);
        neck.mirror = true;
        setRotation(neck, 0F, 0F, 0F);
        bladeFrontUpper = new ModelPart(this, 60, 0);
        bladeFrontUpper.addCuboid(0F, -0.5333334F, -9.6F, 1, 3, 1, false);
        bladeFrontUpper.setPivot(0F, 0F, 0F);
        bladeFrontUpper.setTextureSize(64, 32);
        bladeFrontUpper.mirror = true;
        setRotation(bladeFrontUpper, -0.7853982F, 0F, 0F);
        bladeFrontLower = new ModelPart(this, 58, 0);
        bladeFrontLower.addCuboid(0F, -9.58F, -4F, 1, 5, 2, false);
        bladeFrontLower.setPivot(0F, 0F, 0F);
        bladeFrontLower.setTextureSize(64, 32);
        bladeFrontLower.mirror = true;
        setRotation(bladeFrontLower, 0.7853982F, 0F, 0F);
        neckAngled = new ModelPart(this, 12, 0);
        neckAngled.addCuboid(-0.5F, -8.2F, -2.5F, 2, 1, 1, false);
        neckAngled.setPivot(0F, 0F, 0F);
        neckAngled.setTextureSize(64, 32);
        neckAngled.mirror = true;
        setRotation(neckAngled, 0.7853982F, 0F, 0F);
        bladeFrontConnector = new ModelPart(this, 56, 0);
        bladeFrontConnector.addCuboid(0F, -2.44F, -6.07F, 1, 2, 3, false);
        bladeFrontConnector.setPivot(0F, 0F, 0F);
        bladeFrontConnector.setTextureSize(64, 32);
        bladeFrontConnector.mirror = true;
        setRotation(bladeFrontConnector, 0F, 0F, 0F);
        bladeHolderBack = new ModelPart(this, 42, 14);
        bladeHolderBack.addCuboid(-0.5F, -0.5F, 3.5F, 2, 1, 1, false);
        bladeHolderBack.setPivot(0F, -4F, 0F);
        bladeHolderBack.setTextureSize(64, 32);
        bladeHolderBack.mirror = true;
        setRotation(bladeHolderBack, 0F, 0F, 0F);
        bladeHolderMain = new ModelPart(this, 30, 16);
        bladeHolderMain.addCuboid(-0.5F, -3.5F, -1.5F, 2, 1, 4, false);
        bladeHolderMain.setPivot(0F, 0F, 0F);
        bladeHolderMain.setTextureSize(64, 32);
        bladeHolderMain.mirror = true;
        setRotation(bladeHolderMain, 0F, 0F, 0F);
        bladeHolderFront = new ModelPart(this, 42, 12);
        bladeHolderFront.addCuboid(-0.5F, -4.5F, 1.5F, 2, 1, 1, false);
        bladeHolderFront.setPivot(0F, 0F, 0F);
        bladeHolderFront.setTextureSize(64, 32);
        bladeHolderFront.mirror = true;
        setRotation(bladeHolderFront, 0F, 0F, 0F);
        rearBar = new ModelPart(this, 4, 10);
        rearBar.addCuboid(0F, -5.3F, 0F, 1, 1, 7, false);
        rearBar.setPivot(0F, 0F, 0F);
        rearBar.setTextureSize(64, 32);
        rearBar.mirror = true;
        setRotation(rearBar, 0F, 0F, 0F);
        bladeBackSmall = new ModelPart(this, 60, 0);
        bladeBackSmall.addCuboid(0F, -4F, 6F, 1, 1, 1, false);
        bladeBackSmall.setPivot(0F, 0F, 0F);
        bladeBackSmall.setTextureSize(64, 32);
        bladeBackSmall.mirror = true;
        setRotation(bladeBackSmall, 0F, 0F, 0F);
        handleBase = new ModelPart(this, 26, 9);
        handleBase.addCuboid(-0.5F, 15F, -3.5F, 2, 4, 2, false);
        handleBase.setPivot(0F, 0F, 0F);
        handleBase.setTextureSize(64, 32);
        handleBase.mirror = true;
        setRotation(handleBase, 0F, 0F, 0F);
        handleTopBack = new ModelPart(this, 37, 0);
        handleTopBack.addCuboid(0F, -2F, -2F, 1, 4, 1, false);
        handleTopBack.setPivot(0F, 0F, 0F);
        handleTopBack.setTextureSize(64, 32);
        handleTopBack.mirror = true;
        setRotation(handleTopBack, 0F, 0F, 0F);
    }

    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, boolean hasEffect) {
        render(matrix, getVertexBuilder(renderer, RENDER_TYPE, hasEffect), light, overlayLight, 1, 1, 1, 1);
        renderBlade(matrix, getVertexBuilder(renderer, BLADE_RENDER_TYPE, hasEffect), MekanismRenderer.FULL_LIGHT, overlayLight, 1, 1, 1, 0.75F);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        handle.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        handleTop.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        head.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        neck.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        rearBar.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        neckAngled.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        bladeHolderBack.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        bladeHolderMain.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        bladeHolderFront.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        handleBase.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        handleTopBack.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }

    private void renderBlade(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        bladeFrontConnector.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        bladeBack.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        bladeFrontUpper.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        bladeFrontLower.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        bladeBackSmall.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }
}
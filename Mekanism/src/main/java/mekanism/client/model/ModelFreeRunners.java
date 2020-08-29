package mekanism.client.model;

import javax.annotation.Nonnull;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ModelFreeRunners extends MekanismJavaModel {

    private static final Identifier FREE_RUNNER_TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "free_runners.png");
    private final RenderLayer RENDER_TYPE = getLayer(FREE_RUNNER_TEXTURE);

    private final ModelPart SpringL;
    private final ModelPart SpringR;
    private final ModelPart BraceL;
    private final ModelPart BraceR;
    private final ModelPart SupportL;
    private final ModelPart SupportR;

    public ModelFreeRunners() {
        super(RenderLayer::getEntitySolid);
        textureWidth = 64;
        textureHeight = 32;

        SpringL = new ModelPart(this, 8, 0);
        SpringL.addCuboid(1.5F, 18F, 0F, 1, 6, 1, false);
        SpringL.setPivot(0F, 0F, 0F);
        SpringL.setTextureSize(64, 32);
        SpringL.mirror = true;
        setRotation(SpringL, 0.1047198F, 0F, 0F);
        SpringR = new ModelPart(this, 8, 0);
        SpringR.addCuboid(-2.5F, 18F, 0F, 1, 6, 1, false);
        SpringR.setPivot(0F, 0F, 0F);
        SpringR.setTextureSize(64, 32);
        SpringR.mirror = true;
        setRotation(SpringR, 0.1047198F, 0F, 0F);
        SpringR.mirror = false;
        BraceL = new ModelPart(this, 12, 0);
        BraceL.addCuboid(0.2F, 18F, -0.8F, 4, 2, 3, false);
        BraceL.setPivot(0F, 0F, 0F);
        BraceL.setTextureSize(64, 32);
        BraceL.mirror = true;
        setRotation(BraceL, 0F, 0F, 0F);
        BraceR = new ModelPart(this, 12, 0);
        BraceR.addCuboid(-4.2F, 18F, -0.8F, 4, 2, 3, false);
        BraceR.setPivot(0F, 0F, 0F);
        BraceR.setTextureSize(64, 32);
        BraceR.mirror = true;
        setRotation(BraceR, 0F, 0F, 0F);
        SupportL = new ModelPart(this, 0, 0);
        SupportL.addCuboid(1F, 16.5F, -4.2F, 2, 4, 2, false);
        SupportL.setPivot(0F, 0F, 0F);
        SupportL.setTextureSize(64, 32);
        SupportL.mirror = true;
        setRotation(SupportL, 0.296706F, 0F, 0F);
        SupportR = new ModelPart(this, 0, 0);
        SupportR.addCuboid(-3F, 16.5F, -4.2F, 2, 4, 2, false);
        SupportR.setPivot(0F, 0F, 0F);
        SupportR.setTextureSize(64, 32);
        SupportR.mirror = true;
        setRotation(SupportR, 0.296706F, 0F, 0F);
        SupportR.mirror = false;
    }

    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, boolean hasEffect) {
        render(matrix, getVertexBuilder(renderer, RENDER_TYPE, hasEffect), light, overlayLight, 1, 1, 1, 1);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        SpringL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        BraceL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        SupportL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        SpringR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        BraceR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        SupportR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }

    public void renderLeg(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, boolean hasEffect, boolean left) {
        VertexConsumer vertexBuilder = getVertexBuilder(renderer, RENDER_TYPE, hasEffect);
        float red = 1;
        float green = 1;
        float blue = 1;
        float alpha = 1;
        if (left) {
            SpringL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
            BraceL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
            SupportL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        } else {
            SpringR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
            BraceR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
            SupportR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        }
    }
}
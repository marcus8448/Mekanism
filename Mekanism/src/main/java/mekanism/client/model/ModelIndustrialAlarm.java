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

public class ModelIndustrialAlarm extends MekanismJavaModel {

    private static final Identifier TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "industrial_alarm.png");
    private static final Identifier TEXTURE_ACTIVE = MekanismUtils.getResource(ResourceType.RENDER, "industrial_alarm_active.png");
    private final RenderLayer RENDER_TYPE = MekanismRenderType.mekStandard(TEXTURE);
    private final RenderLayer RENDER_TYPE_ACTIVE = MekanismRenderType.mekStandard(TEXTURE_ACTIVE);

    private final ModelPart base;
    private final ModelPart bulb;
    private final ModelPart light_box;
    private final ModelPart aura;

    public ModelIndustrialAlarm() {
        super(RenderLayer::getEntitySolid);
        textureWidth = 64;
        textureHeight = 64;

        base = new ModelPart(this, 0, 9);
        base.addCuboid(-3F, 0F, -3F, 6, 1, 6);
        base.setPivot(0F, 0F, 0F);
        base.setTextureSize(64, 64);
        setRotation(base, 0F, 0F, 0F);
        bulb = new ModelPart(this, 16, 0);
        bulb.addCuboid(-1F, 1F, -1F, 2, 3, 2);
        bulb.setPivot(0F, 0F, 0F);
        bulb.setTextureSize(64, 64);
        setRotation(bulb, 0F, 0F, 0F);
        light_box = new ModelPart(this, 0, 0);
        light_box.addCuboid(-2F, 1F, -2F, 4, 4, 4);
        light_box.setPivot(0F, 0F, 0F);
        light_box.setTextureSize(64, 64);
        setRotation(light_box, 0F, 0F, 0F);
        aura = new ModelPart(this, 0, 16);
        aura.addCuboid(-6F, 2F, -1F, 12, 1, 2);
        aura.setPivot(0F, 0F, 0F);
        aura.setTextureSize(64, 64);
        setRotation(aura, 0F, 0F, 0F);
    }

    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, boolean active, float rotation, boolean renderBase,
          boolean hasEffect) {
        render(matrix, getVertexBuilder(renderer, active ? RENDER_TYPE_ACTIVE : RENDER_TYPE, hasEffect), light, overlayLight, 1, 1, 1, 1,
              active, rotation, renderBase);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue,
          float alpha) {
        render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha, false, 0, false);
    }

    private void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha,
          boolean active, float rotation, boolean renderBase) {
        if (renderBase) {
            base.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        }
        if (active) {
            setRotation(aura, 0, (float) Math.toRadians(rotation), 0);
            setRotation(bulb, 0, (float) Math.toRadians(rotation), 0);
        } else {
            setRotation(aura, 0, 0, 0);
            setRotation(bulb, 0, 0, 0);
        }
        float bulbAlpha = 0.3F + (Math.abs(((rotation * 2) % 360) - 180F) / 180F) * 0.7F;
        bulb.render(matrix, vertexBuilder, active ? MekanismRenderer.FULL_LIGHT : light, overlayLight, red, green, blue, bulbAlpha);
        light_box.render(matrix, vertexBuilder, active ? MekanismRenderer.FULL_LIGHT : light, overlayLight, red, green, blue, alpha);
        if (!renderBase) {
            aura.render(matrix, vertexBuilder, MekanismRenderer.FULL_LIGHT, overlayLight, red, green, blue, bulbAlpha);
        }
    }
}

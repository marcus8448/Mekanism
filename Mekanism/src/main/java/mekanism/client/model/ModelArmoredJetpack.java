package mekanism.client.model;

import javax.annotation.Nonnull;
import mekanism.client.render.MekanismRenderType;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ModelArmoredJetpack extends ModelJetpack {

    private static final Identifier JETPACK_TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "jetpack.png");
    private static final RenderLayer WING_RENDER_TYPE = MekanismRenderType.mekStandard(JETPACK_TEXTURE);

    private final ModelPart chestplate;
    private final ModelPart leftGuardTop;
    private final ModelPart rightGuardTop;
    private final ModelPart middlePlate;
    private final ModelPart rightGuardBot;
    private final ModelPart leftGuardBot;
    private final ModelPart rightLight;
    private final ModelPart leftLight;

    public ModelArmoredJetpack() {
        super(JETPACK_TEXTURE, WING_RENDER_TYPE, -1.9F);
        chestplate = new ModelPart(this, 104, 22);
        chestplate.addCuboid(-4F, 1.333333F, -3F, 8, 4, 3, false);
        chestplate.setPivot(0F, 0F, 0F);
        chestplate.setTextureSize(128, 64);
        chestplate.mirror = true;
        setRotation(chestplate, -0.3665191F, 0F, 0F);
        leftGuardTop = new ModelPart(this, 87, 31);
        leftGuardTop.addCuboid(0.95F, 3F, -5F, 3, 4, 2, false);
        leftGuardTop.setPivot(0F, 0F, 0F);
        leftGuardTop.setTextureSize(128, 64);
        leftGuardTop.mirror = true;
        setRotation(leftGuardTop, 0.2094395F, 0F, 0F);
        leftGuardTop.mirror = false;
        rightGuardTop = new ModelPart(this, 87, 31);
        rightGuardTop.addCuboid(-3.95F, 3F, -5F, 3, 4, 2, false);
        rightGuardTop.setPivot(0F, 0F, 0F);
        rightGuardTop.setTextureSize(128, 64);
        rightGuardTop.mirror = true;
        setRotation(rightGuardTop, 0.2094395F, 0F, 0F);
        middlePlate = new ModelPart(this, 93, 20);
        middlePlate.addCuboid(-1.5F, 3F, -6.2F, 3, 5, 3, false);
        middlePlate.setPivot(0F, 0F, 0F);
        middlePlate.setTextureSize(128, 64);
        middlePlate.mirror = true;
        setRotation(middlePlate, 0.2094395F, 0F, 0F);
        middlePlate.mirror = false;
        rightGuardBot = new ModelPart(this, 84, 30);
        rightGuardBot.addCuboid(-3.5F, 5.5F, -6.5F, 2, 2, 2, false);
        rightGuardBot.setPivot(0F, 0F, 0F);
        rightGuardBot.setTextureSize(128, 64);
        rightGuardBot.mirror = true;
        setRotation(rightGuardBot, 0.4712389F, 0F, 0F);
        rightGuardBot.mirror = false;
        leftGuardBot = new ModelPart(this, 84, 30);
        leftGuardBot.addCuboid(1.5F, 5.5F, -6.5F, 2, 2, 2, false);
        leftGuardBot.setPivot(0F, 0F, 0F);
        leftGuardBot.setTextureSize(128, 64);
        leftGuardBot.mirror = true;
        setRotation(leftGuardBot, 0.4712389F, 0F, 0F);
        rightLight = new ModelPart(this, 81, 0);
        rightLight.addCuboid(-3F, 4F, -4.5F, 1, 3, 1, false);
        rightLight.setPivot(0F, 0F, 0F);
        rightLight.setTextureSize(128, 64);
        rightLight.mirror = true;
        setRotation(rightLight, 0F, 0F, 0F);
        leftLight = new ModelPart(this, 81, 0);
        leftLight.addCuboid(2F, 4F, -4.5F, 1, 3, 1, false);
        leftLight.setPivot(0F, 0F, 0F);
        leftLight.setTextureSize(128, 64);
        leftLight.mirror = true;
        setRotation(leftLight, 0F, 0F, 0F);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        super.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        matrix.push();
        matrix.translate(0, 0, -0.0625);
        chestplate.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        leftGuardTop.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        rightGuardTop.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        middlePlate.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        rightGuardBot.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        leftGuardBot.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        //Stuff below here uses full bright for the lighting
        light = MekanismRenderer.FULL_LIGHT;
        rightLight.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        leftLight.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        matrix.pop();
    }
}
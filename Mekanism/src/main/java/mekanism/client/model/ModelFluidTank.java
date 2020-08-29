package mekanism.client.model;

import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.common.tier.FluidTankTier;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

//TODO: Replace usage of this by using the json model and drawing fluid inside of it?
public class ModelFluidTank extends MekanismJavaModel {

    private static final Identifier TANK_TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "fluid_tank.png");
    private static final RenderLayer GLASS_RENDER_TYPE = RenderLayer.getEntityCutout(TANK_TEXTURE);
    private final RenderLayer RENDER_TYPE = getLayer(TANK_TEXTURE);

    private final ModelPart Base;
    private final ModelPart PoleFL;
    private final ModelPart PoleLB;
    private final ModelPart PoleBR;
    private final ModelPart PoleRF;
    private final ModelPart Top;
    private final ModelPart FrontGlass;
    private final ModelPart BackGlass;
    private final ModelPart RightGlass;
    private final ModelPart LeftGlass;

    public ModelFluidTank() {
        super(RenderLayer::getEntitySolid);
        textureWidth = 128;
        textureHeight = 128;

        Base = new ModelPart(this, 0, 0);
        Base.addCuboid(0F, 0F, 0F, 12, 1, 12, false);
        Base.setPivot(-6F, 23F, -6F);
        Base.setTextureSize(128, 128);
        Base.mirror = true;
        setRotation(Base, 0F, 0F, 0F);
        PoleFL = new ModelPart(this, 48, 0);
        PoleFL.addCuboid(0F, 0F, 0F, 1, 14, 1, false);
        PoleFL.setPivot(5F, 9F, -6F);
        PoleFL.setTextureSize(128, 128);
        PoleFL.mirror = true;
        setRotation(PoleFL, 0F, 0F, 0F);
        PoleLB = new ModelPart(this, 48, 0);
        PoleLB.addCuboid(0F, 0F, 0F, 1, 14, 1, false);
        PoleLB.setPivot(5F, 9F, 5F);
        PoleLB.setTextureSize(128, 128);
        PoleLB.mirror = true;
        setRotation(PoleLB, 0F, 0F, 0F);
        PoleBR = new ModelPart(this, 48, 0);
        PoleBR.addCuboid(0F, 0F, 0F, 1, 14, 1, false);
        PoleBR.setPivot(-6F, 9F, 5F);
        PoleBR.setTextureSize(128, 128);
        PoleBR.mirror = true;
        setRotation(PoleBR, 0F, 0F, 0F);
        PoleRF = new ModelPart(this, 48, 0);
        PoleRF.addCuboid(0F, 0F, 0F, 1, 14, 1, false);
        PoleRF.setPivot(-6F, 9F, -6F);
        PoleRF.setTextureSize(128, 128);
        PoleRF.mirror = true;
        setRotation(PoleRF, 0F, 0F, 0F);
        Top = new ModelPart(this, 0, 0);
        Top.addCuboid(0F, 0F, 0F, 12, 1, 12, false);
        Top.setPivot(-6F, 8F, -6F);
        Top.setTextureSize(128, 128);
        Top.mirror = true;
        setRotation(Top, 0F, 0F, 0F);
        FrontGlass = new ModelPart(this, 0, 13);
        FrontGlass.addCuboid(0F, 0F, 0F, 10, 14, 1, false);
        FrontGlass.setPivot(-5F, 9F, -6F);
        FrontGlass.setTextureSize(128, 128);
        FrontGlass.mirror = true;
        setRotation(FrontGlass, 0F, 0F, 0F);
        BackGlass = new ModelPart(this, 0, 28);
        BackGlass.addCuboid(0F, 0F, 3F, 10, 14, 1, false);
        BackGlass.setPivot(-5F, 9F, 2F);
        BackGlass.setTextureSize(128, 128);
        BackGlass.mirror = true;
        setRotation(BackGlass, 0F, 0F, 0F);
        RightGlass = new ModelPart(this, 22, 13);
        RightGlass.addCuboid(0F, 0F, 0F, 1, 14, 10, false);
        RightGlass.setPivot(-6F, 9F, -5F);
        RightGlass.setTextureSize(128, 128);
        RightGlass.mirror = true;
        setRotation(RightGlass, 0F, 0F, 0F);
        LeftGlass = new ModelPart(this, 22, 37);
        LeftGlass.addCuboid(0F, 0F, 0F, 1, 14, 10, false);
        LeftGlass.setPivot(5F, 9F, -5F);
        LeftGlass.setTextureSize(128, 128);
        LeftGlass.mirror = true;
        setRotation(LeftGlass, 0F, 0F, 0F);
    }

    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, FluidTankTier tier, boolean hasEffect) {
        render(matrix, getVertexBuilder(renderer, RENDER_TYPE, hasEffect), light, overlayLight, 1, 1, 1, 1);
        EnumColor color = tier.getBaseTier().getColor();
        //TODO: Try to make it so the lines can still show up on the back walls of the tank in first person
        renderGlass(matrix, getVertexBuilder(renderer, GLASS_RENDER_TYPE, hasEffect), light, overlayLight, color.getColor(0), color.getColor(1), color.getColor(2), 1);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        Base.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        PoleFL.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        PoleLB.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        PoleBR.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        PoleRF.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Top.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }

    private void renderGlass(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        FrontGlass.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        BackGlass.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        RightGlass.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        LeftGlass.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }
}
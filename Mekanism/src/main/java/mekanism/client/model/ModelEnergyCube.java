package mekanism.client.model;

import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.api.RelativeSide;
import mekanism.api.text.EnumColor;
import mekanism.client.render.MekanismRenderType;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tier.EnergyCubeTier;
import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ISlotInfo;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.util.Constants.NBT;

public class ModelEnergyCube extends MekanismJavaModel {

    private static final Identifier CUBE_TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "energy_cube.png");
    private static final Identifier OVERLAY_ON = MekanismUtils.getResource(ResourceType.RENDER, "energy_cube_overlay_on.png");
    private static final Identifier OVERLAY_OFF = MekanismUtils.getResource(ResourceType.RENDER, "energy_cube_overlay_off.png");
    private static final Identifier BASE_OVERLAY = MekanismUtils.getResource(ResourceType.RENDER, "energy_cube_overlay_base.png");
    private static final RenderLayer RENDER_TYPE_ON = MekanismRenderType.mekStandard(OVERLAY_ON);
    private static final RenderLayer RENDER_TYPE_OFF = MekanismRenderType.mekStandard(OVERLAY_OFF);
    private static final RenderLayer RENDER_TYPE_BASE = MekanismRenderType.mekStandard(BASE_OVERLAY);

    private final RenderLayer RENDER_TYPE = getLayer(CUBE_TEXTURE);

    private final ModelPart[] leds1;
    private final ModelPart[] leds2;
    private final ModelPart[] ports;
    private final ModelPart[] connectors;
    private final ModelPart frame12;
    private final ModelPart frame11;
    private final ModelPart frame10;
    private final ModelPart frame9;
    private final ModelPart frame8;
    private final ModelPart frame7;
    private final ModelPart frame6;
    private final ModelPart frame5;
    private final ModelPart frame4;
    private final ModelPart frame3;
    private final ModelPart frame2;
    private final ModelPart frame1;
    private final ModelPart corner8;
    private final ModelPart corner7;
    private final ModelPart corner6;
    private final ModelPart corner5;
    private final ModelPart corner4;
    private final ModelPart corner3;
    private final ModelPart corner2;
    private final ModelPart corner1;

    public ModelEnergyCube() {
        super(RenderLayer::getEntitySolid);
        textureWidth = 64;
        textureHeight = 64;

        frame12 = new ModelPart(this, 0, 0);
        frame12.addCuboid(0F, 0F, 0F, 3, 10, 3, false);
        frame12.setPivot(-8F, 11F, 5F);
        frame12.setTextureSize(64, 64);
        frame12.mirror = true;
        setRotation(frame12, 0F, 0F, 0F);
        frame11 = new ModelPart(this, 0, 0);
        frame11.addCuboid(0F, 0F, 0F, 3, 10, 3, false);
        frame11.setPivot(5F, 11F, -8F);
        frame11.setTextureSize(64, 64);
        frame11.mirror = true;
        setRotation(frame11, 0F, 0F, 0F);
        frame10 = new ModelPart(this, 0, 13);
        frame10.addCuboid(0F, 0F, 0F, 10, 3, 3, false);
        frame10.setPivot(-5F, 21F, 5F);
        frame10.setTextureSize(64, 64);
        frame10.mirror = true;
        setRotation(frame10, 0F, 0F, 0F);
        frame9 = new ModelPart(this, 12, 0);
        frame9.addCuboid(0F, 0F, 0F, 3, 3, 10, false);
        frame9.setPivot(5F, 21F, -5F);
        frame9.setTextureSize(64, 64);
        frame9.mirror = true;
        setRotation(frame9, 0F, 0F, 0F);
        frame8 = new ModelPart(this, 0, 13);
        frame8.addCuboid(0F, 0F, 0F, 10, 3, 3, false);
        frame8.setPivot(-5F, 8F, 5F);
        frame8.setTextureSize(64, 64);
        frame8.mirror = true;
        setRotation(frame8, 0F, 0F, 0F);
        frame7 = new ModelPart(this, 0, 13);
        frame7.addCuboid(0F, 0F, 0F, 10, 3, 3, false);
        frame7.setPivot(-5F, 21F, -8F);
        frame7.setTextureSize(64, 64);
        frame7.mirror = true;
        setRotation(frame7, 0F, 0F, 0F);
        frame6 = new ModelPart(this, 0, 0);
        frame6.addCuboid(0F, 0F, 0F, 3, 10, 3, false);
        frame6.setPivot(5F, 11F, 5F);
        frame6.setTextureSize(64, 64);
        frame6.mirror = true;
        setRotation(frame6, 0F, 0F, 0F);
        frame5 = new ModelPart(this, 0, 0);
        frame5.addCuboid(0F, 0F, 0F, 3, 10, 3, false);
        frame5.setPivot(-8F, 11F, -8F);
        frame5.setTextureSize(64, 64);
        frame5.mirror = true;
        setRotation(frame5, 0F, 0F, 0F);
        frame4 = new ModelPart(this, 12, 0);
        frame4.addCuboid(0F, 0F, 0F, 3, 3, 10, false);
        frame4.setPivot(5F, 8F, -5F);
        frame4.setTextureSize(64, 64);
        frame4.mirror = true;
        setRotation(frame4, 0F, 0F, 0F);
        frame3 = new ModelPart(this, 12, 0);
        frame3.addCuboid(0F, 0F, 0F, 3, 3, 10, false);
        frame3.setPivot(-8F, 21F, -5F);
        frame3.setTextureSize(64, 64);
        frame3.mirror = true;
        setRotation(frame3, 0F, 0F, 0F);
        frame2 = new ModelPart(this, 12, 0);
        frame2.addCuboid(0F, 0F, 0F, 3, 3, 10, false);
        frame2.setPivot(-8F, 8F, -5F);
        frame2.setTextureSize(64, 64);
        frame2.mirror = true;
        setRotation(frame2, 0F, 0F, 0F);
        frame1 = new ModelPart(this, 0, 13);
        frame1.addCuboid(0F, 0F, 0F, 10, 3, 3, false);
        frame1.setPivot(-5F, 8F, -8F);
        frame1.setTextureSize(64, 64);
        frame1.mirror = true;
        setRotation(frame1, 0F, 0F, 0F);
        corner8 = new ModelPart(this, 26, 13);
        corner8.addCuboid(0F, 0F, 0F, 3, 3, 3, false);
        corner8.setPivot(5F, 21F, 5F);
        corner8.setTextureSize(64, 64);
        corner8.mirror = true;
        setRotation(corner8, 0F, 0F, 0F);
        corner7 = new ModelPart(this, 26, 13);
        corner7.addCuboid(0F, 0F, 0F, 3, 3, 3, false);
        corner7.setPivot(5F, 21F, -8F);
        corner7.setTextureSize(64, 64);
        corner7.mirror = true;
        setRotation(corner7, 0F, 0F, 0F);
        corner6 = new ModelPart(this, 26, 13);
        corner6.addCuboid(0F, 0F, 0F, 3, 3, 3, false);
        corner6.setPivot(-8F, 21F, 5F);
        corner6.setTextureSize(64, 64);
        corner6.mirror = true;
        setRotation(corner6, 0F, 0F, 0F);
        corner5 = new ModelPart(this, 26, 13);
        corner5.addCuboid(0F, 0F, 0F, 3, 3, 3, false);
        corner5.setPivot(-8F, 21F, -8F);
        corner5.setTextureSize(64, 64);
        corner5.mirror = true;
        setRotation(corner5, 0F, 0F, 0F);
        corner4 = new ModelPart(this, 26, 13);
        corner4.addCuboid(0F, 0F, 0F, 3, 3, 3, false);
        corner4.setPivot(5F, 8F, 5F);
        corner4.setTextureSize(64, 64);
        corner4.mirror = true;
        setRotation(corner4, 0F, 0F, 0F);
        corner3 = new ModelPart(this, 26, 13);
        corner3.addCuboid(0F, 0F, 0F, 3, 3, 3, false);
        corner3.setPivot(5F, 8F, -8F);
        corner3.setTextureSize(64, 64);
        corner3.mirror = true;
        setRotation(corner3, 0F, 0F, 0F);
        corner2 = new ModelPart(this, 26, 13);
        corner2.addCuboid(0F, 0F, 0F, 3, 3, 3, false);
        corner2.setPivot(-8F, 8F, 5F);
        corner2.setTextureSize(64, 64);
        corner2.mirror = true;
        setRotation(corner2, 0F, 0F, 0F);
        corner1 = new ModelPart(this, 26, 13);
        corner1.addCuboid(0F, 0F, 0F, 3, 3, 3, false);
        corner1.setPivot(-8F, 8F, -8F);
        corner1.setTextureSize(64, 64);
        corner1.mirror = true;
        setRotation(corner1, 0F, 0F, 0F);
        ModelPart connectorBackToggle = new ModelPart(this, 38, 16);
        connectorBackToggle.addCuboid(0F, 0F, 0F, 10, 6, 1, false);
        connectorBackToggle.setPivot(-5F, 13F, 6F);
        connectorBackToggle.setTextureSize(64, 64);
        connectorBackToggle.mirror = true;
        setRotation(connectorBackToggle, 0F, 0F, 0F);
        ModelPart connectorRightToggle = new ModelPart(this, 38, 0);
        connectorRightToggle.addCuboid(0F, 0F, 0F, 1, 6, 10, false);
        connectorRightToggle.setPivot(6F, 13F, -5F);
        connectorRightToggle.setTextureSize(64, 64);
        connectorRightToggle.mirror = true;
        setRotation(connectorRightToggle, 0F, 0F, 0F);
        ModelPart connectorBottomToggle = new ModelPart(this, 0, 19);
        connectorBottomToggle.addCuboid(0F, 0F, 0F, 10, 1, 6, false);
        connectorBottomToggle.setPivot(-5F, 22F, -3F);
        connectorBottomToggle.setTextureSize(64, 64);
        connectorBottomToggle.mirror = true;
        setRotation(connectorBottomToggle, 0F, 0F, 0F);
        ModelPart connectorLeftToggle = new ModelPart(this, 38, 0);
        connectorLeftToggle.addCuboid(0F, 0F, 0F, 1, 6, 10, false);
        connectorLeftToggle.setPivot(-7F, 13F, -5F);
        connectorLeftToggle.setTextureSize(64, 64);
        connectorLeftToggle.mirror = true;
        setRotation(connectorLeftToggle, 0F, 0F, 0F);
        ModelPart connectorFrontToggle = new ModelPart(this, 38, 16);
        connectorFrontToggle.addCuboid(0F, 0F, 0F, 10, 6, 1, false);
        connectorFrontToggle.setPivot(-5F, 13F, -7F);
        connectorFrontToggle.setTextureSize(64, 64);
        connectorFrontToggle.mirror = true;
        setRotation(connectorFrontToggle, 0F, 0F, 0F);
        ModelPart connectorTopToggle = new ModelPart(this, 0, 19);
        connectorTopToggle.addCuboid(0F, 0F, 0F, 10, 1, 6, false);
        connectorTopToggle.setPivot(-5F, 9F, -3F);
        connectorTopToggle.setTextureSize(64, 64);
        connectorTopToggle.mirror = true;
        setRotation(connectorTopToggle, 0F, 0F, 0F);
        ModelPart portBackToggle = new ModelPart(this, 18, 35);
        portBackToggle.addCuboid(0F, 0F, 0F, 8, 8, 1, false);
        portBackToggle.setPivot(-4F, 12F, 7F);
        portBackToggle.setTextureSize(64, 64);
        portBackToggle.mirror = true;
        setRotation(portBackToggle, 0F, 0F, 0F);
        ModelPart portBottomToggle = new ModelPart(this, 0, 26);
        portBottomToggle.addCuboid(0F, 0F, 0F, 8, 1, 8, false);
        portBottomToggle.setPivot(-4F, 23F, -4F);
        portBottomToggle.setTextureSize(64, 64);
        portBottomToggle.mirror = true;
        setRotation(portBottomToggle, 0F, 0F, 0F);
        ModelPart portFrontToggle = new ModelPart(this, 18, 35);
        portFrontToggle.addCuboid(0F, 0F, 0F, 8, 8, 1, false);
        portFrontToggle.setPivot(-4F, 12F, -8F);
        portFrontToggle.setTextureSize(64, 64);
        portFrontToggle.mirror = true;
        setRotation(portFrontToggle, 0F, 0F, 0F);
        ModelPart portLeftToggle = new ModelPart(this, 0, 35);
        portLeftToggle.addCuboid(0F, 0F, 0F, 1, 8, 8, false);
        portLeftToggle.setPivot(-8F, 12F, -4F);
        portLeftToggle.setTextureSize(64, 64);
        portLeftToggle.mirror = true;
        setRotation(portLeftToggle, 0F, 0F, 0F);
        ModelPart portRightToggle = new ModelPart(this, 0, 35);
        portRightToggle.addCuboid(0F, 0F, 0F, 1, 8, 8, false);
        portRightToggle.setPivot(7F, 12F, -4F);
        portRightToggle.setTextureSize(64, 64);
        portRightToggle.mirror = true;
        setRotation(portRightToggle, 0F, 0F, 0F);
        ModelPart portTopToggle = new ModelPart(this, 0, 26);
        portTopToggle.addCuboid(0F, 0F, 0F, 8, 1, 8, false);
        portTopToggle.setPivot(-4F, 8F, -4F);
        portTopToggle.setTextureSize(64, 64);
        portTopToggle.mirror = true;
        setRotation(portTopToggle, 0F, 0F, 0F);
        ModelPart ledTop1 = new ModelPart(this, 0, 51);
        ledTop1.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledTop1.setPivot(-5.5F, 8.1F, -0.5F);
        ledTop1.setTextureSize(64, 64);
        ledTop1.mirror = true;
        setRotation(ledTop1, 0F, 0F, 0F);
        ModelPart ledTop2 = new ModelPart(this, 0, 51);
        ledTop2.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledTop2.setPivot(4.5F, 8.1F, -0.5F);
        ledTop2.setTextureSize(64, 64);
        ledTop2.mirror = true;
        setRotation(ledTop2, 0F, 0F, 0F);
        ModelPart ledBack1 = new ModelPart(this, 0, 51);
        ledBack1.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledBack1.setPivot(-5.5F, 15.5F, 6.9F);
        ledBack1.setTextureSize(64, 64);
        ledBack1.mirror = true;
        setRotation(ledBack1, 0F, 0F, 0F);
        ModelPart ledBack2 = new ModelPart(this, 0, 51);
        ledBack2.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledBack2.setPivot(4.5F, 15.5F, 6.9F);
        ledBack2.setTextureSize(64, 64);
        ledBack2.mirror = true;
        setRotation(ledBack2, 0F, 0F, 0F);
        ModelPart ledBottom2 = new ModelPart(this, 0, 51);
        ledBottom2.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledBottom2.setPivot(4.5F, 22.9F, -0.5F);
        ledBottom2.setTextureSize(64, 64);
        ledBottom2.mirror = true;
        setRotation(ledBottom2, 0F, 0F, 0F);
        ModelPart ledBottom1 = new ModelPart(this, 0, 51);
        ledBottom1.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledBottom1.setPivot(-5.5F, 22.9F, -0.5F);
        ledBottom1.setTextureSize(64, 64);
        ledBottom1.mirror = true;
        setRotation(ledBottom1, 0F, 0F, 0F);
        ModelPart ledFront1 = new ModelPart(this, 0, 51);
        ledFront1.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledFront1.setPivot(-5.5F, 15.5F, -7.9F);
        ledFront1.setTextureSize(64, 64);
        ledFront1.mirror = true;
        setRotation(ledFront1, 0F, 0F, 0F);
        ModelPart ledFront2 = new ModelPart(this, 0, 51);
        ledFront2.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledFront2.setPivot(4.5F, 15.5F, -7.9F);
        ledFront2.setTextureSize(64, 64);
        ledFront2.mirror = true;
        setRotation(ledFront2, 0F, 0F, 0F);
        ModelPart ledRight2 = new ModelPart(this, 0, 51);
        ledRight2.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledRight2.setPivot(6.9F, 15.5F, 4.5F);
        ledRight2.setTextureSize(64, 64);
        ledRight2.mirror = true;
        setRotation(ledRight2, 0F, 0F, 0F);
        ModelPart ledRight1 = new ModelPart(this, 0, 51);
        ledRight1.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledRight1.setPivot(6.9F, 15.5F, -5.5F);
        ledRight1.setTextureSize(64, 64);
        ledRight1.mirror = true;
        setRotation(ledRight1, 0F, 0F, 0F);
        ModelPart ledLeft1 = new ModelPart(this, 0, 51);
        ledLeft1.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledLeft1.setPivot(-7.9F, 15.5F, 4.5F);
        ledLeft1.setTextureSize(64, 64);
        ledLeft1.mirror = true;
        setRotation(ledLeft1, 0F, 0F, 0F);
        ModelPart ledLeft2 = new ModelPart(this, 0, 51);
        ledLeft2.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        ledLeft2.setPivot(-7.9F, 15.5F, -5.5F);
        ledLeft2.setTextureSize(64, 64);
        ledLeft2.mirror = true;
        setRotation(ledLeft2, 0F, 0F, 0F);

        leds1 = new ModelPart[]{ledFront1, ledLeft1, ledRight1, ledBack1, ledTop1, ledBottom1};
        leds2 = new ModelPart[]{ledFront2, ledLeft2, ledRight2, ledBack2, ledTop2, ledBottom2};

        ports = new ModelPart[]{portFrontToggle, portLeftToggle, portRightToggle, portBackToggle, portTopToggle, portBottomToggle};
        connectors = new ModelPart[]{connectorFrontToggle, connectorLeftToggle, connectorRightToggle, connectorBackToggle, connectorTopToggle, connectorBottomToggle};
    }

    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, EnergyCubeTier tier, boolean renderMain, boolean hasEffect) {
        if (renderMain) {
            render(matrix, getVertexBuilder(renderer, RENDER_TYPE, hasEffect), light, overlayLight, 1, 1, 1, 1);
        }
        EnumColor color = tier.getBaseTier().getColor();
        renderCorners(matrix, getVertexBuilder(renderer, RENDER_TYPE_BASE, hasEffect), MekanismRenderer.FULL_LIGHT, overlayLight, color.getColor(0),
              color.getColor(1), color.getColor(2), 1);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        frame12.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame11.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame10.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame9.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame8.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame7.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame6.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame5.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame4.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame3.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame2.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        frame1.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);

        corner8.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner7.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner6.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner5.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner4.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner3.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner2.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner1.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }

    private void renderCorners(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue,
          float alpha) {
        matrix.push();
        matrix.scale(1.001F, 1.005F, 1.001F);
        matrix.translate(0, -0.0061, 0);
        corner8.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner7.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner6.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner5.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner4.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner3.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner2.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        corner1.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        matrix.pop();
    }

    public void renderSidesBatched(@Nonnull TileEntityEnergyCube tile, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight) {
        Set<RelativeSide> enabledSides = EnumSet.noneOf(RelativeSide.class);
        Set<RelativeSide> outputSides = EnumSet.noneOf(RelativeSide.class);
        ConfigInfo config = tile.getConfig().getConfig(TransmissionType.ENERGY);
        if (config != null) {
            for (RelativeSide side : EnumUtils.SIDES) {
                ISlotInfo slotInfo = config.getSlotInfo(side);
                if (slotInfo != null) {
                    if (slotInfo.canInput()) {
                        enabledSides.add(side);
                    } else if (slotInfo.canOutput()) {
                        enabledSides.add(side);
                        outputSides.add(side);
                    }
                }
            }
        }
        renderSidesBatched(matrix, renderer, light, overlayLight, enabledSides, outputSides, false);
    }

    public void renderSidesBatched(@Nonnull ItemStack stack, EnergyCubeTier tier, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light,
          int overlayLight, boolean hasEffect) {
        Set<RelativeSide> enabledSides;
        Set<RelativeSide> outputSides;
        CompoundTag configData = ItemDataUtils.getDataMapIfPresent(stack);
        if (configData != null && configData.contains(NBTConstants.COMPONENT_CONFIG, NBT.TAG_COMPOUND)) {
            enabledSides = EnumSet.noneOf(RelativeSide.class);
            outputSides = EnumSet.noneOf(RelativeSide.class);
            CompoundTag sideConfig = configData.getCompound(NBTConstants.COMPONENT_CONFIG).getCompound(NBTConstants.CONFIG + TransmissionType.ENERGY.ordinal());
            //TODO: Maybe improve on this, but for now this is a decent way of making it not have disabled sides show
            for (RelativeSide side : EnumUtils.SIDES) {
                DataType dataType = DataType.byIndexStatic(sideConfig.getInt(NBTConstants.SIDE + side.ordinal()));
                if (dataType.equals(DataType.INPUT)) {
                    enabledSides.add(side);
                } else if (dataType.equals(DataType.OUTPUT)) {
                    enabledSides.add(side);
                    outputSides.add(side);
                }
            }
        } else {
            enabledSides = EnumSet.allOf(RelativeSide.class);
            if (tier == EnergyCubeTier.CREATIVE) {
                outputSides = EnumSet.allOf(RelativeSide.class);
            } else {
                outputSides = EnumSet.of(RelativeSide.FRONT);
            }
        }
        renderSidesBatched(matrix, renderer, light, overlayLight, enabledSides, outputSides, hasEffect);
    }

    /**
     * Batched version of to render sides of the energy cube that render all sides per render type before switching to the next render type. This is because the way
     * Minecraft draws custom render types, is it flushes and instantly draws as soon as it gets a new type if it doesn't know how to handle the type.
     */
    private void renderSidesBatched(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, Set<RelativeSide> enabledSides,
          Set<RelativeSide> outputSides, boolean hasEffect) {
        if (!enabledSides.isEmpty()) {
            VertexConsumer buffer = getVertexBuilder(renderer, RENDER_TYPE, hasEffect);
            for (RelativeSide enabledSide : enabledSides) {
                int sideOrdinal = enabledSide.ordinal();
                connectors[sideOrdinal].render(matrix, buffer, light, overlayLight, 1, 1, 1, 1);
                ports[sideOrdinal].render(matrix, buffer, light, overlayLight, 1, 1, 1, 1);
            }
            if (!outputSides.isEmpty()) {
                buffer = getVertexBuilder(renderer, RENDER_TYPE_BASE, hasEffect);
                for (RelativeSide outputSide : outputSides) {
                    ports[outputSide.ordinal()].render(matrix, buffer, MekanismRenderer.FULL_LIGHT, overlayLight, 1, 1, 1, 1);
                }
                renderLEDS(outputSides, getVertexBuilder(renderer, RENDER_TYPE_ON, hasEffect), matrix, MekanismRenderer.FULL_LIGHT, overlayLight);
            }
        }
        if (outputSides.size() < EnumUtils.SIDES.length) {
            Set<RelativeSide> remainingSides = EnumSet.allOf(RelativeSide.class);
            remainingSides.removeAll(outputSides);
            renderLEDS(remainingSides, getVertexBuilder(renderer, RENDER_TYPE_OFF, hasEffect), matrix, light, overlayLight);
        }
    }

    private void renderLEDS(Set<RelativeSide> sides, VertexConsumer ledBuffer, MatrixStack matrix, int light, int overlayLight) {
        for (RelativeSide side : sides) {
            int sideOrdinal = side.ordinal();
            leds1[sideOrdinal].render(matrix, ledBuffer, light, overlayLight, 1, 1, 1, 1);
            leds2[sideOrdinal].render(matrix, ledBuffer, light, overlayLight, 1, 1, 1, 1);
        }
    }

    public static class ModelEnergyCore extends MekanismJavaModel {

        private static final Identifier CORE_TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "energy_core.png");

        private final RenderLayer RENDER_TYPE = getLayer(CORE_TEXTURE);
        private final ModelPart cube;

        public ModelEnergyCore() {
            super(MekanismRenderType::mekStandard);
            textureWidth = 32;
            textureHeight = 32;

            cube = new ModelPart(this, 0, 0);
            cube.addCuboid(-8, -8, -8, 16, 16, 16, false);
            cube.setPivot(0, 0, 0);
            cube.setTextureSize(32, 32);
            cube.mirror = true;
        }

        public VertexConsumer getBuffer(@Nonnull VertexConsumerProvider renderer) {
            return renderer.getBuffer(RENDER_TYPE);
        }

        public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, EnumColor color, float energyPercentage) {
            render(matrix, getBuffer(renderer), light, overlayLight, color.getColor(0), color.getColor(1), color.getColor(2),
                  energyPercentage);
        }

        public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer buffer, int light, int overlayLight, EnumColor color, float energyPercentage) {
            cube.render(matrix, buffer, light, overlayLight, color.getColor(0), color.getColor(1), color.getColor(2), energyPercentage);
        }

        @Override
        public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue,
              float alpha) {
            cube.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        }
    }
}
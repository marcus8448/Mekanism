package mekanism.client.model;

import javax.annotation.Nonnull;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.entity.EntityRobit;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

public class ModelRobit extends EntityModel<EntityRobit> {

    private final ModelPart Body;
    private final ModelPart Bottom;
    private final ModelPart RightTrack;
    private final ModelPart LeftTrack;
    private final ModelPart Neck;
    private final ModelPart Head;
    private final ModelPart Backpack;
    private final ModelPart headback;
    private final ModelPart rightarn;
    private final ModelPart leftarm;
    private final ModelPart righthand;
    private final ModelPart lefthand;
    private final ModelPart backLight;
    private final ModelPart eyeRight;
    private final ModelPart eyeLeft;

    public ModelRobit() {
        textureWidth = 64;
        textureHeight = 64;

        Body = new ModelPart(this, 0, 0);
        Body.addCuboid(0F, 0F, 1F, 6, 4, 5, false);
        Body.setPivot(-3F, 17F, -3F);
        Body.setTextureSize(64, 64);
        Body.mirror = true;
        setRotation(Body, 0F, 0F, 0F);
        Bottom = new ModelPart(this, 22, 0);
        Bottom.addCuboid(0F, 0F, 0F, 6, 2, 7, false);
        Bottom.setPivot(-3F, 21F, -2.5F);
        Bottom.setTextureSize(64, 64);
        Bottom.mirror = true;
        setRotation(Bottom, 0F, 0F, 0F);
        RightTrack = new ModelPart(this, 26, 9);
        RightTrack.addCuboid(0F, 0F, 0F, 2, 3, 9, false);
        RightTrack.setPivot(3F, 21F, -4F);
        RightTrack.setTextureSize(64, 64);
        RightTrack.mirror = true;
        setRotation(RightTrack, 0F, 0F, 0F);
        LeftTrack = new ModelPart(this, 0, 9);
        LeftTrack.addCuboid(0F, 0F, 0F, 2, 3, 9, false);
        LeftTrack.setPivot(-5F, 21F, -4F);
        LeftTrack.setTextureSize(64, 64);
        LeftTrack.mirror = true;
        setRotation(LeftTrack, 0F, 0F, 0F);
        Neck = new ModelPart(this, 0, 26);
        Neck.addCuboid(0F, 0F, 0F, 3, 1, 2, false);
        Neck.setPivot(-1.5F, 16F, -0.5F);
        Neck.setTextureSize(64, 64);
        Neck.mirror = true;
        setRotation(Neck, 0F, 0F, 0F);
        Head = new ModelPart(this, 26, 21);
        Head.addCuboid(0F, 0F, 0F, 7, 3, 4, false);
        Head.setPivot(-3.5F, 13.5F, -1.533333F);
        Head.setTextureSize(64, 64);
        Head.mirror = true;
        setRotation(Head, 0F, 0F, 0F);
        Backpack = new ModelPart(this, 14, 9);
        Backpack.addCuboid(0F, 0F, 0F, 4, 3, 6, false);
        Backpack.setPivot(-2F, 16.8F, -4F);
        Backpack.setTextureSize(64, 64);
        Backpack.mirror = true;
        setRotation(Backpack, 0F, 0F, 0F);
        headback = new ModelPart(this, 17, 1);
        headback.addCuboid(0F, 0F, 0F, 5, 2, 1, false);
        headback.setPivot(-2.5F, 14F, -2F);
        headback.setTextureSize(64, 64);
        headback.mirror = true;
        setRotation(headback, 0F, 0F, 0F);
        rightarn = new ModelPart(this, 0, 21);
        rightarn.addCuboid(0F, 0F, 0F, 1, 1, 4, false);
        rightarn.setPivot(3F, 17.5F, 0F);
        rightarn.setTextureSize(64, 64);
        rightarn.mirror = true;
        setRotation(rightarn, 0F, 0F, 0F);
        leftarm = new ModelPart(this, 12, 21);
        leftarm.addCuboid(0F, 0F, 0F, 1, 1, 4, false);
        leftarm.setPivot(-4F, 17.5F, 0F);
        leftarm.setTextureSize(64, 64);
        leftarm.mirror = true;
        setRotation(leftarm, 0F, 0F, 0F);
        righthand = new ModelPart(this, 15, 28);
        righthand.addCuboid(0F, 0F, 0F, 1, 1, 0, false);
        righthand.setPivot(2.5F, 17.5F, 4F);
        righthand.setTextureSize(64, 64);
        righthand.mirror = true;
        setRotation(righthand, 0F, 0F, 0F);
        lefthand = new ModelPart(this, 15, 28);
        lefthand.addCuboid(0F, 0F, 0F, 1, 1, 0, false);
        lefthand.setPivot(-3.5F, 17.5F, 4F);
        lefthand.setTextureSize(64, 64);
        lefthand.mirror = true;
        setRotation(lefthand, 0F, 0F, 0F);
        backLight = new ModelPart(this, 20, 15);
        backLight.addCuboid(0F, 0F, 0F, 2, 1, 1, false);
        backLight.setPivot(-1F, 17.8F, -4.001F);
        backLight.setTextureSize(64, 64);
        backLight.mirror = true;
        setRotation(backLight, 0F, 0F, 0F);
        eyeRight = new ModelPart(this, 43, 25);
        eyeRight.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        eyeRight.setPivot(1.5F, 14.5F, 1.50001F);
        eyeRight.setTextureSize(64, 64);
        eyeRight.mirror = true;
        setRotation(eyeRight, 0F, 0F, 0F);
        eyeLeft = new ModelPart(this, 43, 25);
        eyeLeft.addCuboid(0F, 0F, 0F, 1, 1, 1, false);
        eyeLeft.setPivot(-2.5F, 14.5F, 1.50001F);
        eyeLeft.setTextureSize(64, 64);
        eyeLeft.mirror = true;
        setRotation(eyeLeft, 0F, 0F, 0F);
    }

    @Override
    public void setRotationAngles(@Nonnull EntityRobit entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        matrix.push();
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
        Body.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Bottom.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        RightTrack.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        LeftTrack.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Neck.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Head.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Backpack.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        headback.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        rightarn.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        leftarm.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        righthand.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        lefthand.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        //Lights on the robit to render at full brightness
        backLight.render(matrix, vertexBuilder, MekanismRenderer.FULL_LIGHT, overlayLight, red, green, blue, alpha);
        eyeRight.render(matrix, vertexBuilder, MekanismRenderer.FULL_LIGHT, overlayLight, red, green, blue, alpha);
        eyeLeft.render(matrix, vertexBuilder, MekanismRenderer.FULL_LIGHT, overlayLight, red, green, blue, alpha);
        matrix.pop();
    }

    private void setRotation(ModelPart model, float x, float y, float z) {
        model.pitch = x;
        model.yaw = y;
        model.roll = z;
    }
}
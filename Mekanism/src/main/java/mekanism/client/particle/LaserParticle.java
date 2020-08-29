package mekanism.client.particle;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.particle.LaserParticleData;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class LaserParticle extends SpriteBillboardParticle {

    private static final ParticleTextureSheet LASER_TYPE = new ParticleTextureSheet() {
        @Override
        public void begin(BufferBuilder buffer, TextureManager manager) {
            //Copy of PARTICLE_SHEET_TRANSLUCENT but with cull disabled
            RenderSystem.depthMask(true);
            manager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.003921569F);
            RenderSystem.disableCull();
            buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator tesselator) {
            tesselator.draw();
        }

        public String toString() {
            return "MEK_LASER_PARTICLE_TYPE";
        }
    };

    private static final float RADIAN_45 = (float) Math.toRadians(45);
    private static final float RADIAN_90 = (float) Math.toRadians(90);

    private final Direction direction;
    private final float halfLength;

    private LaserParticle(ClientWorld world, Pos3D start, Pos3D end, Direction dir, float energyScale) {
        super(world, (start.x + end.x) / 2D, (start.y + end.y) / 2D, (start.z + end.z) / 2D);
        maxAge = 5;
        colorRed = 1;
        colorGreen = 0;
        colorBlue = 0;
        colorAlpha = 0.1F;
        scale = energyScale;
        halfLength = (float) (end.distance(start) / 2);
        direction = dir;
    }

    @Override
    public void buildGeometry(@Nonnull VertexConsumer vertexBuilder, Camera renderInfo, float partialTicks) {
        Vec3d view = renderInfo.getPos();
        float newX = (float) (MathHelper.lerp(partialTicks, prevPosX, x) - view.getX());
        float newY = (float) (MathHelper.lerp(partialTicks, prevPosY, y) - view.getY());
        float newZ = (float) (MathHelper.lerp(partialTicks, prevPosZ, z) - view.getZ());
        float uMin = getMinU();
        float uMax = getMaxU();
        float vMin = getMinV();
        float vMax = getMaxV();
        Quaternion quaternion = direction.getRotationQuaternion();
        quaternion.hamiltonProduct(Vector3f.POSITIVE_Y.getRadialQuaternion(RADIAN_45));
        drawComponent(vertexBuilder, getResultVector(quaternion, newX, newY, newZ), uMin, uMax, vMin, vMax);
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.hamiltonProduct(Vector3f.POSITIVE_Y.getRadialQuaternion(RADIAN_90));
        drawComponent(vertexBuilder, getResultVector(quaternion2, newX, newY, newZ), uMin, uMax, vMin, vMax);
    }

    private Vector3f[] getResultVector(Quaternion quaternion, float newX, float newY, float newZ) {
        Vector3f[] resultVector = new Vector3f[]{
              new Vector3f(-scale, -halfLength, 0),
              new Vector3f(-scale, halfLength, 0),
              new Vector3f(scale, halfLength, 0),
              new Vector3f(scale, -halfLength, 0)
        };
        for (Vector3f vec : resultVector) {
            vec.rotate(quaternion);
            vec.add(newX, newY, newZ);
        }
        return resultVector;
    }

    private void drawComponent(VertexConsumer vertexBuilder, Vector3f[] resultVector, float uMin, float uMax, float vMin, float vMax) {
        vertexBuilder.vertex(resultVector[0].getX(), resultVector[0].getY(), resultVector[0].getZ()).texture(uMax, vMax).color(colorRed, colorGreen, colorBlue, colorAlpha).light(240, 240).next();
        vertexBuilder.vertex(resultVector[1].getX(), resultVector[1].getY(), resultVector[1].getZ()).texture(uMax, vMin).color(colorRed, colorGreen, colorBlue, colorAlpha).light(240, 240).next();
        vertexBuilder.vertex(resultVector[2].getX(), resultVector[2].getY(), resultVector[2].getZ()).texture(uMin, vMin).color(colorRed, colorGreen, colorBlue, colorAlpha).light(240, 240).next();
        vertexBuilder.vertex(resultVector[3].getX(), resultVector[3].getY(), resultVector[3].getZ()).texture(uMin, vMax).color(colorRed, colorGreen, colorBlue, colorAlpha).light(240, 240).next();
    }

    @Nonnull
    @Override
    public ParticleTextureSheet getType() {
        return LASER_TYPE;
    }

    public static class Factory implements ParticleFactory<LaserParticleData> {

        private final SpriteProvider spriteSet;

        public Factory(SpriteProvider spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public LaserParticle makeParticle(LaserParticleData data, @Nonnull ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Pos3D start = new Pos3D(x, y, z);
            Pos3D end = start.translate(data.direction, data.distance);
            LaserParticle particleLaser = new LaserParticle(world, start, end, data.direction, data.energyScale);
            particleLaser.setSprite(this.spriteSet);
            return particleLaser;
        }
    }
}
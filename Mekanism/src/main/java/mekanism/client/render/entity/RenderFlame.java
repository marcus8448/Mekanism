package mekanism.client.render.entity;

import javax.annotation.Nonnull;
import mekanism.client.render.MekanismRenderType;
import mekanism.common.entity.EntityFlame;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class RenderFlame extends EntityRenderer<EntityFlame> {

    public RenderFlame(EntityRenderDispatcher renderManager) {
        super(renderManager);
    }

    @Override
    public boolean shouldRender(EntityFlame flame, @Nonnull Frustum camera, double camX, double camY, double camZ) {
        return flame.age > 0 && super.shouldRender(flame, camera, camX, camY, camZ);
    }

    @Override
    public void render(@Nonnull EntityFlame flame, float entityYaw, float partialTick, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light) {
        float alpha = (flame.age + partialTick) / EntityFlame.LIFESPAN;
        float actualAlpha = 1 - alpha;
        if (actualAlpha <= 0) {
            return;
        }
        float size = (float) Math.pow(2 * alpha, 2);
        float f5 = 5 / 32F;
        float scale = 0.05625F * (0.8F + size);
        matrix.push();
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((flame.prevYaw + (flame.yaw - flame.prevYaw) * partialTick) - 90F));
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(flame.prevPitch + (flame.pitch - flame.prevPitch) * partialTick));
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45));
        matrix.scale(scale, scale, scale);
        matrix.translate(-4, 0, 0);
        VertexConsumer builder = renderer.getBuffer(MekanismRenderType.renderFlame(getEntityTexture(flame)));
        for (int j = 0; j < 4; j++) {
            matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
            builder.normal(matrix.peek().getNormal(), 0, 0, scale);
            Matrix4f matrix4f = matrix.peek().getModel();
            builder.vertex(matrix4f, -8, -2, 0).color(1, 1, 1, actualAlpha).texture(0, 0).next();
            builder.vertex(matrix4f, 8, -2, 0).color(1, 1, 1, actualAlpha).texture(0.5F, 0).next();
            builder.vertex(matrix4f, 8, 2, 0).color(1, 1, 1, actualAlpha).texture(0.5F, f5).next();
            builder.vertex(matrix4f, -8, 2, 0).color(1, 1, 1, actualAlpha).texture(0, f5).next();
        }
        matrix.pop();
    }

    @Nonnull
    @Override
    public Identifier getEntityTexture(@Nonnull EntityFlame entity) {
        return MekanismUtils.getResource(ResourceType.RENDER, "flame.png");
    }
}

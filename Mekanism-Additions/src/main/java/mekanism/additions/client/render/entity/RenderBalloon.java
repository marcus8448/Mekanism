package mekanism.additions.client.render.entity;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.additions.client.model.AdditionsModelCache;
import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.entity.EntityBalloon;
import mekanism.client.model.BaseModelCache.JSONModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RenderBalloon extends EntityRenderer<EntityBalloon> {

    public static final Identifier BALLOON_TEXTURE = MekanismAdditions.rl("textures/item/balloon.png");

    public RenderBalloon(EntityRenderDispatcher renderManager) {
        super(renderManager);
    }

    @Nonnull
    @Override
    public Identifier getEntityTexture(@Nonnull EntityBalloon entity) {
        return BALLOON_TEXTURE;
    }

    @Override
    public void render(@Nonnull EntityBalloon balloon, float entityYaw, float partialTick, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light) {
        matrix.push();
        matrix.translate(-0.5, -1, -0.5);

        if (balloon.isLatchedToEntity()) {
            //Shift the rendering of the balloon to be over the entity
            double x = balloon.latchedEntity.lastRenderX + (balloon.latchedEntity.getX() - balloon.latchedEntity.lastRenderX) * partialTick
                       - (balloon.lastRenderX + (balloon.getX() - balloon.lastRenderX) * partialTick);
            double y = balloon.latchedEntity.lastRenderY + (balloon.latchedEntity.getY() - balloon.latchedEntity.lastRenderY) * partialTick
                       - (balloon.lastRenderY + (balloon.getY() - balloon.lastRenderY) * partialTick)
                       + balloon.getAddedHeight();
            double z = balloon.latchedEntity.lastRenderZ + (balloon.latchedEntity.getZ() - balloon.latchedEntity.lastRenderZ) * partialTick
                       - (balloon.lastRenderZ + (balloon.getZ() - balloon.lastRenderZ) * partialTick);
            matrix.translate(x, y, z);
        }

        JSONModelData model = balloon.isLatched() ? AdditionsModelCache.INSTANCE.BALLOON : AdditionsModelCache.INSTANCE.BALLOON_FREE;

        List<BakedQuad> quads = model.getBakedModel().getQuads(null, null, null);
        RenderLayer renderType = RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        VertexConsumer builder = renderer.getBuffer(renderType);
        MatrixStack.Entry last = matrix.peek();
        for (BakedQuad quad : quads) {
            float[] color = new float[]{1, 1, 1, 1};
            if (quad.getColorIndex() == 0) {
                color[0] = balloon.color.getRgbCode()[0] / 255F;
                color[1] = balloon.color.getRgbCode()[1] / 255F;
                color[2] = balloon.color.getRgbCode()[2] / 255F;
            }
            builder.addVertexData(last, quad, color[0], color[1], color[2], color[3], light, OverlayTexture.DEFAULT_UV);
        }
        ((VertexConsumerProvider.Immediate) renderer).draw(renderType);
        matrix.pop();
        super.render(balloon, entityYaw, partialTick, matrix, renderer, light);
    }
}
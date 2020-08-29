package mekanism.additions.client.render.entity;

import javax.annotation.Nonnull;
import mekanism.additions.common.entity.EntityObsidianTNT;
import mekanism.additions.common.registries.AdditionsBlocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class RenderObsidianTNTPrimed extends EntityRenderer<EntityObsidianTNT> {

    public RenderObsidianTNTPrimed(EntityRenderDispatcher renderManager) {
        super(renderManager);
        shadowRadius = 0.5F;
    }

    @Override
    public void render(@Nonnull EntityObsidianTNT tnt, float entityYaw, float partialTick, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light) {
        matrix.push();
        matrix.translate(0, 0.5, 0);
        if (tnt.getFuseTimer() - partialTick + 1.0F < 10.0F) {
            float f = 1.0F - (tnt.getFuseTimer() - partialTick + 1.0F) / 10.0F;
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f1 = 1.0F + f * 0.3F;
            matrix.scale(f1, f1, f1);
        }

        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
        matrix.translate(-0.5, -0.5, 0.5);
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
        TntMinecartEntityRenderer.method_23190(AdditionsBlocks.OBSIDIAN_TNT.getBlock().getDefaultState(), matrix, renderer, light, tnt.getFuseTimer() / 5 % 2 == 0);
        matrix.pop();
        super.render(tnt, entityYaw, partialTick, matrix, renderer, light);
    }

    @Nonnull
    @Override
    public Identifier getEntityTexture(@Nonnull EntityObsidianTNT entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}
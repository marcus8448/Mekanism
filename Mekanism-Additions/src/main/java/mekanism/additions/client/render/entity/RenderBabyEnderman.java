package mekanism.additions.client.render.entity;

import java.util.Random;
import javax.annotation.Nonnull;
import mekanism.additions.client.model.ModelBabyEnderman;
import mekanism.additions.client.render.entity.layer.BabyEndermanEyesLayer;
import mekanism.additions.client.render.entity.layer.BabyEndermanHeldBlockLayer;
import mekanism.additions.common.entity.baby.EntityBabyEnderman;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * Copy of vanilla's enderman render, modified to use our own model/layer that is properly scaled, so that the block is held in the correct spot and the head is in the
 * proper place.
 */
public class RenderBabyEnderman extends MobEntityRenderer<EntityBabyEnderman, ModelBabyEnderman> {

    private static final Identifier ENDERMAN_TEXTURES = new Identifier("textures/entity/enderman/enderman.png");
    private final Random rnd = new Random();

    public RenderBabyEnderman(EntityRenderDispatcher renderManager) {
        super(renderManager, new ModelBabyEnderman(), 0.5F);
        this.addFeature(new BabyEndermanEyesLayer(this));
        this.addFeature(new BabyEndermanHeldBlockLayer(this));
    }

    @Override
    public void render(EntityBabyEnderman enderman, float entityYaw, float partialTicks, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int packedLightIn) {
        ModelBabyEnderman model = getModel();
        model.carryingBlock = enderman.getCarriedBlock() != null;
        model.angry = enderman.isAngry();
        super.render(enderman, entityYaw, partialTicks, matrix, renderer, packedLightIn);
    }

    @Nonnull
    @Override
    public Vec3d getRenderOffset(EntityBabyEnderman enderman, float partialTicks) {
        if (enderman.isAngry()) {
            return new Vec3d(this.rnd.nextGaussian() * 0.02, 0, this.rnd.nextGaussian() * 0.02);
        }
        return super.getPositionOffset(enderman, partialTicks);
    }

    @Nonnull
    @Override
    public Identifier getEntityTexture(@Nonnull EntityBabyEnderman enderman) {
        return ENDERMAN_TEXTURES;
    }
}
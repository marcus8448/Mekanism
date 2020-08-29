package mekanism.additions.client.render.entity;

import javax.annotation.Nonnull;
import mekanism.additions.client.model.ModelBabyCreeper;
import mekanism.additions.client.render.entity.layer.BabyCreeperChargeLayer;
import mekanism.additions.common.entity.baby.EntityBabyCreeper;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * Copy of vanilla's creeper render, modified to use our own model/layer that is properly scaled
 */
public class RenderBabyCreeper extends MobEntityRenderer<EntityBabyCreeper, ModelBabyCreeper> {

    private static final Identifier CREEPER_TEXTURES = new Identifier("textures/entity/creeper/creeper.png");

    public RenderBabyCreeper(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelBabyCreeper(), 0.5F);
        this.addFeature(new BabyCreeperChargeLayer(this));
    }

    @Override
    protected void preRenderCallback(EntityBabyCreeper creeper, MatrixStack matrix, float partialTicks) {
        float f = creeper.getClientFuseTime(partialTicks);
        float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = f * f;
        f = f * f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        matrix.scale(f2, f3, f2);
    }

    @Override
    protected float getOverlayProgress(EntityBabyCreeper creeper, float partialTicks) {
        float f = creeper.getClientFuseTime(partialTicks);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(f, 0.5F, 1.0F);
    }

    @Nonnull
    @Override
    public Identifier getEntityTexture(@Nonnull EntityBabyCreeper entity) {
        return CREEPER_TEXTURES;
    }
}
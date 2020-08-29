package mekanism.additions.client.render.entity.layer;

import javax.annotation.Nonnull;
import mekanism.additions.client.model.ModelBabyCreeper;
import mekanism.additions.common.entity.baby.EntityBabyCreeper;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;

public class BabyCreeperChargeLayer extends EnergySwirlOverlayFeatureRenderer<EntityBabyCreeper, ModelBabyCreeper> {

    private static final Identifier LIGHTNING_TEXTURE = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final ModelBabyCreeper creeperModel = new ModelBabyCreeper(1);//Note: Use 1 instead of 2 for size

    public BabyCreeperChargeLayer(FeatureRendererContext<EntityBabyCreeper, ModelBabyCreeper> renderer) {
        super(renderer);
    }

    @Override
    protected float getEnergySwirlX(float p_225634_1_) {
        return p_225634_1_ * 0.01F;
    }

    @Nonnull
    @Override
    protected Identifier getEnergySwirlTexture() {
        return LIGHTNING_TEXTURE;
    }

    @Nonnull
    @Override
    protected EntityModel<EntityBabyCreeper> getEnergySwirlModel() {
        return this.creeperModel;
    }
}
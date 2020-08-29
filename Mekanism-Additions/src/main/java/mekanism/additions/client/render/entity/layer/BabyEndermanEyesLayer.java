package mekanism.additions.client.render.entity.layer;

import javax.annotation.Nonnull;
import mekanism.additions.client.model.ModelBabyEnderman;
import mekanism.additions.common.entity.baby.EntityBabyEnderman;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

public class BabyEndermanEyesLayer extends EyesFeatureRenderer<EntityBabyEnderman, ModelBabyEnderman> {

    private static final RenderLayer RENDER_TYPE = RenderLayer.getEyes(new Identifier("textures/entity/enderman/enderman_eyes.png"));

    public BabyEndermanEyesLayer(FeatureRendererContext<EntityBabyEnderman, ModelBabyEnderman> renderer) {
        super(renderer);
    }

    @Nonnull
    @Override
    public RenderLayer getEyesTexture() {
        return RENDER_TYPE;
    }
}
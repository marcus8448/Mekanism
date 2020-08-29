package mekanism.additions.client.render.entity.layer;

import javax.annotation.Nonnull;
import mekanism.additions.client.model.ModelBabyEnderman;
import mekanism.additions.common.entity.baby.EntityBabyEnderman;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

public class BabyEndermanHeldBlockLayer extends FeatureRenderer<EntityBabyEnderman, ModelBabyEnderman> {

    public BabyEndermanHeldBlockLayer(FeatureRendererContext<EntityBabyEnderman, ModelBabyEnderman> renderer) {
        super(renderer);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, EntityBabyEnderman enderman, float limbSwing, float limbSwingAmount,
          float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        BlockState blockstate = enderman.getCarriedBlock();
        if (blockstate != null) {
            matrix.push();
            matrix.translate(0, 0.6875, -0.75);
            matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(20));
            matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(45));
            matrix.translate(0.25, 0.1875, 0.25);
            //Modify scale of block to be 3/4 of what it is for the adult enderman
            float scale = 0.375F;
            matrix.scale(-scale, -scale, scale);
            matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
            //Adjust the position of the block to actually look more like it is in the enderman's hands
            matrix.translate(0, -1, 0.25);
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockstate, matrix, renderer, light, OverlayTexture.DEFAULT_UV);
            matrix.pop();
        }
    }
}
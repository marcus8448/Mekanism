package mekanism.client.render.item.gear;

import javax.annotation.Nonnull;
import mekanism.client.model.ModelJetpack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;

public class RenderJetpack extends BuiltinModelItemRenderer {

    private static final ModelJetpack jetpack = new ModelJetpack();

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull Mode transformType, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight) {
        matrix.push();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
        jetpack.render(matrix, renderer, light, overlayLight, stack.hasGlint());
        matrix.pop();
    }
}
package mekanism.client.render.item.block;

import javax.annotation.Nonnull;
import mekanism.client.model.ModelSolarNeutronActivator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;

public class RenderSolarNeutronActivatorItem extends BuiltinModelItemRenderer {

    private static final ModelSolarNeutronActivator solarNeutronActivator = new ModelSolarNeutronActivator();

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull Mode transformType, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight) {
        matrix.push();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
        matrix.translate(0, -0.55, 0);
        solarNeutronActivator.render(matrix, renderer, light, overlayLight, stack.hasGlint());
        matrix.pop();
    }
}
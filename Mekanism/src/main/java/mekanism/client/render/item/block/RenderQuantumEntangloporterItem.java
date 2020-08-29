package mekanism.client.render.item.block;

import javax.annotation.Nonnull;
import mekanism.client.model.ModelQuantumEntangloporter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;

public class RenderQuantumEntangloporterItem extends BuiltinModelItemRenderer {

    private static final ModelQuantumEntangloporter quantumEntangloporter = new ModelQuantumEntangloporter();

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull Mode transformType, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight) {
        matrix.push();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
        matrix.translate(0, -1, 0);
        //TODO: Try to get the main part rendering based on the json model instead
        quantumEntangloporter.render(matrix, renderer, light, overlayLight, true, stack.hasGlint());
        matrix.pop();
    }
}
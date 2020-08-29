package mekanism.client.render.item.block;

import javax.annotation.Nonnull;
import mekanism.client.model.ModelIndustrialAlarm;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class RenderIndustrialAlarmItem extends BuiltinModelItemRenderer {

    private static final ModelIndustrialAlarm industrialAlarm = new ModelIndustrialAlarm();

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull Mode transformType, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight) {
        matrix.push();
        matrix.translate(0.5, 0.3, 0.5);
        industrialAlarm.render(matrix, renderer, light, overlayLight, false, 0, true, stack.hasGlint());
        matrix.pop();
    }
}
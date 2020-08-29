package mekanism.generators.client.render.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.generators.client.model.ModelWindGenerator;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class RenderWindGeneratorItem extends BuiltinModelItemRenderer {

    private static final ModelWindGenerator windGenerator = new ModelWindGenerator();
    private static float lastTicksUpdated = 0;
    private static int angle = 0;

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull Mode transformType, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight) {
        float renderPartialTicks = MinecraftClient.getInstance().getTickDelta();
        if (lastTicksUpdated != renderPartialTicks) {
            //Only update the angle if we are in a world and that world is not blacklisted
            if (MinecraftClient.getInstance().world != null) {
                List<Identifier> blacklistedDimensions = MekanismGeneratorsConfig.generators.windGenerationDimBlacklist.get();
                if (blacklistedDimensions.isEmpty() || !blacklistedDimensions.contains(MinecraftClient.getInstance().world.getRegistryKey().getValue())) {
                    angle = (angle + 2) % 360;
                }
            }
            lastTicksUpdated = renderPartialTicks;
        }
        matrix.push();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
        windGenerator.render(matrix, renderer, angle, light, overlayLight, stack.hasGlint());
        matrix.pop();
    }
}
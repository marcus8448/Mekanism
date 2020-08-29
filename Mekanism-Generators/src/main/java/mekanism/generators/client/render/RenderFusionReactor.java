package mekanism.generators.client.render;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.api.text.EnumColor;
import mekanism.client.MekanismClient;
import mekanism.client.model.ModelEnergyCube.ModelEnergyCore;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.client.render.tileentity.RenderEnergyCube;
import mekanism.generators.common.GeneratorsProfilerConstants;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorController;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderFusionReactor extends MekanismTileEntityRenderer<TileEntityFusionReactorController> {

    private static final double SCALE = 100_000_000;
    private final ModelEnergyCore core = new ModelEnergyCore();

    public RenderFusionReactor(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityFusionReactorController tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight,
          Profiler profiler) {
        if (tile.getMultiblock().isFormed() && tile.getMultiblock().isBurning()) {
            matrix.push();
            matrix.translate(0.5, -1.5, 0.5);

            long scaledTemp = Math.round(tile.getMultiblock().getLastPlasmaTemp() / SCALE);
            float ticks = MekanismClient.ticksPassed + partialTick;
            double scale = 1 + 0.7 * Math.sin(Math.toRadians(ticks * 3.14 * scaledTemp + 135F));
            VertexConsumer buffer = core.getBuffer(renderer);
            renderPart(matrix, buffer, overlayLight, EnumColor.AQUA, scale, ticks, scaledTemp, -6, -7, 0, 36);

            scale = 1 + 0.8 * Math.sin(Math.toRadians(ticks * 3 * scaledTemp));
            renderPart(matrix, buffer, overlayLight, EnumColor.RED, scale, ticks, scaledTemp, 4, 4, 0, 36);

            scale = 1 - 0.9 * Math.sin(Math.toRadians(ticks * 4 * scaledTemp + 90F));
            renderPart(matrix, buffer, overlayLight, EnumColor.ORANGE, scale, ticks, scaledTemp, 5, -3, -35, 106);

            matrix.pop();
        }
    }

    @Override
    protected String getProfilerSection() {
        return GeneratorsProfilerConstants.FUSION_REACTOR;
    }

    private void renderPart(MatrixStack matrix, VertexConsumer buffer, int overlayLight, EnumColor color, double scale, float ticks, long scaledTemp, int mult1,
          int mult2, int shift1, int shift2) {
        float ticksScaledTemp = ticks * scaledTemp;
        matrix.push();
        matrix.scale((float) scale, (float) scale, (float) scale);
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(ticksScaledTemp * mult1 + shift1));
        matrix.multiply(RenderEnergyCube.coreVec.getDegreesQuaternion(ticksScaledTemp * mult2 + shift2));
        core.render(matrix, buffer, MekanismRenderer.FULL_LIGHT, overlayLight, color, 1);
        matrix.pop();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityFusionReactorController tile) {
        return tile.getMultiblock().isFormed() && tile.getMultiblock().isBurning();
    }
}
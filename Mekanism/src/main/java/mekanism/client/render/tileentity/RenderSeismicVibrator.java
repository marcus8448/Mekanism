package mekanism.client.render.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.model.ModelSeismicVibrator;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.tile.machine.TileEntitySeismicVibrator;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderSeismicVibrator extends MekanismTileEntityRenderer<TileEntitySeismicVibrator> implements IWireFrameRenderer {

    private final ModelSeismicVibrator model = new ModelSeismicVibrator();

    public RenderSeismicVibrator(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntitySeismicVibrator tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight, Profiler profiler) {
        float actualRate = performTranslationsAndGetRate(tile, partialTick, matrix);
        model.render(matrix, renderer, light, overlayLight, actualRate, false);
        matrix.pop();
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.SEISMIC_VIBRATOR;
    }

    @Override
    public boolean isGlobalRenderer(TileEntitySeismicVibrator tile) {
        return true;
    }

    @Override
    public void renderWireFrame(BlockEntity tile, float partialTick, MatrixStack matrix, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        if (tile instanceof TileEntitySeismicVibrator) {
            float actualRate = performTranslationsAndGetRate((TileEntitySeismicVibrator) tile, partialTick, matrix);
            model.renderWireFrame(matrix, buffer, actualRate, red, green, blue, alpha);
            matrix.pop();
        }
    }

    /**
     * Make sure to call matrix.pop afterwards
     */
    private float performTranslationsAndGetRate(TileEntitySeismicVibrator tile, float partialTick, MatrixStack matrix) {
        matrix.push();
        matrix.translate(0.5, 1.5, 0.5);
        MekanismRenderer.rotate(matrix, tile.getDirection(), 0, 180, 90, 270);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
        return Math.max(0, (float) Math.sin((tile.clientPiston + (tile.getActive() ? partialTick : 0)) / 5F));
    }
}
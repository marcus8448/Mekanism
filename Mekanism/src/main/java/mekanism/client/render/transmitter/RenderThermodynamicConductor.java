package mekanism.client.render.transmitter;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.content.network.transmitter.ThermodynamicConductor;
import mekanism.common.tile.transmitter.TileEntityThermodynamicConductor;
import mekanism.common.util.HeatUtils;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderThermodynamicConductor extends RenderTransmitterBase<TileEntityThermodynamicConductor> {

    public RenderThermodynamicConductor(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityThermodynamicConductor tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight,
          Profiler profiler) {
        matrix.push();
        matrix.translate(0.5, 0.5, 0.5);
        ThermodynamicConductor conductor = tile.getTransmitter();
        int argb = HeatUtils.getColorFromTemp(conductor.getTotalTemperature(), conductor.getBaseColor()).argb();
        renderModel(tile, matrix, renderer.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()), argb, MekanismRenderer.getAlpha(argb), MekanismRenderer.FULL_LIGHT,
              overlayLight, MekanismRenderer.heatIcon);
        matrix.pop();
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.THERMODYNAMIC_CONDUCTOR;
    }
}
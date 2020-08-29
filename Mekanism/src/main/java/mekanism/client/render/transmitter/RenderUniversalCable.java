package mekanism.client.render.transmitter;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.content.network.EnergyNetwork;
import mekanism.common.content.network.transmitter.UniversalCable;
import mekanism.common.tile.transmitter.TileEntityUniversalCable;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderUniversalCable extends RenderTransmitterBase<TileEntityUniversalCable> {

    public RenderUniversalCable(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityUniversalCable tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight,
          Profiler profiler) {
        UniversalCable cable = tile.getTransmitter();
        if (cable.hasTransmitterNetwork()) {
            EnergyNetwork network = cable.getTransmitterNetwork();
            //Note: We don't check if the network is empty as we don't actually ever sync the energy value to the client
            if (network.currentScale > 0) {
                matrix.push();
                matrix.translate(0.5, 0.5, 0.5);
                renderModel(tile, matrix, renderer.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()), 0xFFFFFF, network.currentScale, MekanismRenderer.FULL_LIGHT,
                      overlayLight, MekanismRenderer.energyIcon);
                matrix.pop();
            }
        }
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.UNIVERSAL_CABLE;
    }
}
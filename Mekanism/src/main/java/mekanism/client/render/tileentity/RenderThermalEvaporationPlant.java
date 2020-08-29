package mekanism.client.render.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.ModelRenderer;
import mekanism.client.render.data.FluidRenderData;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationBlock;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderThermalEvaporationPlant extends MekanismTileEntityRenderer<TileEntityThermalEvaporationBlock> {

    public RenderThermalEvaporationPlant(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityThermalEvaporationBlock tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight,
          Profiler profiler) {
        if (tile.isMaster && tile.getMultiblock().isFormed() && tile.getMultiblock().renderLocation != null && !tile.getMultiblock().inputTank.isEmpty()) {
            FluidRenderData data = new FluidRenderData(tile.getMultiblock().inputTank.getFluid());
            data.location = tile.getMultiblock().renderLocation.add(1, 0, 1);
            data.height = tile.getMultiblock().height() - 2;
            data.length = 2;
            data.width = 2;
            matrix.push();
            BlockPos pos = tile.getPos();
            int glow = data.calculateGlowLight(LightmapTextureManager.pack(0, 15));
            matrix.translate(data.location.getX() - pos.getX(), data.location.getY() - pos.getY(), data.location.getZ() - pos.getZ());
            VertexConsumer buffer = renderer.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
            MekanismRenderer.renderObject(ModelRenderer.getModel(data, Math.min(1, tile.getMultiblock().prevScale)), matrix, buffer,
                  data.getColorARGB(tile.getMultiblock().prevScale), glow, overlayLight);
            matrix.pop();
            MekanismRenderer.renderValves(matrix, buffer, tile.getMultiblock().valves, data, pos, glow, overlayLight);
        }
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.THERMAL_EVAPORATION_CONTROLLER;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityThermalEvaporationBlock tile) {
        return tile.isMaster && tile.getMultiblock().isFormed() && !tile.getMultiblock().inputTank.isEmpty() &&
               tile.getMultiblock().renderLocation != null;
    }
}
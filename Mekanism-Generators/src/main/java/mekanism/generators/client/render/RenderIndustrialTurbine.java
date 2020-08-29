package mekanism.generators.client.render;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.client.render.ModelRenderer;
import mekanism.client.render.data.GasRenderData;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.GeneratorsProfilerConstants;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderIndustrialTurbine extends MekanismTileEntityRenderer<TileEntityTurbineCasing> {

    public RenderIndustrialTurbine(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityTurbineCasing tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight, Profiler profiler) {
        if (tile.isMaster && tile.getMultiblock().isFormed() && tile.getMultiblock().complex != null && tile.getMultiblock().renderLocation != null) {
            BlockPos pos = tile.getPos();
            BlockPos complexPos = tile.getMultiblock().complex;
            VertexConsumer buffer = RenderTurbineRotor.INSTANCE.model.getBuffer(renderer);
            profiler.push(GeneratorsProfilerConstants.TURBINE_ROTOR);
            while (true) {
                complexPos = complexPos.down();
                TileEntityTurbineRotor rotor = MekanismUtils.getTileEntity(TileEntityTurbineRotor.class, tile.getWorld(), complexPos);
                if (rotor == null) {
                    break;
                }
                matrix.push();
                matrix.translate(complexPos.getX() - pos.getX(), complexPos.getY() - pos.getY(), complexPos.getZ() - pos.getZ());
                RenderTurbineRotor.INSTANCE.render(rotor, matrix, buffer, LightmapTextureManager.pack(0, 15), overlayLight);
                matrix.pop();
            }
            profiler.pop();
            if (!tile.getMultiblock().gasTank.isEmpty() && tile.getMultiblock().length() > 0) {
                int height = tile.getMultiblock().lowerVolume / (tile.getMultiblock().length() * tile.getMultiblock().width());
                if (height >= 1) {
                    GasRenderData data = new GasRenderData(tile.getMultiblock().gasTank.getStack());
                    data.location = tile.getMultiblock().renderLocation;
                    data.height = height;
                    data.length = tile.getMultiblock().length();
                    data.width = tile.getMultiblock().width();
                    int glow = data.calculateGlowLight(LightmapTextureManager.pack(0, 15));
                    matrix.push();
                    matrix.translate(data.location.getX() - pos.getX(), data.location.getY() - pos.getY(), data.location.getZ() - pos.getZ());
                    Model3D gasModel = ModelRenderer.getModel(data, 1);
                    MekanismRenderer.renderObject(gasModel, matrix, renderer.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()),
                          data.getColorARGB(tile.getMultiblock().prevSteamScale), glow, overlayLight);
                    matrix.pop();
                }
            }
        }
    }

    @Override
    protected String getProfilerSection() {
        return GeneratorsProfilerConstants.INDUSTRIAL_TURBINE;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityTurbineCasing tile) {
        return tile.isMaster && tile.getMultiblock().isFormed() && tile.getMultiblock().complex != null && tile.getMultiblock().renderLocation != null;
    }
}
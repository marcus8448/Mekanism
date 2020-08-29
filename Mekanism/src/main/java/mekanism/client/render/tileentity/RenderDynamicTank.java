package mekanism.client.render.tileentity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.client.render.ModelRenderer;
import mekanism.client.render.data.FluidRenderData;
import mekanism.client.render.data.GasRenderData;
import mekanism.client.render.data.InfusionRenderData;
import mekanism.client.render.data.PigmentRenderData;
import mekanism.client.render.data.RenderData;
import mekanism.client.render.data.SlurryRenderData;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.content.tank.TankMultiblockData;
import mekanism.common.tile.multiblock.TileEntityDynamicTank;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderDynamicTank extends MekanismTileEntityRenderer<TileEntityDynamicTank> {

    public RenderDynamicTank(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityDynamicTank tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight, Profiler profiler) {
        if (tile.isMaster && tile.getMultiblock().isFormed() && tile.getMultiblock().renderLocation != null) {
            RenderData data = getRenderData(tile.getMultiblock());
            if (data != null) {
                data.location = tile.getMultiblock().renderLocation;
                data.height = tile.getMultiblock().height() - 2;
                data.length = tile.getMultiblock().length();
                data.width = tile.getMultiblock().width();
                matrix.push();

                VertexConsumer buffer = renderer.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
                BlockPos pos = tile.getPos();
                matrix.translate(data.location.getX() - pos.getX(), data.location.getY() - pos.getY(), data.location.getZ() - pos.getZ());
                int glow = data.calculateGlowLight(LightmapTextureManager.pack(0, 15));
                Model3D model = ModelRenderer.getModel(data, tile.getMultiblock().prevScale);
                MekanismRenderer.renderObject(model, matrix, buffer, data.getColorARGB(tile.getMultiblock().prevScale), glow, overlayLight);
                matrix.pop();
                if (data instanceof FluidRenderData) {
                    MekanismRenderer.renderValves(matrix, buffer, tile.getMultiblock().valves, (FluidRenderData) data, pos, glow, overlayLight);
                }
            }
        }
    }

    @Nullable
    private RenderData getRenderData(TankMultiblockData multiblock) {
        switch (multiblock.mergedTank.getCurrentType()) {
            case FLUID:
                return new FluidRenderData(multiblock.getFluidTank().getFluid());
            case GAS:
                return new GasRenderData(multiblock.getGasTank().getStack());
            case INFUSION:
                return new InfusionRenderData(multiblock.getInfusionTank().getStack());
            case PIGMENT:
                return new PigmentRenderData(multiblock.getPigmentTank().getStack());
            case SLURRY:
                return new SlurryRenderData(multiblock.getSlurryTank().getStack());
        }
        return null;
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.DYNAMIC_TANK;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityDynamicTank tile) {
        return tile.isMaster && tile.getMultiblock().isFormed() && !tile.getMultiblock().isEmpty() && tile.getMultiblock().renderLocation != null;
    }
}
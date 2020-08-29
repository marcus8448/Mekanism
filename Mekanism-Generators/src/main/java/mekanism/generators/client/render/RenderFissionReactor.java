package mekanism.generators.client.render;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.client.render.ModelRenderer;
import mekanism.client.render.data.FluidRenderData;
import mekanism.client.render.data.GasRenderData;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.generators.common.GeneratorsProfilerConstants;
import mekanism.generators.common.content.fission.FissionReactorValidator.FormedAssembly;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderFissionReactor extends MekanismTileEntityRenderer<TileEntityFissionReactorCasing> {

    private static Model3D glowModel;

    public RenderFissionReactor(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityFissionReactorCasing tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight,
          Profiler profiler) {
        if (tile.isMaster && tile.getMultiblock().isFormed() && tile.getMultiblock().renderLocation != null) {
            BlockPos pos = tile.getPos();
            VertexConsumer buffer = renderer.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
            if (tile.getMultiblock().isBurning()) {
                if (glowModel == null) {
                    glowModel = new Model3D();
                    glowModel.minX = 0.05;
                    glowModel.minY = 0.01;
                    glowModel.minZ = 0.05;
                    glowModel.maxX = 0.95;
                    glowModel.maxY = 0.99;
                    glowModel.maxZ = 0.95;
                    glowModel.setTexture(MekanismRenderer.whiteIcon);
                }
                for (FormedAssembly assembly : tile.getMultiblock().assemblies) {
                    matrix.push();
                    matrix.translate(assembly.getPos().getX() - pos.getX(), assembly.getPos().getY() - pos.getY(), assembly.getPos().getZ() - pos.getZ());
                    matrix.scale(1, assembly.getHeight(), 1);
                    int argb = MekanismRenderer.getColorARGB(0.466F, 0.882F, 0.929F, 0.6F);
                    MekanismRenderer.renderObject(glowModel, matrix, buffer, argb, MekanismRenderer.FULL_LIGHT, overlayLight);
                    matrix.pop();
                }
            }
            if (!tile.getMultiblock().fluidCoolantTank.isEmpty()) {
                int height = tile.getMultiblock().height() - 2;
                if (height >= 1) {
                    FluidRenderData data = new FluidRenderData(tile.getMultiblock().fluidCoolantTank.getFluid());
                    data.location = tile.getMultiblock().renderLocation;
                    data.height = height;
                    data.length = tile.getMultiblock().length();
                    data.width = tile.getMultiblock().width();
                    int glow = data.calculateGlowLight(LightmapTextureManager.pack(0, 15));
                    matrix.push();
                    matrix.translate(data.location.getX() - pos.getX(), data.location.getY() - pos.getY(), data.location.getZ() - pos.getZ());
                    MekanismRenderer.renderObject(ModelRenderer.getModel(data, tile.getMultiblock().prevCoolantScale), matrix, buffer,
                          data.getColorARGB(tile.getMultiblock().prevCoolantScale), glow, overlayLight);
                    matrix.pop();
                    MekanismRenderer.renderValves(matrix, buffer, tile.getMultiblock().valves, data, pos, glow, overlayLight);
                }
            }
            if (!tile.getMultiblock().heatedCoolantTank.isEmpty()) {
                int height = tile.getMultiblock().height() - 2;
                if (height >= 1) {
                    GasRenderData data = new GasRenderData(tile.getMultiblock().heatedCoolantTank.getStack());
                    data.location = tile.getMultiblock().renderLocation;
                    data.height = height;
                    data.length = tile.getMultiblock().length();
                    data.width = tile.getMultiblock().width();
                    int glow = data.calculateGlowLight(LightmapTextureManager.pack(0, 15));
                    matrix.push();
                    matrix.scale(0.998F, 0.998F, 0.998F);
                    matrix.translate(data.location.getX() - pos.getX() + 0.001, data.location.getY() - pos.getY() + 0.001, data.location.getZ() - pos.getZ() + 0.001);
                    Model3D gasModel = ModelRenderer.getModel(data, 1);
                    MekanismRenderer.renderObject(gasModel, matrix, buffer, data.getColorARGB(tile.getMultiblock().prevHeatedCoolantScale), glow, overlayLight);
                    matrix.pop();
                }
            }
        }
    }

    @Override
    protected String getProfilerSection() {
        return GeneratorsProfilerConstants.FISSION_REACTOR;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityFissionReactorCasing tile) {
        return tile.isMaster && tile.getMultiblock().isFormed() && tile.getMultiblock().renderLocation != null;
    }
}
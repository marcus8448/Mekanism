package mekanism.client.render.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.model.ModelSolarNeutronActivator;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.tile.machine.TileEntitySolarNeutronActivator;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderSolarNeutronActivator extends MekanismTileEntityRenderer<TileEntitySolarNeutronActivator> implements IWireFrameRenderer {

    private final ModelSolarNeutronActivator model = new ModelSolarNeutronActivator();

    public RenderSolarNeutronActivator(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntitySolarNeutronActivator tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight,
          Profiler profiler) {
        performTranslations(tile, matrix);
        model.render(matrix, renderer, light, overlayLight, false);
        matrix.pop();
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.SOLAR_NEUTRON_ACTIVATOR;
    }

    @Override
    public boolean isGlobalRenderer(TileEntitySolarNeutronActivator tile) {
        return true;
    }

    @Override
    public void renderWireFrame(BlockEntity tile, float partialTick, MatrixStack matrix, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        if (tile instanceof TileEntitySolarNeutronActivator) {
            performTranslations((TileEntitySolarNeutronActivator) tile, matrix);
            model.renderWireFrame(matrix, buffer, red, green, blue, alpha);
            matrix.pop();
        }
    }

    /**
     * Make sure to call matrix.pop afterwards
     */
    private void performTranslations(TileEntitySolarNeutronActivator tile, MatrixStack matrix) {
        matrix.push();
        matrix.translate(0.5, 1.5, 0.5);
        MekanismRenderer.rotate(matrix, tile.getDirection(), 0, 180, 90, 270);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
    }
}
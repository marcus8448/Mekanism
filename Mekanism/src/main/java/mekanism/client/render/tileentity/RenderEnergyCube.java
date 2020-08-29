package mekanism.client.render.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.MekanismClient;
import mekanism.client.model.ModelEnergyCube;
import mekanism.client.model.ModelEnergyCube.ModelEnergyCore;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderEnergyCube extends MekanismTileEntityRenderer<TileEntityEnergyCube> {

    public static final Vector3f coreVec = new Vector3f(0.0F, MekanismUtils.ONE_OVER_ROOT_TWO, MekanismUtils.ONE_OVER_ROOT_TWO);
    private final ModelEnergyCube model = new ModelEnergyCube();
    private final ModelEnergyCore core = new ModelEnergyCore();

    public RenderEnergyCube(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityEnergyCube tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight, Profiler profiler) {
        profiler.push(ProfilerConstants.FRAME);
        matrix.push();
        matrix.translate(0.5, 1.5, 0.5);
        switch (tile.getDirection()) {
            case DOWN:
                matrix.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(90));
                matrix.translate(0, 1, -1);
                break;
            case UP:
                matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
                matrix.translate(0, 1, 1);
                break;
            default:
                //Otherwise use the helper method for handling different face options because it is one of them
                MekanismRenderer.rotate(matrix, tile.getDirection(), 0, 180, 90, 270);
                break;
        }
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
        profiler.push(ProfilerConstants.CORNERS);
        model.render(matrix, renderer, light, overlayLight, tile.getTier(), false, false);
        profiler.swap(ProfilerConstants.SIDES);
        model.renderSidesBatched(tile, matrix, renderer, light, overlayLight);
        profiler.pop();//End sides
        matrix.pop();

        profiler.swap(ProfilerConstants.CORE);//End frame start core
        float energyScale = tile.getEnergyScale();
        if (energyScale > 0) {
            matrix.push();
            matrix.translate(0.5, 0.5, 0.5);
            float ticks = MekanismClient.ticksPassed + partialTick;
            matrix.scale(0.4F, 0.4F, 0.4F);
            matrix.translate(0, Math.sin(Math.toRadians(3 * ticks)) / 7, 0);
            float scaledTicks = 4 * ticks;
            matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(scaledTicks));
            matrix.multiply(coreVec.getDegreesQuaternion(36F + scaledTicks));
            core.render(matrix, renderer, MekanismRenderer.FULL_LIGHT, overlayLight, tile.getTier().getBaseTier().getColor(), energyScale);
            matrix.pop();
        }
        profiler.pop();
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.ENERGY_CUBE;
    }
}
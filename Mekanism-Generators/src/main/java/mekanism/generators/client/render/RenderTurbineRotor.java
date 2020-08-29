package mekanism.generators.client.render;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.common.Mekanism;
import mekanism.generators.client.model.ModelTurbine;
import mekanism.generators.common.GeneratorsProfilerConstants;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderTurbineRotor extends MekanismTileEntityRenderer<TileEntityTurbineRotor> {

    public static RenderTurbineRotor INSTANCE;
    private static final float BASE_SPEED = 512F;
    public final ModelTurbine model = new ModelTurbine();

    public RenderTurbineRotor(BlockEntityRenderDispatcher renderer) {
        super(renderer);
        INSTANCE = this;
    }

    @Override
    protected void render(TileEntityTurbineRotor tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight, Profiler profiler) {
        if (tile.getMultiblock() == null) {
            render(tile, matrix, model.getBuffer(renderer), light, overlayLight);
        }
    }

    public void render(TileEntityTurbineRotor tile, MatrixStack matrix, VertexConsumer buffer, int light, int overlayLight) {
        int housedBlades = tile.getHousedBlades();
        if (housedBlades == 0) {
            return;
        }
        int baseIndex = tile.getPosition() * 2;
        if (!Mekanism.proxy.isPaused()) {
            if (tile.getMultiblock() != null && TurbineMultiblockData.clientRotationMap.containsKey(tile.getMultiblock())) {
                float rotateSpeed = TurbineMultiblockData.clientRotationMap.getFloat(tile.getMultiblock()) * BASE_SPEED;
                tile.rotationLower = (tile.rotationLower + rotateSpeed * (1F / (baseIndex + 1))) % 360;
                tile.rotationUpper = (tile.rotationUpper + rotateSpeed * (1F / (baseIndex + 2))) % 360;
            } else {
                tile.rotationLower = tile.rotationLower % 360;
                tile.rotationUpper = tile.rotationUpper % 360;
            }
        }
        //Bottom blade
        matrix.push();
        matrix.translate(0.5, -1, 0.5);
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(tile.rotationLower));
        model.render(matrix, buffer, light, overlayLight, baseIndex);
        matrix.pop();
        //Top blade
        if (housedBlades == 2) {
            matrix.push();
            matrix.translate(0.5, -0.5, 0.5);
            matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(tile.rotationUpper));
            model.render(matrix, buffer, light, overlayLight, baseIndex + 1);
            matrix.pop();
        }
    }

    @Override
    protected String getProfilerSection() {
        return GeneratorsProfilerConstants.TURBINE_ROTOR;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityTurbineRotor tile) {
        return tile.getMultiblock() == null && tile.getHousedBlades() > 0;
    }
}
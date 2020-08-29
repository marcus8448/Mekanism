package mekanism.client.render.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.model.ModelQuantumEntangloporter;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderQuantumEntangloporter extends MekanismTileEntityRenderer<TileEntityQuantumEntangloporter> {

    private final ModelQuantumEntangloporter model = new ModelQuantumEntangloporter();

    public RenderQuantumEntangloporter(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityQuantumEntangloporter tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight,
          Profiler profiler) {
        matrix.push();
        matrix.translate(0.5, 1.5, 0.5);
        MekanismRenderer.rotate(matrix, tile.getDirection(), 0, 180, 90, 270);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
        model.render(matrix, renderer, light, overlayLight, false, false);
        matrix.pop();
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.QUANTUM_ENTANGLOPORTER;
    }
}
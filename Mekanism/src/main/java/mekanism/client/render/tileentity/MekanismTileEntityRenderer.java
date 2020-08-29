package mekanism.client.render.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public abstract class MekanismTileEntityRenderer<TILE extends BlockEntity> extends BlockEntityRenderer<TILE> {

    protected MekanismTileEntityRenderer(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    public void render(TILE tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight) {
        if (tile.getWorld() != null) {
            Profiler profiler = tile.getWorld().getProfiler();
            profiler.push(getProfilerSection());
            render(tile, partialTick, matrix, renderer, light, overlayLight, profiler);
            profiler.pop();
        }
    }

    protected abstract void render(TILE tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight, Profiler profiler);

    protected abstract String getProfilerSection();
}
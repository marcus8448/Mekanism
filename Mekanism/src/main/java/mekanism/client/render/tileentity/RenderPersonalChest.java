package mekanism.client.render.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.tile.TileEntityPersonalChest;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderPersonalChest extends MekanismTileEntityRenderer<TileEntityPersonalChest> {

    private static final Identifier texture = MekanismUtils.getResource(ResourceType.TEXTURE_BLOCKS, "models/personal_chest.png");

    private final ModelPart lid;
    private final ModelPart base;
    private final ModelPart latch;

    public RenderPersonalChest(BlockEntityRenderDispatcher renderer) {
        super(renderer);
        this.base = new ModelPart(64, 64, 0, 19);
        this.base.addCuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
        this.lid = new ModelPart(64, 64, 0, 0);
        this.lid.addCuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.lid.pivotY = 9.0F;
        this.lid.pivotZ = 1.0F;
        this.latch = new ModelPart(64, 64, 0, 0);
        this.latch.addCuboid(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.latch.pivotY = 8.0F;
    }

    @Override
    protected void render(TileEntityPersonalChest tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight, Profiler profiler) {
        matrix.push();
        if (!tile.isRemoved()) {
            matrix.translate(0.5D, 0.5D, 0.5D);
            matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-tile.getDirection().asRotation()));
            matrix.translate(-0.5D, -0.5D, -0.5D);
        }
        float lidAngle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTick;
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        VertexConsumer builder = renderer.getBuffer(RenderLayer.getEntityCutout(texture));
        lid.pitch = -(lidAngle * ((float) Math.PI / 2F));
        latch.pitch = lid.pitch;
        lid.render(matrix, builder, light, overlayLight);
        latch.render(matrix, builder, light, overlayLight);
        base.render(matrix, builder, light, overlayLight);
        matrix.pop();
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.PERSONAL_CHEST;
    }
}
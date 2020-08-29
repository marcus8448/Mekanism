package mekanism.client.render.tileentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public interface IWireFrameRenderer {

    void renderWireFrame(BlockEntity tile, float partialTick, MatrixStack matrix, VertexConsumer buffer, float red, float green, float blue, float alpha);
}
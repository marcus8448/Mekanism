package mekanism.client.render.lib.effect;

import mekanism.client.render.MekanismRenderType;
import mekanism.common.lib.effect.CustomEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class BillboardingEffectRenderer {

    private static final MinecraftClient minecraft = MinecraftClient.getInstance();

    public static void render(CustomEffect effect, BlockPos renderPos, MatrixStack matrixStack, VertexConsumerProvider renderer, long time, float partialTick) {
        matrixStack.push();
        int gridSize = effect.getTextureGridSize();
        VertexConsumer buffer = getRenderBuffer(renderer, effect.getTexture());
        Matrix4f matrix = matrixStack.peek().getModel();
        Camera renderInfo = minecraft.gameRenderer.getCamera();
        int tick = (int) time % (gridSize * gridSize);
        int xIndex = tick % gridSize, yIndex = tick / gridSize;
        float spriteSize = 1F / gridSize;
        Quaternion quaternion = renderInfo.getRotation();
        new Vector3f(-1.0F, -1.0F, 0.0F).rotate(quaternion);
        Vector3f[] vertexPos = new Vector3f[]{new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F),
                                              new Vector3f(1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, -1.0F, 0.0F)};
        Vec3d pos = effect.getPos(partialTick).subtract(Vec3d.of(renderPos));
        for (int i = 0; i < 4; i++) {
            Vector3f vector3f = vertexPos[i];
            vector3f.rotate(quaternion);
            vector3f.scale(effect.getScale());
            vector3f.add((float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
        }

        int[] color = effect.getColor().rgbaArray();
        float minU = xIndex * spriteSize, maxU = minU + spriteSize;
        float minV = yIndex * spriteSize, maxV = minV + spriteSize;

        buffer.vertex(matrix, vertexPos[0].getX(), vertexPos[0].getY(), vertexPos[0].getZ()).color(color[0], color[1], color[2], color[3]).texture(minU, maxV).next();
        buffer.vertex(matrix, vertexPos[1].getX(), vertexPos[1].getY(), vertexPos[1].getZ()).color(color[0], color[1], color[2], color[3]).texture(maxU, maxV).next();
        buffer.vertex(matrix, vertexPos[2].getX(), vertexPos[2].getY(), vertexPos[2].getZ()).color(color[0], color[1], color[2], color[3]).texture(maxU, minV).next();
        buffer.vertex(matrix, vertexPos[3].getX(), vertexPos[3].getY(), vertexPos[3].getZ()).color(color[0], color[1], color[2], color[3]).texture(minU, minV).next();
        matrixStack.pop();
    }

    protected static VertexConsumer getRenderBuffer(VertexConsumerProvider renderer, Identifier texture) {
        return renderer.getBuffer(MekanismRenderType.renderSPS(texture));
    }
}

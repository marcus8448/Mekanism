package mekanism.client.model;

import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class ExtendedModelRenderer extends ModelPart {

    public ExtendedModelRenderer(Model model, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
    }

    public void render(MatrixStack matrix, VertexConsumer buffer, int light, int overlayLight, float red, float green, float blue, float alpha, boolean wireFrame) {
        if (wireFrame) {
            renderWireFrame(matrix, buffer, red, green, blue, alpha);
        } else {
            render(matrix, buffer, light, overlayLight, red, green, blue, alpha);
        }
    }

    public void renderWireFrame(MatrixStack matrix, VertexConsumer buffer, float red, float green, float blue, float alpha) {
        if (visible) {
            if (!cuboids.isEmpty() || !children.isEmpty()) {
                matrix.push();
                rotate(matrix);
                MatrixStack.Entry matrixEntry = matrix.peek();
                Matrix4f matrix4f = matrixEntry.getModel();
                Matrix3f matrix3f = matrixEntry.getNormal();
                for (ModelPart.Cuboid box : cuboids) {
                    for (ModelPart.Quad quad : box.sides) {
                        Vector3f normal = quad.direction.copy();
                        normal.transform(matrix3f);
                        float normalX = normal.getX();
                        float normalY = normal.getY();
                        float normalZ = normal.getZ();
                        Vector4f vertex = getVertex(matrix4f, quad.vertices[0]);
                        Vector4f vertex2 = getVertex(matrix4f, quad.vertices[1]);
                        Vector4f vertex3 = getVertex(matrix4f, quad.vertices[2]);
                        Vector4f vertex4 = getVertex(matrix4f, quad.vertices[3]);
                        buffer.vertex(vertex.getX(), vertex.getY(), vertex.getZ()).normal(normalX, normalY, normalZ).color(red, green, blue, alpha).next();
                        buffer.vertex(vertex2.getX(), vertex2.getY(), vertex2.getZ()).normal(normalX, normalY, normalZ).color(red, green, blue, alpha).next();

                        buffer.vertex(vertex3.getX(), vertex3.getY(), vertex3.getZ()).normal(normalX, normalY, normalZ).color(red, green, blue, alpha).next();
                        buffer.vertex(vertex4.getX(), vertex4.getY(), vertex4.getZ()).normal(normalX, normalY, normalZ).color(red, green, blue, alpha).next();
                        //Vertices missing from base implementation
                        buffer.vertex(vertex2.getX(), vertex2.getY(), vertex2.getZ()).normal(normalX, normalY, normalZ).color(red, green, blue, alpha).next();
                        buffer.vertex(vertex3.getX(), vertex3.getY(), vertex3.getZ()).normal(normalX, normalY, normalZ).color(red, green, blue, alpha).next();

                        buffer.vertex(vertex.getX(), vertex.getY(), vertex.getZ()).normal(normalX, normalY, normalZ).color(red, green, blue, alpha).next();
                        buffer.vertex(vertex4.getX(), vertex4.getY(), vertex4.getZ()).normal(normalX, normalY, normalZ).color(red, green, blue, alpha).next();
                    }
                }

                for (ModelPart modelrenderer : children) {
                    if (modelrenderer instanceof ExtendedModelRenderer) {
                        ((ExtendedModelRenderer) modelrenderer).renderWireFrame(matrix, buffer, red, green, blue, alpha);
                    } else {
                        modelrenderer.render(matrix, buffer, MekanismRenderer.FULL_LIGHT, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
                    }
                }
                matrix.pop();
            }
        }
    }

    private static Vector4f getVertex(Matrix4f matrix4f, ModelPart.Vertex vertex) {
        Vector4f vector4f = new Vector4f(vertex.pos.getX() / 16F, vertex.pos.getY() / 16F, vertex.pos.getZ() / 16F, 1);
        vector4f.transform(matrix4f);
        return vector4f;
    }
}
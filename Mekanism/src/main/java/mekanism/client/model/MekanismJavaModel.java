package mekanism.client.model;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;

public abstract class MekanismJavaModel extends Model {

    public MekanismJavaModel(Function<Identifier, RenderLayer> renderType) {
        super(renderType);
    }

    protected VertexConsumer getVertexBuilder(@Nonnull VertexConsumerProvider renderer, @Nonnull RenderLayer renderType, boolean hasEffect) {
        return ItemRenderer.method_29711(renderer, renderType, false, hasEffect);
    }

    protected void setRotation(ModelPart model, float x, float y, float z) {
        model.pitch = x;
        model.yaw = y;
        model.roll = z;
    }
}
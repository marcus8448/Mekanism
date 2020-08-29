package mekanism.client.render.armor;

import javax.annotation.Nonnull;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public abstract class CustomArmor extends BipedEntityModel<LivingEntity> {

    protected CustomArmor(float size) {
        super(size);
    }

    public abstract void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, boolean hasEffect, LivingEntity entity,
          ItemStack stack);
}
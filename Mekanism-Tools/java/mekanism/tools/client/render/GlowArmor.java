package mekanism.tools.client.render;

import javax.annotation.Nonnull;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class GlowArmor extends BipedEntityModel<LivingEntity> {

    private static final GlowArmor BIG = new GlowArmor(1.0F);
    private static final GlowArmor SMALL = new GlowArmor(0.5F);

    private GlowArmor(float size) {
        super(size);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumer vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        //Make it render at full brightness
        super.render(matrix, vertexBuilder, MekanismRenderer.FULL_LIGHT, overlayLight, red, green, blue, alpha);
    }

    public static BipedEntityModel<LivingEntity> getGlow(EquipmentSlot index) {
        BipedEntityModel<LivingEntity> biped = index == EquipmentSlot.LEGS ? SMALL : BIG;
        biped.head.visible = index == EquipmentSlot.HEAD;
        biped.helmet.visible = index == EquipmentSlot.HEAD;
        biped.torso.visible = index == EquipmentSlot.CHEST || index == EquipmentSlot.LEGS;
        biped.rightArm.visible = index == EquipmentSlot.CHEST;
        biped.leftArm.visible = index == EquipmentSlot.CHEST;
        biped.rightLeg.visible = index == EquipmentSlot.LEGS || index == EquipmentSlot.FEET;
        biped.leftLeg.visible = index == EquipmentSlot.LEGS || index == EquipmentSlot.FEET;
        return biped;
    }
}
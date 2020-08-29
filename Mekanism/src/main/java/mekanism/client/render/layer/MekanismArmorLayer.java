package mekanism.client.render.layer;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.armor.CustomArmor;
import mekanism.common.item.interfaces.ISpecialGear;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@ParametersAreNonnullByDefault
public class MekanismArmorLayer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends ArmorFeatureRenderer<T, M, A> {

    public MekanismArmorLayer(FeatureRendererContext<T, M> entityRenderer, A modelLeggings, A modelArmor) {
        super(entityRenderer, modelLeggings, modelArmor);
    }

    @Override
    public void render(MatrixStack matrix, VertexConsumerProvider renderer, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTick,
          float ageInTicks, float netHeadYaw, float headPitch) {
        renderArmorPart(matrix, renderer, entity, EquipmentSlot.CHEST, packedLightIn);
        renderArmorPart(matrix, renderer, entity, EquipmentSlot.LEGS, packedLightIn);
        renderArmorPart(matrix, renderer, entity, EquipmentSlot.FEET, packedLightIn);
        renderArmorPart(matrix, renderer, entity, EquipmentSlot.HEAD, packedLightIn);
    }

    private void renderArmorPart(MatrixStack matrix, VertexConsumerProvider renderer, T entity, EquipmentSlot slot, int light) {
        ItemStack stack = entity.getEquippedStack(slot);
        Item item = stack.getItem();
        if (item instanceof ISpecialGear && item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem) item;
            if (armorItem.getSlotType() == slot) {
                CustomArmor model = ((ISpecialGear) item).getGearModel();
                getContextModel().setAttributes((BipedEntityModel<T>) model);
                setVisible((A) model, slot);
                model.render(matrix, renderer, light, OverlayTexture.DEFAULT_UV, stack.hasGlint(), entity, stack);
            }
        }
    }
}
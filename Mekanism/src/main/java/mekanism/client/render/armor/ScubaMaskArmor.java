package mekanism.client.render.armor;

import javax.annotation.Nonnull;
import mekanism.client.model.ModelScubaMask;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ScubaMaskArmor extends CustomArmor {

    public static final ScubaMaskArmor SCUBA_MASK = new ScubaMaskArmor(0.5F);
    private static final ModelScubaMask model = new ModelScubaMask();

    private ScubaMaskArmor(float size) {
        super(size);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, boolean hasEffect, LivingEntity entity,
          ItemStack stack) {
        if (!head.visible) {
            //If the head model shouldn't show don't bother displaying it
            return;
        }
        if (child) {
            matrix.push();
            if (headScaled) {
                float f = 1.5F / invertedChildHeadScale;
                matrix.scale(f, f, f);
            }
            matrix.translate(0.0D, childHeadYOffset / 16.0F, childHeadZOffset / 16.0F);
            renderMask(matrix, renderer, light, overlayLight, hasEffect);
            matrix.pop();
        } else {
            renderMask(matrix, renderer, light, overlayLight, hasEffect);
        }
    }

    private void renderMask(@Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight, boolean hasEffect) {
        matrix.push();
        head.rotate(matrix);
        matrix.translate(0, 0, 0.01);
        model.render(matrix, renderer, light, overlayLight, hasEffect);
        matrix.pop();
    }
}
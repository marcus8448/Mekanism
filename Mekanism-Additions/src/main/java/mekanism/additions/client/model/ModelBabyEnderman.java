package mekanism.additions.client.model;

import javax.annotation.Nonnull;
import mekanism.additions.common.entity.baby.EntityBabyEnderman;
import net.minecraft.client.render.entity.model.EndermanEntityModel;

public class ModelBabyEnderman extends EndermanEntityModel<EntityBabyEnderman> {

    public ModelBabyEnderman() {
        super(0);
    }

    @Override
    public void setRotationAngles(@Nonnull EntityBabyEnderman enderman, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setAngles(enderman, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        //Shift the head to be in the proper place for baby endermen
        head.pivotY += 5.0F;
        if (angry) {
            //Shift the head when angry to only the third the distance it goes up when it is an adult
            head.pivotY += 1.67F;
        }
    }
}
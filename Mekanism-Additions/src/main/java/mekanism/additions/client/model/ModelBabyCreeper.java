package mekanism.additions.client.model;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import mekanism.additions.common.entity.baby.EntityBabyCreeper;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.util.math.MathHelper;

public class ModelBabyCreeper extends AnimalModel<EntityBabyCreeper> {

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public ModelBabyCreeper() {
        this(0);
    }

    public ModelBabyCreeper(float size) {
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4, -8, -4, 8, 8, 8, size);
        //Only real difference between this model and the vanilla creeper model is the "fix" for the head's rotation point
        // the other difference is extending ageable model instead
        this.head.setPivot(0, 10, -2);
        this.body = new ModelPart(this, 16, 16);
        this.body.addCuboid(-4, 0, -2, 8, 12, 4, size);
        this.body.setPivot(0, 6, 0);
        this.leg1 = new ModelPart(this, 0, 16);
        this.leg1.addCuboid(-2, 0, -2, 4, 6, 4, size);
        this.leg1.setPivot(-2, 18, 4);
        this.leg2 = new ModelPart(this, 0, 16);
        this.leg2.addCuboid(-2, 0, -2, 4, 6, 4, size);
        this.leg2.setPivot(2, 18, 4);
        this.leg3 = new ModelPart(this, 0, 16);
        this.leg3.addCuboid(-2, 0, -2, 4, 6, 4, size);
        this.leg3.setPivot(-2, 18, -4);
        this.leg4 = new ModelPart(this, 0, 16);
        this.leg4.addCuboid(-2, 0, -2, 4, 6, 4, size);
        this.leg4.setPivot(2, 18, -4);
    }

    @Nonnull
    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Nonnull
    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.leg1, this.leg2, this.leg3, this.leg4);
    }

    @Override
    public void setRotationAngles(@Nonnull EntityBabyCreeper creeper, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yaw = netHeadYaw * ((float) Math.PI / 180F);
        this.head.pitch = headPitch * ((float) Math.PI / 180F);
        this.leg1.pitch = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leg2.pitch = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leg3.pitch = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leg4.pitch = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }
}
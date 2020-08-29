package mekanism.additions.common.entity.baby;

import javax.annotation.Nonnull;
import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.registries.AdditionsItems;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class EntityBabyWitherSkeleton extends WitherSkeletonEntity {

    private static final TrackedData<Boolean> IS_CHILD = DataTracker.registerData(EntityBabyWitherSkeleton.class, TrackedDataHandlerRegistry.BOOLEAN);

    public EntityBabyWitherSkeleton(EntityType<EntityBabyWitherSkeleton> type, World world) {
        super(type, world);
        setBaby(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(IS_CHILD, false);
    }

    @Override
    public boolean isBaby() {
        return getDataTracker().get(IS_CHILD);
    }

    @Override
    public void setBaby(boolean child) {
        getDataTracker().set(IS_CHILD, child);
        if (world != null && !world.isClient) {
            EntityAttributeInstance attributeInstance = getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            attributeInstance.removeModifier(MekanismAdditions.babySpeedBoostModifier);
            if (child) {
                attributeInstance.addTemporaryModifier(MekanismAdditions.babySpeedBoostModifier);
            }
        }
    }

    @Override
    public void onTrackedDataSet(@Nonnull TrackedData<?> key) {
        if (IS_CHILD.equals(key)) {
            calculateDimensions();
        }
        super.onTrackedDataSet(key);
    }

    @Override
    protected int getCurrentExperience(@Nonnull PlayerEntity player) {
        if (isBaby()) {
            experiencePoints = (int) (experiencePoints * 2.5F);
        }
        return super.getCurrentExperience(player);
    }

    @Override
    public double getHeightOffset() {
        return isBaby() ? 0.0D : super.getHeightOffset();
    }

    @Override
    protected float getActiveEyeHeight(@Nonnull EntityPose pose, @Nonnull EntityDimensions size) {
        return this.isBaby() ? 1.12F : super.getActiveEyeHeight(pose, size);
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return AdditionsItems.BABY_WITHER_SKELETON_SPAWN_EGG.getItemStack();
    }
}
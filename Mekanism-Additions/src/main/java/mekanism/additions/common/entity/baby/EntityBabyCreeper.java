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
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityBabyCreeper extends CreeperEntity {

    private static final TrackedData<Boolean> IS_CHILD = DataTracker.registerData(EntityBabyCreeper.class, TrackedDataHandlerRegistry.BOOLEAN);

    public EntityBabyCreeper(EntityType<EntityBabyCreeper> type, World world) {
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
    protected float getActiveEyeHeight(@Nonnull EntityPose pose, @Nonnull EntityDimensions size) {
        return isBaby() ? 0.77F : super.getActiveEyeHeight(pose, size);
    }

    /**
     * Modify vanilla's explode method to half the explosion strength of baby creepers, and charged baby creepers
     */
    @Override
    protected void explode() {
        if (!world.isClient) {
            Explosion.DestructionType mode = ForgeEventFactory.getMobGriefingEvent(world, this) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
            float f = shouldRenderOverlay() ? 1 : 0.5F;
            dead = true;
            world.createExplosion(this, getX(), getY(), getZ(), explosionRadius * f, mode);
            remove();
            spawnEffectsCloud();
        }
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return AdditionsItems.BABY_CREEPER_SPAWN_EGG.getItemStack();
    }
}
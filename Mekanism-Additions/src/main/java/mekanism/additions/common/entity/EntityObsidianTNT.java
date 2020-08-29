package mekanism.additions.common.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.additions.common.config.MekanismAdditionsConfig;
import mekanism.additions.common.registries.AdditionsBlocks;
import mekanism.additions.common.registries.AdditionsEntityTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityObsidianTNT extends TntEntity {

    public EntityObsidianTNT(EntityType<EntityObsidianTNT> type, World world) {
        super(type, world);
        setFuse(MekanismAdditionsConfig.additions.obsidianTNTDelay.get());
        inanimate = true;
    }

    public EntityObsidianTNT(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(world, x, y, z, igniter);
        setFuse(MekanismAdditionsConfig.additions.obsidianTNTDelay.get());
        inanimate = true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (isAlive() && getFuseTimer() > 0) {
            world.addParticle(ParticleTypes.LAVA, getX(), getY() + 0.5, getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void explode() {
        world.createExplosion(this, getX(), getY() + (double) (getHeight() / 16.0F), getZ(), MekanismAdditionsConfig.additions.obsidianTNTBlastRadius.get(), DestructionType.BREAK);
    }

    @Nonnull
    @Override
    public EntityType<?> getType() {
        return AdditionsEntityTypes.OBSIDIAN_TNT.getEntityType();
    }

    @Nonnull
    @Override
    public Packet<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return AdditionsBlocks.OBSIDIAN_TNT.getItemStack();
    }
}
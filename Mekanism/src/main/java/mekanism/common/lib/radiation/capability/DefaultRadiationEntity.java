package mekanism.common.lib.radiation.capability;

import java.util.Random;
import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.CapabilityCache;
import mekanism.common.capabilities.resolver.basic.BasicCapabilityResolver;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.radiation.RadiationManager.RadiationScale;
import mekanism.common.network.PacketRadiationData;
import mekanism.common.registries.MekanismDamageSource;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class DefaultRadiationEntity implements IRadiationEntity {

    private double radiation;
    private double clientSeverity = 0;

    @Override
    public double getRadiation() {
        return radiation;
    }

    @Override
    public void radiate(double magnitude) {
        radiation += magnitude;
    }

    @Override
    public void update(LivingEntity entity) {
        if (entity instanceof PlayerEntity && !MekanismUtils.isPlayingMode((PlayerEntity) entity)) {
            return;
        }

        Random rand = entity.world.getRandom();
        double minSeverity = MekanismConfig.general.radiationNegativeEffectsMinSeverity.get();
        double severityScale = RadiationScale.getScaledDoseSeverity(radiation);
        double chance = minSeverity + rand.nextDouble() * (1 - minSeverity);

        // Hurt randomly
        chance = minSeverity + rand.nextDouble() * (1 - minSeverity);
        if (severityScale > chance && rand.nextInt() % 2 == 0) {
            entity.damage(MekanismDamageSource.RADIATION, 1);
        }

        if (entity instanceof PlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;

            if (clientSeverity != radiation) {
                clientSeverity = radiation;
                PacketRadiationData.sync(player);
            }

            if (severityScale > chance) {
                player.getHungerManager().addExhaustion(1F);
            }
        }
    }

    @Override
    public void set(double magnitude) {
        radiation = magnitude;
    }

    @Override
    public void decay() {
        radiation = Math.max(RadiationManager.BASELINE, radiation * MekanismConfig.general.radiationTargetDecayRate.get());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag ret = new CompoundTag();
        ret.putDouble(NBTConstants.RADIATION, radiation);
        return ret;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        radiation = nbt.getDouble(NBTConstants.RADIATION);
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IRadiationEntity.class, new Capability.IStorage<IRadiationEntity>() {
            @Override
            public CompoundTag writeNBT(Capability<IRadiationEntity> capability, IRadiationEntity instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IRadiationEntity> capability, IRadiationEntity instance, Direction side, Tag nbt) {
                if (nbt instanceof CompoundTag) {
                    instance.deserializeNBT((CompoundTag) nbt);
                }
            }
        }, DefaultRadiationEntity::new);
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {

        public static final Identifier NAME = Mekanism.rl(NBTConstants.RADIATION);
        private final IRadiationEntity defaultImpl = new DefaultRadiationEntity();
        private final CapabilityCache capabilityCache = new CapabilityCache();

        public Provider() {
            capabilityCache.addCapabilityResolver(BasicCapabilityResolver.constant(Capabilities.RADIATION_ENTITY_CAPABILITY, defaultImpl));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side) {
            return capabilityCache.getCapability(capability, side);
        }

        public void invalidate() {
            capabilityCache.invalidate(Capabilities.RADIATION_ENTITY_CAPABILITY, null);
        }

        @Override
        public CompoundTag serializeNBT() {
            return defaultImpl.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            defaultImpl.deserializeNBT(nbt);
        }
    }
}

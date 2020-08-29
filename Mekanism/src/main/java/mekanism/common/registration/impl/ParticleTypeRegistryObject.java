package mekanism.common.registration.impl;

import javax.annotation.Nonnull;
import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraftforge.fml.RegistryObject;

public class ParticleTypeRegistryObject<PARTICLE extends ParticleEffect> extends WrappedRegistryObject<ParticleType<PARTICLE>> {

    public ParticleTypeRegistryObject(RegistryObject<ParticleType<PARTICLE>> registryObject) {
        super(registryObject);
    }

    @Nonnull
    public ParticleType<PARTICLE> getParticleType() {
        return get();
    }
}
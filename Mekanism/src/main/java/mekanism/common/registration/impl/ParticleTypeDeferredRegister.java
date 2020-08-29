package mekanism.common.registration.impl;

import java.util.function.Supplier;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleTypeDeferredRegister extends WrappedDeferredRegister<ParticleType<?>> {

    public ParticleTypeDeferredRegister(String modid) {
        super(modid, ForgeRegistries.PARTICLE_TYPES);
    }

    public ParticleTypeRegistryObject<DefaultParticleType> registerBasicParticle(String name) {
        return register(name, () -> new DefaultParticleType(false));
    }

    public <PARTICLE extends ParticleEffect> ParticleTypeRegistryObject<PARTICLE> register(String name, Supplier<ParticleType<PARTICLE>> sup) {
        return register(name, sup, ParticleTypeRegistryObject::new);
    }
}
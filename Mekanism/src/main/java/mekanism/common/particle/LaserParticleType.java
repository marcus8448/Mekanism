package mekanism.common.particle;

import com.mojang.serialization.Codec;
import javax.annotation.Nonnull;
import net.minecraft.particle.ParticleType;

public class LaserParticleType extends ParticleType<LaserParticleData> {

    public LaserParticleType() {
        super(false, LaserParticleData.DESERIALIZER);
    }

    @Nonnull
    @Override
    public Codec<LaserParticleData> method_29138() {
        return LaserParticleData.CODEC;
    }
}
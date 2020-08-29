package mekanism.client.particle;

import javax.annotation.Nonnull;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class ScubaBubbleParticle extends WaterBubbleParticle {

    private ScubaBubbleParticle(ClientWorld world, double posX, double posY, double posZ, double velX, double velY, double velZ) {
        super(world, posX, posY, posZ, velX, velY, velZ);
        maxAge *= 2;
    }

    @Override
    public void tick() {
        super.tick();
        age++;
    }

    @Override
    public void buildGeometry(@Nonnull VertexConsumer vertexBuilder, @Nonnull Camera renderInfo, float partialTicks) {
        if (age > 0) {
            super.buildGeometry(vertexBuilder, renderInfo, partialTicks);
        }
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteSet;

        public Factory(SpriteProvider spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(@Nonnull DefaultParticleType type, @Nonnull ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ScubaBubbleParticle particle = new ScubaBubbleParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.setSprite(this.spriteSet);
            return particle;
        }
    }
}
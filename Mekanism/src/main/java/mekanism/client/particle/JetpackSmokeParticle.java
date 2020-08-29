package mekanism.client.particle;

import javax.annotation.Nonnull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class JetpackSmokeParticle extends FireSmokeParticle {

    private JetpackSmokeParticle(ClientWorld world, double posX, double posY, double posZ, double velX, double velY, double velZ, SpriteProvider sprite) {
        super(world, posX, posY, posZ, velX, velY, velZ, 1.0F, sprite);
    }

    @Override
    public int getColorMultiplier(float partialTick) {
        return 190 + (int) (20F * (1.0F - MinecraftClient.getInstance().options.gamma));
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
            return new JetpackSmokeParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
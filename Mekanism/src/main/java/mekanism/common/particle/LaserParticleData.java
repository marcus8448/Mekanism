package mekanism.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import javax.annotation.Nonnull;
import mekanism.common.registries.MekanismParticleTypes;
import mekanism.common.util.MekanismUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Direction;

public class LaserParticleData implements ParticleEffect {

    public static final Factory<LaserParticleData> DESERIALIZER = new Factory<LaserParticleData>() {
        @Nonnull
        @Override
        public LaserParticleData read(@Nonnull ParticleType<LaserParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            Direction direction = Direction.byId(reader.readInt());
            reader.expect(' ');
            double distance = reader.readDouble();
            reader.expect(' ');
            float energyScale = reader.readFloat();
            return new LaserParticleData(direction, distance, energyScale);
        }

        @Override
        public LaserParticleData read(@Nonnull ParticleType<LaserParticleData> type, PacketByteBuf buf) {
            return new LaserParticleData(buf.readEnumConstant(Direction.class), buf.readDouble(), buf.readFloat());
        }
    };
    public static final Codec<LaserParticleData> CODEC = RecordCodecBuilder.create(val -> val.group(
          MekanismUtils.DIRECTION_CODEC.fieldOf("direction").forGetter(data -> data.direction),
          Codec.DOUBLE.fieldOf("distance").forGetter(data -> data.distance),
          Codec.FLOAT.fieldOf("energyScale").forGetter(data -> data.energyScale)
    ).apply(val, LaserParticleData::new));

    public final Direction direction;
    public final double distance;
    public final float energyScale;

    public LaserParticleData(Direction direction, double distance, float energyScale) {
        this.direction = direction;
        this.distance = distance;
        this.energyScale = energyScale;
    }

    @Nonnull
    @Override
    public ParticleType<?> getType() {
        return MekanismParticleTypes.LASER.getParticleType();
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer) {
        buffer.writeEnumConstant(direction);
        buffer.writeDouble(distance);
        buffer.writeFloat(energyScale);
    }

    @Nonnull
    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %d %.2f %.2f", getType().getRegistryName(), direction.ordinal(), distance, energyScale);
    }
}
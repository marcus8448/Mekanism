package mekanism.common.world;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.CountTopSolidDecorator;

public class TopSolidRetrogenPlacement extends CountTopSolidDecorator {

    public TopSolidRetrogenPlacement(Codec<CountDecoratorConfig> configFactory) {
        super(configFactory);
    }

    @Nonnull
    @Override
    public Stream<BlockPos> getPositions(@Nonnull WorldAccess world, @Nonnull ChunkGenerator generator, @Nonnull Random random,
          CountDecoratorConfig config, @Nonnull BlockPos pos) {
        return IntStream.range(0, config.count).mapToObj(num -> {
            int i = random.nextInt(16) + pos.getX();
            int j = random.nextInt(16) + pos.getZ();
            //Use OCEAN_FLOOR instead of OCEAN_FLOOR_WG as the chunks are already generated
            int k = world.getTopY(Type.OCEAN_FLOOR, i, j);
            return new BlockPos(i, k, j);
        });
    }
}
package mekanism.common.lib.radiation;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class Meltdown {

    private static final int DURATION = 100;

    private final World world;
    private final BlockPos minPos, maxPos;
    private final double magnitude, chance;

    private int ticksExisted;

    public Meltdown(World world, BlockPos minPos, BlockPos maxPos, double magnitude, double chance) {
        this.world = world;
        this.minPos = minPos;
        this.maxPos = maxPos;
        this.magnitude = magnitude;
        this.chance = chance;
    }

    public boolean update() {
        ticksExisted++;

        if (world.random.nextInt() % 10 == 0 && world.random.nextDouble() < magnitude * chance) {
            world.createExplosion(null,
                  minPos.getX() + world.random.nextInt(maxPos.getX() - minPos.getX()),
                  minPos.getY() + world.random.nextInt(maxPos.getY() - minPos.getY()),
                  minPos.getZ() + world.random.nextInt(maxPos.getZ() - minPos.getZ()),
                  8, true, Explosion.DestructionType.DESTROY);
        }

        if (!world.canSetBlock(minPos) || !world.canSetBlock(maxPos)) {
            return true;
        }

        return ticksExisted >= DURATION;
    }
}

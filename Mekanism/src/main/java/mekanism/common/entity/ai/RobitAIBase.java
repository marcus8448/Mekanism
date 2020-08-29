package mekanism.common.entity.ai;

import mekanism.common.entity.EntityRobit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class RobitAIBase extends Goal {

    /**
     * The robit entity.
     */
    protected final EntityRobit theRobit;

    /**
     * The world the robit is located in.
     */
    protected final World world;

    /**
     * How fast the robit can travel.
     */
    protected final float moveSpeed;

    /**
     * The robit's pathfinder.
     */
    protected final EntityNavigation thePathfinder;

    /**
     * The ticker for updates.
     */
    protected int timeToRecalcPath;

    protected float oldWaterCost;

    protected RobitAIBase(EntityRobit entityRobit, float speed) {
        theRobit = entityRobit;
        world = entityRobit.world;
        moveSpeed = speed;
        thePathfinder = entityRobit.getNavigation();
    }

    @Override
    public void start() {
        timeToRecalcPath = 0;
        oldWaterCost = theRobit.getPathfindingPenalty(PathNodeType.WATER);
        theRobit.setPathfindingPenalty(PathNodeType.WATER, 0);
    }

    @Override
    public void stop() {
        thePathfinder.stop();
        theRobit.setPathfindingPenalty(PathNodeType.WATER, oldWaterCost);
    }

    protected void updateTask(Entity target) {
        theRobit.getLookControl().lookAt(target, 6, theRobit.getLookPitchSpeed() / 10F);
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10;
            if (!theRobit.hasVehicle()) {
                if (theRobit.squaredDistanceTo(target) >= 144.0) {
                    BlockPos targetPos = target.getBlockPos();
                    for (int i = 0; i < 10; i++) {
                        if (tryPathTo(target, targetPos.getX() + randomize(-3, 3), targetPos.getY() + randomize(-1, 1), targetPos.getZ() + randomize(-3, 3))) {
                            return;
                        }
                    }
                } else {
                    thePathfinder.startMovingTo(target, moveSpeed);
                }
            }
        }
    }

    private int randomize(int min, int max) {
        return theRobit.getRandom().nextInt(max - min + 1) + min;
    }

    private boolean tryPathTo(Entity target, int x, int y, int z) {
        if (Math.abs(x - target.getX()) < 2 && Math.abs(z - target.getZ()) < 2 || !canNavigate(new BlockPos(x, y, z))) {
            return false;
        }
        theRobit.refreshPositionAndAngles(x + 0.5, y, z + 0.5, theRobit.yaw, theRobit.pitch);
        thePathfinder.stop();
        return true;
    }

    private boolean canNavigate(BlockPos pos) {
        PathNodeType pathnodetype = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
        if (pathnodetype == PathNodeType.WALKABLE) {
            BlockPos blockpos = pos.subtract(theRobit.getBlockPos());
            return world.doesNotCollide(theRobit, theRobit.getBoundingBox().offset(blockpos));
        }
        return false;
    }
}
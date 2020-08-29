package mekanism.common.entity.ai;

import mekanism.common.entity.EntityRobit;
import net.minecraft.entity.player.PlayerEntity;

public class RobitAIFollow extends RobitAIBase {

    /**
     * The robit's owner.
     */
    private PlayerEntity theOwner;
    /**
     * The distance between the owner the robit must be at in order for the protocol to begin.
     */
    private final float maxDist;
    /**
     * The distance between the owner the robit must reach before it stops the protocol.
     */
    private final float minDist;

    public RobitAIFollow(EntityRobit entityRobit, float speed, float min, float max) {
        super(entityRobit, speed);
        minDist = min;
        maxDist = max;
    }

    @Override
    public boolean canStart() {
        PlayerEntity player = theRobit.getOwner();
        if (player == null || player.isSpectator()) {
            return false;
        } else if (theRobit.world.getRegistryKey() != player.world.getRegistryKey()) {
            return false;
        } else if (!theRobit.getFollowing()) {
            //Still looks up at the player if on chargepad or not following
            theRobit.getLookControl().lookAt(player, 6, theRobit.getLookPitchSpeed() / 10F);
            return false;
        } else if (theRobit.squaredDistanceTo(player) < (minDist * minDist)) {
            return false;
        } else if (theRobit.getEnergyContainer().isEmpty()) {
            return false;
        }
        theOwner = player;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return !thePathfinder.isIdle() && theRobit.squaredDistanceTo(theOwner) > (maxDist * maxDist) && theRobit.getFollowing() &&
               !theRobit.getEnergyContainer().isEmpty() && theOwner.world.getRegistryKey() == theRobit.world.getRegistryKey();
    }

    @Override
    public void stop() {
        theOwner = null;
        super.stop();
    }

    @Override
    public void tick() {
        if (theRobit.getFollowing()) {
            updateTask(theOwner);
        }
    }
}
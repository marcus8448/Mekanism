package mekanism.common.entity.ai;

import java.util.Iterator;
import java.util.List;
import mekanism.common.entity.EntityRobit;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;

public class RobitAIPickup extends RobitAIBase {

    private ItemEntity closest;

    public RobitAIPickup(EntityRobit entityRobit, float speed) {
        super(entityRobit, speed);
    }

    @Override
    public boolean canStart() {
        if (!theRobit.getDropPickup()) {
            return false;
        } else if (closest != null && closest.squaredDistanceTo(closest) > 100 && thePathfinder.findPathTo(closest, 0) != null) {
            return true;
        }
        //TODO: Look at and potentially mimic the way piglins search for items to pickup once their AI has mappings
        List<ItemEntity> items = theRobit.world.getNonSpectatingEntities(ItemEntity.class,
              new Box(theRobit.getX() - 10, theRobit.getY() - 10, theRobit.getZ() - 10,
                    theRobit.getX() + 10, theRobit.getY() + 10, theRobit.getZ() + 10));
        Iterator<ItemEntity> iter = items.iterator();
        //Cached for slight performance
        double closestDistance = -1;
        while (iter.hasNext()) {
            ItemEntity entity = iter.next();
            double distance = theRobit.distanceTo(entity);
            if (distance <= 10) {
                if (closestDistance == -1 || distance < closestDistance) {
                    if (thePathfinder.findPathTo(entity, 0) != null) {
                        closest = entity;
                        closestDistance = distance;
                    }
                }
            }
        }
        //No valid items
        return closest != null && closest.isAlive();
    }

    @Override
    public boolean shouldContinue() {
        return closest.isAlive() && !thePathfinder.isIdle() && theRobit.squaredDistanceTo(closest) > 100 && theRobit.getDropPickup() &&
               !theRobit.getEnergyContainer().isEmpty() && closest.world.getRegistryKey() == theRobit.world.getRegistryKey();
    }

    @Override
    public void tick() {
        if (theRobit.getDropPickup()) {
            updateTask(closest);
        }
    }
}
package mekanism.generators.common.tile.turbine;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.NBTConstants;
import mekanism.common.tile.prefab.TileEntityInternalMultiblock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class TileEntityTurbineRotor extends TileEntityInternalMultiblock {

    // Blades on this rotor
    public int blades = 0;

    // Position of this rotor, relative to bottom
    private int position = -1;
    //Rough radius of blades
    private int radius = -1;

    // Rendering helpers
    public float rotationLower;
    public float rotationUpper;

    public TileEntityTurbineRotor() {
        super(GeneratorsBlocks.TURBINE_ROTOR);
    }

    @Override
    public void onNeighborChange(Block block, BlockPos neighborPos) {
        super.onNeighborChange(block, neighborPos);
        if (!isRemote()) {
            updateRotors();
        }
    }

    public void updateRotors() {
        // In order to render properly, each rotor has to know its position, relative to other contiguous rotors
        // along the Y axis. When a neighbor changes, rescan the rotors and figure out everyone's position
        // N.B. must be in bottom->top order.

        // Find the bottom-most rotor and start scan from there
        TileEntityTurbineRotor rotor = getRotor(getPos().down());
        if (rotor == null) {
            // This is the bottom-most rotor, so start scan up
            scanRotors(0);
        } else {
            rotor.updateRotors();
        }
    }

    private void scanRotors(int index) {
        if (index != position) {
            // Our position has changed, update and generate an update packet for client
            position = index;
            updateRadius();
            if (blades > 0) {
                //Only send an update packet to the client if we actually have some blades installed
                // otherwise we don't bother updating the client on what position we are at as they do not
                // actually need it for rendering, and may not even have the tile placed yet
                sendUpdatePacket();
            }
        }

        // Pass the scan along to next rotor up, along with their new index
        TileEntityTurbineRotor rotor = getRotor(getPos().up());
        if (rotor != null) {
            rotor.scanRotors(index + 1);
        }
    }

    public boolean addBlade() {
        // If the the rotor beneath has less than two blades, add to it
        TileEntityTurbineRotor next = getRotor(getPos().down());
        if (next != null && next.blades < 2) {
            return next.addBlade();
        } else if (blades < 2) {
            // Add the blades to this rotor
            blades++;
            if (position == -1) {
                //If we haven't gotten a position assigned yet (single rotor height) then rescan it to set things to the correct values
                // This will also handle sending the update to the client
                scanRotors(0);
            } else {
                // Update client state
                sendUpdatePacket();
            }
            return true;
        }

        // This rotor and the rotor below are full up; pass the call
        // on up to the next rotor in stack
        next = getRotor(getPos().up());
        return next != null && next.addBlade();
    }

    public boolean removeBlade() {
        // If the the rotor above has any blades, remove them first
        TileEntityTurbineRotor next = getRotor(getPos().up());
        if (next != null && next.blades > 0) {
            return next.removeBlade();
        } else if (blades > 0) {
            // Remove blades from this rotor
            blades--;

            // Update client state
            sendUpdatePacket();
            return true;
        }

        // This rotor and the rotor above are empty; pass the call
        // on up to the next rotor in stack
        next = getRotor(getPos().down());
        return next != null && next.removeBlade();
    }


    public int getHousedBlades() {
        return blades;
    }

    public int getPosition() {
        return position;
    }

    private void updateRadius() {
        radius = 1 + position / 4;
    }

    @Nullable
    private TileEntityTurbineRotor getRotor(BlockPos pos) {
        return MekanismUtils.getTileEntity(TileEntityTurbineRotor.class, getWorld(), pos);
    }

    @Override
    public void fromTag(@Nonnull BlockState state, @Nonnull CompoundTag nbtTags) {
        super.fromTag(state, nbtTags);
        blades = nbtTags.getInt(NBTConstants.BLADES);
        position = nbtTags.getInt(NBTConstants.POSITION);
        updateRadius();
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull CompoundTag nbtTags) {
        super.toTag(nbtTags);
        nbtTags.putInt(NBTConstants.BLADES, getHousedBlades());
        nbtTags.putInt(NBTConstants.POSITION, getPosition());
        return nbtTags;
    }

    @Nonnull
    @Override
    public Box getRenderBoundingBox() {
        if (blades == 0 || radius == -1) {
            //If there are no blades default to the collision box of the rotor
            return super.getRenderBoundingBox();
        }
        return new Box(pos.add(-radius, 0, -radius), pos.add(1 + radius, 1, 1 + radius));
    }

    @Override
    public void setMultiblock(UUID id) {
        // Override the multiblock setter so that we can be sure to relay the ID down to the client; otherwise,
        // the rendering won't work properly
        super.setMultiblock(id);
        if (!isRemote()) {
            sendUpdatePacket();
        }
    }

    @Nonnull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag updateTag = super.getReducedUpdateTag();
        updateTag.putInt(NBTConstants.BLADES, blades);
        updateTag.putInt(NBTConstants.POSITION, position);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(BlockState state, @Nonnull CompoundTag tag) {
        super.handleUpdateTag(state, tag);
        int prevBlades = blades;
        int prevPosition = position;
        NBTUtils.setIntIfPresent(tag, NBTConstants.BLADES, value -> blades = value);
        NBTUtils.setIntIfPresent(tag, NBTConstants.POSITION, value -> {
            position = value;
            updateRadius();
        });
        if (prevBlades != blades || prevPosition != prevBlades) {
            rotationLower = 0;
            rotationUpper = 0;
        }
    }
}
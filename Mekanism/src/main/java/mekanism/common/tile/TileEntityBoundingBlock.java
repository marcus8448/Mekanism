package mekanism.common.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.NBTConstants;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

/**
 * Multi-block used by wind turbines, solar panels, and other machines
 */
public class TileEntityBoundingBlock extends TileEntityUpdateable {

    private BlockPos mainPos = BlockPos.ORIGIN;

    public boolean receivedCoords;

    private int currentRedstoneLevel;

    public TileEntityBoundingBlock() {
        this(MekanismTileEntityTypes.BOUNDING_BLOCK.getTileEntityType());
    }

    public TileEntityBoundingBlock(BlockEntityType<TileEntityBoundingBlock> type) {
        super(type);
    }

    public void setMainLocation(BlockPos pos) {
        receivedCoords = pos != null;
        if (!isRemote()) {
            mainPos = pos;
            sendUpdatePacket();
        }
    }

    public BlockPos getMainPos() {
        if (mainPos == null) {
            mainPos = BlockPos.ORIGIN;
        }
        return mainPos;
    }

    @Nullable
    public BlockEntity getMainTile() {
        return receivedCoords ? MekanismUtils.getTileEntity(world, getMainPos()) : null;
    }

    public void onNeighborChange(BlockState state) {
        final BlockEntity tile = getMainTile();
        if (tile instanceof TileEntityMekanism) {
            int power = world.getReceivedRedstonePower(getPos());
            if (currentRedstoneLevel != power) {
                if (power > 0) {
                    onPower();
                } else {
                    onNoPower();
                }
                currentRedstoneLevel = power;
                ((TileEntityMekanism) tile).sendUpdatePacket(this);
            }
        }
    }

    public void onPower() {
    }

    public void onNoPower() {
    }

    @Override
    public void fromTag(@Nonnull BlockState state, @Nonnull CompoundTag nbtTags) {
        super.fromTag(state, nbtTags);
        NBTUtils.setBlockPosIfPresent(nbtTags, NBTConstants.MAIN, pos -> mainPos = pos);
        currentRedstoneLevel = nbtTags.getInt(NBTConstants.REDSTONE);
        receivedCoords = nbtTags.getBoolean(NBTConstants.RECEIVED_COORDS);
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull CompoundTag nbtTags) {
        super.toTag(nbtTags);
        nbtTags.put(NBTConstants.MAIN, NbtHelper.fromBlockPos(getMainPos()));
        nbtTags.putInt(NBTConstants.REDSTONE, currentRedstoneLevel);
        nbtTags.putBoolean(NBTConstants.RECEIVED_COORDS, receivedCoords);
        return nbtTags;
    }

    @Nonnull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag updateTag = super.getReducedUpdateTag();
        updateTag.put(NBTConstants.MAIN, NbtHelper.fromBlockPos(getMainPos()));
        updateTag.putInt(NBTConstants.REDSTONE, currentRedstoneLevel);
        updateTag.putBoolean(NBTConstants.RECEIVED_COORDS, receivedCoords);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(BlockState state, @Nonnull CompoundTag tag) {
        super.handleUpdateTag(state, tag);
        NBTUtils.setBlockPosIfPresent(tag, NBTConstants.MAIN, pos -> mainPos = pos);
        currentRedstoneLevel = tag.getInt(NBTConstants.REDSTONE);
        receivedCoords = tag.getBoolean(NBTConstants.RECEIVED_COORDS);
    }
}
package mekanism.common.tile;

import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.common.block.BlockCardboardBox.BlockData;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.util.NBTUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;

public class TileEntityCardboardBox extends TileEntityUpdateable {

    public BlockData storedData;

    public TileEntityCardboardBox() {
        super(MekanismTileEntityTypes.CARDBOARD_BOX.getTileEntityType());
    }

    @Override
    public void fromTag(@Nonnull BlockState state, @Nonnull CompoundTag nbtTags) {
        super.fromTag(state, nbtTags);
        NBTUtils.setCompoundIfPresent(nbtTags, NBTConstants.DATA, nbt -> storedData = BlockData.read(nbt));
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull CompoundTag nbtTags) {
        super.toTag(nbtTags);
        if (storedData != null) {
            nbtTags.put(NBTConstants.DATA, storedData.write(new CompoundTag()));
        }
        return nbtTags;
    }
}
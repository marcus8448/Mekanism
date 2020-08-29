package mekanism.common.item.block;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.machine.ItemBlockMachine;
import mekanism.common.tile.TileEntityModificationStation;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ItemBlockModificationStation extends ItemBlockMachine {

    public ItemBlockModificationStation(BlockTileModel<TileEntityModificationStation, BlockTypeTile<TileEntityModificationStation>> block) {
        super(block);
    }

    @Override
    public boolean place(@Nonnull ItemPlacementContext context, @Nonnull BlockState state) {
        Direction side = MekanismUtils.getRight(Attribute.getFacing(state));
        BlockPos pos = context.getBlockPos();
        List<BlockPos> checkList = Arrays.asList(pos.up(), pos.offset(side), pos.up().offset(side));
        return MekanismUtils.areBlocksValidAndReplaceable(context.getWorld(), checkList) && super.place(context, state);
    }
}
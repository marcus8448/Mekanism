package mekanism.common.item.block.machine;

import javax.annotation.Nonnull;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.tile.machine.TileEntityIsotopicCentrifuge;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;

public class ItemBlockIsotopicCentrifuge extends ItemBlockMachine {

    public ItemBlockIsotopicCentrifuge(BlockTile<TileEntityIsotopicCentrifuge, Machine<TileEntityIsotopicCentrifuge>> block) {
        super(block);
    }

    @Override
    public boolean place(@Nonnull ItemPlacementContext context, @Nonnull BlockState state) {
        if (!MekanismUtils.isValidReplaceableBlock(context.getWorld(), context.getBlockPos().up())) {
            //If there isn't room then fail
            return false;
        }
        return super.place(context, state);
    }
}

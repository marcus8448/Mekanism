package mekanism.common.block.basic;

import javax.annotation.Nonnull;
import mekanism.common.block.prefab.BlockTileGlass;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.tile.prefab.TileEntityStructuralMultiblock;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStructuralGlass<TILE extends TileEntityStructuralMultiblock> extends BlockTileGlass<TILE, BlockTypeTile<TILE>> {

    public BlockStructuralGlass(BlockTypeTile<TILE> type) {
        super(type);
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        TileEntityStructuralMultiblock tile = MekanismUtils.getTileEntity(TileEntityStructuralMultiblock.class, world, pos);
        if (tile == null) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() instanceof BlockItem && new ItemPlacementContext(player, hand, stack, hit).canPlace()) {
                if (!tile.structuralGuiAccessAllowed() || !tile.hasFormedMultiblock()) {
                    //If the block's multiblock doesn't allow gui access via structural multiblocks (for example the evaporation plant),
                    // or if the multiblock is not formed then pass
                    return ActionResult.PASS;
                }
            }
            return ActionResult.SUCCESS;
        }
        return tile.onActivate(player, hand, player.getStackInHand(hand));
    }
}

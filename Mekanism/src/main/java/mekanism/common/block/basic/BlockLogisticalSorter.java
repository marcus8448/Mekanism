package mekanism.common.block.basic;

import javax.annotation.Nonnull;
import mekanism.api.IMekWrench;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.registries.MekanismBlockTypes;
import mekanism.common.tile.TileEntityLogisticalSorter;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BlockLogisticalSorter extends BlockTileModel<TileEntityLogisticalSorter, Machine<TileEntityLogisticalSorter>> {

    public BlockLogisticalSorter() {
        super(MekanismBlockTypes.LOGISTICAL_SORTER);
    }

    @Override
    public void setTileData(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, @Nonnull TileEntityMekanism tile) {
        if (tile instanceof TileEntityLogisticalSorter) {
            TileEntityLogisticalSorter transporter = (TileEntityLogisticalSorter) tile;
            if (!transporter.hasConnectedInventory()) {
                BlockPos tilePos = tile.getPos();
                for (Direction dir : EnumUtils.DIRECTIONS) {
                    BlockEntity tileEntity = MekanismUtils.getTileEntity(world, tilePos.offset(dir));
                    if (InventoryUtils.isItemHandler(tileEntity, dir)) {
                        transporter.setFacing(dir.getOpposite());
                        break;
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        TileEntityLogisticalSorter tile = MekanismUtils.getTileEntity(TileEntityLogisticalSorter.class, world, pos);
        if (tile == null) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return genericClientActivated(player, hand, hit);
        }
        //TODO: Make this be moved into the logistical sorter tile
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isEmpty()) {
            IMekWrench wrenchHandler = MekanismUtils.getWrench(stack);
            if (wrenchHandler != null) {
                if (wrenchHandler.canUseWrench(stack, player, hit.getBlockPos())) {
                    if (SecurityUtils.canAccess(player, tile)) {
                        if (player.isSneaking()) {
                            MekanismUtils.dismantleBlock(state, world, pos);
                            return ActionResult.SUCCESS;
                        }
                        Direction change = tile.getDirection().rotateYClockwise();
                        if (!tile.hasConnectedInventory()) {
                            for (Direction dir : EnumUtils.DIRECTIONS) {
                                BlockEntity tileEntity = MekanismUtils.getTileEntity(world, pos.offset(dir));
                                if (InventoryUtils.isItemHandler(tileEntity, dir)) {
                                    change = dir.getOpposite();
                                    break;
                                }
                            }
                        }
                        tile.setFacing(change);
                        world.updateNeighborsAlways(pos, this);
                    } else {
                        SecurityUtils.displayNoAccess(player);
                    }
                    return ActionResult.SUCCESS;
                }
            }
        }
        return tile.openGui(player);
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState state, @Nonnull Direction dir, @Nonnull BlockState facingState, @Nonnull WorldAccess world, @Nonnull BlockPos pos,
          @Nonnull BlockPos neighborPos) {
        if (!world.isClient()) {
            TileEntityLogisticalSorter sorter = MekanismUtils.getTileEntity(TileEntityLogisticalSorter.class, world, pos);
            if (sorter != null && !sorter.hasConnectedInventory()) {
                BlockEntity tileEntity = MekanismUtils.getTileEntity(world, neighborPos);
                if (InventoryUtils.isItemHandler(tileEntity, dir)) {
                    sorter.setFacing(dir.getOpposite());
                    state = sorter.getCachedState();
                }
            }
        }
        return super.getStateForNeighborUpdate(state, dir, facingState, world, pos, neighborPos);
    }
}
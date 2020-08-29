package mekanism.generators.common.block.turbine;

import javax.annotation.Nonnull;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.item.ItemTurbineBlade;
import mekanism.generators.common.registries.GeneratorsBlockTypes;
import mekanism.generators.common.registries.GeneratorsItems;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockTurbineRotor extends BlockTileModel<TileEntityTurbineRotor, BlockTypeTile<TileEntityTurbineRotor>> {

    private static final VoxelShape bounds = createCuboidShape(6, 0, 6, 10, 16, 10);

    public BlockTurbineRotor() {
        super(GeneratorsBlockTypes.TURBINE_ROTOR);
    }

    @Override
    @Deprecated
    public void onStateReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (!world.isClient && state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
            TileEntityTurbineRotor tile = MekanismUtils.getTileEntity(TileEntityTurbineRotor.class, world, pos);
            if (tile != null) {
                int amount = tile.getHousedBlades();
                if (amount > 0) {
                    dropStack(world, pos, GeneratorsItems.TURBINE_BLADE.getItemStack(amount));
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, isMoving);
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        TileEntityTurbineRotor tile = MekanismUtils.getTileEntity(TileEntityTurbineRotor.class, world, pos);
        if (tile == null) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return genericClientActivated(player, hand, hit);
        }
        if (tile.tryWrench(state, player, hand, hit) != WrenchResult.PASS) {
            return ActionResult.SUCCESS;
        }
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isSneaking()) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemTurbineBlade) {
                if (tile.addBlade()) {
                    if (!player.isCreative()) {
                        stack.decrement(1);
                        if (stack.getCount() == 0) {
                            player.setStackInHand(hand, ItemStack.EMPTY);
                        }
                    }
                }
            }
        } else if (stack.isEmpty()) {
            if (tile.removeBlade()) {
                if (!player.isCreative()) {
                    player.setStackInHand(hand, GeneratorsItems.TURBINE_BLADE.getItemStack());
                    player.inventory.markDirty();
                }
            }
        } else if (stack.getItem() instanceof ItemTurbineBlade) {
            if (stack.getCount() < stack.getMaxCount()) {
                if (tile.removeBlade()) {
                    if (!player.isCreative()) {
                        stack.increment(1);
                        player.inventory.markDirty();
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getOutlineShape(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, @Nonnull ShapeContext context) {
        return bounds;
    }
}
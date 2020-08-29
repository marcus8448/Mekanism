package mekanism.common.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.Mekanism;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO: Extend MekanismBlock. Not done yet as checking is needed to ensure how drops happen still happens correctly and things in the super class don't mess this up
public class BlockBounding extends Block implements IHasTileEntity<TileEntityBoundingBlock>, IStateFluidLoggable {

    @Nullable
    public static BlockPos getMainBlockPos(BlockView world, BlockPos thisPos) {
        TileEntityBoundingBlock te = MekanismUtils.getTileEntity(TileEntityBoundingBlock.class, world, thisPos);
        if (te != null && te.receivedCoords && !thisPos.equals(te.getMainPos())) {
            return te.getMainPos();
        }
        return null;
    }

    //Note: This is not a block state so that we can have it easily create the correct TileEntity.
    // If we end up merging some logic from the TileEntities then this can become a property
    private final boolean advanced;

    public BlockBounding(boolean advanced) {
        //Note: We require setting variable opacity so that the block state does not cache the ability of if blocks can be placed on top of the bounding block
        // Torches cannot be placed on the sides due to vanilla checking the incorrect shape
        super(Block.Properties.of(Material.METAL).strength(3.5F, 8F).requiresTool().dynamicBounds());
        this.advanced = advanced;
        setDefaultState(BlockStateHelper.getDefaultState(stateManager.getDefaultState()));
    }

    @Override
    protected void appendProperties(@Nonnull StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        BlockStateHelper.fillBlockStateContainer(this, builder);
    }

    @Nonnull
    @Override
    @Deprecated
    public PistonBehavior getPistonBehavior(@Nonnull BlockState state) {
        //Protect against mods like Quark that allow blocks with TEs to be moved
        return PistonBehavior.BLOCK;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(@Nonnull ItemPlacementContext context) {
        return BlockStateHelper.getStateForPlacement(this, super.getPlacementState(context), context);
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            return ActionResult.FAIL;
        }
        BlockState state1 = world.getBlockState(mainPos);
        //TODO: Use proper ray trace result, currently is using the one we got but we probably should make one with correct position information
        return state1.getBlock().onUse(state1, world, mainPos, player, hand, hit);
    }

    @Override
    @Deprecated
    public void onStateReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        //Remove the main block if a bounding block gets broken by being directly replaced
        // Note: We only do this if we don't go from bounding block to bounding block
        if (state.getBlock() != newState.getBlock()) {
            BlockPos mainPos = getMainBlockPos(world, pos);
            if (mainPos != null) {
                BlockState mainState = world.getBlockState(mainPos);
                if (!mainState.isAir(world, mainPos)) {
                    //Set the main block to air, which will invalidate the rest of the bounding blocks
                    world.removeBlock(mainPos, false);
                }
            }
            super.onStateReplaced(state, world, pos, newState, isMoving);
        }
    }

    /**
     * {@inheritDoc} Delegate to main {@link Block#getPickBlock(BlockState, RayTraceResult, IBlockReader, BlockPos, PlayerEntity)}.
     */
    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull BlockState state, HitResult target, @Nonnull BlockView world, @Nonnull BlockPos pos, PlayerEntity player) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            return ItemStack.EMPTY;
        }
        BlockState state1 = world.getBlockState(mainPos);
        return state1.getBlock().getPickBlock(state1, target, world, mainPos, player);
    }

    @Override
    public boolean removedByPlayer(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, boolean willHarvest,
          FluidState fluidState) {
        if (willHarvest) {
            return true;
        }
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            if (!mainState.isAir(world, mainPos)) {
                //Set the main block to air, which will invalidate the rest of the bounding blocks
                mainState.removedByPlayer(world, mainPos, player, false, world.getFluidState(mainPos));
            }
        }
        return super.removedByPlayer(state, world, pos, player, false, fluidState);
    }

    @Override
    public void afterBreak(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull BlockPos pos, @Nonnull BlockState state, BlockEntity te,
          @Nonnull ItemStack stack) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            mainState.getBlock().afterBreak(world, player, mainPos, mainState, MekanismUtils.getTileEntity(world, mainPos), stack);
        } else {
            super.afterBreak(world, player, pos, state, te, stack);
        }
        world.removeBlock(pos, false);
    }

    @Override
    @Deprecated
    public void neighborUpdate(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighborPos,
          boolean isMoving) {
        TileEntityBoundingBlock tile = MekanismUtils.getTileEntity(TileEntityBoundingBlock.class, world, pos);
        if (tile != null) {
            tile.onNeighborChange(state);
        }
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos != null) {
            world.getBlockState(mainPos).neighborUpdate(world, mainPos, neighborBlock, neighborPos, isMoving);
        }
    }

    @Override
    @Deprecated
    public float calcBlockBreakingDelta(@Nonnull BlockState state, @Nonnull PlayerEntity player, @Nonnull BlockView world, @Nonnull BlockPos pos) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            return super.calcBlockBreakingDelta(state, player, world, pos);
        }
        return world.getBlockState(mainPos).calcBlockBreakingDelta(player, world, mainPos);
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockRenderType getRenderType(@Nonnull BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockEntity createTileEntity(@Nonnull BlockState state, @Nonnull BlockView world) {
        return getTileType().instantiate();
    }

    @Override
    public BlockEntityType<TileEntityBoundingBlock> getTileType() {
        if (advanced) {
            return MekanismTileEntityTypes.ADVANCED_BOUNDING_BLOCK.getTileEntityType();
        }
        return MekanismTileEntityTypes.BOUNDING_BLOCK.getTileEntityType();
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getOutlineShape(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, @Nonnull ShapeContext context) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            //If we don't have a main pos, then act as if the block is empty so that we can move into it properly
            return VoxelShapes.empty();
        }
        BlockState mainState;
        try {
            mainState = world.getBlockState(mainPos);
        } catch (ArrayIndexOutOfBoundsException e) {
            //Note: ChunkRenderCache is client side only, though it does not seem to have any class loading issues on the server
            // due to this exception not being caught in that specific case
            if (world instanceof ChunkRendererRegion) {
                //Workaround for when the main spot of the miner is out of bounds of the ChunkRenderCache thus causing an
                // ArrayIndexOutOfBoundException on the client as seen by:
                // https://github.com/mekanism/Mekanism/issues/5792
                // https://github.com/mekanism/Mekanism/issues/5844
                world = ((ChunkRendererRegion) world).world;
                mainState = world.getBlockState(mainPos);
            } else {
                Mekanism.logger.error("Error getting bounding block shape, for position {}, with main position {}. World of type {}", pos, mainPos, world.getClass().getName());
                return VoxelShapes.empty();
            }
        }
        VoxelShape shape = mainState.getOutlineShape(world, mainPos, context);
        BlockPos offset = pos.subtract(mainPos);
        //TODO: Can we somehow cache the withOffset? It potentially would have to then be moved into the Tile, but that is probably fine
        return shape.offset(-offset.getX(), -offset.getY(), -offset.getZ());
    }

    @Nonnull
    @Override
    @Deprecated
    public FluidState getFluidState(@Nonnull BlockState state) {
        return getFluid(state);
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getStateForNeighborUpdate(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull WorldAccess world,
          @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        updateFluids(state, world, currentPos);
        return super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    @Deprecated
    public boolean canPathfindThrough(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, @Nonnull NavigationType type) {
        //Mark that bounding blocks do not allow movement for use by AI pathing
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean addHitEffects(BlockState state, World world, HitResult target, ParticleManager manager) {
        if (target.getType() == Type.BLOCK && target instanceof BlockHitResult) {
            BlockHitResult blockTarget = (BlockHitResult) target;
            BlockPos pos = blockTarget.getBlockPos();
            BlockPos mainPos = getMainBlockPos(world, pos);
            if (mainPos != null) {
                BlockState mainState = world.getBlockState(mainPos);
                if (!mainState.isAir(world, mainPos)) {
                    //Copy of ParticleManager#addBlockHitEffects except using the block state for the main position
                    Box axisalignedbb = state.getOutlineShape(world, pos).getBoundingBox();
                    double x = pos.getX() + world.random.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2) + 0.1 + axisalignedbb.minX;
                    double y = pos.getY() + world.random.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2) + 0.1 + axisalignedbb.minY;
                    double z = pos.getZ() + world.random.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2) + 0.1 + axisalignedbb.minZ;
                    Direction side = blockTarget.getSide();
                    if (side == Direction.DOWN) {
                        y = pos.getY() + axisalignedbb.minY - 0.1;
                    } else if (side == Direction.UP) {
                        y = pos.getY() + axisalignedbb.maxY + 0.1;
                    } else if (side == Direction.NORTH) {
                        z = pos.getZ() + axisalignedbb.minZ - 0.1;
                    } else if (side == Direction.SOUTH) {
                        z = pos.getZ() + axisalignedbb.maxZ + 0.1;
                    } else if (side == Direction.WEST) {
                        x = pos.getX() + axisalignedbb.minX - 0.1;
                    } else if (side == Direction.EAST) {
                        x = pos.getX() + axisalignedbb.maxX + 0.1;
                    }
                    manager.addParticle(new BlockDustParticle((ClientWorld) world, x, y, z, 0, 0, 0, mainState)
                          .setBlockPos(mainPos).move(0.2F).scale(0.6F));
                    return true;
                }
            }
        }
        return false;
    }
}
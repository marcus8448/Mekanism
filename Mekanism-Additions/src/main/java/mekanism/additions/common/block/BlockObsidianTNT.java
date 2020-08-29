package mekanism.additions.common.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.additions.common.entity.EntityObsidianTNT;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.util.VoxelShapeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;

public class BlockObsidianTNT extends TntBlock implements IStateFluidLoggable {

    private static final VoxelShape bounds = VoxelShapeUtils.combine(
          createCuboidShape(0, 0, 0, 16, 3, 16),//Wooden1
          createCuboidShape(0, 8, 0, 16, 11, 16),//Wooden2
          createCuboidShape(12.5, 11.8, 12.5, 13.5, 13.8, 13.5),//Wick1
          createCuboidShape(12.5, 11.5, 7.5, 13.5, 13.5, 8.5),//Wick2
          createCuboidShape(12.5, 11.8, 2.5, 13.5, 13.8, 3.5),//Wick3
          createCuboidShape(2.5, 11.8, 12.5, 3.5, 13.8, 13.5),//Wick4
          createCuboidShape(2.5, 11.5, 7.5, 3.5, 13.5, 8.5),//Wick5
          createCuboidShape(2.5, 11.8, 2.5, 3.5, 13.8, 3.5),//Wick6
          createCuboidShape(7.5, 11.5, 12.5, 8.5, 13.5, 13.5),//Wick7
          createCuboidShape(7.5, 11.5, 2.5, 8.5, 13.5, 3.5),//Wick8
          createCuboidShape(7.5, 11.8, 7.5, 8.5, 13.8, 8.5),//Wick9
          createCuboidShape(11, -1, 11, 15, 12, 15),//Rod1
          createCuboidShape(11, -1, 6, 15, 12, 10),//Rod2
          createCuboidShape(11, -1, 1, 15, 12, 5),//Rod3
          createCuboidShape(6, -1, 1, 10, 12, 5),//Rod4
          createCuboidShape(6, -1, 6, 10, 12, 10),//Rod5
          createCuboidShape(6, -1, 11, 10, 12, 15),//Rod6
          createCuboidShape(1, -1, 6, 5, 12, 10),//Rod7
          createCuboidShape(1, -1, 11, 5, 12, 15),//Rod8
          createCuboidShape(1, -1, 1, 5, 12, 5)//Rod9
    );

    public BlockObsidianTNT() {
        super(Block.Properties.of(Material.TNT));
        //Uses getDefaultState as starting state to take into account the stuff from super
        setDefaultState(BlockStateHelper.getDefaultState(getDefaultState()));
    }

    @Override
    protected void appendProperties(@Nonnull StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        BlockStateHelper.fillBlockStateContainer(this, builder);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(@Nonnull ItemPlacementContext context) {
        return BlockStateHelper.getStateForPlacement(this, super.getPlacementState(context), context);
    }

    @Override
    public int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face) {
        //300 is 100% chance fire will spread to this block, 100 is default for TNT
        // Given we are "obsidian" make ours slightly more stable against fire being spread than vanilla TNT
        return 75;
    }

    @Override
    public void catchFire(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nullable Direction side, @Nullable LivingEntity igniter) {
        if (!world.isClient) {
            TntEntity tnt = new EntityObsidianTNT(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, igniter);
            world.spawnEntity(tnt);
            world.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void onDestroyedByExplosion(World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
        if (!world.isClient) {
            TntEntity tnt = new EntityObsidianTNT(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, explosion.getCausingEntity());
            tnt.setFuse((short) (world.random.nextInt(tnt.getFuseTimer() / 4) + tnt.getFuseTimer() / 8));
            world.spawnEntity(tnt);
        }
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getOutlineShape(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, @Nonnull ShapeContext context) {
        return bounds;
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
}
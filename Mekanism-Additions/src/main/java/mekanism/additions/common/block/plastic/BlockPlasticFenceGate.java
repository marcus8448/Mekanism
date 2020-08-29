package mekanism.additions.common.block.plastic;

import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.common.block.interfaces.IColoredBlock;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.IStateFluidLoggable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraftforge.common.ToolType;

public class BlockPlasticFenceGate extends FenceGateBlock implements IColoredBlock, IStateFluidLoggable {

    private final EnumColor color;

    public BlockPlasticFenceGate(EnumColor color) {
        super(Settings.of(BlockPlastic.PLASTIC, color.getMapColor()).strength(5F, 10F).harvestTool(ToolType.PICKAXE));
        this.color = color;
        this.setDefaultState(getDefaultState().with(BlockStateHelper.FLUID_LOGGED, false));
    }

    @Override
    public EnumColor getColor() {
        return color;
    }

    @Override
    public BlockState getPlacementState(@Nonnull ItemPlacementContext context) {
        return BlockStateHelper.getStateForPlacement(this, super.getPlacementState(context), context);
    }

    @Nonnull
    @Override
    @Deprecated
    public FluidState getFluidState(@Nonnull BlockState state) {
        return getFluid(state);
    }

    @Nonnull
    @Override
    public BlockState getStateForNeighborUpdate(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull WorldAccess world,
          @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        updateFluids(state, world, currentPos);
        return super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    protected void appendProperties(@Nonnull StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        BlockStateHelper.fillBlockStateContainer(this, builder);
    }
}
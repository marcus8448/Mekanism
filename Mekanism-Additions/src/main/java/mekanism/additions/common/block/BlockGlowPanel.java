package mekanism.additions.common.block;

import javax.annotation.Nonnull;
import mekanism.additions.common.registries.AdditionsBlockTypes;
import mekanism.api.text.EnumColor;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.block.interfaces.IColoredBlock;
import mekanism.common.block.prefab.BlockBase.BlockBaseModel;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class BlockGlowPanel extends BlockBaseModel<BlockType> implements IColoredBlock {

    private static final VoxelShape[] MIN_SHAPES = new VoxelShape[EnumUtils.DIRECTIONS.length];

    static {
        VoxelShapeUtils.setShape(createCuboidShape(4, 0, 4, 12, 16, 12), MIN_SHAPES, true);
    }

    private final EnumColor color;

    public BlockGlowPanel(EnumColor color) {
        super(AdditionsBlockTypes.GLOW_PANEL, Block.Properties.of(Material.PISTON, color.getMapColor()).strength(1F, 10F).lightLevel(state -> 15));
        this.color = color;
    }

    @Override
    public EnumColor getColor() {
        return color;
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull WorldAccess world,
          @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        if (facing.getOpposite() == Attribute.get(state.getBlock(), AttributeStateFacing.class).getDirection(state) && !state.canPlaceAt(world, currentPos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    @Deprecated
    public boolean canPlaceAt(BlockState state, @Nonnull WorldView world, @Nonnull BlockPos pos) {
        Direction side = Attribute.get(state.getBlock(), AttributeStateFacing.class).getDirection(state);
        Direction sideOn = side.getOpposite();
        BlockPos offsetPos = pos.offset(sideOn);
        VoxelShape projected = world.getBlockState(offsetPos).getCollisionShape(world, offsetPos).getFace(side);
        //hasEnoughSolidSide does not quite work for us, as the shape is incorrect
        //Don't allow placing on leaves or a block that is too small
        // same restrictions as vanilla except we have a better check for placing against the side
        return !state.isIn(BlockTags.LEAVES) && !VoxelShapes.matchesAnywhere(projected, MIN_SHAPES[sideOn.ordinal()], BooleanBiFunction.ONLY_SECOND);
    }
}
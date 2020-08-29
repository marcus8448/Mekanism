package mekanism.additions.common.block.plastic;

import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.common.block.interfaces.IColoredBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction.Location;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraftforge.common.ToolType;

public class BlockPlasticTransparentSlab extends SlabBlock implements IColoredBlock {

    private final EnumColor color;

    public BlockPlasticTransparentSlab(EnumColor color) {
        super(Block.Properties.of(BlockPlastic.PLASTIC, color.getMapColor()).strength(5F, 10F).nonOpaque().harvestTool(ToolType.PICKAXE));
        this.color = color;
    }

    @Override
    public EnumColor getColor() {
        return color;
    }

    @Override
    @Deprecated
    public float getAmbientOcclusionLightLevel(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos) {
        return 0.8F;
    }

    @Override
    @Deprecated
    public boolean hasSidedTransparency(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public boolean isTranslucent(@Nonnull BlockState state, @Nonnull BlockView reader, @Nonnull BlockPos pos) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, BlockView world, BlockPos pos, Location type, EntityType<?> entityType) {
        return false;
    }

    @Override
    public boolean isSideInvisible(@Nonnull BlockState state, @Nonnull BlockState adjacentBlockState, @Nonnull Direction side) {
        final Block adjacentBlock = adjacentBlockState.getBlock();
        if (adjacentBlock instanceof BlockPlasticTransparent || adjacentBlock instanceof BlockPlasticTransparentSlab
            || adjacentBlock instanceof BlockPlasticTransparentStairs) {
            IColoredBlock plastic = ((IColoredBlock) adjacentBlock);
            if (plastic.getColor() == getColor()) {
                try {
                    VoxelShape shape = state.getOutlineShape(null, null);
                    VoxelShape adjacentShape = adjacentBlockState.getOutlineShape(null, null);

                    VoxelShape faceShape = shape.getFace(side);
                    VoxelShape adjacentFaceShape = adjacentShape.getFace(side.getOpposite());
                    return !VoxelShapes.matchesAnywhere(faceShape, adjacentFaceShape, BooleanBiFunction.ONLY_FIRST);
                } catch (Exception ignored) {
                    //Something might have errored due to the null world and position
                }
            }
        }
        return false;
    }
}
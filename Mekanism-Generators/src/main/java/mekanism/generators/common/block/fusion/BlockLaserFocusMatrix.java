package mekanism.generators.common.block.fusion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.block.basic.BlockStructuralGlass;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.generators.common.registries.GeneratorsBlockTypes;
import mekanism.generators.common.tile.fusion.TileEntityLaserFocusMatrix;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction.Location;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

public class BlockLaserFocusMatrix extends BlockBasicMultiblock<TileEntityLaserFocusMatrix> {

    public BlockLaserFocusMatrix() {
        super(GeneratorsBlockTypes.LASER_FOCUS_MATRIX, Block.Properties.of(Material.GLASS).strength(3.5F, 8F).nonOpaque());
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockRenderView world, BlockPos pos, FluidState fluidState) {
        return true;
    }

    @Override
    @Deprecated
    public boolean isSideInvisible(@Nonnull BlockState state, @Nonnull BlockState adjacentBlockState, @Nonnull Direction side) {
        Block blockOffset = adjacentBlockState.getBlock();
        if (blockOffset instanceof BlockStructuralGlass || blockOffset instanceof BlockLaserFocusMatrix) {
            return true;
        }
        return super.isSideInvisible(state, adjacentBlockState, side);
    }

    @Override
    @Deprecated
    public float getAmbientOcclusionLightLevel(@Nonnull BlockState state, @Nonnull BlockView worldIn, @Nonnull BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTranslucent(@Nonnull BlockState state, @Nonnull BlockView reader, @Nonnull BlockPos pos) {
        return true;
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getVisualShape(@Nonnull BlockState state, @Nonnull BlockView reader, @Nonnull BlockPos pos, @Nonnull ShapeContext ctx) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, Location type, @Nullable EntityType<?> entityType) {
        return false;
    }
}
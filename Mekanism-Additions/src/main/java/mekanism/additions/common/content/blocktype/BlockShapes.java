package mekanism.additions.common.content.blocktype;

import static mekanism.common.util.VoxelShapeUtils.setShape;
import static net.minecraft.block.Block.createCuboidShape;

import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;
import net.minecraft.util.shape.VoxelShape;

public final class BlockShapes {

    public static final VoxelShape[] GLOW_PANEL = new VoxelShape[EnumUtils.DIRECTIONS.length];

    static {
        setShape(VoxelShapeUtils.combine(
              createCuboidShape(4, 14, 4, 12, 16, 12),
              createCuboidShape(5, 13.5, 5, 11, 14, 11)
        ), GLOW_PANEL, true);
    }
}

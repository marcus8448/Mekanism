package mekanism.additions.common.block.plastic;

import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.common.block.interfaces.IColoredBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockPlasticRoad extends Block implements IColoredBlock {

    private final EnumColor color;

    public BlockPlasticRoad(EnumColor color) {
        super(Block.Properties.of(BlockPlastic.PLASTIC, color.getMapColor()).strength(5F, 10F).harvestTool(ToolType.PICKAXE));
        this.color = color;
    }

    @Override
    public EnumColor getColor() {
        return color;
    }

    @Override
    public void onSteppedOn(@Nonnull World world, @Nonnull BlockPos pos, Entity entity) {
        double boost = 1.6;
        Vec3d motion = entity.getVelocity();
        double a = Math.atan2(motion.getX(), motion.getZ());
        float slipperiness = getSlipperiness(world.getBlockState(pos), world, pos, entity);
        motion = motion.add(Math.sin(a) * boost * slipperiness, 0, Math.cos(a) * boost * slipperiness);
        entity.setVelocity(motion);
    }
}
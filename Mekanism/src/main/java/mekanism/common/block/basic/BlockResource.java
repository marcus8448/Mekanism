package mekanism.common.block.basic;

import javax.annotation.Nonnull;
import mekanism.common.block.BlockMekanism;
import mekanism.common.resource.BlockResourceInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraftforge.common.ToolType;

public class BlockResource extends BlockMekanism {

    @Nonnull
    private final BlockResourceInfo resource;

    //TODO: Isn't as "generic"? So make it be from one BlockType thing?
    public BlockResource(@Nonnull BlockResourceInfo resource) {
        super(Block.Properties.of(Material.METAL).strength(resource.getHardness(), resource.getResistance()).lightLevel(state -> resource.getLightValue())
              .requiresTool().harvestTool(ToolType.PICKAXE).harvestLevel(resource.getHarvestLevel()));
        this.resource = resource;
    }

    @Nonnull
    public BlockResourceInfo getResourceInfo() {
        return resource;
    }

    @Nonnull
    @Override
    @Deprecated
    public PistonBehavior getPistonBehavior(@Nonnull BlockState state) {
        return resource.getPushReaction();
    }

    @Override
    public boolean isPortalFrame(BlockState state, WorldView world, BlockPos pos) {
        return resource.isPortalFrame();
    }
}
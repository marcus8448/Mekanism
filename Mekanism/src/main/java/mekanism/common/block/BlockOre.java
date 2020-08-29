package mekanism.common.block;

import javax.annotation.Nonnull;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.resource.OreType;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraftforge.common.ToolType;

public class BlockOre extends Block implements IHasDescription {

    private final OreType ore;

    public BlockOre(OreType ore) {
        super(Block.Properties.of(Material.STONE).strength(3F, 5F).requiresTool().harvestTool(ToolType.PICKAXE).harvestLevel(1));
        this.ore = ore;
    }

    @Nonnull
    @Override
    public ILangEntry getDescription() {
        return () -> "description.mekanism." + ore.getResource().getRegistrySuffix() + "_ore";
    }
}
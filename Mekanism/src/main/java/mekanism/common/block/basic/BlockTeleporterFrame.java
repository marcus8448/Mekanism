package mekanism.common.block.basic;

import javax.annotation.Nonnull;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.interfaces.IHasDescription;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class BlockTeleporterFrame extends BlockMekanism implements IHasDescription {

    public BlockTeleporterFrame() {
        super(Block.Properties.of(Material.METAL).strength(5F, 10F).requiresTool().lightLevel(state -> 10));
    }

    @Nonnull
    @Override
    public ILangEntry getDescription() {
        return MekanismLang.DESCRIPTION_TELEPORTER_FRAME;
    }
}
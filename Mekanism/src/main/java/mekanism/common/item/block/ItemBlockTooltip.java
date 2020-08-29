package mekanism.common.item.block;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.MekanismLang;
import mekanism.common.block.interfaces.IHasDescription;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBlockTooltip<BLOCK extends Block & IHasDescription> extends ItemBlockMekanism<BLOCK> {

    private final boolean hasDetails;

    public ItemBlockTooltip(BLOCK block, Item.Settings properties) {
        this(block, false, properties);
    }

    public ItemBlockTooltip(BLOCK block, boolean hasDetails, Settings properties) {
        super(block, properties);
        this.hasDetails = hasDetails;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        if (MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.descriptionKey)) {
            tooltip.add(getBlock().getDescription().translate());
        } else if (hasDetails && MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.detailsKey)) {
            addDetails(stack, world, tooltip, flag.isAdvanced());
        } else {
            addStats(stack, world, tooltip, flag.isAdvanced());
            if (hasDetails) {
                tooltip.add(MekanismLang.HOLD_FOR_DETAILS.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getBoundKeyLocalizedText()));
            }
            tooltip.add(MekanismLang.HOLD_FOR_DESCRIPTION.translateColored(EnumColor.GRAY, EnumColor.AQUA, MekanismKeyHandler.descriptionKey.getBoundKeyLocalizedText()));
        }
    }

    public void addStats(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, boolean advanced) {
    }

    public void addDetails(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, boolean advanced) {
    }
}
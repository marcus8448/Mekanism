package mekanism.common.item.block;

import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.api.tier.ITier;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemBlockMekanism<BLOCK extends Block> extends BlockItem {

    @Nonnull
    private final BLOCK block;

    public ItemBlockMekanism(@Nonnull BLOCK block, Item.Settings properties) {
        super(block, properties);
        this.block = block;
    }

    @Nonnull
    @Override
    public BLOCK getBlock() {
        return block;
    }

    public ITier getTier() {
        return null;
    }

    public EnumColor getTextColor(ItemStack stack) {
        return getTier() != null ? getTier().getBaseTier().getTextColor() : null;
    }

    @Nonnull
    @Override
    public Text getName(@Nonnull ItemStack stack) {
        EnumColor color = getTextColor(stack);
        if (color == null) {
            return super.getName(stack);
        }
        return TextComponentUtil.build(color, super.getName(stack));
    }
}
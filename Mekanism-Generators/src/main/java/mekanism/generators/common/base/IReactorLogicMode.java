package mekanism.generators.common.base;

import mekanism.api.text.EnumColor;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public interface IReactorLogicMode<TYPE extends Enum<TYPE> & IReactorLogicMode<TYPE>> {

    Text getDescription();

    ItemStack getRenderStack();

    EnumColor getColor();
}
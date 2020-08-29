package mekanism.common.item.interfaces;

import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public interface IItemHUDProvider {

    void addHUDStrings(List<Text> list, ItemStack stack, EquipmentSlot slotType);
}
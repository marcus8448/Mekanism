package mekanism.common.recipe.bin;

import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.common.inventory.BinMekanismInventory;
import mekanism.common.inventory.slot.BinInventorySlot;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

//Note: We don't bother checking anywhere to ensure the bin's item stack size is one, as we only allow bins
// to be in stacks of one anyways. If this changes at some point, then we will need to adjust this recipe
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BinRecipe extends SpecialCraftingRecipe {

    protected BinRecipe(Identifier id) {
        super(id);
    }

    protected static BinInventorySlot convertToSlot(ItemStack binStack) {
        return BinMekanismInventory.create(binStack).getBinSlot();
    }

    @Override
    public abstract boolean matches(CraftingInventory inv, World world);

    @Override
    public abstract ItemStack getCraftingResult(CraftingInventory inv);

    @Override
    public abstract DefaultedList<ItemStack> getRemainingItems(CraftingInventory inv);

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 1;
    }
}
package mekanism.common.recipe.bin;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.Action;
import mekanism.api.NBTConstants;
import mekanism.api.inventory.AutomationType;
import mekanism.common.inventory.slot.BinInventorySlot;
import mekanism.common.item.block.ItemBlockBin;
import mekanism.common.registries.MekanismRecipeSerializers;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.StackUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

//TODO: Test this recipe in various modded crafting tables/auto crafters
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BinInsertRecipe extends BinRecipe {

    public BinInsertRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ItemStack binStack = ItemStack.EMPTY;
        ItemStack foundType = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stackInSlot = inv.getStack(i);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getItem() instanceof ItemBlockBin) {
                    if (!binStack.isEmpty()) {
                        //If we already have a bin then this is not a bin recipe
                        return false;
                    }
                    binStack = stackInSlot;
                } else if (foundType.isEmpty()) {
                    foundType = stackInSlot;
                } else if (!ItemHandlerHelper.canItemStacksStack(foundType, stackInSlot)) {
                    //If we have types that don't stack in the grid at once,
                    // then we cannot combine them both into the bin
                    return false;
                }
            }
        }
        if (binStack.isEmpty() || foundType.isEmpty()) {
            //If we didn't find a bin or an item to add it, we don't match the bin insertion recipe
            return false;
        }
        BinInventorySlot slot = convertToSlot(binStack);
        ItemStack remaining = slot.insertItem(foundType, Action.SIMULATE, AutomationType.MANUAL);
        //Return that it doesn't match if our simulation claims we would not be able to accept any items into the bin
        return !ItemStack.areEqual(remaining, foundType);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack binStack = ItemStack.EMPTY;
        ItemStack foundType = ItemStack.EMPTY;
        List<ItemStack> foundItems = new ArrayList<>();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stackInSlot = inv.getStack(i);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getItem() instanceof ItemBlockBin) {
                    if (!binStack.isEmpty()) {
                        //If we already have a bin then this is not a bin recipe
                        return ItemStack.EMPTY;
                    }
                    binStack = stackInSlot;
                    continue;
                } else if (foundType.isEmpty()) {
                    foundType = stackInSlot;
                } else if (!ItemHandlerHelper.canItemStacksStack(foundType, stackInSlot)) {
                    //If we have types that don't stack in the grid at once,
                    // then we cannot combine them both into the bin
                    return ItemStack.EMPTY;
                }
                foundItems.add(StackUtils.size(stackInSlot, 1));
            }
        }
        if (binStack.isEmpty() || foundType.isEmpty()) {
            //If we didn't find a bin or an item to add it, we don't match the bin insertion recipe
            return ItemStack.EMPTY;
        }
        //Copy the stack
        binStack = binStack.copy();
        BinInventorySlot slot = convertToSlot(binStack);
        boolean hasInserted = false;
        for (ItemStack stack : foundItems) {
            if (ItemStack.areEqual(stack, slot.insertItem(stack, Action.EXECUTE, AutomationType.MANUAL))) {
                if (hasInserted) {
                    //If we can't insert anymore into the bin, and we did manage to insert some into it
                    // exit and return our stack
                    break;
                }
                //Return that it doesn't match if our simulation claims we would not be able to accept any items into the bin
                return ItemStack.EMPTY;
            }
            hasInserted = true;
        }
        ItemDataUtils.setBoolean(binStack, NBTConstants.FROM_RECIPE, true);
        return binStack;
    }

    @Override
    public DefaultedList<ItemStack> getRemainingItems(CraftingInventory inv) {
        DefaultedList<ItemStack> remainingItems = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY);
        ItemStack binStack = ItemStack.EMPTY;
        ItemStack foundType = ItemStack.EMPTY;
        IntList foundSlots = new IntArrayList();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stackInSlot = inv.getStack(i);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getItem() instanceof ItemBlockBin) {
                    if (!binStack.isEmpty()) {
                        //If we already have a bin then this is not a bin recipe
                        return remainingItems;
                    }
                    binStack = stackInSlot;
                    continue;
                } else if (foundType.isEmpty()) {
                    foundType = stackInSlot;
                } else if (!ItemHandlerHelper.canItemStacksStack(foundType, stackInSlot)) {
                    //If we have types that don't stack in the grid at once,
                    // then we cannot combine them both into the bin
                    return remainingItems;
                }
                foundSlots.add(i);
            }
        }
        if (binStack.isEmpty() || foundType.isEmpty()) {
            //If we didn't find a bin or an item to add it, we don't match the bin insertion recipe
            return remainingItems;
        }
        //Copy the stack
        binStack = binStack.copy();
        BinInventorySlot slot = convertToSlot(binStack);
        for (int i = 0; i < foundSlots.size(); i++) {
            int index = foundSlots.getInt(i);
            ItemStack stack = StackUtils.size(inv.getStack(index), 1);
            ItemStack remaining = slot.insertItem(stack, Action.EXECUTE, AutomationType.MANUAL);
            remainingItems.set(index, remaining);
        }
        return remainingItems;
    }

    @Override
    public boolean fits(int width, int height) {
        //Require at least two slots as we have to represent at least the bin and the stack we are adding to it
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MekanismRecipeSerializers.BIN_INSERT.getRecipeSerializer();
    }

    @SubscribeEvent
    public static void onCrafting(ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (!result.isEmpty() && result.getItem() instanceof ItemBlockBin && ItemDataUtils.getBoolean(result, NBTConstants.FROM_RECIPE)) {
            BinInventorySlot slot = convertToSlot(result);
            ItemStack storedStack = slot.getStack();
            if (!storedStack.isEmpty()) {
                Inventory craftingMatrix = event.getInventory();
                for (int i = 0; i < craftingMatrix.size(); ++i) {
                    ItemStack stack = craftingMatrix.getStack(i);
                    //Check remaining items
                    stack = StackUtils.size(stack, stack.getCount() - 1);
                    if (!stack.isEmpty() && ItemHandlerHelper.canItemStacksStack(storedStack, stack)) {
                        ItemStack remaining = slot.insertItem(stack, Action.EXECUTE, AutomationType.MANUAL);
                        craftingMatrix.setStack(i, remaining);
                    } else {
                        craftingMatrix.setStack(i, ItemStack.EMPTY);
                    }
                }
                ItemDataUtils.removeData(storedStack, NBTConstants.FROM_RECIPE);
            }
        }
    }
}
package mekanism.common.inventory.container;

import mekanism.common.inventory.slot.SlotElectricChest;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.tileentity.TileEntityDigitalMiner;
import mekanism.common.util.ChargeUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDigitalMiner extends Container
{
    private TileEntityDigitalMiner tileEntity;

    public ContainerDigitalMiner(InventoryPlayer inventory, TileEntityDigitalMiner tentity)
    {
        tileEntity = tentity;
        
        for(int slotY = 0; slotY < 3; slotY++)
        {
            for(int slotX = 0; slotX < 9; slotX++)
            {
                addSlotToContainer(new Slot(tentity, slotX + slotY * 9, 8 + slotX * 18, 26 + slotY * 18));
            }
        }

        addSlotToContainer(new SlotDischarge(tentity, 27, 180, 11));
        
        int slotX;

        for(slotX = 0; slotX < 3; ++slotX)
        {
            for(int slotY = 0; slotY < 9; ++slotY)
            {
                addSlotToContainer(new SlotElectricChest(inventory, slotY + slotX * 9 + 9, 8 + slotY * 18, 148 + slotX * 18));
            }
        }

        for(slotX = 0; slotX < 9; ++slotX)
        {
            addSlotToContainer(new SlotElectricChest(inventory, slotX, 8 + slotX * 18, 206));
        }
        
        tileEntity.openChest();
        tileEntity.playersUsing.add(inventory.player);
    }
    
    @Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
		super.onContainerClosed(entityplayer);
		
		tileEntity.closeChest();
		tileEntity.playersUsing.remove(entityplayer);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
		return tileEntity.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
    {
        ItemStack stack = null;
        Slot currentSlot = (Slot)inventorySlots.get(slotID);

        if(currentSlot != null && currentSlot.getHasStack())
        {
            ItemStack slotStack = currentSlot.getStack();
            stack = slotStack.copy();
            
        	if(ChargeUtils.canBeDischarged(slotStack))
            {
	            if(slotID != 54)
	            {
	                if(!mergeItemStack(slotStack, 54, 55, false))
	                {
	                	return null;
	                }
	            }
	            else if(slotID == 54)
	            {
	            	if(!mergeItemStack(slotStack, 55, inventorySlots.size(), true))
	            	{
	            		return null;
	            	}
	            }
            }
            else {
            	if(slotID < 54)
            	{
            		if(!mergeItemStack(slotStack, 55, inventorySlots.size(), true))
            		{
            			return null;
            		}
            	}
            	else if(!mergeItemStack(slotStack, 0, 54, false))
            	{
            		return null;
            	}
            }
            
            if(slotStack.stackSize == 0)
            {
                currentSlot.putStack((ItemStack)null);
            }
            else {
                currentSlot.onSlotChanged();
            }

            if(slotStack.stackSize == stack.stackSize)
            {
                return null;
            }

            currentSlot.onPickupFromSlot(player, slotStack);
        }

        return stack;
    }
}

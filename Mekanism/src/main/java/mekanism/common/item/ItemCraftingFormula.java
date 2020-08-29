package mekanism.common.item;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.MekanismLang;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.ItemDataUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemCraftingFormula extends Item {

    public ItemCraftingFormula(Settings properties) {
        super(properties);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack itemStack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        DefaultedList<ItemStack> inv = getInventory(itemStack);
        if (inv != null) {
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack stack : inv) {
                if (!stack.isEmpty()) {
                    boolean found = false;
                    for (ItemStack iterStack : stacks) {
                        if (InventoryUtils.areItemsStackable(stack, iterStack)) {
                            iterStack.increment(stack.getCount());
                            found = true;
                        }
                    }
                    if (!found) {
                        stacks.add(stack);
                    }
                }
            }
            tooltip.add(MekanismLang.INGREDIENTS.translateColored(EnumColor.GRAY));
            for (ItemStack stack : stacks) {
                tooltip.add(MekanismLang.GENERIC_TRANSFER.translateColored(EnumColor.GRAY, stack, stack.getCount()));
            }
        }
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking()) {
            if (!world.isClient) {
                setInventory(stack, null);
                setInvalid(stack, false);
                ((ServerPlayerEntity) player).openHandledScreen(player.currentScreenHandler);
            }
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return getInventory(stack) != null ? 1 : 64;
    }

    @Nonnull
    @Override
    public Text getName(@Nonnull ItemStack stack) {
        if (getInventory(stack) == null) {
            return super.getName(stack);
        }
        if (isInvalid(stack)) {
            return TextComponentUtil.build(super.getName(stack), " ", EnumColor.DARK_RED, MekanismLang.INVALID);
        }
        return TextComponentUtil.build(super.getName(stack), " ", EnumColor.DARK_GREEN, MekanismLang.ENCODED);
    }

    public boolean isInvalid(ItemStack stack) {
        return ItemDataUtils.getBoolean(stack, NBTConstants.INVALID);
    }

    public void setInvalid(ItemStack stack, boolean invalid) {
        ItemDataUtils.setBoolean(stack, NBTConstants.INVALID, invalid);
    }

    public DefaultedList<ItemStack> getInventory(ItemStack stack) {
        if (!ItemDataUtils.hasData(stack, NBTConstants.ITEMS, NBT.TAG_LIST)) {
            return null;
        }
        ListTag tagList = ItemDataUtils.getList(stack, NBTConstants.ITEMS);
        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
        for (int tagCount = 0; tagCount < tagList.size(); tagCount++) {
            CompoundTag tagCompound = tagList.getCompound(tagCount);
            byte slotID = tagCompound.getByte(NBTConstants.SLOT);
            if (slotID >= 0 && slotID < 9) {
                inventory.set(slotID, ItemStack.fromTag(tagCompound));
            }
        }
        return inventory;
    }

    public void setInventory(ItemStack stack, DefaultedList<ItemStack> inv) {
        if (inv == null) {
            ItemDataUtils.removeData(stack, NBTConstants.ITEMS);
            return;
        }
        ListTag tagList = new ListTag();
        for (int slotCount = 0; slotCount < 9; slotCount++) {
            if (!inv.get(slotCount).isEmpty()) {
                CompoundTag tagCompound = new CompoundTag();
                tagCompound.putByte(NBTConstants.SLOT, (byte) slotCount);
                inv.get(slotCount).toTag(tagCompound);
                tagList.add(tagCompound);
            }
        }
        ItemDataUtils.setList(stack, NBTConstants.ITEMS, tagList);
    }
}
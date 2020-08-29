package mekanism.common.content.transporter;

import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.common.content.filter.FilterType;
import mekanism.common.content.filter.IItemStackFilter;
import mekanism.common.lib.inventory.Finder;
import mekanism.common.lib.inventory.TransitRequest;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

public class SorterItemStackFilter extends SorterFilter<SorterItemStackFilter> implements IItemStackFilter<SorterItemStackFilter> {

    public boolean sizeMode;
    public boolean fuzzyMode;

    public int min;
    public int max;

    private ItemStack itemType = ItemStack.EMPTY;

    @Override
    public TransitRequest mapInventory(BlockEntity tile, Direction side, boolean singleItem) {
        if (sizeMode && !singleItem) {
            return TransitRequest.definedItem(tile, side, min, max, getFinder());
        }
        return super.mapInventory(tile, side, singleItem);
    }

    @Override
    public Finder getFinder() {
        return fuzzyMode ? Finder.item(itemType) : Finder.strict(itemType);
    }

    @Override
    public CompoundTag write(CompoundTag nbtTags) {
        super.write(nbtTags);
        nbtTags.putBoolean(NBTConstants.SIZE_MODE, sizeMode);
        nbtTags.putBoolean(NBTConstants.FUZZY_MODE, fuzzyMode);
        nbtTags.putInt(NBTConstants.MIN, min);
        nbtTags.putInt(NBTConstants.MAX, max);
        itemType.toTag(nbtTags);
        return nbtTags;
    }

    @Override
    public void read(CompoundTag nbtTags) {
        super.read(nbtTags);
        sizeMode = nbtTags.getBoolean(NBTConstants.SIZE_MODE);
        fuzzyMode = nbtTags.getBoolean(NBTConstants.FUZZY_MODE);
        min = nbtTags.getInt(NBTConstants.MIN);
        max = nbtTags.getInt(NBTConstants.MAX);
        itemType = ItemStack.fromTag(nbtTags);
    }

    @Override
    public void write(PacketByteBuf buffer) {
        super.write(buffer);
        buffer.writeBoolean(sizeMode);
        buffer.writeBoolean(fuzzyMode);
        buffer.writeVarInt(min);
        buffer.writeVarInt(max);
        buffer.writeItemStack(itemType);
    }

    @Override
    public void read(PacketByteBuf dataStream) {
        super.read(dataStream);
        sizeMode = dataStream.readBoolean();
        fuzzyMode = dataStream.readBoolean();
        min = dataStream.readVarInt();
        max = dataStream.readVarInt();
        itemType = dataStream.readItemStack();
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + super.hashCode();
        code = 31 * code + itemType.hashCode();
        code = 31 * code + (sizeMode ? 1 : 0);
        code = 31 * code + (fuzzyMode ? 1 : 0);
        code = 31 * code + min;
        code = 31 * code + max;
        return code;
    }

    @Override
    public boolean equals(Object filter) {
        return super.equals(filter) && filter instanceof SorterItemStackFilter && ((SorterItemStackFilter) filter).itemType.isItemEqualIgnoreDamage(itemType)
               && ((SorterItemStackFilter) filter).sizeMode == sizeMode && ((SorterItemStackFilter) filter).fuzzyMode == fuzzyMode && ((SorterItemStackFilter) filter).min == min
               && ((SorterItemStackFilter) filter).max == max;
    }

    @Override
    public SorterItemStackFilter clone() {
        SorterItemStackFilter filter = new SorterItemStackFilter();
        filter.allowDefault = allowDefault;
        filter.color = color;
        filter.itemType = itemType.copy();
        filter.sizeMode = sizeMode;
        filter.fuzzyMode = fuzzyMode;
        filter.min = min;
        filter.max = max;
        return filter;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.SORTER_ITEMSTACK_FILTER;
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        return itemType;
    }

    @Override
    public void setItemStack(@Nonnull ItemStack stack) {
        itemType = stack;
    }
}
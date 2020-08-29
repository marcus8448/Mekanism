package mekanism.common.content.qio.filter;

import javax.annotation.Nonnull;
import mekanism.common.content.filter.FilterType;
import mekanism.common.content.filter.IItemStackFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class QIOItemStackFilter extends QIOFilter<QIOItemStackFilter> implements IItemStackFilter<QIOItemStackFilter> {

    private ItemStack itemType = ItemStack.EMPTY;

    public QIOItemStackFilter(ItemStack item) {
        itemType = item;
    }

    public QIOItemStackFilter() {
    }

    @Override
    public CompoundTag write(CompoundTag nbtTags) {
        super.write(nbtTags);
        itemType.toTag(nbtTags);
        return nbtTags;
    }

    @Override
    public void read(CompoundTag nbtTags) {
        itemType = ItemStack.fromTag(nbtTags);
    }

    @Override
    public void write(PacketByteBuf buffer) {
        super.write(buffer);
        buffer.writeItemStack(itemType);
    }

    @Override
    public void read(PacketByteBuf dataStream) {
        itemType = dataStream.readItemStack();
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + itemType.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object filter) {
        return filter instanceof QIOItemStackFilter && ((QIOItemStackFilter) filter).itemType.isItemEqualIgnoreDamage(itemType);
    }

    @Override
    public QIOItemStackFilter clone() {
        QIOItemStackFilter filter = new QIOItemStackFilter();
        filter.itemType = itemType.copy();
        return filter;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.QIO_ITEMSTACK_FILTER;
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

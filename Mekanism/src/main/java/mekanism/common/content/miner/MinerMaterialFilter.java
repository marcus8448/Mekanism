package mekanism.common.content.miner;

import javax.annotation.Nonnull;
import mekanism.common.content.filter.FilterType;
import mekanism.common.content.filter.IMaterialFilter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class MinerMaterialFilter extends MinerFilter<MinerMaterialFilter> implements IMaterialFilter<MinerMaterialFilter> {

    private ItemStack materialItem = ItemStack.EMPTY;

    public Material getMaterial() {
        return Block.getBlockFromItem(materialItem.getItem()).getDefaultState().getMaterial();
    }

    @Override
    public boolean canFilter(BlockState state) {
        return state.getMaterial() == getMaterial();
    }

    @Override
    public CompoundTag write(CompoundTag nbtTags) {
        super.write(nbtTags);
        materialItem.toTag(nbtTags);
        return nbtTags;
    }

    @Override
    public void read(CompoundTag nbtTags) {
        super.read(nbtTags);
        materialItem = ItemStack.fromTag(nbtTags);
    }

    @Override
    public void write(PacketByteBuf buffer) {
        super.write(buffer);
        buffer.writeItemStack(materialItem);
    }

    @Override
    public void read(PacketByteBuf dataStream) {
        super.read(dataStream);
        materialItem = dataStream.readItemStack();
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + materialItem.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object filter) {
        return filter instanceof MinerMaterialFilter && ((MinerMaterialFilter) filter).materialItem.isItemEqualIgnoreDamage(materialItem);
    }

    @Override
    public MinerMaterialFilter clone() {
        MinerMaterialFilter filter = new MinerMaterialFilter();
        filter.replaceStack = replaceStack;
        filter.requireStack = requireStack;
        filter.materialItem = materialItem;
        return filter;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.MINER_MATERIAL_FILTER;
    }

    @Nonnull
    @Override
    public ItemStack getMaterialItem() {
        return materialItem;
    }

    @Override
    public void setMaterialItem(@Nonnull ItemStack stack) {
        materialItem = stack;
    }
}
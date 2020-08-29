package mekanism.common.content.transporter;

import mekanism.api.NBTConstants;
import mekanism.common.content.filter.FilterType;
import mekanism.common.content.filter.IModIDFilter;
import mekanism.common.lib.inventory.Finder;
import mekanism.common.network.BasePacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class SorterModIDFilter extends SorterFilter<SorterModIDFilter> implements IModIDFilter<SorterModIDFilter> {

    private String modID;

    @Override
    public Finder getFinder() {
        return Finder.modID(modID);
    }

    @Override
    public CompoundTag write(CompoundTag nbtTags) {
        super.write(nbtTags);
        nbtTags.putString(NBTConstants.MODID, modID);
        return nbtTags;
    }

    @Override
    public void read(CompoundTag nbtTags) {
        super.read(nbtTags);
        modID = nbtTags.getString(NBTConstants.MODID);
    }

    @Override
    public void write(PacketByteBuf buffer) {
        super.write(buffer);
        buffer.writeString(modID);
    }

    @Override
    public void read(PacketByteBuf dataStream) {
        super.read(dataStream);
        modID = BasePacketHandler.readString(dataStream);
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + super.hashCode();
        code = 31 * code + modID.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object filter) {
        return super.equals(filter) && filter instanceof SorterModIDFilter && ((SorterModIDFilter) filter).modID.equals(modID);
    }

    @Override
    public SorterModIDFilter clone() {
        SorterModIDFilter filter = new SorterModIDFilter();
        filter.allowDefault = allowDefault;
        filter.color = color;
        filter.modID = modID;
        return filter;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.SORTER_MODID_FILTER;
    }

    @Override
    public void setModID(String id) {
        modID = id;
    }

    @Override
    public String getModID() {
        return modID;
    }
}
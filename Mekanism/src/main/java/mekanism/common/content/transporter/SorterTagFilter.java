package mekanism.common.content.transporter;

import mekanism.api.NBTConstants;
import mekanism.common.content.filter.FilterType;
import mekanism.common.content.filter.ITagFilter;
import mekanism.common.lib.inventory.Finder;
import mekanism.common.network.BasePacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class SorterTagFilter extends SorterFilter<SorterTagFilter> implements ITagFilter<SorterTagFilter> {

    private String tagName;

    @Override
    public Finder getFinder() {
        return Finder.tag(tagName);
    }

    @Override
    public CompoundTag write(CompoundTag nbtTags) {
        super.write(nbtTags);
        nbtTags.putString(NBTConstants.TAG_NAME, tagName);
        return nbtTags;
    }

    @Override
    public void read(CompoundTag nbtTags) {
        super.read(nbtTags);
        tagName = nbtTags.getString(NBTConstants.TAG_NAME);
    }

    @Override
    public void write(PacketByteBuf buffer) {
        super.write(buffer);
        buffer.writeString(tagName);
    }

    @Override
    public void read(PacketByteBuf dataStream) {
        super.read(dataStream);
        tagName = BasePacketHandler.readString(dataStream);
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + super.hashCode();
        code = 31 * code + tagName.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object filter) {
        return super.equals(filter) && filter instanceof SorterTagFilter && ((SorterTagFilter) filter).tagName.equals(tagName);
    }

    @Override
    public SorterTagFilter clone() {
        SorterTagFilter filter = new SorterTagFilter();
        filter.allowDefault = allowDefault;
        filter.color = color;
        filter.tagName = tagName;
        return filter;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.SORTER_TAG_FILTER;
    }

    @Override
    public void setTagName(String name) {
        tagName = name;
    }

    @Override
    public String getTagName() {
        return tagName;
    }
}
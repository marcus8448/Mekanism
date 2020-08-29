package mekanism.common.content.teleporter;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.UUID;
import mekanism.api.Coord4D;
import mekanism.api.NBTConstants;
import mekanism.api.text.EnumColor;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.util.NBTUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class TeleporterFrequency extends Frequency {

    private final Set<Coord4D> activeCoords = new ObjectOpenHashSet<>();
    private EnumColor color = EnumColor.PURPLE;

    public TeleporterFrequency(String n, UUID uuid) {
        super(FrequencyType.TELEPORTER, n, uuid);
    }

    public TeleporterFrequency() {
        super(FrequencyType.TELEPORTER);
    }

    public Set<Coord4D> getActiveCoords() {
        return activeCoords;
    }

    @Override
    public int getSyncHash() {
        int code = super.getSyncHash();
        code = 31 * code + color.ordinal();
        return code;
    }

    public EnumColor getColor() {
        return color;
    }

    public void setColor(EnumColor color) {
        this.color = color;
    }

    @Override
    public void update(BlockEntity tile) {
        super.update(tile);
        activeCoords.add(Coord4D.get(tile));
    }

    @Override
    public void onDeactivate(BlockEntity tile) {
        super.onDeactivate(tile);
        activeCoords.remove(Coord4D.get(tile));
    }

    public Coord4D getClosestCoords(Coord4D coord) {
        Coord4D closest = null;
        for (Coord4D iterCoord : activeCoords) {
            if (iterCoord.equals(coord)) {
                continue;
            }
            if (closest == null) {
                closest = iterCoord;
                continue;
            }

            if (coord.dimension != closest.dimension && coord.dimension == iterCoord.dimension) {
                closest = iterCoord;
            } else if (coord.dimension != closest.dimension || coord.dimension == iterCoord.dimension) {
                if (coord.distanceTo(closest) > coord.distanceTo(iterCoord)) {
                    closest = iterCoord;
                }
            }
        }
        return closest;
    }

    @Override
    protected void read(CompoundTag nbtTags) {
        super.read(nbtTags);
        NBTUtils.setEnumIfPresent(nbtTags, NBTConstants.COLOR, EnumColor::byIndexStatic, (value) -> color = value);
    }

    @Override
    protected void read(PacketByteBuf dataStream) {
        super.read(dataStream);
        color = dataStream.readEnumConstant(EnumColor.class);
    }

    @Override
    public void write(CompoundTag nbtTags) {
        super.write(nbtTags);
        nbtTags.putInt(NBTConstants.COLOR, color.ordinal());
    }

    @Override
    public void write(PacketByteBuf buffer) {
        super.write(buffer);
        buffer.writeEnumConstant(color);
    }
}

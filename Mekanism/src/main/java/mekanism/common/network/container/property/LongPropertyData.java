package mekanism.common.network.container.property;

import mekanism.common.inventory.container.MekanismContainer;
import net.minecraft.network.PacketByteBuf;

public class LongPropertyData extends PropertyData {

    private final long value;

    public LongPropertyData(short property, long value) {
        super(PropertyType.LONG, property);
        this.value = value;
    }

    @Override
    public void handleWindowProperty(MekanismContainer container) {
        container.handleWindowProperty(getProperty(), value);
    }

    @Override
    public void writeToPacket(PacketByteBuf buffer) {
        super.writeToPacket(buffer);
        buffer.writeVarLong(value);
    }
}
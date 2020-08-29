package mekanism.common.network.container.property;

import mekanism.common.inventory.container.MekanismContainer;
import net.minecraft.network.PacketByteBuf;

public class BytePropertyData extends PropertyData {

    private final byte value;

    public BytePropertyData(short property, byte value) {
        super(PropertyType.BYTE, property);
        this.value = value;
    }

    @Override
    public void handleWindowProperty(MekanismContainer container) {
        container.handleWindowProperty(getProperty(), value);
    }

    @Override
    public void writeToPacket(PacketByteBuf buffer) {
        super.writeToPacket(buffer);
        buffer.writeByte(value);
    }
}
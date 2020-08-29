package mekanism.common.network.container.property;

import mekanism.common.inventory.container.MekanismContainer;
import net.minecraft.network.PacketByteBuf;

public class BooleanPropertyData extends PropertyData {

    private final boolean value;

    public BooleanPropertyData(short property, boolean value) {
        super(PropertyType.BOOLEAN, property);
        this.value = value;
    }

    @Override
    public void handleWindowProperty(MekanismContainer container) {
        container.handleWindowProperty(getProperty(), value);
    }

    @Override
    public void writeToPacket(PacketByteBuf buffer) {
        super.writeToPacket(buffer);
        buffer.writeBoolean(value);
    }
}
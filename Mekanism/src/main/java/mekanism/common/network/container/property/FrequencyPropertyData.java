package mekanism.common.network.container.property;

import javax.annotation.Nullable;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.lib.frequency.Frequency;
import net.minecraft.network.PacketByteBuf;

public class FrequencyPropertyData<FREQUENCY extends Frequency> extends PropertyData {

    @Nullable
    private final FREQUENCY value;

    public FrequencyPropertyData(short property, @Nullable FREQUENCY value) {
        super(PropertyType.FREQUENCY, property);
        this.value = value;
    }

    public static <FREQUENCY extends Frequency> FrequencyPropertyData<FREQUENCY> readFrequency(short property, PacketByteBuf buffer) {
        return new FrequencyPropertyData<>(property, buffer.readBoolean() ? Frequency.readFromPacket(buffer) : null);
    }

    @Override
    public void handleWindowProperty(MekanismContainer container) {
        container.handleWindowProperty(getProperty(), value);
    }

    @Override
    public void writeToPacket(PacketByteBuf buffer) {
        super.writeToPacket(buffer);
        if (value == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            value.write(buffer);
        }
    }
}
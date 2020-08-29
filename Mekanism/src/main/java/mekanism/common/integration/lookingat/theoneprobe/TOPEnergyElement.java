package mekanism.common.integration.lookingat.theoneprobe;

import mcjty.theoneprobe.api.IElement;
import mekanism.api.math.FloatingLong;
import mekanism.common.integration.lookingat.EnergyElement;
import net.minecraft.network.PacketByteBuf;

public class TOPEnergyElement extends EnergyElement implements IElement {

    public TOPEnergyElement(FloatingLong energy, FloatingLong maxEnergy) {
        super(energy, maxEnergy);
    }

    public TOPEnergyElement(PacketByteBuf buf) {
        this(FloatingLong.readFromBuffer(buf), FloatingLong.readFromBuffer(buf));
    }

    @Override
    public void toBytes(PacketByteBuf buf) {
        energy.writeToBuffer(buf);
        maxEnergy.writeToBuffer(buf);
    }

    @Override
    public int getID() {
        return TOPProvider.ENERGY_ELEMENT_ID;
    }
}
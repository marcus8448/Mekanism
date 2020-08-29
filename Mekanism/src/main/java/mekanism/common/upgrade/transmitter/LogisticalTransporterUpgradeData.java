package mekanism.common.upgrade.transmitter;

import javax.annotation.Nullable;
import mekanism.api.text.EnumColor;
import mekanism.common.lib.transmitter.ConnectionType;
import net.minecraft.nbt.ListTag;

public class LogisticalTransporterUpgradeData extends TransmitterUpgradeData {

    @Nullable
    public final EnumColor color;
    public final ListTag stacks;

    //Note: Currently redstone reactive is always false here
    public LogisticalTransporterUpgradeData(boolean redstoneReactive, ConnectionType[] connectionTypes, @Nullable EnumColor color, ListTag stacks) {
        super(redstoneReactive, connectionTypes);
        this.color = color;
        this.stacks = stacks;
    }
}
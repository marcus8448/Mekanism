package mekanism.common.lib.security;

import mekanism.common.lib.security.ISecurityTile.SecurityMode;
import net.minecraft.network.PacketByteBuf;

public class SecurityData {

    public SecurityMode mode = SecurityMode.PUBLIC;
    public boolean override;

    public SecurityData() {
    }

    public SecurityData(SecurityFrequency frequency) {
        mode = frequency.getSecurityMode();
        override = frequency.isOverridden();
    }

    public static SecurityData read(PacketByteBuf dataStream) {
        SecurityData data = new SecurityData();
        data.mode = dataStream.readEnumConstant(SecurityMode.class);
        data.override = dataStream.readBoolean();
        return data;
    }

    public void write(PacketByteBuf dataStream) {
        dataStream.writeEnumConstant(mode);
        dataStream.writeBoolean(override);
    }
}
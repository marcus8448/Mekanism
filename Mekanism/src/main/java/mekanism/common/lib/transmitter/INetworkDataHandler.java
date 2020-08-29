package mekanism.common.lib.transmitter;

import javax.annotation.Nullable;
import net.minecraft.text.Text;

public interface INetworkDataHandler {

    @Nullable
    default Text getNeededInfo() {
        return null;
    }

    @Nullable
    default Text getStoredInfo() {
        return null;
    }

    @Nullable
    default Text getFlowInfo() {
        return null;
    }

    @Nullable
    default Object getNetworkReaderCapacity() {
        return null;
    }
}
package mekanism.common.network;

import java.util.UUID;
import java.util.function.Supplier;
import mekanism.common.Mekanism;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketResetPlayerClient {

    private final UUID uuid;

    public PacketResetPlayerClient(UUID uuid) {
        this.uuid = uuid;
    }

    public static void handle(PacketResetPlayerClient message, Supplier<Context> context) {
        context.get().enqueueWork(() -> Mekanism.playerState.clearPlayer(message.uuid));
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketResetPlayerClient pkt, PacketByteBuf buf) {
        buf.writeUuid(pkt.uuid);
    }

    public static PacketResetPlayerClient decode(PacketByteBuf buf) {
        return new PacketResetPlayerClient(buf.readUuid());
    }
}

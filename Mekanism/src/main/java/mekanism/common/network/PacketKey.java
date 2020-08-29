package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.common.Mekanism;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketKey {

    private final int key;
    private final boolean add;

    public PacketKey(int key, boolean add) {
        this.key = key;
        this.add = add;
    }

    public static void handle(PacketKey message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            if (message.add) {
                Mekanism.keyMap.add(player.getUuid(), message.key);
            } else {
                Mekanism.keyMap.remove(player.getUuid(), message.key);
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketKey pkt, PacketByteBuf buf) {
        buf.writeVarInt(pkt.key);
        buf.writeBoolean(pkt.add);
    }

    public static PacketKey decode(PacketByteBuf buf) {
        return new PacketKey(buf.readVarInt(), buf.readBoolean());
    }
}
package mekanism.common.network;

import java.util.function.Supplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketFlyingSync {

    private final boolean allowFlying;
    private final boolean isFlying;

    public PacketFlyingSync(boolean allowFlying, boolean isFlying) {
        this.allowFlying = allowFlying;
        this.isFlying = isFlying;
    }

    public static void handle(PacketFlyingSync message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            player.abilities.allowFlying = message.allowFlying;
            player.abilities.flying = message.isFlying;
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketFlyingSync pkt, PacketByteBuf buf) {
        buf.writeBoolean(pkt.allowFlying);
        buf.writeBoolean(pkt.isFlying);
    }

    public static PacketFlyingSync decode(PacketByteBuf buf) {
        return new PacketFlyingSync(buf.readBoolean(), buf.readBoolean());
    }
}
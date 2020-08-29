package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.lib.radiation.RadiationManager.RadiationScale;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketRadiationData {

    private final RadiationPacketType type;
    private RadiationScale scale;
    private double radiation;

    private PacketRadiationData(RadiationScale scale) {
        this.type = RadiationPacketType.SCALE;
        this.scale = scale;
    }

    private PacketRadiationData(double radiation) {
        this.type = RadiationPacketType.PLAYER;
        this.radiation = radiation;
    }

    public static PacketRadiationData create(RadiationScale scale) {
        return new PacketRadiationData(scale);
    }

    public static void sync(ServerPlayerEntity player) {
        player.getCapability(Capabilities.RADIATION_ENTITY_CAPABILITY).ifPresent(c -> Mekanism.packetHandler.sendTo(new PacketRadiationData(c.getRadiation()), player));
    }

    public static void handle(PacketRadiationData message, Supplier<Context> context) {
        // Queue up the processing on the central thread
        PlayerEntity player = PacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            if (message.type == RadiationPacketType.SCALE) {
                Mekanism.radiationManager.setClientScale(message.scale);
            } else if (message.type == RadiationPacketType.PLAYER) {
                player.getCapability(Capabilities.RADIATION_ENTITY_CAPABILITY).ifPresent(c -> c.set(message.radiation));
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketRadiationData pkt, PacketByteBuf buf) {
        buf.writeEnumConstant(pkt.type);
        if (pkt.type == RadiationPacketType.SCALE) {
            buf.writeEnumConstant(pkt.scale);
        } else if (pkt.type == RadiationPacketType.PLAYER) {
            buf.writeDouble(pkt.radiation);
        }
    }

    public static PacketRadiationData decode(PacketByteBuf buf) {
        RadiationPacketType type = buf.readEnumConstant(RadiationPacketType.class);
        if (type == RadiationPacketType.SCALE) {
            return new PacketRadiationData(buf.readEnumConstant(RadiationScale.class));
        } else if (type == RadiationPacketType.PLAYER) {
            return new PacketRadiationData(buf.readDouble());
        }
        return null;
    }

    public enum RadiationPacketType {
        SCALE,
        PLAYER
    }
}

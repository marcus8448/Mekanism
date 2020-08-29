package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.client.gui.item.GuiPortableTeleporter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketPortableTeleporter {

    private final byte status;

    public PacketPortableTeleporter(byte status) {
        this.status = status;
    }

    public static void handle(PacketPortableTeleporter message, Supplier<Context> context) {
        context.get().enqueueWork(() -> {
            Screen screen = MinecraftClient.getInstance().currentScreen;
            if (screen instanceof GuiPortableTeleporter) {
                GuiPortableTeleporter teleporter = (GuiPortableTeleporter) screen;
                teleporter.setStatus(message.status);
                teleporter.updateButtons();
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketPortableTeleporter pkt, PacketByteBuf buf) {
        buf.writeByte(pkt.status);
    }

    public static PacketPortableTeleporter decode(PacketByteBuf buf) {
        return new PacketPortableTeleporter(buf.readByte());
    }
}
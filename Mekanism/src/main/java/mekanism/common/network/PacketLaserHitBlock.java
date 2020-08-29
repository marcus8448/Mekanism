package mekanism.common.network;

import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketLaserHitBlock {

    private final BlockHitResult result;

    public PacketLaserHitBlock(BlockHitResult result) {
        this.result = result;
    }

    public static void handle(PacketLaserHitBlock message, Supplier<Context> context) {
        context.get().enqueueWork(() -> {
            if (MinecraftClient.getInstance().world != null) {
                MinecraftClient.getInstance().particleManager.addBlockHitEffects(message.result.getBlockPos(), message.result);
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketLaserHitBlock pkt, PacketByteBuf buf) {
        buf.writeBlockHitResult(pkt.result);
    }

    public static PacketLaserHitBlock decode(PacketByteBuf buf) {
        return new PacketLaserHitBlock(buf.readBlockHitResult());
    }
}
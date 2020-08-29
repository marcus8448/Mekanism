package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.client.render.RenderTickHandler;
import mekanism.common.lib.effect.BoltEffect;
import mekanism.common.lib.effect.BoltEffect.BoltRenderInfo;
import mekanism.common.lib.effect.BoltEffect.SpawnFunction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketLightningRender {

    private final LightningPreset preset;
    private final Vec3d start;
    private final Vec3d end;
    private final int renderer;
    private final int segments;

    public PacketLightningRender(LightningPreset preset, int renderer, Vec3d start, Vec3d end, int segments) {
        this.preset = preset;
        this.renderer = renderer;
        this.start = start;
        this.end = end;
        this.segments = segments;
    }

    public static void handle(PacketLightningRender message, Supplier<Context> context) {
        context.get().enqueueWork(() -> RenderTickHandler.renderBolt(message.renderer, message.preset.boltCreator.create(message.start, message.end, message.segments)));
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketLightningRender pkt, PacketByteBuf buf) {
        buf.writeEnumConstant(pkt.preset);
        buf.writeVarInt(pkt.renderer);
        BasePacketHandler.writeVector3d(buf, pkt.start);
        BasePacketHandler.writeVector3d(buf, pkt.end);
        buf.writeVarInt(pkt.segments);
    }

    public static PacketLightningRender decode(PacketByteBuf buf) {
        LightningPreset preset = buf.readEnumConstant(LightningPreset.class);
        int renderer = buf.readVarInt();
        Vec3d start = BasePacketHandler.readVector3d(buf);
        Vec3d end = BasePacketHandler.readVector3d(buf);
        int segments = buf.readVarInt();
        return new PacketLightningRender(preset, renderer, start, end, segments);
    }

    @FunctionalInterface
    public interface BoltCreator {

        BoltEffect create(Vec3d start, Vec3d end, int segments);
    }

    public enum LightningPreset {
        MAGNETIC_ATTRACTION((start, end, segments) -> new BoltEffect(BoltRenderInfo.ELECTRICITY, start, end, segments).size(0.04F).lifespan(8).spawn(SpawnFunction.noise(8, 4))),
        TOOL_AOE((start, end, segments) -> new BoltEffect(BoltRenderInfo.ELECTRICITY, start, end, segments).size(0.015F).lifespan(12).spawn(SpawnFunction.NO_DELAY));

        private final BoltCreator boltCreator;

        LightningPreset(BoltCreator boltCreator) {
            this.boltCreator = boltCreator;
        }
    }
}
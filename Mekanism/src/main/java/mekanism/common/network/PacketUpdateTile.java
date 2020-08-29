package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.common.Mekanism;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketUpdateTile {

    private final CompoundTag updateTag;
    private final BlockPos pos;

    public PacketUpdateTile(TileEntityUpdateable tile) {
        this(tile.getPos(), tile.getReducedUpdateTag());
    }

    private PacketUpdateTile(BlockPos pos, CompoundTag updateTag) {
        this.pos = pos;
        this.updateTag = updateTag;
    }

    public static void handle(PacketUpdateTile message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            TileEntityUpdateable tile = MekanismUtils.getTileEntity(TileEntityUpdateable.class, player.world, message.pos, true);
            if (tile == null) {
                Mekanism.logger.info("Update tile packet received for position: {} in world: {}, but no valid tile was found.", message.pos,
                      player.world.getRegistryKey().getValue());
            } else {
                tile.handleUpdatePacket(message.updateTag);
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketUpdateTile pkt, PacketByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeCompoundTag(pkt.updateTag);
    }

    public static PacketUpdateTile decode(PacketByteBuf buf) {
        return new PacketUpdateTile(buf.readBlockPos(), buf.readCompoundTag());
    }
}
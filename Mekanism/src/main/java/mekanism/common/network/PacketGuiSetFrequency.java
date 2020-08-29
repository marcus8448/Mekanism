package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.common.Mekanism;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.frequency.IFrequencyHandler;
import mekanism.common.lib.frequency.IFrequencyItem;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketGuiSetFrequency<FREQ extends Frequency> {

    private final FrequencyType<FREQ> type;
    private final FrequencyUpdate updateType;
    private final FrequencyIdentity data;
    private final BlockPos tilePosition;
    private final Hand currentHand;

    private PacketGuiSetFrequency(FrequencyUpdate updateType, FrequencyType<FREQ> type, FrequencyIdentity data, BlockPos tilePosition, Hand currentHand) {
        this.updateType = updateType;
        this.type = type;
        this.data = data;
        this.tilePosition = tilePosition;
        this.currentHand = currentHand;
    }

    public static <FREQ extends Frequency> PacketGuiSetFrequency<FREQ> create(FrequencyUpdate updateType, FrequencyType<FREQ> type, FrequencyIdentity data, BlockPos tilePosition) {
        return new PacketGuiSetFrequency<>(updateType, type, data, tilePosition, null);
    }

    public static <FREQ extends Frequency> PacketGuiSetFrequency<FREQ> create(FrequencyUpdate updateType, FrequencyType<FREQ> type, FrequencyIdentity data, Hand currentHand) {
        return new PacketGuiSetFrequency<>(updateType, type, data, null, currentHand);
    }

    public static <FREQ extends Frequency> void handle(PacketGuiSetFrequency<FREQ> message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            if (message.updateType.isTile()) {
                BlockEntity tile = MekanismUtils.getTileEntity(player.world, message.tilePosition);
                if (tile instanceof IFrequencyHandler) {
                    if (message.updateType == FrequencyUpdate.SET_TILE) {
                        ((IFrequencyHandler) tile).setFrequency(message.type, message.data);
                    } else if (message.updateType == FrequencyUpdate.REMOVE_TILE) {
                        ((IFrequencyHandler) tile).removeFrequency(message.type, message.data);
                    }
                }
            } else {
                FrequencyManager<FREQ> manager = message.type.getManager(message.data.isPublic() ? null : player.getUuid());
                ItemStack stack = player.getStackInHand(message.currentHand);
                if (stack.getItem() instanceof IFrequencyItem) {
                    IFrequencyItem item = (IFrequencyItem) stack.getItem();
                    FREQ toUse = null;
                    if (message.updateType == FrequencyUpdate.SET_ITEM) {
                        toUse = manager.getOrCreateFrequency(message.data, player.getUuid());
                        item.setFrequency(stack, toUse);
                    } else if (message.updateType == FrequencyUpdate.REMOVE_ITEM) {
                        manager.remove(message.data.getKey(), player.getUuid());
                        FrequencyIdentity current = item.getFrequency(stack);
                        if (current != null) {
                            if (current.equals(message.data)) {
                                //If the frequency we are removing matches the stored frequency set it to nothing
                                item.setFrequency(stack, null);
                            } else {
                                //Otherwise just delete the frequency and keep what the item is set to
                                FrequencyManager<FREQ> currentManager = manager;
                                if (message.data.isPublic() != current.isPublic()) {
                                    //Update the manager if it is the wrong one for getting our actual current frequency
                                    currentManager = message.type.getManager(current.isPublic() ? null : player.getUuid());
                                }
                                toUse = currentManager.getFrequency(current.getKey());
                            }
                        }
                    }
                    Mekanism.packetHandler.sendTo(PacketFrequencyItemGuiUpdate.update(message.currentHand, message.type, player.getUuid(), toUse), (ServerPlayerEntity) player);
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    public static <FREQ extends Frequency> void encode(PacketGuiSetFrequency<FREQ> pkt, PacketByteBuf buf) {
        buf.writeEnumConstant(pkt.updateType);
        pkt.type.write(buf);
        pkt.type.getIdentitySerializer().write(buf, pkt.data);
        if (pkt.updateType.isTile()) {
            buf.writeBlockPos(pkt.tilePosition);
        } else {
            buf.writeEnumConstant(pkt.currentHand);
        }
    }

    public static <FREQ extends Frequency> PacketGuiSetFrequency<FREQ> decode(PacketByteBuf buf) {
        FrequencyUpdate updateType = buf.readEnumConstant(FrequencyUpdate.class);
        FrequencyType<FREQ> type = FrequencyType.load(buf);
        FrequencyIdentity data = type.getIdentitySerializer().read(buf);
        BlockPos pos = updateType.isTile() ? buf.readBlockPos() : null;
        Hand hand = !updateType.isTile() ? buf.readEnumConstant(Hand.class) : null;
        return new PacketGuiSetFrequency<>(updateType, type, data, pos, hand);
    }

    public enum FrequencyUpdate {
        SET_TILE,
        SET_ITEM,
        REMOVE_TILE,
        REMOVE_ITEM;

        boolean isTile() {
            return this == SET_TILE || this == REMOVE_TILE;
        }
    }
}
package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.common.Mekanism;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.inventory.container.QIOItemViewerContainer;
import mekanism.common.inventory.container.item.FrequencyItemContainer;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.lib.frequency.IFrequencyItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketGuiItemDataRequest {

    private final Type type;
    private final Hand hand;

    public PacketGuiItemDataRequest(Type type, Hand hand) {
        this.type = type;
        this.hand = hand;
    }

    public static PacketGuiItemDataRequest frequencyList(Hand hand) {
        return new PacketGuiItemDataRequest(Type.FREQUENCY_LIST_GUI, hand);
    }

    public static PacketGuiItemDataRequest qioItemViewer() {
        return new PacketGuiItemDataRequest(Type.QIO_ITEM_VIEWER, null);
    }

    public static void handle(PacketGuiItemDataRequest message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            if (message.type == Type.FREQUENCY_LIST_GUI) {
                if (player.currentScreenHandler instanceof FrequencyItemContainer) {
                    handleFrequencyList(message, player);
                }
            } else if (message.type == Type.QIO_ITEM_VIEWER) {
                if (player.currentScreenHandler instanceof QIOItemViewerContainer) {
                    QIOItemViewerContainer container = (QIOItemViewerContainer) player.currentScreenHandler;
                    QIOFrequency freq = container.getFrequency();
                    if (!player.world.isClient() && freq != null) {
                        freq.openItemViewer((ServerPlayerEntity) player);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    private static <FREQ extends Frequency> void handleFrequencyList(PacketGuiItemDataRequest message, PlayerEntity player) {
        FrequencyItemContainer<FREQ> container = (FrequencyItemContainer<FREQ>) player.currentScreenHandler;
        ItemStack stack = player.getStackInHand(message.hand);
        FrequencyIdentity identity = ((IFrequencyItem) stack.getItem()).getFrequency(stack);
        FREQ freq = null;
        if (identity != null) {
            FrequencyManager<FREQ> manager = identity.isPublic() ? container.getFrequencyType().getManager(null) : container.getFrequencyType().getManager(player.getUuid());
            freq = manager.getFrequency(identity.getKey());
            // if this frequency no longer exists, remove the reference from the stack
            if (freq == null) {
                ((IFrequencyItem) stack.getItem()).setFrequency(stack, null);
            }
        }
        Mekanism.packetHandler.sendTo(PacketFrequencyItemGuiUpdate.update(message.hand, container.getFrequencyType(), player.getUuid(), freq), (ServerPlayerEntity) player);
    }

    public static void encode(PacketGuiItemDataRequest pkt, PacketByteBuf buf) {
        buf.writeEnumConstant(pkt.type);
        if (pkt.type == Type.FREQUENCY_LIST_GUI) {
            buf.writeEnumConstant(pkt.hand);
        }
    }

    public static PacketGuiItemDataRequest decode(PacketByteBuf buf) {
        Type type = buf.readEnumConstant(Type.class);
        Hand hand = null;
        if (type == Type.FREQUENCY_LIST_GUI) {
            hand = buf.readEnumConstant(Hand.class);
        }
        return new PacketGuiItemDataRequest(type, hand);
    }

    private enum Type {
        FREQUENCY_LIST_GUI,
        QIO_ITEM_VIEWER
    }
}

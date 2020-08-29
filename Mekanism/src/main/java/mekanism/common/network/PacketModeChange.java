package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.common.item.interfaces.IModeItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketModeChange {

    private final boolean displayChangeMessage;
    private final EquipmentSlot slot;
    private final int shift;

    public PacketModeChange(EquipmentSlot slot, boolean holdingShift) {
        this(slot, holdingShift ? -1 : 1, true);
    }

    public PacketModeChange(EquipmentSlot slot, int shift) {
        this(slot, shift, false);
    }

    public PacketModeChange(EquipmentSlot slot, int shift, boolean displayChangeMessage) {
        this.slot = slot;
        this.shift = shift;
        this.displayChangeMessage = displayChangeMessage;
    }

    public static void handle(PacketModeChange message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            ItemStack stack = player.getEquippedStack(message.slot);
            if (!stack.isEmpty() && stack.getItem() instanceof IModeItem) {
                ((IModeItem) stack.getItem()).changeMode(player, stack, message.shift, message.displayChangeMessage);
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketModeChange pkt, PacketByteBuf buf) {
        buf.writeEnumConstant(pkt.slot);
        buf.writeVarInt(pkt.shift);
        buf.writeBoolean(pkt.displayChangeMessage);
    }

    public static PacketModeChange decode(PacketByteBuf buf) {
        return new PacketModeChange(buf.readEnumConstant(EquipmentSlot.class), buf.readVarInt(), buf.readBoolean());
    }
}
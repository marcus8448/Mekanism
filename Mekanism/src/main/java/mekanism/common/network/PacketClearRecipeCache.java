package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketClearRecipeCache {

    public static void handle(PacketClearRecipeCache message, Supplier<Context> context) {
        context.get().enqueueWork(MekanismRecipeType::clearCache);
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketClearRecipeCache pkt, PacketByteBuf buf) {
    }

    public static PacketClearRecipeCache decode(PacketByteBuf buf) {
        return new PacketClearRecipeCache();
    }
}
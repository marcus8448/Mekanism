package mekanism.common.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkCommand {

    private static final LongSet chunkWatchers = new LongOpenHashSet();

    static ArgumentBuilder<ServerCommandSource, ?> register() {
        MinecraftForge.EVENT_BUS.register(ChunkCommand.class);
        return CommandManager.literal("chunk")
              .then(WatchCommand.register())
              .then(UnwatchCommand.register())
              .then(ClearCommand.register())
              .then(FlushCommand.register());
    }

    private static class WatchCommand {

        static ArgumentBuilder<ServerCommandSource, ?> register() {
            return CommandManager.literal("watch")
                  .requires(cs -> cs.hasPermissionLevel(4))
                  .executes(ctx -> {
                      ServerCommandSource source = ctx.getSource();
                      Entity entity = source.getEntity();
                      ChunkPos chunkPos = new ChunkPos(entity.getBlockPos());
                      chunkWatchers.add(ChunkPos.toLong(chunkPos.x, chunkPos.z));
                      source.sendFeedback(MekanismLang.COMMAND_CHUNK_WATCH.translate(chunkPos.x, chunkPos.z), true);
                      return 0;
                  });
        }
    }

    private static class UnwatchCommand {

        static ArgumentBuilder<ServerCommandSource, ?> register() {
            return CommandManager.literal("unwatch")
                  .requires(cs -> cs.hasPermissionLevel(4))
                  .executes(ctx -> {
                      ServerCommandSource source = ctx.getSource();
                      Entity entity = source.getEntity();
                      ChunkPos chunkPos = new ChunkPos(entity.getBlockPos());
                      chunkWatchers.remove(ChunkPos.toLong(chunkPos.x, chunkPos.z));
                      source.sendFeedback(MekanismLang.COMMAND_CHUNK_UNWATCH.translate(chunkPos.x, chunkPos.z), true);
                      return 0;
                  });
        }
    }

    private static class ClearCommand {

        static ArgumentBuilder<ServerCommandSource, ?> register() {
            return CommandManager.literal("clear")
                  .requires(cs -> cs.hasPermissionLevel(4))
                  .executes(ctx -> {
                      int count = chunkWatchers.size();
                      chunkWatchers.clear();
                      ctx.getSource().sendFeedback(MekanismLang.COMMAND_CHUNK_CLEAR.translate(count), true);
                      return 0;
                  });
        }
    }

    private static class FlushCommand {

        static ArgumentBuilder<ServerCommandSource, ?> register() {
            return CommandManager.literal("flush")
                  .requires(cs -> cs.hasPermissionLevel(4))
                  .executes(ctx -> {
                      ServerCommandSource source = ctx.getSource();
                      ServerChunkManager sp = source.getWorld().getChunkManager();
                      int startCount = sp.getLoadedChunkCount();
                      //TODO: Check this
                      //sp.queueUnloadAll();
                      sp.tick(() -> false);
                      ctx.getSource().sendFeedback(MekanismLang.COMMAND_CHUNK_FLUSH.translate(startCount - sp.getLoadedChunkCount()), true);
                      return 0;
                  });
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        handleChunkEvent(event, MekanismLang.COMMAND_CHUNK_LOADED);
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        handleChunkEvent(event, MekanismLang.COMMAND_CHUNK_UNLOADED);
    }

    private static void handleChunkEvent(ChunkEvent event, ILangEntry direction) {
        if (event.getWorld() == null || event.getWorld().isClient()) {
            return;
        }
        ChunkPos pos = event.getChunk().getPos();
        if (chunkWatchers.contains(pos.toLong())) {
            Text message = MekanismLang.COMMAND_CHUNK.translate(direction, pos.x, pos.z);
            event.getWorld().getPlayers().forEach(player -> player.sendSystemMessage(message, Util.NIL_UUID));
        }
    }
}
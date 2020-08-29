package mekanism.common.command;


import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.command.builders.BuildCommand;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;

public class CommandMek {

    private static final Map<UUID, Stack<BlockPos>> tpStack = new Object2ObjectOpenHashMap<>();

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("mek")
              .requires(cs -> cs.getEntity() instanceof ServerPlayerEntity)
              .then(DebugCommand.register())
              .then(TestRulesCommand.register())
              .then(TpCommand.register())
              .then(TppopCommand.register())
              .then(ChunkCommand.register())
              .then(BuildCommand.COMMAND)
              .then(RadiationCommand.register());
    }

    private static class DebugCommand {

        static ArgumentBuilder<ServerCommandSource, ?> register() {
            return CommandManager.literal("debug")
                  .requires(cs -> cs.hasPermissionLevel(4))
                  .executes(ctx -> {
                      MekanismAPI.debug = !MekanismAPI.debug;
                      ctx.getSource().sendFeedback(MekanismLang.COMMAND_DEBUG.translate(OnOff.of(MekanismAPI.debug)), true);
                      return 0;
                  });
        }
    }

    private static class TestRulesCommand {

        static ArgumentBuilder<ServerCommandSource, ?> register() {
            return CommandManager.literal("testrules")
                  .requires(cs -> cs.hasPermissionLevel(4))
                  .executes(ctx -> {
                      ServerCommandSource source = ctx.getSource();
                      MinecraftServer server = source.getMinecraftServer();
                      GameRules rules = server.getGameRules();
                      rules.get(GameRules.KEEP_INVENTORY).set(true, server);
                      rules.get(GameRules.DO_MOB_SPAWNING).set(false, server);
                      rules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, server);
                      rules.get(GameRules.DO_WEATHER_CYCLE).set(false, server);
                      rules.get(GameRules.DO_MOB_GRIEFING).set(false, server);
                      ((ServerWorld) source.getPlayer().getEntityWorld()).setTimeOfDay(2_000);
                      source.sendFeedback(MekanismLang.COMMAND_TEST_RULES.translate(), true);
                      return 0;
                  });
        }
    }

    private static class TpCommand {

        static ArgumentBuilder<ServerCommandSource, ?> register() {
            return CommandManager.literal("tp")
                  .requires(cs -> cs.hasPermissionLevel(4))
                  .then(CommandManager.argument("location", Vec3ArgumentType.vec3())
                        .executes(ctx -> {
                            ServerCommandSource source = ctx.getSource();
                            Entity entity = source.getEntity();
                            // Save the current location on the stack
                            if (entity != null) {
                                UUID player = entity.getUuid();
                                Stack<BlockPos> playerLocations = tpStack.getOrDefault(player, new Stack<>());
                                playerLocations.push(entity.getBlockPos());
                                tpStack.put(player, playerLocations);

                                PosArgument location = Vec3ArgumentType.getPosArgument(ctx, "location");
                                Vec3d position = location.toAbsolutePos(source);
                                // Teleport user to new location
                                teleport(entity, position.getX(), position.getY(), position.getZ());
                                source.sendFeedback(MekanismLang.COMMAND_TP.translate(position.getX(), position.getY(), position.getZ()), true);
                            }
                            return 0;
                        }));
        }
    }

    private static class TppopCommand {

        static ArgumentBuilder<ServerCommandSource, ?> register() {
            return CommandManager.literal("tpop")
                  .requires(cs -> cs.hasPermissionLevel(4))
                  .executes(ctx -> {
                      ServerCommandSource source = ctx.getSource();
                      UUID player = source.getEntity().getUuid();

                      // Get stack of locations for the user; if there's at least one entry, pop it off
                      // and send the user back there
                      Stack<BlockPos> playerLocations = tpStack.getOrDefault(player, new Stack<>());
                      if (playerLocations.isEmpty()) {
                          source.sendFeedback(MekanismLang.COMMAND_TPOP_EMPTY.translate(), true);
                      } else {
                          BlockPos lastPos = playerLocations.pop();
                          tpStack.put(player, playerLocations);
                          teleport(source.getEntity(), lastPos.getX(), lastPos.getY(), lastPos.getZ());
                          source.sendFeedback(MekanismLang.COMMAND_TPOP.translate(lastPos.getX(), lastPos.getY(), lastPos.getZ(), playerLocations.size()), true);
                      }
                      return 0;
                  });
        }
    }

    private static class RadiationCommand {

        static ArgumentBuilder<ServerCommandSource, ?> register() {
            return CommandManager.literal("radiation")
                  .requires(cs -> cs.hasPermissionLevel(4))
                  .then(CommandManager.literal("add").then(CommandManager.argument("magnitude", DoubleArgumentType.doubleArg(0, 10_000))
                        .executes(ctx -> {
                            try {
                                ServerCommandSource source = ctx.getSource();
                                Coord4D location = new Coord4D(source.getPosition().x, source.getPosition().y, source.getPosition().z, source.getWorld().getRegistryKey());
                                double magnitude = DoubleArgumentType.getDouble(ctx, "magnitude");
                                Mekanism.radiationManager.radiate(location, magnitude);
                                source.sendFeedback(MekanismLang.COMMAND_RADIATION_ADD.translate(location), true);
                            } catch (Exception e) {
                                Mekanism.logger.error("Failed to radiate", e);
                            }
                            return 0;
                        })))
                  .then(CommandManager.literal("get")
                        .executes(ctx -> {
                            ServerCommandSource source = ctx.getSource();
                            Coord4D location = new Coord4D(source.getPosition().x, source.getPosition().y, source.getPosition().z, source.getWorld().getRegistryKey());
                            double radiation = Mekanism.radiationManager.getRadiationLevel(location);
                            source.sendFeedback(MekanismLang.COMMAND_RADIATION_GET.translate(radiation), true);
                            return 0;
                        }))
                  .then(CommandManager.literal("heal")
                        .executes(ctx -> {
                            if (ctx.getSource().getEntity() instanceof ServerPlayerEntity) {
                                ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
                                player.getCapability(Capabilities.RADIATION_ENTITY_CAPABILITY).ifPresent(c -> c.set(0));
                                ctx.getSource().sendFeedback(MekanismLang.COMMAND_RADIATION_CLEAR.translate(), true);
                            }
                            return 0;
                        }))
                  .then(CommandManager.literal("removeAll")
                        .executes(ctx -> {
                            ServerCommandSource source = ctx.getSource();
                            Mekanism.radiationManager.clearSources();
                            source.sendFeedback(MekanismLang.COMMAND_RADIATION_REMOVE_ALL.translate(), true);
                            return 0;
                        }));
        }
    }

    private static void teleport(Entity player, double x, double y, double z) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity mp = (ServerPlayerEntity) player;
            mp.networkHandler.requestTeleport(x, y, z, mp.yaw, mp.pitch);
        } else {
            ClientPlayerEntity sp = (ClientPlayerEntity) player;
            sp.refreshPositionAndAngles(x, y, z, sp.yaw, sp.pitch);
        }
    }
}
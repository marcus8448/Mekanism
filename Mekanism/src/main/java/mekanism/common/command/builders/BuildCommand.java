package mekanism.common.command.builders;

import com.mojang.brigadier.builder.ArgumentBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import mekanism.common.Mekanism;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BuildCommand {

    public static final ArgumentBuilder<ServerCommandSource, ?> COMMAND = CommandManager.literal("build")
          .then(CommandManager.literal("remove")
                .requires(cs -> cs.hasPermissionLevel(4))
                .executes(ctx -> {
                    ServerCommandSource source = ctx.getSource();
                    Entity entity = source.getEntity();
                    if (entity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
                        BlockHitResult result = MekanismUtils.rayTrace(player, 100);
                        if (result.getType() != HitResult.Type.MISS) {
                            destroy(source.getWorld(), result.getBlockPos());
                        }
                    }
                    return 0;
                }));

    public static void register(String name, StructureBuilder builder) {
        COMMAND.then(CommandManager.literal(name)
              .requires(cs -> cs.hasPermissionLevel(4))
              .executes(ctx -> {
                  ServerCommandSource source = ctx.getSource();
                  Entity entity = source.getEntity();
                  if (entity instanceof ServerPlayerEntity) {
                      ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
                      BlockHitResult result = MekanismUtils.rayTrace(player, 100);
                      if (result.getType() != HitResult.Type.MISS) {
                          BlockPos pos = result.getBlockPos().offset(Direction.UP);
                          builder.build(source.getWorld(), pos);
                      }
                  }
                  return 0;
              }));
    }

    private static void destroy(World world, BlockPos pos) {
        Set<BlockPos> traversed = new HashSet<>();
        Stack<BlockPos> openSet = new Stack<>();
        openSet.add(pos);
        traversed.add(pos);
        while (!openSet.isEmpty()) {
            BlockPos ptr = openSet.pop();
            BlockState state = world.getBlockState(ptr);
            if (state.getBlock().getRegistryName().getNamespace().contains(Mekanism.MODID)) {
                world.removeBlock(ptr, false);
                for (Direction side : EnumUtils.DIRECTIONS) {
                    BlockPos offset = ptr.offset(side);
                    if (!traversed.contains(offset)) {
                        openSet.add(offset);
                        traversed.add(offset);
                    }
                }
            }
        }
    }
}

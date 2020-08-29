package mekanism.generators.common.network;

import java.util.function.Supplier;
import mekanism.api.functions.TriConsumer;
import mekanism.common.network.BasePacketHandler;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorLogicAdapter;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorLogicAdapter.FissionReactorLogic;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorController;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorLogicAdapter;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorLogicAdapter.FusionReactorLogic;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

/**
 * Used for informing the server that an action happened in a GUI
 */
public class PacketGeneratorsGuiInteract {

    private final GeneratorsGuiInteraction interaction;
    private final BlockPos tilePosition;
    private final double extra;

    public PacketGeneratorsGuiInteract(GeneratorsGuiInteraction interaction, BlockEntity tile) {
        this(interaction, tile.getPos());
    }

    public PacketGeneratorsGuiInteract(GeneratorsGuiInteraction interaction, BlockEntity tile, double extra) {
        this(interaction, tile.getPos(), extra);
    }

    public PacketGeneratorsGuiInteract(GeneratorsGuiInteraction interaction, BlockPos tilePosition) {
        this(interaction, tilePosition, 0);
    }

    public PacketGeneratorsGuiInteract(GeneratorsGuiInteraction interaction, BlockPos tilePosition, double extra) {
        this.interaction = interaction;
        this.tilePosition = tilePosition;
        this.extra = extra;
    }

    public static void handle(PacketGeneratorsGuiInteract message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            TileEntityMekanism tile = MekanismUtils.getTileEntity(TileEntityMekanism.class, player.world, message.tilePosition);
            if (tile != null) {
                message.interaction.consume(tile, player, message.extra);
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketGeneratorsGuiInteract pkt, PacketByteBuf buf) {
        buf.writeEnumConstant(pkt.interaction);
        buf.writeBlockPos(pkt.tilePosition);
        buf.writeDouble(pkt.extra);
    }

    public static PacketGeneratorsGuiInteract decode(PacketByteBuf buf) {
        return new PacketGeneratorsGuiInteract(buf.readEnumConstant(GeneratorsGuiInteraction.class), buf.readBlockPos(), buf.readDouble());
    }

    public enum GeneratorsGuiInteraction {
        INJECTION_RATE((tile, player, extra) -> {
            if (tile instanceof TileEntityFusionReactorBlock) {
                ((TileEntityFusionReactorController) tile).setInjectionRateFromPacket((int) Math.round(extra));
            } else if (tile instanceof TileEntityFissionReactorCasing) {
                ((TileEntityFissionReactorCasing) tile).setRateLimitFromPacket(extra);
            }
        }),
        LOGIC_TYPE((tile, player, extra) -> {
            if (tile instanceof TileEntityFissionReactorLogicAdapter) {
                ((TileEntityFissionReactorLogicAdapter) tile).setLogicTypeFromPacket(FissionReactorLogic.byIndexStatic((int) Math.round(extra)));
            } else if (tile instanceof TileEntityFusionReactorLogicAdapter) {
                ((TileEntityFusionReactorLogicAdapter) tile).setLogicTypeFromPacket(FusionReactorLogic.byIndexStatic((int) Math.round(extra)));
            }
        }),
        FISSION_ACTIVE((tile, player, extra) -> {
            if (tile instanceof TileEntityFissionReactorCasing) {
                ((TileEntityFissionReactorCasing) tile).setReactorActive(Math.round(extra) == 1);
            }
        });

        private final TriConsumer<TileEntityMekanism, PlayerEntity, Double> consumerForTile;

        GeneratorsGuiInteraction(TriConsumer<TileEntityMekanism, PlayerEntity, Double> consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, PlayerEntity player, double extra) {
            consumerForTile.accept(tile, player, extra);
        }
    }
}
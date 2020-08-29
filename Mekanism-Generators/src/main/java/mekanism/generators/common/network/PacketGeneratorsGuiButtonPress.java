package mekanism.generators.common.network;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.network.BasePacketHandler;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.container.FusionReactorFuelTabContainer;
import mekanism.generators.common.container.FusionReactorHeatTabContainer;
import mekanism.generators.common.registries.GeneratorsContainerTypes;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorController;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Used for informing the server that a click happened in a GUI and the gui window needs to change
 */
public class PacketGeneratorsGuiButtonPress {

    private final ClickedGeneratorsTileButton tileButton;
    private final int extra;
    private final BlockPos tilePosition;

    public PacketGeneratorsGuiButtonPress(ClickedGeneratorsTileButton buttonClicked, BlockPos tilePosition) {
        this(buttonClicked, tilePosition, 0);
    }

    public PacketGeneratorsGuiButtonPress(ClickedGeneratorsTileButton buttonClicked, BlockPos tilePosition, int extra) {
        this.tileButton = buttonClicked;
        this.tilePosition = tilePosition;
        this.extra = extra;
    }

    public static void handle(PacketGeneratorsGuiButtonPress message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            if (!player.world.isClient) {
                //If we are on the server (the only time we should be receiving this packet), let forge handle switching the Gui
                TileEntityMekanism tile = MekanismUtils.getTileEntity(TileEntityMekanism.class, player.world, message.tilePosition);
                if (tile != null) {
                    NamedScreenHandlerFactory provider = message.tileButton.getProvider(tile, message.extra);
                    if (provider != null) {
                        //Ensure valid data
                        NetworkHooks.openGui((ServerPlayerEntity) player, provider, buf -> {
                            buf.writeBlockPos(message.tilePosition);
                            buf.writeVarInt(message.extra);
                        });
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketGeneratorsGuiButtonPress pkt, PacketByteBuf buf) {
        buf.writeEnumConstant(pkt.tileButton);
        buf.writeBlockPos(pkt.tilePosition);
        buf.writeVarInt(pkt.extra);
    }

    public static PacketGeneratorsGuiButtonPress decode(PacketByteBuf buf) {
        return new PacketGeneratorsGuiButtonPress(buf.readEnumConstant(ClickedGeneratorsTileButton.class), buf.readBlockPos(), buf.readVarInt());
    }

    public enum ClickedGeneratorsTileButton {
        TAB_MAIN((tile, extra) -> {
            if (tile instanceof TileEntityTurbineCasing) {
                return new ContainerProvider(GeneratorsLang.TURBINE, (i, inv, player) -> new MekanismTileContainer<>(GeneratorsContainerTypes.INDUSTRIAL_TURBINE, i, inv, (TileEntityTurbineCasing) tile));
            } else if (tile instanceof TileEntityFissionReactorCasing) {
                return new ContainerProvider(GeneratorsLang.FISSION_REACTOR, (i, inv, player) -> new EmptyTileContainer<>(GeneratorsContainerTypes.FISSION_REACTOR, i, inv, (TileEntityFissionReactorCasing) tile));
            }
            return null;
        }),
        TAB_HEAT((tile, extra) -> {
            if (tile instanceof TileEntityFusionReactorController) {
                return new ContainerProvider(GeneratorsLang.HEAT_TAB, (i, inv, player) -> new FusionReactorHeatTabContainer(i, inv, (TileEntityFusionReactorController) tile));
            }
            return null;
        }),
        TAB_FUEL((tile, extra) -> {
            if (tile instanceof TileEntityFusionReactorController) {
                return new ContainerProvider(GeneratorsLang.FUEL_TAB, (i, inv, player) -> new FusionReactorFuelTabContainer(i, inv, (TileEntityFusionReactorController) tile));
            }
            return null;
        }),
        TAB_STATS((tile, extra) -> {
            if (tile instanceof TileEntityTurbineCasing) {
                return new ContainerProvider(GeneratorsLang.TURBINE_STATS, (i, inv, player) -> new EmptyTileContainer<>(GeneratorsContainerTypes.TURBINE_STATS, i, inv, (TileEntityTurbineCasing) tile));
            } else if (tile instanceof TileEntityFusionReactorController) {
                return new ContainerProvider(GeneratorsLang.STATS_TAB, (i, inv, player) -> new EmptyTileContainer<>(GeneratorsContainerTypes.FUSION_REACTOR_STATS, i, inv, (TileEntityFusionReactorController) tile));
            } else if (tile instanceof TileEntityFissionReactorCasing) {
                return new ContainerProvider(GeneratorsLang.STATS_TAB, (i, inv, player) -> new EmptyTileContainer<>(GeneratorsContainerTypes.FISSION_REACTOR_STATS, i, inv, (TileEntityFissionReactorCasing) tile));
            }
            return null;
        });

        private final BiFunction<TileEntityMekanism, Integer, NamedScreenHandlerFactory> providerFromTile;

        ClickedGeneratorsTileButton(BiFunction<TileEntityMekanism, Integer, NamedScreenHandlerFactory> providerFromTile) {
            this.providerFromTile = providerFromTile;
        }

        public NamedScreenHandlerFactory getProvider(TileEntityMekanism tile, int extra) {
            return providerFromTile.apply(tile, extra);
        }
    }
}
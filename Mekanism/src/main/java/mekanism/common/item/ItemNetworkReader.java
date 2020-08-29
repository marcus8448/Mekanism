package mekanism.common.item;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.network.transmitter.Transmitter;
import mekanism.common.lib.transmitter.DynamicNetwork;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemNetworkReader extends ItemEnergized {

    public ItemNetworkReader(Settings properties) {
        super(MekanismConfig.gear.networkReaderChargeRate, MekanismConfig.gear.networkReaderMaxEnergy, properties.rarity(Rarity.UNCOMMON));
    }

    private void displayBorder(PlayerEntity player, Object toDisplay, boolean brackets) {
        player.sendSystemMessage(MekanismLang.NETWORK_READER_BORDER.translateColored(EnumColor.GRAY, "-------------", EnumColor.DARK_BLUE,
              brackets ? MekanismLang.GENERIC_SQUARE_BRACKET.translate(toDisplay) : toDisplay), Util.NIL_UUID);
    }

    private void displayEndBorder(PlayerEntity player) {
        displayBorder(player, "[=======]", false);
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        if (!world.isClient && player != null) {
            BlockPos pos = context.getBlockPos();
            BlockEntity tile = MekanismUtils.getTileEntity(world, pos);
            if (tile != null) {
                if (!player.isCreative()) {
                    FloatingLong energyPerUse = MekanismConfig.gear.networkReaderEnergyUsage.get();
                    IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(player.getStackInHand(context.getHand()), 0);
                    if (energyContainer == null || energyContainer.extract(energyPerUse, Action.SIMULATE, AutomationType.MANUAL).smallerThan(energyPerUse)) {
                        return ActionResult.FAIL;
                    }
                    energyContainer.extract(energyPerUse, Action.EXECUTE, AutomationType.MANUAL);
                }
                Direction opposite = context.getSide().getOpposite();
                if (tile instanceof TileEntityTransmitter) {
                    displayTransmitterInfo(player, ((TileEntityTransmitter) tile).getTransmitter(), tile, opposite);
                } else {
                    Optional<IHeatHandler> heatHandler = MekanismUtils.toOptional(CapabilityUtils.getCapability(tile, Capabilities.HEAT_HANDLER_CAPABILITY, opposite));
                    if (heatHandler.isPresent()) {
                        IHeatHandler transfer = heatHandler.get();
                        displayBorder(player, MekanismLang.MEKANISM, true);
                        sendTemperature(player, transfer);
                        displayEndBorder(player);
                    } else {
                        displayConnectedNetworks(player, world, pos);
                    }
                }
                return ActionResult.SUCCESS;
            } else if (player.isSneaking() && MekanismAPI.debug) {
                displayBorder(player, MekanismLang.DEBUG_TITLE, true);
                for (Text component : TransmitterNetworkRegistry.getInstance().toComponents()) {
                    player.sendSystemMessage(TextComponentUtil.build(EnumColor.DARK_GRAY, component), Util.NIL_UUID);
                }
                displayEndBorder(player);
            }
        }
        return ActionResult.PASS;
    }

    private void displayTransmitterInfo(PlayerEntity player, Transmitter<?, ?, ?> transmitter, BlockEntity tile, Direction opposite) {
        displayBorder(player, MekanismLang.MEKANISM, true);
        if (transmitter.hasTransmitterNetwork()) {
            DynamicNetwork<?, ?, ?> transmitterNetwork = transmitter.getTransmitterNetwork();
            player.sendSystemMessage(MekanismLang.NETWORK_READER_TRANSMITTERS.translateColored(EnumColor.GRAY, EnumColor.DARK_GRAY, transmitterNetwork.transmittersSize()), Util.NIL_UUID);
            player.sendSystemMessage(MekanismLang.NETWORK_READER_ACCEPTORS.translateColored(EnumColor.GRAY, EnumColor.DARK_GRAY, transmitterNetwork.getAcceptorCount()), Util.NIL_UUID);
            sendMessageIfNonNull(player, MekanismLang.NETWORK_READER_NEEDED, transmitterNetwork.getNeededInfo());
            sendMessageIfNonNull(player, MekanismLang.NETWORK_READER_BUFFER, transmitterNetwork.getStoredInfo());
            sendMessageIfNonNull(player, MekanismLang.NETWORK_READER_THROUGHPUT, transmitterNetwork.getFlowInfo());
            sendMessageIfNonNull(player, MekanismLang.NETWORK_READER_CAPACITY, transmitterNetwork.getNetworkReaderCapacity());
            CapabilityUtils.getCapability(tile, Capabilities.HEAT_HANDLER_CAPABILITY, opposite).ifPresent(heatHandler -> sendTemperature(player, heatHandler));
        } else {
            player.sendSystemMessage(MekanismLang.NO_NETWORK.translate(), Util.NIL_UUID);
        }
        displayEndBorder(player);
    }

    private void displayConnectedNetworks(PlayerEntity player, World world, BlockPos pos) {
        Set<DynamicNetwork<?, ?, ?>> iteratedNetworks = new ObjectOpenHashSet<>();
        for (Direction side : EnumUtils.DIRECTIONS) {
            BlockEntity tile = MekanismUtils.getTileEntity(world, pos.offset(side));
            if (tile instanceof TileEntityTransmitter) {
                Transmitter<?, ?, ?> transmitter = ((TileEntityTransmitter) tile).getTransmitter();
                DynamicNetwork<?, ?, ?> transmitterNetwork = transmitter.getTransmitterNetwork();
                if (transmitterNetwork.hasAcceptor(pos) && !iteratedNetworks.contains(transmitterNetwork)) {
                    displayBorder(player, compileList(transmitter.getSupportedTransmissionTypes()), false);
                    player.sendSystemMessage(MekanismLang.NETWORK_READER_CONNECTED_SIDES.translateColored(EnumColor.GRAY, EnumColor.DARK_GRAY,
                          compileList(transmitterNetwork.getAcceptorDirections(pos))), Util.NIL_UUID);
                    displayEndBorder(player);
                    iteratedNetworks.add(transmitterNetwork);
                }
            }
        }
    }

    private void sendTemperature(PlayerEntity player, IHeatHandler handler) {
        Text temp = MekanismUtils.getTemperatureDisplay(handler.getTotalTemperature(), TemperatureUnit.KELVIN, true);
        player.sendSystemMessage(MekanismLang.NETWORK_READER_TEMPERATURE.translateColored(EnumColor.GRAY, EnumColor.DARK_GRAY, temp), Util.NIL_UUID);
    }

    private void sendMessageIfNonNull(PlayerEntity player, ILangEntry langEntry, Object toSend) {
        if (toSend != null) {
            player.sendSystemMessage(langEntry.translateColored(EnumColor.GRAY, EnumColor.DARK_GRAY, toSend), Util.NIL_UUID);
        }
    }

    private <ENUM extends Enum<ENUM>> Text compileList(Set<ENUM> elements) {
        if (elements.isEmpty()) {
            return MekanismLang.GENERIC_SQUARE_BRACKET.translate("");
        }
        Text component = null;
        for (ENUM element : elements) {
            if (component == null) {
                component = TextComponentUtil.build(element);
            } else {
                component = MekanismLang.GENERIC_WITH_COMMA.translate(component, element);
            }
        }
        return MekanismLang.GENERIC_SQUARE_BRACKET.translate(component);
    }
}
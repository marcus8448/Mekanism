package mekanism.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.Action;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.item.SeismicReaderContainer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemSeismicReader extends ItemEnergized {

    public ItemSeismicReader(Settings properties) {
        super(MekanismConfig.gear.seismicReaderChargeRate, MekanismConfig.gear.seismicReaderMaxEnergy, properties.rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        if (MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.descriptionKey)) {
            tooltip.add(MekanismLang.DESCRIPTION_SEISMIC_READER.translate());
        } else if (MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.detailsKey)) {
            super.appendTooltip(stack, world, tooltip, flag);
        } else {
            tooltip.add(MekanismLang.HOLD_FOR_DETAILS.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getBoundKeyLocalizedText()));
            tooltip.add(MekanismLang.HOLD_FOR_DESCRIPTION.translateColored(EnumColor.GRAY, EnumColor.AQUA, MekanismKeyHandler.descriptionKey.getBoundKeyLocalizedText()));
        }
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) {
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        if (!MekanismUtils.isChunkVibrated(new ChunkPos(player.getBlockPos()), player.world)) {
            player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.RED, MekanismLang.NO_VIBRATIONS), Util.NIL_UUID);
        } else {
            if (!player.isCreative()) {
                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                FloatingLong energyUsage = MekanismConfig.gear.seismicReaderEnergyUsage.get();
                if (energyContainer == null || energyContainer.extract(energyUsage, Action.SIMULATE, AutomationType.MANUAL).smallerThan(energyUsage)) {
                    player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.RED, MekanismLang.NEEDS_ENERGY), Util.NIL_UUID);
                    return new TypedActionResult<>(ActionResult.SUCCESS, stack);
                }
                energyContainer.extract(energyUsage, Action.EXECUTE, AutomationType.MANUAL);
            }
            NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack.getName(), (i, inv, p) -> new SeismicReaderContainer(i, inv, hand, stack)), buf -> {
                buf.writeEnumConstant(hand);
                buf.writeItemStack(stack);
            });
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }
}
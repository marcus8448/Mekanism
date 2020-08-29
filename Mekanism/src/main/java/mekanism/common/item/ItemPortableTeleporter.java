package mekanism.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.item.PortableTeleporterContainer;
import mekanism.common.lib.frequency.IFrequencyItem;
import mekanism.common.network.PacketSecurityUpdate;
import mekanism.common.util.SecurityUtils;
import mekanism.common.util.text.OwnerDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemPortableTeleporter extends ItemEnergized implements IFrequencyItem {

    public ItemPortableTeleporter(Settings properties) {
        super(MekanismConfig.gear.portableTeleporterChargeRate, MekanismConfig.gear.portableTeleporterMaxEnergy, properties.rarity(Rarity.RARE));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        tooltip.add(OwnerDisplay.of(MinecraftClient.getInstance().player, getOwnerUUID(stack)).getTextComponent());
        if (getFrequency(stack) != null) {
            tooltip.add(MekanismLang.FREQUENCY.translateColored(EnumColor.INDIGO, EnumColor.GRAY, getFrequency(stack).getKey()));
            tooltip.add(MekanismLang.MODE.translateColored(EnumColor.INDIGO, EnumColor.GRAY, !getFrequency(stack).isPublic() ? MekanismLang.PRIVATE : MekanismLang.PUBLIC));
        }
        super.appendTooltip(stack, world, tooltip, flag);
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient) {
            if (getOwnerUUID(stack) == null) {
                setOwnerUUID(stack, player.getUuid());
                Mekanism.packetHandler.sendToAll(new PacketSecurityUpdate(player.getUuid(), null));
                player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.GRAY, MekanismLang.NOW_OWN), Util.NIL_UUID);
            } else if (SecurityUtils.canAccess(player, stack)) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack.getName(), (i, inv, p) -> new PortableTeleporterContainer(i, inv, hand, stack)), buf -> {
                    buf.writeEnumConstant(hand);
                    buf.writeItemStack(stack);
                });
            } else {
                SecurityUtils.displayNoAccess(player);
            }
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }
}
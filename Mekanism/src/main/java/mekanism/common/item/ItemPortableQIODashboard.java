package mekanism.common.item;

import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.api.text.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.item.PortableQIODashboardContainer;
import mekanism.common.item.interfaces.IGuiItem;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.IFrequencyItem;
import mekanism.common.network.PacketSecurityUpdate;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemPortableQIODashboard extends Item implements IFrequencyItem, IGuiItem {

    public ItemPortableQIODashboard(Settings properties) {
        super(properties.maxCount(1).rarity(Rarity.RARE));
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient) {
            if (getOwnerUUID(stack) == null) {
                setOwnerUUID(stack, player.getUuid());
                Mekanism.packetHandler.sendToAll(new PacketSecurityUpdate(player.getUuid(), null));
                player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.GRAY, MekanismLang.NOW_OWN),
                      Util.NIL_UUID);
            } else if (SecurityUtils.canAccess(player, stack)) {
                NetworkHooks.openGui((ServerPlayerEntity) player, getContainerProvider(stack, hand), buf -> {
                    buf.writeEnumConstant(hand);
                    buf.writeItemStack(stack);
                });
            } else {
                SecurityUtils.displayNoAccess(player);
            }
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    public NamedScreenHandlerFactory getContainerProvider(ItemStack stack, Hand hand) {
        return new ContainerProvider(stack.getName(), (i, inv, p) -> new PortableQIODashboardContainer(i, inv, hand, stack));
    }

    @Override
    public void setFrequency(ItemStack stack, Frequency frequency) {
        IFrequencyItem.super.setFrequency(stack, frequency);
        setColor(stack, frequency != null ? ((QIOFrequency) frequency).getColor() : null);
    }

    public EnumColor getColor(ItemStack stack) {
        if (ItemDataUtils.hasData(stack, NBTConstants.COLOR, NBT.TAG_INT)) {
            return EnumColor.byIndexStatic(ItemDataUtils.getInt(stack, NBTConstants.COLOR));
        }
        return null;
    }

    public void setColor(ItemStack stack, EnumColor color) {
        if (color == null) {
            ItemDataUtils.removeData(stack, NBTConstants.COLOR);
        } else {
            ItemDataUtils.setInt(stack, NBTConstants.COLOR, color.ordinal());
        }
    }
}

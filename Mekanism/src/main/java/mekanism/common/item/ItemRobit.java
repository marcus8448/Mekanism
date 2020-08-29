package mekanism.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.Coord4D;
import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.entity.EntityRobit;
import mekanism.common.item.interfaces.IItemSustainedInventory;
import mekanism.common.tile.TileEntityChargepad;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.BooleanStateDisplay.YesNo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemRobit extends ItemEnergized implements IItemSustainedInventory {

    public ItemRobit(Settings properties) {
        super(() -> EntityRobit.MAX_ENERGY.multiply(0.005), () -> EntityRobit.MAX_ENERGY, properties.rarity(Rarity.RARE));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        super.appendTooltip(stack, world, tooltip, flag);
        tooltip.add(MekanismLang.ROBIT_NAME.translateColored(EnumColor.INDIGO, EnumColor.GRAY, getName(stack)));
        tooltip.add(MekanismLang.HAS_INVENTORY.translateColored(EnumColor.AQUA, EnumColor.GRAY, YesNo.of(hasInventory(stack))));
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResult.PASS;
        }
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        TileEntityMekanism chargepad = MekanismUtils.getTileEntity(TileEntityChargepad.class, world, pos);
        if (chargepad != null) {
            if (!chargepad.getActive()) {
                Hand hand = context.getHand();
                ItemStack stack = player.getStackInHand(hand);
                if (!world.isClient) {
                    EntityRobit robit = new EntityRobit(world, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
                    robit.setHome(Coord4D.get(chargepad));
                    IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                    if (energyContainer != null) {
                        robit.getEnergyContainer().setEnergy(energyContainer.getEnergy());
                    }
                    robit.setOwnerUUID(player.getUuid());
                    robit.setInventory(getInventory(stack));
                    robit.setCustomName(getName(stack));
                    world.spawnEntity(robit);
                }
                player.setStackInHand(hand, ItemStack.EMPTY);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public void setName(ItemStack stack, Text name) {
        ItemDataUtils.setString(stack, NBTConstants.NAME, Text.Serializer.toJson(name));
    }

    public Text getName(ItemStack stack) {
        String name = ItemDataUtils.getString(stack, NBTConstants.NAME);
        return name.isEmpty() ? MekanismLang.ROBIT.translate() : Text.Serializer.fromJson(name);
    }
}
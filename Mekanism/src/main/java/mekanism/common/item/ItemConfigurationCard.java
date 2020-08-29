package mekanism.common.item;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import mekanism.api.IConfigCardAccess.ISpecialConfigData;
import mekanism.api.NBTConstants;
import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.tile.interfaces.IRedstoneControl.RedstoneControl;
import mekanism.common.tile.interfaces.ISideConfiguration;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemConfigurationCard extends Item {

    public ItemConfigurationCard(Settings properties) {
        super(properties.maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, List<Text> tooltip, @Nonnull TooltipContext flag) {
        tooltip.add(MekanismLang.CONFIG_CARD_HAS_DATA.translateColored(EnumColor.GRAY, EnumColor.INDIGO, TextComponentUtil.translate(getDataType(stack))));
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        if (!world.isClient && player != null) {
            BlockPos pos = context.getBlockPos();
            Direction side = context.getSide();
            BlockEntity tile = MekanismUtils.getTileEntity(world, pos);
            if (CapabilityUtils.getCapability(tile, Capabilities.CONFIG_CARD_CAPABILITY, side).isPresent()) {
                if (SecurityUtils.canAccess(player, tile)) {
                    ItemStack stack = player.getStackInHand(context.getHand());
                    if (player.isSneaking()) {
                        Optional<ISpecialConfigData> configData = MekanismUtils.toOptional(CapabilityUtils.getCapability(tile, Capabilities.SPECIAL_CONFIG_DATA_CAPABILITY, side));
                        CompoundTag data = configData.isPresent() ? configData.get().getConfigurationData(getBaseData(tile)) : getBaseData(tile);
                        if (data != null) {
                            data.putString(NBTConstants.DATA_TYPE, getNameFromTile(tile, side));
                            setData(stack, data);
                            player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM,
                                  MekanismLang.CONFIG_CARD_GOT.translateColored(EnumColor.GRAY, EnumColor.INDIGO,
                                        TextComponentUtil.translate(data.getString(NBTConstants.DATA_TYPE)))), Util.NIL_UUID);
                        }
                        return ActionResult.SUCCESS;
                    }
                    CompoundTag data = getData(stack);
                    if (data != null) {
                        if (getNameFromTile(tile, side).equals(getDataType(stack))) {
                            setBaseData(data, tile);
                            CapabilityUtils.getCapability(tile, Capabilities.SPECIAL_CONFIG_DATA_CAPABILITY, side).ifPresent(special -> special.setConfigurationData(data));

                            if (tile instanceof TileEntityMekanism) {
                                TileEntityMekanism mekanismTile = (TileEntityMekanism) tile;
                                mekanismTile.invalidateCachedCapabilities();
                                mekanismTile.sendUpdatePacket();
                                MekanismUtils.notifyLoadedNeighborsOfTileChange(world, pos);
                            }
                            player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM,
                                  MekanismLang.CONFIG_CARD_SET.translateColored(EnumColor.DARK_GREEN, EnumColor.INDIGO,
                                        TextComponentUtil.translate(getDataType(stack)))), Util.NIL_UUID);
                        } else {
                            player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.RED,
                                  MekanismLang.CONFIG_CARD_UNEQUAL), Util.NIL_UUID);
                        }
                        return ActionResult.SUCCESS;
                    }
                } else {
                    SecurityUtils.displayNoAccess(player);
                }
            }
        }
        return ActionResult.PASS;
    }

    private CompoundTag getBaseData(BlockEntity tile) {
        CompoundTag nbtTags = new CompoundTag();
        if (tile instanceof IRedstoneControl) {
            nbtTags.putInt(NBTConstants.CONTROL_TYPE, ((IRedstoneControl) tile).getControlType().ordinal());
        }
        if (tile instanceof ISideConfiguration) {
            ((ISideConfiguration) tile).getConfig().write(nbtTags);
            ((ISideConfiguration) tile).getEjector().write(nbtTags);
        }
        return nbtTags;
    }

    private void setBaseData(CompoundTag nbtTags, BlockEntity tile) {
        if (tile instanceof IRedstoneControl) {
            ((IRedstoneControl) tile).setControlType(RedstoneControl.byIndexStatic(nbtTags.getInt(NBTConstants.CONTROL_TYPE)));
        }
        if (tile instanceof ISideConfiguration) {
            ((ISideConfiguration) tile).getConfig().read(nbtTags);
            ((ISideConfiguration) tile).getEjector().read(nbtTags);
        }
    }

    private String getNameFromTile(BlockEntity tile, Direction side) {
        Optional<ISpecialConfigData> capability = MekanismUtils.toOptional(CapabilityUtils.getCapability(tile, Capabilities.SPECIAL_CONFIG_DATA_CAPABILITY, side));
        if (capability.isPresent()) {
            return capability.get().getDataType();
        }
        String ret = Integer.toString(tile.hashCode());
        if (tile instanceof TileEntityMekanism) {
            ret = ((TileEntityMekanism) tile).getBlockType().getTranslationKey();
        }
        return ret;
    }

    private void setData(ItemStack stack, CompoundTag data) {
        if (data == null) {
            ItemDataUtils.removeData(stack, NBTConstants.DATA);
        } else {
            ItemDataUtils.setCompound(stack, NBTConstants.DATA, data);
        }
    }

    private CompoundTag getData(ItemStack stack) {
        CompoundTag data = ItemDataUtils.getCompound(stack, NBTConstants.DATA);
        if (data.isEmpty()) {
            return null;
        }
        return ItemDataUtils.getCompound(stack, NBTConstants.DATA);
    }

    public String getDataType(ItemStack stack) {
        CompoundTag data = getData(stack);
        if (data == null) {
            return MekanismLang.NONE.getTranslationKey();
        }
        return data.getString(NBTConstants.DATA_TYPE);
    }
}
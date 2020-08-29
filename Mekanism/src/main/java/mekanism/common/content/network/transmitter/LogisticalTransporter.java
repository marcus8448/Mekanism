package mekanism.common.content.network.transmitter;

import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.content.transporter.PathfinderCache;
import mekanism.common.tier.TransporterTier;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.TransporterUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

public class LogisticalTransporter extends LogisticalTransporterBase {

    private EnumColor color;

    public LogisticalTransporter(IBlockProvider blockProvider, TileEntityTransmitter tile) {
        super(tile, Attribute.getTier(blockProvider.getBlock(), TransporterTier.class));
    }

    public TransporterTier getTier() {
        return tier;
    }

    @Override
    public EnumColor getColor() {
        return color;
    }

    public void setColor(EnumColor c) {
        color = c;
    }

    @Override
    public ActionResult onConfigure(PlayerEntity player, Direction side) {
        TransporterUtils.incrementColor(this);
        PathfinderCache.onChanged(getTransmitterNetwork());
        getTransmitterTile().sendUpdatePacket();
        EnumColor color = getColor();
        player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM,
              MekanismLang.TOGGLE_COLOR.translateColored(EnumColor.GRAY, color != null ? color.getColoredName() : MekanismLang.NONE)), Util.NIL_UUID);
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult onRightClick(PlayerEntity player, Direction side) {
        EnumColor color = getColor();
        player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM,
              MekanismLang.CURRENT_COLOR.translateColored(EnumColor.GRAY, color != null ? color.getColoredName() : MekanismLang.NONE)), Util.NIL_UUID);
        return super.onRightClick(player, side);
    }

    @Override
    protected void readFromNBT(CompoundTag nbtTags) {
        super.readFromNBT(nbtTags);
        NBTUtils.setEnumIfPresent(nbtTags, NBTConstants.COLOR, TransporterUtils::readColor, this::setColor);
    }

    @Override
    public void writeToNBT(CompoundTag nbtTags) {
        super.writeToNBT(nbtTags);
        nbtTags.putInt(NBTConstants.COLOR, TransporterUtils.getColorIndex(getColor()));
    }

    @Nonnull
    @Override
    public CompoundTag getReducedUpdateTag(CompoundTag updateTag) {
        updateTag = super.getReducedUpdateTag(updateTag);
        updateTag.putInt(NBTConstants.COLOR, TransporterUtils.getColorIndex(getColor()));
        return updateTag;
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundTag tag) {
        super.handleUpdateTag(tag);
        NBTUtils.setEnumIfPresent(tag, NBTConstants.COLOR, TransporterUtils::readColor, this::setColor);
    }
}
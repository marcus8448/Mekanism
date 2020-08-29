package mekanism.additions.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.additions.common.AdditionsLang;
import mekanism.additions.common.config.MekanismAdditionsConfig;
import mekanism.api.NBTConstants;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemWalkieTalkie extends Item implements IModeItem {

    public ItemWalkieTalkie(Item.Settings properties) {
        super(properties.maxCount(1));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        tooltip.add(OnOff.of(getOn(stack), true).getTextComponent());
        tooltip.add(AdditionsLang.CHANNEL.translateColored(EnumColor.DARK_AQUA, EnumColor.GRAY, getChannel(stack)));
        if (!MekanismAdditionsConfig.additions.voiceServerEnabled.get()) {
            tooltip.add(AdditionsLang.WALKIE_DISABLED.translateColored(EnumColor.DARK_RED));
        }
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (player.isSneaking()) {
            setOn(itemStack, !getOn(itemStack));
            return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
        }
        return new TypedActionResult<>(ActionResult.PASS, itemStack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
        return !ItemStack.areItemsEqualIgnoreDamage(oldStack, newStack);
    }

    public void setOn(ItemStack itemStack, boolean on) {
        ItemDataUtils.setBoolean(itemStack, NBTConstants.RUNNING, on);
    }

    public boolean getOn(ItemStack itemStack) {
        return ItemDataUtils.getBoolean(itemStack, NBTConstants.RUNNING);
    }

    public void setChannel(ItemStack itemStack, int channel) {
        ItemDataUtils.setInt(itemStack, NBTConstants.CHANNEL, channel);
    }

    public int getChannel(ItemStack itemStack) {
        int channel = ItemDataUtils.getInt(itemStack, NBTConstants.CHANNEL);
        if (channel == 0) {
            setChannel(itemStack, 1);
            channel = 1;
        }
        return channel;
    }

    @Override
    public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {
        if (getOn(stack)) {
            int channel = getChannel(stack);
            int newChannel = Math.floorMod(channel + shift - 1, 8) + 1;
            if (channel != newChannel) {
                setChannel(stack, newChannel);
                if (displayChangeMessage) {
                    player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM,
                          AdditionsLang.CHANNEL_CHANGE.translateColored(EnumColor.GRAY, newChannel)), Util.NIL_UUID);
                }
            }
        }
    }

    @Nonnull
    @Override
    public Text getScrollTextComponent(@Nonnull ItemStack stack) {
        return AdditionsLang.CHANNEL.translateColored(EnumColor.GRAY, getChannel(stack));
    }
}
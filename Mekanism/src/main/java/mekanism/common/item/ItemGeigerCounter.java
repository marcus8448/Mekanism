package mekanism.common.item;

import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.lib.radiation.RadiationManager.RadiationScale;
import mekanism.common.util.UnitDisplayUtils;
import mekanism.common.util.UnitDisplayUtils.RadiationUnit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class ItemGeigerCounter extends Item {

    public ItemGeigerCounter(Settings props) {
        super(props.maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isSneaking() && !world.isClient()) {
            double magnitude = Mekanism.radiationManager.getRadiationLevel(player);
            player.sendSystemMessage(MekanismLang.RADIATION_EXPOSURE.translateColored(EnumColor.GRAY,
                  RadiationScale.getSeverityColor(magnitude), UnitDisplayUtils.getDisplayShort(magnitude, RadiationUnit.SVH, 3)), Util.NIL_UUID);
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }
}

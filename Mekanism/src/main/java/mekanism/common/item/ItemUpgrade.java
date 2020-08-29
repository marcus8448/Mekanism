package mekanism.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.Upgrade;
import mekanism.api.text.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.MekanismLang;
import mekanism.common.item.interfaces.IUpgradeItem;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.tile.interfaces.IUpgradeTile;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemUpgrade extends Item implements IUpgradeItem {

    private final Upgrade upgrade;

    public ItemUpgrade(Upgrade type, Settings properties) {
        super(properties.maxCount(type.getMax()).rarity(Rarity.UNCOMMON));
        upgrade = type;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        if (MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.detailsKey)) {
            tooltip.add(getUpgradeType(stack).getDescription());
        } else {
            tooltip.add(MekanismLang.HOLD_FOR_DETAILS.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getBoundKeyLocalizedText()));
        }
    }

    @Override
    public Upgrade getUpgradeType(ItemStack stack) {
        return upgrade;
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && player.isSneaking()) {
            World world = context.getWorld();
            BlockEntity tile = MekanismUtils.getTileEntity(world, context.getBlockPos());
            ItemStack stack = player.getStackInHand(context.getHand());
            Upgrade type = getUpgradeType(stack);
            if (tile instanceof IUpgradeTile) {
                IUpgradeTile upgradeTile = (IUpgradeTile) tile;
                if (!upgradeTile.supportsUpgrades()) {
                    //Can't support upgrades so continue on
                    return ActionResult.PASS;
                }
                TileComponentUpgrade component = upgradeTile.getComponent();
                if (component.supports(type)) {
                    if (!world.isClient && component.getUpgrades(type) < type.getMax()) {
                        component.addUpgrade(type);
                        stack.decrement(1);
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
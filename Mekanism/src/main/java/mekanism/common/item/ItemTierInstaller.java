package mekanism.common.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.text.TextComponentUtil;
import mekanism.api.tier.BaseTier;
import mekanism.common.Mekanism;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeUpgradeable;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.ITierUpgradable;
import mekanism.common.tile.interfaces.ITileDirectional;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTierInstaller extends Item {

    @Nullable
    private final BaseTier fromTier;
    @Nonnull
    private final BaseTier toTier;

    public ItemTierInstaller(@Nullable BaseTier fromTier, @Nonnull BaseTier toTier, Settings properties) {
        super(properties);
        this.fromTier = fromTier;
        this.toTier = toTier;
    }

    @Nullable
    public BaseTier getFromTier() {
        return fromTier;
    }

    @Nonnull
    public BaseTier getToTier() {
        return toTier;
    }

    @Nonnull
    @Override
    public Text getName(@Nonnull ItemStack stack) {
        return TextComponentUtil.build(toTier.getTextColor(), super.getName(stack));
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        if (world.isClient || player == null) {
            return ActionResult.PASS;
        }
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (Attribute.has(block, AttributeUpgradeable.class)) {
            AttributeUpgradeable upgradeableBlock = Attribute.get(block, AttributeUpgradeable.class);
            BaseTier baseTier = Attribute.getBaseTier(block);
            if (baseTier == fromTier && baseTier != toTier) {
                BlockState upgradeState = upgradeableBlock.upgradeResult(state, toTier);
                if (state == upgradeState) {
                    return ActionResult.PASS;
                }
                BlockEntity tile = MekanismUtils.getTileEntity(world, pos);
                if (tile instanceof ITierUpgradable) {
                    if (tile instanceof TileEntityMekanism && !((TileEntityMekanism) tile).playersUsing.isEmpty()) {
                        return ActionResult.FAIL;
                    }
                    IUpgradeData upgradeData = ((ITierUpgradable) tile).getUpgradeData();
                    if (upgradeData == null) {
                        if (((ITierUpgradable) tile).canBeUpgraded()) {
                            Mekanism.logger.warn("Got no upgrade data for block {} at position: {} in {} but it said it would be able to provide some.", block, pos, world);
                            return ActionResult.FAIL;
                        }
                    } else {
                        world.setBlockState(pos, upgradeState);
                        //TODO: Make it so it doesn't have to be a TileEntityMekanism?
                        TileEntityMekanism upgradedTile = MekanismUtils.getTileEntity(TileEntityMekanism.class, world, pos);
                        if (upgradedTile == null) {
                            Mekanism.logger.warn("Error upgrading block at position: {} in {}.", pos, world);
                            return ActionResult.FAIL;
                        } else {
                            if (tile instanceof ITileDirectional && ((ITileDirectional) tile).isDirectional()) {
                                upgradedTile.setFacing(((ITileDirectional) tile).getDirection());
                            }
                            upgradedTile.parseUpgradeData(upgradeData);
                            upgradedTile.sendUpdatePacket();
                            upgradedTile.markDirty();
                            if (!player.isCreative()) {
                                player.getStackInHand(context.getHand()).decrement(1);
                            }
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
package mekanism.common.item;

import javax.annotation.Nonnull;
import mekanism.api.IAlloyInteraction;
import mekanism.api.tier.AlloyTier;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class ItemAlloy extends Item {

    private final AlloyTier tier;

    public ItemAlloy(AlloyTier tier, Settings properties) {
        super(properties);
        this.tier = tier;
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && MekanismConfig.general.transmitterAlloyUpgrade.get()) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            BlockEntity tile = MekanismUtils.getTileEntity(world, pos);
            LazyOptional<IAlloyInteraction> capability = CapabilityUtils.getCapability(tile, Capabilities.ALLOY_INTERACTION_CAPABILITY, context.getSide());
            if (capability.isPresent()) {
                if (!world.isClient) {
                    Hand hand = context.getHand();
                    MekanismUtils.toOptional(capability).get().onAlloyInteraction(player, hand, player.getStackInHand(hand), tier);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public AlloyTier getTier() {
        return tier;
    }
}
package mekanism.common.item.block;

import javax.annotation.Nonnull;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Thiakil on 19/11/2017.
 */
//TODO: Re-evaluate this class
public abstract class ItemBlockMultipartAble<BLOCK extends Block> extends ItemBlockMekanism<BLOCK> {

    public ItemBlockMultipartAble(BLOCK block) {
        super(block, ItemDeferredRegister.getMekBaseProperties());
    }

    /**
     * Reimplementation of onItemUse that will divert to MCMultipart placement functions if applicable
     */
    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResult.PASS;
        }
        ItemStack stack = player.getStackInHand(context.getHand());
        if (stack.isEmpty()) {
            return ActionResult.FAIL;//WTF
        }
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (!MekanismUtils.isValidReplaceableBlock(world, pos)) {
            pos = pos.offset(context.getSide());
        }
        if (player.canPlaceOn(pos, context.getSide(), stack)) {
            ItemPlacementContext blockItemUseContext = new ItemPlacementContext(context);
            BlockState state = getPlacementState(blockItemUseContext);
            if (state == null) {
                return ActionResult.FAIL;
            }
            if (place(blockItemUseContext, state)) {
                state = world.getBlockState(pos);
                BlockSoundGroup soundtype = state.getSoundType(world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1) / 2F, soundtype.getPitch() * 0.8F);
                stack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public boolean place(@Nonnull ItemPlacementContext context, @Nonnull BlockState state) {
        if (MekanismUtils.isValidReplaceableBlock(context.getWorld(), context.getBlockPos())) {
            return super.place(context, state);
        }
        return false;
    }
}
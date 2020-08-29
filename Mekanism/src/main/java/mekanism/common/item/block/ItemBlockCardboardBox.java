package mekanism.common.item.block;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.api.text.EnumColor;
import mekanism.common.CommonPlayerTracker;
import mekanism.common.MekanismLang;
import mekanism.common.block.BlockCardboardBox;
import mekanism.common.block.BlockCardboardBox.BlockData;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tile.TileEntityCardboardBox;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.common.util.text.BooleanStateDisplay.YesNo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemBlockCardboardBox extends ItemBlockMekanism<BlockCardboardBox> {

    public ItemBlockCardboardBox(BlockCardboardBox block) {
        super(block, ItemDeferredRegister.getMekBaseProperties().maxCount(16));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        tooltip.add(MekanismLang.BLOCK_DATA.translateColored(EnumColor.INDIGO, YesNo.of(getBlockData(stack) != null)));
        BlockData data = getBlockData(stack);
        if (data != null) {
            try {
                tooltip.add(MekanismLang.BLOCK.translate(data.blockState.getBlock()));
                if (data.tileTag != null) {
                    tooltip.add(MekanismLang.TILE.translate(data.tileTag.getString(NBTConstants.ID)));
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Nonnull
    @Override
    public ActionResult onItemUseFirst(ItemStack stack, ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (stack.isEmpty() || player == null) {
            return ActionResult.PASS;
        }
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (getBlockData(stack) == null && !player.isSneaking() && !world.isAir(pos)) {
            BlockState state = world.getBlockState(pos);
            if (state.getHardness(world, pos) != -1) {
                if (state.isIn(MekanismTags.Blocks.CARDBOARD_BLACKLIST) || MekanismConfig.general.cardboardModBlacklist.get().contains(state.getBlock().getRegistryName().getNamespace())) {
                    return ActionResult.FAIL;
                }
                BlockEntity tile = MekanismUtils.getTileEntity(world, pos);
                if (tile != null && !SecurityUtils.canAccess(player, tile)) {
                    //If the player cannot access the tile don't allow them to pick it up with a cardboard box
                    return ActionResult.FAIL;
                }
                if (!world.isClient) {
                    BlockData data = new BlockData(state);
                    if (tile != null) {
                        //Note: We check security access above
                        CompoundTag tag = new CompoundTag();
                        tile.toTag(tag);
                        data.tileTag = tag;
                    }
                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                    CommonPlayerTracker.monitoringCardboardBox = true;
                    // First, set the block to air to give the underlying block a chance to process
                    // any updates (esp. if it's a tile entity backed block). Ideally, we could avoid
                    // double updates, but if the block we are wrapping has multiple stacked blocks,
                    // we need to make sure it has a chance to update.
                    world.removeBlock(pos, false);
                    world.setBlockState(pos, getBlock().getDefaultState().with(BlockStateHelper.storageProperty, true));
                    CommonPlayerTracker.monitoringCardboardBox = false;
                    TileEntityCardboardBox box = MekanismUtils.getTileEntity(TileEntityCardboardBox.class, world, pos);
                    if (box != null) {
                        box.storedData = data;
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean place(@Nonnull ItemPlacementContext context, @Nonnull BlockState state) {
        World world = context.getWorld();
        if (world.isClient) {
            return true;
        }
        if (super.place(context, state)) {
            TileEntityCardboardBox tile = MekanismUtils.getTileEntity(TileEntityCardboardBox.class, world, context.getBlockPos());
            if (tile != null) {
                tile.storedData = getBlockData(context.getStack());
            }
            return true;
        }
        return false;
    }

    public void setBlockData(ItemStack stack, BlockData data) {
        ItemDataUtils.setCompound(stack, NBTConstants.DATA, data.write(new CompoundTag()));
    }

    public BlockData getBlockData(ItemStack stack) {
        if (ItemDataUtils.hasData(stack, NBTConstants.DATA, NBT.TAG_COMPOUND)) {
            return BlockData.read(ItemDataUtils.getCompound(stack, NBTConstants.DATA));
        }
        return null;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        BlockData blockData = getBlockData(stack);
        if (blockData != null) {
            return 1;
        }
        return super.getItemStackLimit(stack);
    }
}
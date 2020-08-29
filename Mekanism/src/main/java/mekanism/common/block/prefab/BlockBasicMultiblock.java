package mekanism.common.block.prefab;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction.Location;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockBasicMultiblock<TILE extends TileEntityMekanism> extends BlockTile<TILE, BlockTypeTile<TILE>> {

    public BlockBasicMultiblock(BlockTypeTile<TILE> type) {
        this(type, Block.Properties.of(Material.METAL).strength(5F, 10F).requiresTool());
    }

    public BlockBasicMultiblock(BlockTypeTile<TILE> type, Block.Properties properties) {
        super(type, properties);
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, Location type, @Nullable EntityType<?> entityType) {
        TileEntityMultiblock<?> tile = MekanismUtils.getTileEntity(TileEntityMultiblock.class, world, pos);
        if (tile != null && tile.getMultiblock().isFormed()) {
            return false;
        }
        return super.canCreatureSpawn(state, world, pos, type, entityType);
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        TileEntityMultiblock<?> tile = MekanismUtils.getTileEntity(TileEntityMultiblock.class, world, pos);
        if (tile == null) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() instanceof BlockItem && new ItemPlacementContext(player, hand, stack, hit).canPlace()) {
                if (!tile.hasGui() || !tile.getMultiblock().isFormed()) {
                    //If the block doesn't have a gui (frames of things like the evaporation plant), or the multiblock is not formed then pass
                    return ActionResult.PASS;
                }
            }
            return ActionResult.SUCCESS;
        }
        if (tile.tryWrench(state, player, hand, hit) != WrenchResult.PASS) {
            return ActionResult.SUCCESS;
        }
        return tile.onActivate(player, hand, player.getStackInHand(hand));
    }
}
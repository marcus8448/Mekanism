package mekanism.common.block.basic;

import java.util.UUID;
import javax.annotation.Nonnull;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registries.MekanismBlockTypes;
import mekanism.common.tile.TileEntitySecurityDesk;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockSecurityDesk extends BlockTileModel<TileEntitySecurityDesk, BlockTypeTile<TileEntitySecurityDesk>> {

    public BlockSecurityDesk() {
        super(MekanismBlockTypes.SECURITY_DESK);
    }

    @Override
    public void setTileData(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, TileEntityMekanism tile) {
        if (tile instanceof TileEntitySecurityDesk) {
            ((TileEntitySecurityDesk) tile).ownerUUID = placer.getUuid();
        }
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        TileEntitySecurityDesk tile = MekanismUtils.getTileEntity(TileEntitySecurityDesk.class, world, pos);
        if (tile != null) {
            if (!player.isSneaking()) {
                if (!world.isClient) {
                    UUID ownerUUID = tile.ownerUUID;
                    if (ownerUUID == null || player.getUuid().equals(ownerUUID)) {
                        NetworkHooks.openGui((ServerPlayerEntity) player, Attribute.get(this, AttributeGui.class).getProvider(tile), pos);
                    } else {
                        SecurityUtils.displayNoAccess(player);
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
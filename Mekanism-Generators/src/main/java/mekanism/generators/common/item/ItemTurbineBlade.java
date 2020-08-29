package mekanism.generators.common.item;

import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class ItemTurbineBlade extends Item {

    public ItemTurbineBlade(Settings properties) {
        super(properties);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, WorldView world, BlockPos pos, PlayerEntity player) {
        return MekanismUtils.getTileEntity(TileEntityTurbineRotor.class, world, pos) != null;
    }
}
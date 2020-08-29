package mekanism.common.item.block.machine;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.math.FloatingLong;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeEnergy;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemBlockLaser extends ItemBlockTooltip<BlockTileModel<?, ?>> {

    public ItemBlockLaser(BlockTileModel<?, ?> block) {
        super(block, true, ItemDeferredRegister.getMekBaseProperties().maxCount(1));
    }

    @Override
    public void addDetails(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, boolean advanced) {
        StorageUtils.addStoredEnergy(stack, tooltip, true);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        FloatingLong maxEnergy = MekanismUtils.getMaxEnergy(stack, Attribute.get(getBlock(), AttributeEnergy.class).getStorage());
        return new ItemCapabilityWrapper(stack, RateLimitEnergyHandler.create(() -> maxEnergy, BasicEnergyContainer.manualOnly, BasicEnergyContainer.alwaysTrue));
    }
}
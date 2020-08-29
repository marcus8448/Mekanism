package mekanism.common.item.gear;

import java.util.List;
import java.util.function.LongSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.inventory.AutomationType;
import mekanism.api.providers.IGasProvider;
import mekanism.client.render.armor.CustomArmor;
import mekanism.client.render.armor.ScubaTankArmor;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.chemical.item.RateLimitGasHandler;
import mekanism.common.item.interfaces.IGasItem;
import mekanism.common.item.interfaces.ISpecialGear;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.StorageUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class ItemGasArmor extends ArmorItem implements ISpecialGear, IGasItem {

    protected ItemGasArmor(ArmorMaterial material, EquipmentSlot slot, Settings properties) {
        super(material, slot, properties.rarity(Rarity.RARE).setNoRepair().maxCount(1));
    }

    protected abstract LongSupplier getMaxGas();

    protected abstract LongSupplier getFillRate();

    protected abstract IGasProvider getGasType();

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        StorageUtils.addStoredGas(stack, tooltip, true, false);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return StorageUtils.getDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return ChemicalUtil.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "mekanism:render/null_armor.png";
    }

    @Nonnull
    @Override
    @Environment(EnvType.CLIENT)
    public CustomArmor getGearModel() {
        return ScubaTankArmor.SCUBA_TANK;
    }

    @Override
    public void appendStacks(@Nonnull ItemGroup group, @Nonnull DefaultedList<ItemStack> items) {
        super.appendStacks(group, items);
        if (isIn(group)) {
            items.add(ChemicalUtil.getFilledVariant(new ItemStack(this), getMaxGas().getAsLong(), getGasType()));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new ItemCapabilityWrapper(stack, RateLimitGasHandler.create(getFillRate(), getMaxGas(),
              (item, automationType) -> automationType != AutomationType.EXTERNAL, ChemicalTankBuilder.GAS.alwaysTrueBi, gas -> gas == getGasType().getChemical()));
    }
}
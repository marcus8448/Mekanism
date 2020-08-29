package mekanism.common.item.gear;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.client.render.armor.CustomArmor;
import mekanism.client.render.armor.ScubaMaskArmor;
import mekanism.client.render.item.ISTERProvider;
import mekanism.common.Mekanism;
import mekanism.common.item.interfaces.ISpecialGear;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemScubaMask extends ArmorItem implements ISpecialGear {

    private static final ScubaMaskMaterial SCUBA_MASK_MATERIAL = new ScubaMaskMaterial();

    public ItemScubaMask(Settings properties) {
        super(SCUBA_MASK_MATERIAL, EquipmentSlot.HEAD, properties.rarity(Rarity.RARE).setNoRepair().setISTER(ISTERProvider::scubaMask));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "mekanism:render/null_armor.png";
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (stack.getTag() == null) {
            stack.setTag(new CompoundTag());
        }
        stack.getTag().putInt("HideFlags", 2);
        return super.initCapabilities(stack, nbt);
    }

    @Nonnull
    @Override
    @Environment(EnvType.CLIENT)
    public CustomArmor getGearModel() {
        return ScubaMaskArmor.SCUBA_MASK;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return type.getEnchantability() > 0;
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    protected static class ScubaMaskMaterial extends BaseSpecialArmorMaterial {

        @Override
        public String getName() {
            return Mekanism.MODID + ":scuba_mask";
        }
    }
}
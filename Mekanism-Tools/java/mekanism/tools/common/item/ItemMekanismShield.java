package mekanism.tools.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.material.BaseMekanismMaterial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemMekanismShield extends ShieldItem implements IHasRepairType {

    private final BaseMekanismMaterial material;

    public ItemMekanismShield(BaseMekanismMaterial material, Item.Settings properties) {
        super(properties.maxDamage(material.getShieldDurability()));
        this.material = material;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        super.appendTooltip(stack, world, tooltip, flag);//Add the banner type description
        tooltip.add(ToolsLang.HP.translate(stack.getMaxDamage() - stack.getDamage()));
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return material.getRepairIngredient();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return material.getShieldDurability();
    }

    @Override
    public boolean isDamageable() {
        return material.getShieldDurability() > 0;
    }

    @Override
    public boolean canRepair(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return getRepairMaterial().test(repair);
    }

    @Override
    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
        //Has to override this because default impl in IForgeItem checks for exact equality with the shield item instead of instanceof
        return true;
    }

    @Override
    public int getEnchantability() {
        return material.getEnchantability();
    }
}
package mekanism.common.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.client.render.armor.CustomArmor;
import mekanism.client.render.armor.JetpackArmor;
import mekanism.client.render.item.ISTERProvider;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemArmoredJetpack extends ItemJetpack implements IAttributeRefresher {

    private static final ArmoredJetpackMaterial ARMORED_JETPACK_MATERIAL = new ArmoredJetpackMaterial();

    private final AttributeCache attributeCache;

    public ItemArmoredJetpack(Settings properties) {
        super(ARMORED_JETPACK_MATERIAL, properties.setISTER(ISTERProvider::armoredJetpack));
        this.attributeCache = new AttributeCache(this, MekanismConfig.gear.armoredJetpackArmor, MekanismConfig.gear.armoredJetpackToughness);
    }

    @Override
    public int getProtection() {
        return getMaterial().getProtectionAmount(getSlotType());
    }

    @Override
    public float method_26353() {
        return getMaterial().getToughness();
    }

    @Nonnull
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot, @Nonnull ItemStack stack) {
        return slot == getSlotType() ? attributeCache.getAttributes() : ImmutableMultimap.of();
    }

    @Override
    public void addToBuilder(ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder) {
        UUID modifier = MODIFIERS[getSlotType().getEntitySlotId()];
        builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(modifier, "Armor modifier", getProtection(), Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(modifier, "Armor toughness", method_26353(), Operation.ADDITION));
    }

    @Nonnull
    @Override
    @Environment(EnvType.CLIENT)
    public CustomArmor getGearModel() {
        return JetpackArmor.ARMORED_JETPACK;
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class ArmoredJetpackMaterial extends JetpackMaterial {

        @Override
        public int getProtectionAmount(EquipmentSlot slotType) {
            return slotType == EquipmentSlot.CHEST ? MekanismConfig.gear.armoredJetpackArmor.get() : 0;
        }

        @Override
        public String getName() {
            return Mekanism.MODID + ":jetpack_armored";
        }

        @Override
        public float getToughness() {
            return MekanismConfig.gear.armoredJetpackToughness.get();
        }
    }
}
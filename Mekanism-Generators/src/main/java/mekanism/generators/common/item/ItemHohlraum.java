package mekanism.generators.common.item;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.chemical.item.RateLimitGasHandler;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import mekanism.generators.common.GeneratorTags;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.registries.GeneratorsGases;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemHohlraum extends Item {

    public ItemHohlraum(Settings properties) {
        super(properties.maxCount(1));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        if (Capabilities.GAS_HANDLER_CAPABILITY != null) {
            //Ensure the capability is not null, as the first call to addInformation happens before capability injection
            Optional<IGasHandler> capability = MekanismUtils.toOptional(stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY));
            if (capability.isPresent()) {
                IGasHandler gasHandlerItem = capability.get();
                if (gasHandlerItem.getTanks() > 0) {
                    //Validate something didn't go terribly wrong and we actually do have the tank we expect to have
                    GasStack storedGas = gasHandlerItem.getChemicalInTank(0);
                    if (!storedGas.isEmpty()) {
                        tooltip.add(MekanismLang.STORED.translate(storedGas, storedGas.getAmount()));
                        if (storedGas.getAmount() == gasHandlerItem.getTankCapacity(0)) {
                            tooltip.add(GeneratorsLang.READY_FOR_REACTION.translateColored(EnumColor.DARK_GREEN));
                        } else {
                            tooltip.add(GeneratorsLang.INSUFFICIENT_FUEL.translateColored(EnumColor.DARK_RED));
                        }
                        return;
                    }
                }
            }
        }
        tooltip.add(MekanismLang.NO_GAS.translate());
        tooltip.add(GeneratorsLang.INSUFFICIENT_FUEL.translateColored(EnumColor.DARK_RED));
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
    public void appendStacks(@Nonnull ItemGroup group, @Nonnull DefaultedList<ItemStack> items) {
        super.appendStacks(group, items);
        if (isIn(group)) {
            items.add(ChemicalUtil.getFilledVariant(new ItemStack(this), MekanismGeneratorsConfig.generators.hohlraumMaxGas.get(), GeneratorsGases.FUSION_FUEL));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new ItemCapabilityWrapper(stack, RateLimitGasHandler.create(MekanismGeneratorsConfig.generators.hohlraumFillRate,
              MekanismGeneratorsConfig.generators.hohlraumMaxGas, ChemicalTankBuilder.GAS.notExternal, ChemicalTankBuilder.GAS.alwaysTrueBi,
              gas -> gas.isIn(GeneratorTags.Gases.FUSION_FUEL)));
    }
}
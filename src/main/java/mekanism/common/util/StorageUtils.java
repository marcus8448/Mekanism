package mekanism.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Action;
import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.BasicGasTank;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.BasicInfusionTank;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.BasicPigmentTank;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.BasicSlurryTank;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.common.util.text.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class StorageUtils {

    public static void addStoredEnergy(@Nonnull ItemStack stack, @Nonnull List<ITextComponent> tooltip, boolean showMissingCap) {
        addStoredEnergy(stack, tooltip, showMissingCap, MekanismLang.STORED_ENERGY);
    }

    public static void addStoredEnergy(@Nonnull ItemStack stack, @Nonnull List<ITextComponent> tooltip, boolean showMissingCap, ILangEntry langEntry) {
        if (Capabilities.STRICT_ENERGY_CAPABILITY != null) {
            //Ensure the capability is not null, as the first call to addInformation happens before capability injection
            Optional<IStrictEnergyHandler> capability = MekanismUtils.toOptional(stack.getCapability(Capabilities.STRICT_ENERGY_CAPABILITY));
            if (capability.isPresent()) {
                IStrictEnergyHandler energyHandlerItem = capability.get();
                int energyContainerCount = energyHandlerItem.getEnergyContainerCount();
                for (int container = 0; container < energyContainerCount; container++) {
                    tooltip.add(langEntry.translateColored(EnumColor.BRIGHT_GREEN, EnumColor.GRAY,
                          EnergyDisplay.of(energyHandlerItem.getEnergy(container), energyHandlerItem.getMaxEnergy(container))));
                }
            } else if (showMissingCap) {
                tooltip.add(langEntry.translateColored(EnumColor.BRIGHT_GREEN, EnumColor.GRAY, EnergyDisplay.ZERO));
            }
        }
    }

    public static void addStoredGas(@Nonnull ItemStack stack, @Nonnull List<ITextComponent> tooltip, boolean showMissingCap, boolean showAttributes) {
        addStoredGas(stack, tooltip, showMissingCap, showAttributes, MekanismLang.NO_GAS, stored -> {
            if (stored.isEmpty()) {
                return MekanismLang.NO_GAS.translateColored(EnumColor.GRAY);
            }
            return MekanismLang.STORED.translateColored(EnumColor.ORANGE, EnumColor.ORANGE, stored, EnumColor.GRAY, stored.getAmount());
        });
    }

    public static void addStoredGas(@Nonnull ItemStack stack, @Nonnull List<ITextComponent> tooltip, boolean showMissingCap, boolean showAttributes,
          ILangEntry emptyLangEntry, Function<GasStack, ITextComponent> storedFunction) {
        if (Capabilities.GAS_HANDLER_CAPABILITY != null) {
            //Ensure the capability is not null, as the first call to addInformation happens before capability injection
            Optional<IGasHandler> capability = MekanismUtils.toOptional(stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY));
            if (capability.isPresent()) {
                IGasHandler gasHandlerItem = capability.get();
                int tanks = gasHandlerItem.getTanks();
                for (int tank = 0; tank < tanks; tank++) {
                    GasStack gasInTank = gasHandlerItem.getChemicalInTank(tank);
                    tooltip.add(storedFunction.apply(gasInTank));
                    tooltip.addAll(GasUtils.getAttributeTooltips(gasInTank.getType()));
                }
            } else if (showMissingCap) {
                tooltip.add(emptyLangEntry.translate());
            }
        }
    }

    /**
     * Gets the fluid if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a fluid handler
     * attached to our item but it may have stored data in its tank from when it was a block
     */
    @Nonnull
    public static FluidStack getStoredFluidFromNBT(ItemStack stack) {
        BasicFluidTank tank = BasicFluidTank.create(Integer.MAX_VALUE, null);
        DataHandlerUtils.readContainers(Collections.singletonList(tank), ItemDataUtils.getList(stack, NBTConstants.FLUID_TANKS));
        return tank.getFluid();
    }

    /**
     * Gets the gas if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a gas handler
     * attached to our item but it may have stored data in its tank from when it was a block
     */
    @Nonnull
    public static GasStack getStoredGasFromNBT(ItemStack stack) {
        return getStoredChemicalFromNBT(stack, BasicGasTank.create(Long.MAX_VALUE, null), NBTConstants.GAS_TANKS);
    }

    /**
     * Gets the infuse type if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a infusion
     * handler attached to our item but it may have stored data in its tank from when it was a block
     */
    @Nonnull
    public static InfusionStack getStoredInfusionFromNBT(ItemStack stack) {
        return getStoredChemicalFromNBT(stack, BasicInfusionTank.create(Long.MAX_VALUE, null), NBTConstants.INFUSION_TANKS);
    }

    /**
     * Gets the pigment if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a pigment handler
     * attached to our item but it may have stored data in its tank from when it was a block
     */
    @Nonnull
    public static PigmentStack getStoredPigmentFromNBT(ItemStack stack) {
        return getStoredChemicalFromNBT(stack, BasicPigmentTank.create(Long.MAX_VALUE, null), NBTConstants.PIGMENT_TANKS);
    }

    /**
     * Gets the slurry if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a slurry handler
     * attached to our item but it may have stored data in its tank from when it was a block
     */
    @Nonnull
    public static SlurryStack getStoredSlurryFromNBT(ItemStack stack) {
        return getStoredChemicalFromNBT(stack, BasicSlurryTank.create(Long.MAX_VALUE, null), NBTConstants.SLURRY_TANKS);
    }

    @Nonnull
    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>>
    STACK getStoredChemicalFromNBT(ItemStack stack, TANK tank, String tag) {
        DataHandlerUtils.readContainers(Collections.singletonList(tank), ItemDataUtils.getList(stack, tag));
        return tank.getStack();
    }

    /**
     * Gets the energy if one is stored from an item's container going off the basis there is a single energy container. This is for cases when we may not actually have
     * an energy handler attached to our item but it may have stored data in its container from when it was a block
     */
    public static FloatingLong getStoredEnergyFromNBT(ItemStack stack) {
        BasicEnergyContainer container = BasicEnergyContainer.create(FloatingLong.MAX_VALUE, null);
        DataHandlerUtils.readContainers(Collections.singletonList(container), ItemDataUtils.getList(stack, NBTConstants.ENERGY_CONTAINERS));
        return container.getEnergy();
    }

    public static ItemStack getFilledEnergyVariant(ItemStack toFill, FloatingLong capacity) {
        //Manually handle this as capabilities are not necessarily loaded yet (at least not on the first call to this, which is made via fillItemGroup)
        BasicEnergyContainer container = BasicEnergyContainer.create(capacity, null);
        container.setEnergy(capacity);
        ItemDataUtils.setList(toFill, NBTConstants.ENERGY_CONTAINERS, DataHandlerUtils.writeContainers(Collections.singletonList(container)));
        //The item is now filled return it for convenience
        return toFill;
    }

    @Nullable
    public static IEnergyContainer getEnergyContainer(ItemStack stack, int container) {
        Optional<IStrictEnergyHandler> energyCapability = MekanismUtils.toOptional(stack.getCapability(Capabilities.STRICT_ENERGY_CAPABILITY));
        if (energyCapability.isPresent()) {
            IStrictEnergyHandler energyHandlerItem = energyCapability.get();
            if (energyHandlerItem instanceof IMekanismStrictEnergyHandler) {
                return ((IMekanismStrictEnergyHandler) energyHandlerItem).getEnergyContainer(container, null);
            }
        }
        return null;
    }

    public static ITextComponent getEnergyPercent(ItemStack stack) {
        IEnergyContainer container = getEnergyContainer(stack, 0);
        double ratio = 0.0D;
        if (container != null) {
            ratio = container.getEnergy().divideToLevel(container.getMaxEnergy());
        }
        EnumColor color;
        if (ratio < 0.01F) {
            color = EnumColor.DARK_RED;
        } else if (ratio < 0.1F) {
            color = EnumColor.RED;
        } else if (ratio < 0.25F) {
            color = EnumColor.ORANGE;
        } else if (ratio < 0.5F) {
            color = EnumColor.YELLOW;
        } else {
            color = EnumColor.BRIGHT_GREEN;
        }
        return new StringTextComponent(TextUtils.getPercent(ratio)).applyTextStyle(color.textFormatting);
    }

    public static double getDurabilityForDisplay(ItemStack stack) {
        //Note we ensure the capabilities are not null, as the first call to getDurabilityForDisplay happens before capability injection
        if (Capabilities.GAS_HANDLER_CAPABILITY == null || Capabilities.INFUSION_HANDLER_CAPABILITY == null || Capabilities.PIGMENT_HANDLER_CAPABILITY == null ||
            Capabilities.SLURRY_HANDLER_CAPABILITY == null || CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null || Capabilities.STRICT_ENERGY_CAPABILITY == null) {
            return 1;
        }
        double bestRatio = 0;
        bestRatio = calculateRatio(stack, bestRatio, Capabilities.GAS_HANDLER_CAPABILITY);
        bestRatio = calculateRatio(stack, bestRatio, Capabilities.INFUSION_HANDLER_CAPABILITY);
        bestRatio = calculateRatio(stack, bestRatio, Capabilities.PIGMENT_HANDLER_CAPABILITY);
        bestRatio = calculateRatio(stack, bestRatio, Capabilities.SLURRY_HANDLER_CAPABILITY);
        Optional<IFluidHandlerItem> fluidCapability = MekanismUtils.toOptional(stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY));
        if (fluidCapability.isPresent()) {
            IFluidHandlerItem fluidHandlerItem = fluidCapability.get();
            int tanks = fluidHandlerItem.getTanks();
            for (int tank = 0; tank < tanks; tank++) {
                bestRatio = Math.max(bestRatio, getRatio(fluidHandlerItem.getFluidInTank(tank).getAmount(), fluidHandlerItem.getTankCapacity(tank)));
            }
        }
        Optional<IStrictEnergyHandler> energyCapability = MekanismUtils.toOptional(stack.getCapability(Capabilities.STRICT_ENERGY_CAPABILITY));
        if (energyCapability.isPresent()) {
            IStrictEnergyHandler energyHandlerItem = energyCapability.get();
            int containers = energyHandlerItem.getEnergyContainerCount();
            for (int container = 0; container < containers; container++) {
                bestRatio = Math.max(bestRatio, energyHandlerItem.getEnergy(container).divideToLevel(energyHandlerItem.getMaxEnergy(container)));
            }
        }
        return 1 - bestRatio;
    }

    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, HANDLER extends IChemicalHandler<CHEMICAL, STACK>>
    double calculateRatio(ItemStack stack, double bestRatio, Capability<HANDLER> capability) {
        Optional<HANDLER> cap = MekanismUtils.toOptional(stack.getCapability(capability));
        if (cap.isPresent()) {
            HANDLER handler = cap.get();
            for (int tank = 0, tanks = handler.getTanks(); tank < tanks; tank++) {
                bestRatio = Math.max(bestRatio, getRatio(handler.getChemicalInTank(tank).getAmount(), handler.getTankCapacity(tank)));
            }
        }
        return bestRatio;
    }

    private static double getRatio(long amount, long capacity) {
        return capacity == 0 ? 1 : amount / (double) capacity;
    }

    public static void mergeTanks(IExtendedFluidTank tank, IExtendedFluidTank mergeTank) {
        if (tank.isEmpty()) {
            tank.setStack(mergeTank.getFluid());
        } else if (!mergeTank.isEmpty() && tank.isFluidEqual(mergeTank.getFluid())) {
            tank.growStack(mergeTank.getFluidAmount(), Action.EXECUTE);
        }
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void mergeTanks(IChemicalTank<CHEMICAL, STACK> tank,
          IChemicalTank<CHEMICAL, STACK> mergeTank) {
        if (tank.isEmpty()) {
            tank.setStack(mergeTank.getStack());
        } else if (!mergeTank.isEmpty() && tank.isTypeEqual(mergeTank.getStack())) {
            tank.growStack(mergeTank.getStored(), Action.EXECUTE);
        }
    }

    public static void mergeContainers(IEnergyContainer container, IEnergyContainer mergeContainer) {
        container.setEnergy(container.getEnergy().add(mergeContainer.getEnergy()));
    }

    public static void mergeContainers(IHeatCapacitor capacitor, IHeatCapacitor mergeCapacitor) {
        capacitor.setHeat(capacitor.getHeat() + mergeCapacitor.getHeat());
        if (capacitor instanceof BasicHeatCapacitor) {
            ((BasicHeatCapacitor) capacitor).setHeatCapacity(capacitor.getHeatCapacity() + mergeCapacitor.getHeatCapacity(), false);
        }
    }
}
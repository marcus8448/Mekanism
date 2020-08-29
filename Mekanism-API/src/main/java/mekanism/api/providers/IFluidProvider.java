package mekanism.api.providers;

import javax.annotation.Nonnull;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidProvider extends IBaseProvider {

    @Nonnull
    Fluid getFluid();

    //Note: Uses FluidStack in case we want to check NBT or something
    default boolean fluidMatches(FluidStack other) {
        return getFluid() == other.getFluid();
    }

    @Nonnull
    default FluidStack getFluidStack(int size) {
        return new FluidStack(getFluid(), size);
    }

    @Override
    default Identifier getRegistryName() {
        return getFluid().getRegistryName();
    }

    @Override
    default Text getTextComponent() {
        return getFluid().getAttributes().getDisplayName(getFluidStack(1));
    }

    @Override
    default String getTranslationKey() {
        return getFluid().getAttributes().getTranslationKey();
    }
}
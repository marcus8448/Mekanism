package mekanism.common.integration.lookingat;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.math.FloatingLong;
import net.minecraft.text.Text;
import net.minecraftforge.fluids.FluidStack;

public interface LookingAtHelper {

    void addText(Text text);

    void addEnergyElement(FloatingLong energy, FloatingLong maxEnergy);

    void addFluidElement(FluidStack stored, int capacity);

    void addChemicalElement(ChemicalStack<?> stored, long capacity);
}
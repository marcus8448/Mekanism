package mekanism.api.recipes;

import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.chemical.ChemicalToChemicalRecipe;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import net.minecraft.util.Identifier;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class GasToGasRecipe extends ChemicalToChemicalRecipe<Gas, GasStack, GasStackIngredient> {

    public GasToGasRecipe(Identifier id, GasStackIngredient input, GasStack output) {
        super(id, input, output);
    }

    @Override
    public GasStack getOutput(GasStack input) {
        return output.copy();
    }
}
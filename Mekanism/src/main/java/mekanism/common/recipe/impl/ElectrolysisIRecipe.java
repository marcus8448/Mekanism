package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.ElectrolysisRecipe;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class ElectrolysisIRecipe extends ElectrolysisRecipe {

    public ElectrolysisIRecipe(Identifier id, FluidStackIngredient input, FloatingLong energyMultiplier, GasStack leftGasOutput, GasStack rightGasOutput) {
        super(id, input, energyMultiplier, leftGasOutput, rightGasOutput);
    }

    @Nonnull
    @Override
    public RecipeType<ElectrolysisRecipe> getType() {
        return MekanismRecipeType.SEPARATING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<ElectrolysisRecipe> getSerializer() {
        return MekanismRecipeSerializers.SEPARATING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.ELECTROLYTIC_SEPARATOR.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.ELECTROLYTIC_SEPARATOR.getItemStack();
    }
}
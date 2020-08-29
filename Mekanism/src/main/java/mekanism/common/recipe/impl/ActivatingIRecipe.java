package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.GasToGasRecipe;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class ActivatingIRecipe extends GasToGasRecipe {

    public ActivatingIRecipe(Identifier id, GasStackIngredient input, GasStack output) {
        super(id, input, output);
    }

    @Nonnull
    @Override
    public RecipeType<GasToGasRecipe> getType() {
        return MekanismRecipeType.ACTIVATING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<GasToGasRecipe> getSerializer() {
        return MekanismRecipeSerializers.ACTIVATING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.SOLAR_NEUTRON_ACTIVATOR.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.SOLAR_NEUTRON_ACTIVATOR.getItemStack();
    }
}
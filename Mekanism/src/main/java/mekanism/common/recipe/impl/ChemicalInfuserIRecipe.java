package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ChemicalInfuserRecipe;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class ChemicalInfuserIRecipe extends ChemicalInfuserRecipe {

    public ChemicalInfuserIRecipe(Identifier id, GasStackIngredient leftInput, GasStackIngredient rightInput, GasStack output) {
        super(id, leftInput, rightInput, output);
    }

    @Nonnull
    @Override
    public RecipeType<ChemicalInfuserRecipe> getType() {
        return MekanismRecipeType.CHEMICAL_INFUSING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<ChemicalInfuserRecipe> getSerializer() {
        return MekanismRecipeSerializers.CHEMICAL_INFUSING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.CHEMICAL_INFUSER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.CHEMICAL_INFUSER.getItemStack();
    }
}
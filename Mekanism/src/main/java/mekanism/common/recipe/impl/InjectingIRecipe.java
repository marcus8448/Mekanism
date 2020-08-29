package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class InjectingIRecipe extends ItemStackGasToItemStackRecipe {

    public InjectingIRecipe(Identifier id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack output) {
        super(id, itemInput, gasInput, output);
    }

    @Nonnull
    @Override
    public RecipeType<ItemStackGasToItemStackRecipe> getType() {
        return MekanismRecipeType.INJECTING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<ItemStackGasToItemStackRecipe> getSerializer() {
        return MekanismRecipeSerializers.INJECTING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.CHEMICAL_INJECTION_CHAMBER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.CHEMICAL_INJECTION_CHAMBER.getItemStack();
    }
}
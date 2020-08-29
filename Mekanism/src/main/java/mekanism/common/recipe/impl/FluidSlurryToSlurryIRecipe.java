package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.FluidSlurryToSlurryRecipe;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import mekanism.api.recipes.inputs.chemical.SlurryStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class FluidSlurryToSlurryIRecipe extends FluidSlurryToSlurryRecipe {

    public FluidSlurryToSlurryIRecipe(Identifier id, FluidStackIngredient fluidInput, SlurryStackIngredient slurryInput, SlurryStack output) {
        super(id, fluidInput, slurryInput, output);
    }

    @Nonnull
    @Override
    public RecipeType<FluidSlurryToSlurryRecipe> getType() {
        return MekanismRecipeType.WASHING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<FluidSlurryToSlurryRecipe> getSerializer() {
        return MekanismRecipeSerializers.WASHING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.CHEMICAL_WASHER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.CHEMICAL_WASHER.getItemStack();
    }
}
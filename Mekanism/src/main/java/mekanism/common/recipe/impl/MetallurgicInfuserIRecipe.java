package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.recipes.MetallurgicInfuserRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.InfusionStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class MetallurgicInfuserIRecipe extends MetallurgicInfuserRecipe {

    public MetallurgicInfuserIRecipe(Identifier id, ItemStackIngredient itemInput, InfusionStackIngredient infusionInput, ItemStack output) {
        super(id, itemInput, infusionInput, output);
    }

    @Nonnull
    @Override
    public RecipeType<MetallurgicInfuserRecipe> getType() {
        return MekanismRecipeType.METALLURGIC_INFUSING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<MetallurgicInfuserRecipe> getSerializer() {
        return MekanismRecipeSerializers.METALLURGIC_INFUSING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.METALLURGIC_INFUSER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.METALLURGIC_INFUSER.getItemStack();
    }
}
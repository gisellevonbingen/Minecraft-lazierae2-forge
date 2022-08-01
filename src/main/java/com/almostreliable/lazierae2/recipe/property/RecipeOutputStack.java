package com.almostreliable.lazierae2.recipe.property;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record RecipeOutputStack(ItemStack stack) implements IRecipeOutputProvider {

    @Override
    public ResourceLocation id() {
        return Objects.requireNonNull(stack.getItem().getRegistryName());
    }

    @Override
    public int count() {
        return stack.getCount();
    }
}

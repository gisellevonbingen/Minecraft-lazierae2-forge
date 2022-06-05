package com.almostreliable.lazierae2.compat.jei.category;

import com.almostreliable.lazierae2.gui.ProcessorScreen;
import com.almostreliable.lazierae2.recipe.type.TripleInputRecipe;
import com.almostreliable.lazierae2.util.GuiUtil;
import com.almostreliable.lazierae2.util.GuiUtil.ANCHOR;
import com.almostreliable.lazierae2.util.TextUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.almostreliable.lazierae2.util.TextUtil.f;

public abstract class TripleInputCategory extends ProcessorCategory<TripleInputRecipe> {

    private final IDrawable background;
    private final IDrawable slot;

    TripleInputCategory(IGuiHelper guiHelper, String id, ItemLike iconProvider) {
        super(guiHelper, id, iconProvider);
        var backgroundTexture = ProcessorScreen.TEXTURE;
        background = guiHelper.drawableBuilder(backgroundTexture, 42, 6, 92, 62)
            .setTextureSize(ProcessorScreen.TEXTURE_WIDTH, ProcessorScreen.TEXTURE_HEIGHT)
            .build();
        slot = guiHelper.drawableBuilder(
                backgroundTexture,
                43,
                28,
                ProcessorScreen.SLOT_SIZE,
                ProcessorScreen.SLOT_SIZE
            )
            .setTextureSize(ProcessorScreen.TEXTURE_WIDTH, ProcessorScreen.TEXTURE_HEIGHT)
            .build();
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TripleInputRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 23).addItemStack(recipe.getResultItem());

        var inputs = recipe.getInputs();
        Map<Integer, List<ItemStack>> inputMap = new HashMap<>();
        for (var i = 0; i < inputs.size(); i++) {
            var stacks = inputs.get(i).ingredient().getItems();
            for (var stack : stacks) {
                stack.setCount(inputs.get(i).count());
            }
            inputMap.put(i, List.of(stacks));
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).addIngredients(VanillaTypes.ITEM_STACK, inputMap.get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 23)
            .addIngredients(VanillaTypes.ITEM_STACK, inputMap.size() >= 2 ? inputMap.get(1) : List.of(ItemStack.EMPTY));
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 44)
            .addIngredients(VanillaTypes.ITEM_STACK, inputMap.size() >= 3 ? inputMap.get(2) : List.of(ItemStack.EMPTY));
        builder.setShapeless();
        super.setRecipe(builder, recipe, focuses);
    }

    @Override
    public void draw(
        TripleInputRecipe recipe, IRecipeSlotsView slotsView, PoseStack stack, double mX, double mY
    ) {
        // additional input slots
        slot.draw(stack, 1, 1);
        slot.draw(stack, 1, 43);
        // progress
        progressEmpty.draw(stack, 36, 17);
        progress.draw(stack, 36, 17);
        // required energy
        var energy = TextUtil.formatEnergy(recipe.getEnergyCost(), 1, 3, false, true);
        GuiUtil.renderText(stack, energy, ANCHOR.TOP_RIGHT, 91, 46, 0.8f, 0x00_0000);
        // required time
        var time = f("{} ticks", recipe.getProcessTime());
        GuiUtil.renderText(stack, time, ANCHOR.TOP_RIGHT, 91, 54, 0.8f, 0x00_0000);
    }

    @SuppressWarnings("removal")
    @Override
    public Class<? extends TripleInputRecipe> getRecipeClass() {
        return TripleInputRecipe.class;
    }
}

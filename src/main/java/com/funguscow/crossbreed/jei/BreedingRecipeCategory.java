package com.funguscow.crossbreed.jei;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.init.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The category of recipes in JEI for crossbreeding quails.
 */
public class BreedingRecipeCategory<TIngredient extends IngredientLike, TRecipe extends BreedingRecipe<TIngredient>> implements IRecipeCategory<TRecipe> {

    public static final RecipeType<QuailBreedingRecipe> QUAIL_BREEDING_RECIPE_TYPE = RecipeType.create(BreedMod.MODID,
            "quail_breeding_recipe_type",
            QuailBreedingRecipe.class);

    public static final ResourceLocation GUI = new ResourceLocation(BreedMod.MODID, "textures/gui/breeding_recipe_bg.png");

    private final IDrawable background, icon;
    private final RecipeType<TRecipe> recipeType;

    public BreedingRecipeCategory(IGuiHelper helper, RecipeType<TRecipe> recipeType) {
        background = helper.createDrawable(GUI, 0, 0, 176, 87);
        icon = helper.createDrawableItemStack(new ItemStack(ModItems.QUAIL_EGG.get()));
        this.recipeType = recipeType;
    }

    @Override
    public @NotNull RecipeType<TRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("text." + BreedMod.MODID + ".recipe_category.quail_breeding");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, TRecipe recipe, @NotNull IFocusGroup iFocusGroup) {
        List<TIngredient> parents = recipe.getParents();
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 35).addIngredients(parents.get(0).getIcon());
        builder.addSlot(RecipeIngredientRole.INPUT, 66, 35).addIngredients(parents.get(1).getIcon());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 35).addIngredients(recipe.getChild().getIcon());
    }
}

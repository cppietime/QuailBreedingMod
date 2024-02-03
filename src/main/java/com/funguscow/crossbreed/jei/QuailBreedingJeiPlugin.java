package com.funguscow.crossbreed.jei;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.QuailType;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Plugin for JEI mod to display "recipes" for crossbreeding.
 */
@JeiPlugin
public class QuailBreedingJeiPlugin implements IModPlugin {

    @Override
    @NotNull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(BreedMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BreedingRecipeCategory<>(registration.getJeiHelpers().getGuiHelper(), BreedingRecipeCategory.QUAIL_BREEDING_RECIPE_TYPE));
        registration.addRecipeCategories(new BreedingRecipeCategory<>(registration.getJeiHelpers().getGuiHelper(), BreedingRecipeCategory.TREE_BREEDING_RECIPE_TYPE));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        List<QuailBreedingRecipe> quailBreedingRecipes = new ArrayList<>();
        for (QuailType quailType : QuailType.Types.values()) {
            if (!quailType.enabled) {
                continue;
            }
            String p1key = quailType.parent1, p2key = quailType.parent2;
            if (p1key.isEmpty() || p2key.isEmpty()) {
                continue;
            }
            QuailType p1type = QuailType.Types.get(p1key);
            QuailType p2type = QuailType.Types.get(p2key);
            QuailBreedingRecipe recipe = new QuailBreedingRecipe(p1type, p2type, quailType);
            quailBreedingRecipes.add(recipe);
        }
        registration.addRecipes(BreedingRecipeCategory.QUAIL_BREEDING_RECIPE_TYPE, quailBreedingRecipes);

        List<TreeBreedingRecipe> treeBreedingRecipes = new ArrayList<>();
        for (TreeSpecies species : TreeSpecies.Species.values()) {
            if (!species.enabled) {
                continue;
            }
            String p1key = species.parent1, p2key = species.parent2;
            if (p1key.isEmpty() || p2key.isEmpty()) {
                continue;
            }
            TreeSpecies p1type = TreeSpecies.Species.get(p1key);
            TreeSpecies p2type = TreeSpecies.Species.get(p2key);
            TreeBreedingRecipe recipe = new TreeBreedingRecipe(p1type, p2type, species);
            treeBreedingRecipes.add(recipe);
        }
        registration.addRecipes(BreedingRecipeCategory.TREE_BREEDING_RECIPE_TYPE, treeBreedingRecipes);
    }
}

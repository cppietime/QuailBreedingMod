package com.funguscow.crossbreed.jei;

import net.minecraft.world.item.crafting.Ingredient;

/**
 * Anything that can be represented as an icon in the JEI GUI.
 */
public interface IngredientLike {

    Ingredient getIcon();

}

package com.funguscow.crossbreed.jei;

import java.util.List;

/**
 * A recipe that represents the crossbreeding of two quail types to produce a third.
 */
public class BreedingRecipe<T extends IngredientLike> {

    private final T mother, father, child;

    public BreedingRecipe(T mother, T father, T child) {
        this.mother = mother;
        this.father = father;
        this.child = child;
    }

    public List<T> getParents() {
        return List.of(mother, father);
    }

    public T getChild() {
        return child;
    }

}

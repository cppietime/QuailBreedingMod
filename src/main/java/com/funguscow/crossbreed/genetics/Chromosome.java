package com.funguscow.crossbreed.genetics;

import net.minecraft.util.RandomSource;

public abstract class Chromosome<T> {

    public abstract Chromosome<T> crossover(Chromosome<?> other, RandomSource random);

    public abstract T value();

}

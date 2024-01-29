package com.funguscow.crossbreed.genetics;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class IntChromosome extends Chromosome<Integer> {

    private final int value,
        min,
        max;

    private final double sigma;

    public IntChromosome(int value, double sigma, int min, int max) {
        this.value = value;
        this.sigma = sigma;
        this.min = min;
        this.max = max;
    }

    public IntChromosome(int value, int sigma) {
        this(value, sigma, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public Chromosome<Integer> crossover(Chromosome<?> other, RandomSource random) {
        int base = random.nextBoolean() ? this.value : ((Chromosome<Integer>)other).value();
        int newValue = (int) Mth.clamp(base + random.nextGaussian() * sigma, min, max);
        return new IntChromosome(newValue, sigma, min, max);
    }
}

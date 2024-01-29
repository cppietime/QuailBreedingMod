package com.funguscow.crossbreed.genetics;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class DoubleChromosome extends Chromosome<Double> {

    private final double value,
        min,
        max;

    private final double sigma;

    public DoubleChromosome(double value, double sigma, double min, double max) {
        this.value = value;
        this.sigma = sigma;
        this.min = min;
        this.max = max;
    }

    public DoubleChromosome(double value, double sigma) {
        this(value, sigma, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    @Override
    public Double value() {
        return value;
    }

    @Override
    public Chromosome<Double> crossover(Chromosome<?> other, RandomSource random) {
        double base = random.nextBoolean() ? value : ((Chromosome<Double>)other).value();
        double newValue = Mth.clamp(base + random.nextGaussian() * sigma, min, max);
        return new DoubleChromosome(newValue, sigma, min, max);
    }
}

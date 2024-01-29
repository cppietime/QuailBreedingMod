package com.funguscow.crossbreed.genetics;

import net.minecraft.util.RandomSource;

public class StringChromosome extends Chromosome<String> {

    private final String value;

    public StringChromosome(String value) {
        this.value = value;
    }

    @Override
    public Chromosome<String> crossover(Chromosome<?> other, RandomSource random) {
        String newValue = random.nextBoolean() ? this.value : ((Chromosome<String>)other).value();
        return new StringChromosome(newValue);
    }

    @Override
    public String value() {
        return value;
    }
}

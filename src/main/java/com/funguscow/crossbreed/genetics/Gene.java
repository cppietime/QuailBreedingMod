package com.funguscow.crossbreed.genetics;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Something that can crossover and mutate.
 */
public interface Gene<T extends Gene<T>> {

    List<Chromosome<?>> chromosomes();

    T reconstruct(List<Chromosome<?>> chromosomes);

    static <T extends Gene<T>> T crossover(Gene<T> a, Gene<T> b, RandomSource random) {
        List<Chromosome<?>> chromosomesA = a.chromosomes();
        List<Chromosome<?>> chromosomesB = b.chromosomes();
        if (chromosomesA.size() != chromosomesB.size()) {
            throw new IllegalArgumentException("Size of genes do not match");
        }
        List<Chromosome<?>> mutants = new ArrayList<>();
        for (int i = 0; i < chromosomesA.size(); i++) {
            Chromosome<?> mutant = chromosomesA.get(i).crossover(chromosomesB.get(i), random);
            mutants.add(mutant);
        }
        return a.reconstruct(mutants);
    }

}

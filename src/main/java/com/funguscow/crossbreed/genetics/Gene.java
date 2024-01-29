package com.funguscow.crossbreed.genetics;

import com.funguscow.crossbreed.util.Pair;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Something that can crossover and mutate.
 */
public interface Gene<T extends Gene<T>> {

    List<Chromosome<?>> chromosomes();

    T reconstruct(List<Chromosome<?>> chromosomes);

    static <T extends Gene<T>> T crossover(T a, T b, RandomSource random) {
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

    static <T extends Gene<T>> Pair<T, T> diploid(T a1, T a2, T b1, T b2, RandomSource random) {
        T alleleA, alleleB;
        if (random.nextBoolean()) {
            alleleA = crossover(a1, b1, random);
            alleleB = crossover(a2, b2, random);
        } else {
            alleleA = crossover(a1, b2, random);
            alleleB = crossover(a2, b1, random);
        }
        if (random.nextBoolean()) {
            return new Pair<>(alleleA, alleleB);
        }
        return new Pair<>(alleleB, alleleA);
    }

}

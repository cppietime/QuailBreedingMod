package com.funguscow.crossbreed.worldgen.botany;

import com.funguscow.crossbreed.genetics.*;
import com.funguscow.crossbreed.util.TagUtils;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class TreeGene implements Gene<TreeGene> {

    private static final double WIDTH_SIGMA = 0,
        HEIGHT_SIGMA = 0.5,
        HEIGHT_RANGE_SIGMA = 0.1,
        YIELD_SIGMA = 0.1,
        FERTILITY_SIGMA = 0.1,
        DOMINANCE_SIGMA = 0.05;

    public static final int MAX_WIDTH = 8,
        MAX_HEIGHT = 50;

    public static final double MAX_YIELD = 0.25,
        MAX_FERTILITY = 0.25;

    public int trunkWidth;

    public int minHeight;
    public double heightRange;

    public String species; // Provides log and leaf blocks.

    public String trunkType;

    public String leafType;

    public String fruitItem; // String instead of ResourceLocation so it can be optional

    public double yield;

    public double fertility;

    public float dominance;

    public TreeGene(int trunkWidth,
                    int minHeight,
                    double heightRange,
                    String species,
                    String trunkType,
                    String leafType,
                    String fruitItem,
                    double yield,
                    double fertility,
                    float dominance) {
        this.trunkWidth = trunkWidth;
        this.minHeight = minHeight;
        this.heightRange = heightRange;
        this.species = species;
        this.trunkType = trunkType;
        this.leafType = leafType;
        this.fruitItem = fruitItem;
        this.yield = yield;
        this.fertility = fertility;
        this.dominance = dominance;
    }

    public TreeGene(int trunkWidth,
                    int minHeight,
                    double heightRange,
                    String species,
                    String trunkType,
                    String leafType,
                    String fruitItem,
                    double yield,
                    double fertility) {
        this(trunkWidth, minHeight, heightRange, species, trunkType, leafType, fruitItem, yield, fertility, 0.5f);
    }

    public TreeGene(int trunkWidth,
                    int minHeight,
                    double heightRange,
                    String species,
                    String trunkType,
                    String leafType,
                    double fertility) {
        this(trunkWidth, minHeight, heightRange, species, trunkType, leafType, "", 0, fertility);
    }

    public static TreeGene of(CompoundTag nbt) {
        int width = TagUtils.getOrDefault(nbt, "TrunkWidth", 1);
        int height = nbt.getInt("MinHeight");
        int heightRange = TagUtils.getOrDefault(nbt, "HeightRange", 0);
        String species = nbt.getString("Species");
        String trunkType = TagUtils.getOrDefault(nbt, "TrunkType", "straight");
        String leafType = TagUtils.getOrDefault(nbt, "LeafType", "blob");
        String fruit = TagUtils.getOrDefault(nbt, "Fruit", "");
        double yield = TagUtils.getOrDefault(nbt, "Yield", 0.);
        double fertility = TagUtils.getOrDefault(nbt, "Fertility", 0.1);
        float dominance = TagUtils.getOrDefault(nbt, "Dominance", 0.5f);
        return new TreeGene(width, height, heightRange, species, trunkType, leafType, fruit, yield, fertility, dominance);
    }

    public TreeSpecies species() {
        return TreeSpecies.Species.get(species);
    }

    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("TrunkWidth", trunkWidth);
        nbt.putInt("MinHeight", minHeight);
        nbt.putDouble("HeightRange", heightRange);
        nbt.putString("Species", species);
        nbt.putString("TrunkType", trunkType);
        nbt.putString("LeafType", leafType);
        nbt.putString("Fruit", fruitItem);
        nbt.putFloat("Dominance", dominance);
        nbt.putDouble("Yield", yield);
        nbt.putDouble("Fertility", fertility);
        return nbt;
    }

    public TreeGene readFromTag(CompoundTag nbt) {
        trunkWidth = TagUtils.getOrDefault(nbt, "TrunkWidth", trunkWidth);
        minHeight = TagUtils.getOrDefault(nbt, "MinHeight", minHeight);
        heightRange = TagUtils.getOrDefault(nbt, "HeightRange", heightRange);
        species = TagUtils.getOrDefault(nbt, "Species", species);
        trunkType = TagUtils.getOrDefault(nbt, "TrunkType", trunkType);
        leafType = TagUtils.getOrDefault(nbt, "LeafType", leafType);
        fruitItem = TagUtils.getOrDefault(nbt, "Fruit", fruitItem);
        dominance = TagUtils.getOrDefault(nbt, "Dominance", dominance);
        yield = TagUtils.getOrDefault(nbt, "Yield", yield);
        fertility = TagUtils.getOrDefault(nbt, "Fertility", fertility);
        return this;
    }

    @Override
    public List<Chromosome<?>> chromosomes() {
        return List.of(
                new IntChromosome(trunkWidth, WIDTH_SIGMA, 0, MAX_WIDTH),
                new IntChromosome(minHeight, HEIGHT_SIGMA, 1, MAX_HEIGHT),
                new DoubleChromosome(heightRange, HEIGHT_RANGE_SIGMA, 0, 2),
                new StringChromosome(species),
                new StringChromosome(trunkType),
                new StringChromosome(leafType),
                new StringChromosome(fruitItem),
                new DoubleChromosome(dominance, DOMINANCE_SIGMA, 0, 1),
                new DoubleChromosome(yield, YIELD_SIGMA, 0, MAX_YIELD),
                new DoubleChromosome(fertility, FERTILITY_SIGMA, 0, MAX_FERTILITY)
        );
    }

    @Override
    public TreeGene reconstruct(List<Chromosome<?>> chromosomes) {
        int trunkWidth = ((IntChromosome)chromosomes.get(0)).value();
        int minHeight = ((IntChromosome)chromosomes.get(1)).value();
        double heightRange = ((DoubleChromosome)chromosomes.get(2)).value();
        String species = ((StringChromosome)chromosomes.get(3)).value();
        String trunkType = ((StringChromosome)chromosomes.get(4)).value();
        String leafType = ((StringChromosome)chromosomes.get(5)).value();
        String fruit = ((StringChromosome)chromosomes.get(6)).value();
        float dominance = ((DoubleChromosome)chromosomes.get(7)).value().floatValue();
        double yield = ((DoubleChromosome)chromosomes.get(8)).value();
        double fertility = ((DoubleChromosome)chromosomes.get(9)).value();
        return new TreeGene(trunkWidth, minHeight, heightRange, species, trunkType, leafType, fruit, yield, fertility, dominance);
    }

    public TreeGene copy() {
        return new TreeGene(
                trunkWidth, minHeight, heightRange, species, trunkType, leafType, fruitItem, yield, fertility, dominance
        );
    }
}

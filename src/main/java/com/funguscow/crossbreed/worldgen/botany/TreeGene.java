package com.funguscow.crossbreed.worldgen.botany;

import com.funguscow.crossbreed.util.TagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class TreeGene {

    public int trunkWidth;

    public int minHeight;
    public double heightRange;

    public String species; // Provides log and leaf blocks.

    public String trunkType;

    public String leafType;

    public String fruitItem; // String instead of ResourceLocation so it can be optional

    public float dominance;

    public TreeGene(int trunkWidth,
                    int minHeight,
                    double heightRange,
                    String species,
                    String trunkType,
                    String leafType,
                    String fruitItem,
                    float dominance) {
        this.trunkWidth = trunkWidth;
        this.minHeight = minHeight;
        this.heightRange = heightRange;
        this.species = species;
        this.trunkType = trunkType;
        this.leafType = leafType;
        this.fruitItem = fruitItem;
        this.dominance = dominance;
    }

    public TreeGene(int trunkWidth,
                    int minHeight,
                    double heightRange,
                    String species,
                    String trunkType,
                    String leafType,
                    String fruitItem) {
        this(trunkWidth, minHeight, heightRange, species, trunkType, leafType, fruitItem, 0.5f);
    }

    public TreeGene(int trunkWidth,
                    int minHeight,
                    double heightRange,
                    String species,
                    String trunkType,
                    String leafType) {
        this(trunkWidth, minHeight, heightRange, species, trunkType, leafType, "");
    }

    public static TreeGene of(CompoundTag nbt) {
        int width = TagUtils.getOrDefault(nbt, "TrunkWidth", 1);
        int height = nbt.getInt("MinHeight");
        int heightRange = TagUtils.getOrDefault(nbt, "HeightRange", 0);
        String species = nbt.getString("Species");
        String trunkType = TagUtils.getOrDefault(nbt, "TrunkType", "straight");
        String leafType = TagUtils.getOrDefault(nbt, "LeafType", "blob");
        String fruit = TagUtils.getOrDefault(nbt, "Fruit", "");
        float dominance = TagUtils.getOrDefault(nbt, "Dominance", 0.5f);
        return new TreeGene(width, height, heightRange, species, trunkType, leafType, fruit, dominance);
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
        return this;
    }

    public TreeGene crossover(TreeGene other, RandomSource rand) {
        // TODO add mutation
        int trunkWidth = rand.nextBoolean() ? this.trunkWidth : other.trunkWidth;
        return null;
    }

}

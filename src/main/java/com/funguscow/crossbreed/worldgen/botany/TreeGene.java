package com.funguscow.crossbreed.worldgen.botany;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class TreeGene {

    public int trunkWidth;

    public int minHeight;
    public double heightRange;

    public String species; // Provides log and leaf blocks.

    public String trunkType;

    public String leafType;

    public String fruitItem; // String instead of ResourceLocation so it can be optional

    public TreeGene(int trunkWidth,
                    int minHeight,
                    double heightRange,
                    String species,
                    String trunkType,
                    String leafType,
                    String fruitItem) {
        this.trunkWidth = trunkWidth;
        this.minHeight = minHeight;
        this.heightRange = heightRange;
        this.species = species;
        this.trunkType = trunkType;
        this.leafType = leafType;
        this.fruitItem = fruitItem;
    }

    public TreeGene(int trunkWidth,
                    int minHeight,
                    double heightRange,
                    String species,
                    String trunkType,
                    String leafType) {
        this(trunkWidth, minHeight, heightRange, species, trunkType, leafType, "");
    }

    public TreeSpecies species() {
        return TreeSpecies.Species.get(species);
    }

}

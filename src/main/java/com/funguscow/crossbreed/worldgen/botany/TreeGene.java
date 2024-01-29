package com.funguscow.crossbreed.worldgen.botany;

import com.funguscow.crossbreed.util.TagUtils;
import net.minecraft.nbt.CompoundTag;

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

    public static TreeGene of(CompoundTag nbt) {
        int width = TagUtils.getOrDefault(nbt, "TrunkWidth", 1);
        int height = nbt.getInt("MinHeight");
        int heightRange = TagUtils.getOrDefault(nbt, "HeightRange", 0);
        String species = nbt.getString("Species");
        String trunkType = TagUtils.getOrDefault(nbt, "TrunkType", "straight");
        String leafType = TagUtils.getOrDefault(nbt, "LeafType", "blob");
        String fruit = TagUtils.getOrDefault(nbt, "Fruit", "");
        return new TreeGene(width, height, heightRange, species, trunkType, leafType, fruit);
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
        return nbt;
    }

}

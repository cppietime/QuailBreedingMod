package com.funguscow.crossbreed.worldgen.botany;

import com.electronwill.nightconfig.core.Config;
import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.util.RandomPool;
import com.funguscow.crossbreed.util.UnorderedPair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TreeSpecies {

    public static final Map<String, TreeSpecies> Species = new HashMap<>();
    public static final Map<UnorderedPair<String>, RandomPool<String>> Pairings = new HashMap<>();

    public final String id;

    public ResourceLocation logBlock;

    public ResourceLocation leafBlock;

    public ResourceLocation sapling;

    public int minWidth;

    public TreeGene defaultGene;

    public String parent1, parent2;

    public boolean enabled;

    public float hybridChance;

    public TreeSpecies(String id, ResourceLocation logBlock, ResourceLocation leafBlock, int minWidth, TreeGene defaultGene) {
        this.id = id;
        this.logBlock = logBlock;
        this.leafBlock = leafBlock;
        this.sapling = new ResourceLocation(BreedMod.MODID, id + "_sapling");
        this.minWidth = minWidth;
        this.defaultGene = defaultGene;
        enabled = true;
        hybridChance = 0;
        parent1 = parent2 = "";
        Species.put(id, this);
    }

    public TreeSpecies enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public BlockState logBlock() {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(logBlock)).defaultBlockState();
    }

    public static void matchConfig() {
        for (Map.Entry<String, TreeSpecies> entry : Species.entrySet()) {
            String key = entry.getKey();
            TreeSpecies species = entry.getValue();
            TreeGene gene = species.defaultGene;
            QuailConfig.Common.TreeSpeciesConfig config = QuailConfig.COMMON.treeSpecies.get(key);
            species.logBlock = new ResourceLocation(config.logBlock.get());
            species.leafBlock = new ResourceLocation(config.leafBlock.get());
            species.sapling = new ResourceLocation(BreedMod.MODID, key + "_sapling");
            species.minWidth = config.minWidth.get();
            species.parent1 = config.parent1.get();
            species.parent2 = config.parent2.get();
            species.enabled = config.enabled.get();
            species.hybridChance = config.hybridChance.get().floatValue();
            gene.trunkType = config.trunkType.get();
            gene.leafType = config.leafType.get();
            gene.minHeight = config.minHeight.get();
            gene.heightRange = config.heightRange.get();
            gene.trunkWidth = config.width.get();
            gene.fruitItem = config.fruit.get();
            gene.species = key;
        }
        for (Config config : QuailConfig.COMMON.extraTrees.get()) {
            String name = config.get("Name");
            String trunk = config.get("TrunkType");
            String leaf = config.get("LeafType");
            String fruit = config.getOrElse("Fruit", "");
            String parent1 = config.getOrElse("Parent1", "");
            String parent2 = config.getOrElse("Parent2", "");
            ResourceLocation logBlock = new ResourceLocation(config.get("LogBlock"));
            ResourceLocation leafBlock = new ResourceLocation(config.get("LeafBlock"));
            ResourceLocation sapling = new ResourceLocation(BreedMod.MODID, name + "_sapling");
            boolean enabled = config.getOrElse("Enabled", true);
            int trunkWidth = config.getIntOrElse("Width", 1);
            int baseHeight = config.getIntOrElse("Height", 4);
            int minWidth = config.getIntOrElse("MinWidth", 1);
            double heightRange = config.getOrElse("HeightRange", (double) 0);
            TreeGene gene = new TreeGene(trunkWidth, baseHeight, heightRange, name, trunk, leaf, fruit);
            TreeSpecies species = new TreeSpecies(name, logBlock, leafBlock, minWidth, gene).enabled(enabled);
            species.parent1 = parent1;
            species.parent2 = parent2;
            species.hybridChance = config.getOrElse("HybridChance", 0.).floatValue();
        }
        for (TreeSpecies species : Species.values()) {
            if (!species.parent1.isEmpty() && !species.parent2.isEmpty() && species.enabled) {
                UnorderedPair<String> pair = new UnorderedPair<>(species.parent1, species.parent2);
                if (Pairings.containsKey(pair)) {
                    throw new IllegalStateException("Pair " + species.parent1 + "+" + species.parent2 + " has already been registered.");
                }
                RandomPool<String> pool = Pairings.computeIfAbsent(pair, keyPair -> new RandomPool<>((String) null));
                pool.add(species.id, species.hybridChance);
            }
        }
    }

    public static final TreeSpecies
        TEST_TREE = new TreeSpecies("test", new ResourceLocation("coal_block"), new ResourceLocation("emerald_block"), 1,
            new TreeGene(1, 4, 2.0, "test", "straight", "blob", "minecraft:stick"));

}

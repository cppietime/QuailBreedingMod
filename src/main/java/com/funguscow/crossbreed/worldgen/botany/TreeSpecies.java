package com.funguscow.crossbreed.worldgen.botany;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.block.GeneticSaplingBlock;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.init.ModBlocks;
import com.funguscow.crossbreed.init.ModCreativeTabs;
import com.funguscow.crossbreed.util.RandomPool;
import com.funguscow.crossbreed.util.UnorderedPair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public class TreeSpecies {

    public static final Map<String, TreeSpecies> Species = new HashMap<>();
    public static final Map<UnorderedPair<String>, RandomPool<String>> Pairings = new HashMap<>();
    public static final List<RegistryObject<Block>> Saplings = new ArrayList<>();

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

    public static void registerItems() {
        for (TreeSpecies species : Species.values()) {
            Supplier<Block> supplier = () -> new GeneticSaplingBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING));
            Saplings.add(ModBlocks.registerBlockAndItem(species.id + "_sapling",
                    supplier,
                    block -> new BlockItem(block, new Item.Properties()),
                    Optional.of(ModCreativeTabs.QUAIL_MOD_TAB)));
        }
    }

}

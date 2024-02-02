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
    public static final Map<Block, TreeSpecies> VanillaSpecies = new HashMap<>();
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
        TEST_TREE = new TreeSpecies("test", new ResourceLocation("acacia_log"), new ResourceLocation("breesources", "test_leaves"), 1,
            new TreeGene(2,
                    4,
                    2.0,
                    "test",
                    "straight",
                    "blob",
                    "minecraft:stick",
                    0.25,
                    0.25,
                    1)),
            OAK = new TreeSpecies("oak",
                    new ResourceLocation("oak_log"),
                    new ResourceLocation(BreedMod.MODID, "g_oak_leaves"),
                    1,
                    new TreeGene(1,
                            4,
                            1.5,
                            "oak",
                            "straight",
                            "cube",
                            "minecraft:apple",
                            0.005,
                            0.05,
                            0.14)),
            BIRCH = new TreeSpecies("birch",
                    new ResourceLocation("birch_log"),
                    new ResourceLocation(BreedMod.MODID, "g_birch_leaves"),
                    2,
                    new TreeGene(1,
                            5,
                            1.3,
                            "birch",
                            "straight",
                            "cube",
                            0.05,
                            0.14)),
            SPRUCE = new TreeSpecies("spruce",
                    new ResourceLocation("spruce_log"),
                    new ResourceLocation(BreedMod.MODID, "g_spruce_leaves"),
                    2,
                    new TreeGene(1,
                            5,
                            2.0,
                            "spruce",
                            "straight",
                            "cube",
                            0.05,
                            0.14)),
            JUNGLE = new TreeSpecies("jungle",
                    new ResourceLocation("jungle_log"),
                    new ResourceLocation(BreedMod.MODID, "g_jungle_leaves"),
                    2,
                    new TreeGene(1,
                            6,
                            2.0,
                            "jungle",
                            "straight",
                            "cube",
                            "minecraft:cocoa_beans",
                            0.005,
                            0.025,
                            0.14)),
            DARK_OAK = new TreeSpecies("dark_oak",
                    new ResourceLocation("dark_oak_log"),
                    new ResourceLocation(BreedMod.MODID, "g_dark_oak_leaves"),
                    3,
                    new TreeGene(2,
                            4,
                            1,
                            "dark_oak",
                            "straight",
                            "cube",
                            "minecraft:apple",
                            0.005,
                            0.05,
                            0.14)),
            ACACIA = new TreeSpecies("acacia",
                    new ResourceLocation("acacia_log"),
                    new ResourceLocation(BreedMod.MODID, "g_acacia_leaves"),
                    3,
                    new TreeGene(1,
                            5,
                            1.3,
                            "acacia",
                            "straight",
                            "cube",
                            0.05,
                            0.14)),
            MANGROVE = new TreeSpecies("mangrove",
                    new ResourceLocation("mangrove_log"),
                    new ResourceLocation(BreedMod.MODID, "g_mangrove_leaves"),
                    2,
                    new TreeGene(1,
                            5,
                            1.3,
                            "mangrove",
                            "straight",
                            "cube",
                            0.05,
                            0.14)),
            CHERRY = new TreeSpecies("cherry",
                    new ResourceLocation("cherry_log"),
                    new ResourceLocation(BreedMod.MODID, "g_cherry_leaves"),
                    2,
                    new TreeGene(1,
                            5,
                            1.3,
                            "cherry",
                            "straight",
                            "cube",
                            0.05,
                            0.14));

    public static void registerItems() {
        for (TreeSpecies species : Species.values()) {
            Supplier<Block> supplier = () -> new GeneticSaplingBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING));
            Saplings.add(ModBlocks.registerBlockAndItem(species.sapling.getPath(),
                    supplier,
                    block -> new BlockItem(block, new Item.Properties()),
                    Optional.of(ModCreativeTabs.QUAIL_MOD_TAB)));
        }

        VanillaSpecies.put(Blocks.OAK_LEAVES, OAK);
        VanillaSpecies.put(Blocks.BIRCH_LEAVES, BIRCH);
        VanillaSpecies.put(Blocks.SPRUCE_LEAVES, SPRUCE);
        VanillaSpecies.put(Blocks.JUNGLE_LEAVES, JUNGLE);
        VanillaSpecies.put(Blocks.DARK_OAK_LEAVES, DARK_OAK);
        VanillaSpecies.put(Blocks.ACACIA_LEAVES, ACACIA);
        VanillaSpecies.put(Blocks.MANGROVE_LEAVES, MANGROVE);
        VanillaSpecies.put(Blocks.CHERRY_LEAVES, CHERRY);
    }

}

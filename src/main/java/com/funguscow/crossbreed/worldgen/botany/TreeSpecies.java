package com.funguscow.crossbreed.worldgen.botany;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.block.GeneticSaplingBlock;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.init.ModBlocks;
import com.funguscow.crossbreed.init.ModCreativeTabs;
import com.funguscow.crossbreed.jei.IngredientLike;
import com.funguscow.crossbreed.util.RandomPool;
import com.funguscow.crossbreed.util.UnorderedPair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public class TreeSpecies implements IngredientLike {

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

    public String lang;

    public TreeSpecies(String id, ResourceLocation logBlock, ResourceLocation leafBlock, int minWidth, TreeGene defaultGene, String lang) {
        this.id = id;
        this.logBlock = logBlock;
        this.leafBlock = leafBlock;
        this.sapling = new ResourceLocation(BreedMod.MODID, id + "_sapling");
        this.minWidth = minWidth;
        this.defaultGene = defaultGene;
        enabled = true;
        hybridChance = 0;
        parent1 = parent2 = "";
        this.lang = lang;
        Species.put(id, this);
    }

    public TreeSpecies(String id, ResourceLocation logBlock, ResourceLocation leafBlock, int minWidth, TreeGene defaultGene) {
        this(id, logBlock, leafBlock, minWidth, defaultGene, "");
    }

    public BlockState logBlock() {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(logBlock)).defaultBlockState();
    }

    @Override
    public Ingredient getIcon() {
        return Ingredient.of(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(sapling)).asItem());
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
            species.lang = config.lang.get();
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
            PINE = new TreeSpecies("pine", new ResourceLocation(BreedMod.MODID, "pine_log"), new ResourceLocation(BreedMod.MODID, "g_spruce_leaves"), 2,
            new TreeGene(1,
                    4,
                    1.0,
                    "pine",
                    "cross",
                    "cone",
                    0.05,
                    0.14),
            "Yellow Pine"),
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
                            0.14),
                    "Red Oak"),
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
                            0.14),
                    "Silver Birch"),
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
                            0.14),
                    "Norway Spruce"),
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
                            0.3,
                            "cherry",
                            "straight",
                            "cube",
                            0.05,
                            0.14),
                    "Wild Cherry"),
    // Mod species
    ALDER = new TreeSpecies("alder",
            new ResourceLocation(BreedMod.MODID, "alder_log"),
            new ResourceLocation(BreedMod.MODID, "g_birch_leaves"),
            2,
            new TreeGene(1,
                    4,
                    0.6,
                    "alder",
                    "straight",
                    "blob",
                    0.05,
                    0.14)),
            HAZEL = new TreeSpecies("hazel",
                    new ResourceLocation(BreedMod.MODID, "alder_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(1,
                            3,
                            1,
                            "hazel",
                            "straight",
                            "sphere",
                            0.05,
                            0.14)),
            HORNBEAM = new TreeSpecies("hornbeam",
                    new ResourceLocation(BreedMod.MODID, "alder_log"),
                    new ResourceLocation(BreedMod.MODID, "g_birch_leaves"),
                    2,
                    new TreeGene(2,
                            4,
                            0.5,
                            "hornbeam",
                            "hourglass",
                            "sphere",
                            0.05,
                            0.14)),
            WHITE_OAK = new TreeSpecies("white_oak",
                    new ResourceLocation("oak_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(3,
                            8,
                            1,
                            "white_oak",
                            "straight",
                            "sphere",
                            0.05,
                            0.14)),
            BEECH = new TreeSpecies("beech",
                    new ResourceLocation("birch_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(2,
                            4,
                            1,
                            "beech",
                            "straight",
                            "blob",
                            0.05,
                            0.14)),
            WALNUT = new TreeSpecies("walnut",
                    new ResourceLocation(BreedMod.MODID, "walnut_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(1,
                            8,
                            1,
                            "walnut",
                            "branch",
                            "blob",
                            0.05,
                            0.14),
                    "Black Walnut"),
            HICKORY = new TreeSpecies("hickory",
                    new ResourceLocation(BreedMod.MODID, "walnut_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(1,
                            6,
                            1,
                            "hickory",
                            "branch",
                            "cube",
                            0.05,
                            0.14),
                    "Shagbark Hickory"),
            PECAN = new TreeSpecies("pecan",
                    new ResourceLocation(BreedMod.MODID, "walnut_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(1,
                            7,
                            0.5,
                            "walnut",
                            "branch",
                            "cube",
                            0.05,
                            0.14)),
            APPLE = new TreeSpecies("apple",
                    new ResourceLocation(BreedMod.MODID, "apple_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            5,
                            0.5,
                            "apple",
                            "straight",
                            "blob",
                            "minecraft:apple",
                            0.005,
                            0.05,
                            0.14)),
            PEAR = new TreeSpecies("pear",
                    new ResourceLocation(BreedMod.MODID, "apple_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            5,
                            0.5,
                            "pear",
                            "straight",
                            "cone",
                            "minecraft:apple",
                            0.005,
                            0.05,
                            0.14)),
            QUINCE = new TreeSpecies("quince",
                    new ResourceLocation(BreedMod.MODID, "apple_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            5,
                            0.8,
                            "quince",
                            "straight",
                            "blob",
                            "minecraft:apple",
                            0.005,
                            0.05,
                            0.14)),
            PLUM = new TreeSpecies("plum",
                    new ResourceLocation(BreedMod.MODID, "plum_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            4,
                            0.8,
                            "plum",
                            "straight",
                            "flat",
                            0.05,
                            0.14)),
            ALMOND = new TreeSpecies("almond",
                    new ResourceLocation(BreedMod.MODID, "plum_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(1,
                            4,
                            0.8,
                            "almond",
                            "straight",
                            "flat",
                            0.05,
                            0.14)),
            PEACH = new TreeSpecies("peach",
                    new ResourceLocation(BreedMod.MODID, "plum_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            4,
                            0.8,
                            "peach",
                            "straight",
                            "sphere",
                            0.05,
                            0.14)),
            APRICOT = new TreeSpecies("apricot",
                    new ResourceLocation(BreedMod.MODID, "plum_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            4,
                            0.8,
                            "apricot",
                            "straight",
                            "sphere",
                            0.05,
                            0.14)),
            YEW = new TreeSpecies("yew",
                    new ResourceLocation(BreedMod.MODID, "pine_log"),
                    new ResourceLocation(BreedMod.MODID, "g_spruce_leaves"),
                    2,
                    new TreeGene(2,
                            2,
                            0.5,
                            "yew",
                            "rotten",
                            "blob",
                            0.05,
                            0.14)),
            MYRTLE = new TreeSpecies("myrtle",
                    new ResourceLocation("mangrove_log"),
                    new ResourceLocation(BreedMod.MODID, "g_cherry_leaves"),
                    2,
                    new TreeGene(1,
                            3,
                            0.7,
                            "myrtle",
                            "straight",
                            "cone",
                            0.05,
                            0.14)),
            HEMLOCK = new TreeSpecies("hemlock",
                    new ResourceLocation(BreedMod.MODID, "pine_log"),
                    new ResourceLocation(BreedMod.MODID, "g_spruce_leaves"),
                    2,
                    new TreeGene(1,
                            5,
                            2,
                            "hemlock",
                            "branch",
                            "cone",
                            0.05,
                            0.14)),
            FIR = new TreeSpecies("fir",
                    new ResourceLocation(BreedMod.MODID, "pine_log"),
                    new ResourceLocation(BreedMod.MODID, "g_spruce_leaves"),
                    2,
                    new TreeGene(2,
                            5,
                            2,
                            "fir",
                            "branch",
                            "cone",
                            0.05,
                            0.14)),
            SUMAC = new TreeSpecies("sumac",
                    new ResourceLocation(BreedMod.MODID, "sumac_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            4,
                            2,
                            "sumac",
                            "branch",
                            "blob",
                            0.05,
                            0.14),
                    "Staghorn Sumac"),
            CASHEW = new TreeSpecies("cashew",
                    new ResourceLocation(BreedMod.MODID, "sumac_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(1,
                            3,
                            0.4,
                            "cashew",
                            "cross",
                            "flat",
                            0.05,
                            0.14)),
            PISTACHIO = new TreeSpecies("pistachio",
                    new ResourceLocation(BreedMod.MODID, "sumac_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(2,
                            3,
                            0.7,
                            "pistachio",
                            "rotten",
                            "cone",
                            0.05,
                            0.14)),
            MANGO = new TreeSpecies("mango",
                    new ResourceLocation(BreedMod.MODID, "sumac_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            4,
                            1,
                            "mango",
                            "straight",
                            "blob",
                            0.05,
                            0.14)),
            MAPLE = new TreeSpecies("maple",
                    new ResourceLocation(BreedMod.MODID, "maple_log"),
                    new ResourceLocation(BreedMod.MODID, "red_maple_leaves"),
                    2,
                    new TreeGene(2,
                            5,
                            1.5,
                            "maple",
                            "runner",
                            "blob",
                            0.05,
                            0.14),
                    "Sugar Maple"),
            HORSE_CHESTNUT = new TreeSpecies("horse_chestnut",
                    new ResourceLocation(BreedMod.MODID, "maple_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(1,
                            7,
                            0.3,
                            "horse_chestnut",
                            "branch",
                            "sphere",
                            0.05,
                            0.14)),
            LYCHEE = new TreeSpecies("lychee",
                    new ResourceLocation(BreedMod.MODID, "maple_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(2,
                            4,
                            0.5,
                            "lychee",
                            "spiral",
                            "flat",
                            0.05,
                            0.14)),
            RAMBUTAN = new TreeSpecies("rambutan",
                    new ResourceLocation(BreedMod.MODID, "maple_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(2,
                            4,
                            0.5,
                            "rambutan",
                            "spiral",
                            "flat",
                            0.05,
                            0.14)),
            ACKEE = new TreeSpecies("ackee",
                    new ResourceLocation(BreedMod.MODID, "maple_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            4,
                            0.5,
                            "ackee",
                            "branch",
                            "flat",
                            0.05,
                            0.14)),
            CLOVE = new TreeSpecies("clove",
                    new ResourceLocation("mangrove_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    1,
                    new TreeGene(1,
                            4,
                            0.8,
                            "clove",
                            "straight",
                            "blob",
                            0.05,
                            0.14)),
            ALLSPICE = new TreeSpecies("allspice",
                    new ResourceLocation("mangrove_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    1,
                    new TreeGene(1,
                            4,
                            0.8,
                            "allspice",
                            "straight",
                            "blob",
                            0.05,
                            0.14)),
            POMEGRANATE = new TreeSpecies("pomegranate",
                    new ResourceLocation("mangrove_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    1,
                    new TreeGene(1,
                            2,
                            1,
                            "pomegranate",
                            "straight",
                            "flat",
                            0.05,
                            0.14)),
            EUCALYPTUS = new TreeSpecies("eucalyptus",
                    new ResourceLocation(BreedMod.MODID, "eucalyptus_log"),
                    new ResourceLocation(BreedMod.MODID, "g_oak_leaves"),
                    2,
                    new TreeGene(3,
                            6,
                            0.8,
                            "eucalyptus",
                            "hollow",
                            "flat",
                            0.05,
                            0.14)),
            MIMOSA = new TreeSpecies("mimosa",
                    new ResourceLocation(BreedMod.MODID, "carob_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(1,
                            4,
                            1,
                            "mimosa",
                            "straight",
                            "blob",
                            0.05,
                            0.14)),
            CAROB = new TreeSpecies("carob",
                    new ResourceLocation(BreedMod.MODID, "carob_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(2,
                            6,
                            0.8,
                            "carob",
                            "spiral",
                            "blob",
                            0.05,
                            0.14)),
            BLACK_LOCUST = new TreeSpecies("black_locust",
                    new ResourceLocation(BreedMod.MODID, "carob_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(2,
                            8,
                            0.4,
                            "black_locust",
                            "straight",
                            "blob",
                            0.05,
                            0.14)),
            CACAO = new TreeSpecies("cacao",
                    new ResourceLocation("jungle_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(4,
                            4,
                            0.8,
                            "cacao",
                            "hourglass",
                            "blob",
                            0.05,
                            0.14)),
            DURIAN = new TreeSpecies("durian",
                    new ResourceLocation("jungle_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(2,
                            6,
                            0.8,
                            "durian",
                            "runner",
                            "blob",
                            0.05,
                            0.14)),
            EBONY = new TreeSpecies("ebony",
                    new ResourceLocation(BreedMod.MODID, "ebony_log"),
                    new ResourceLocation(BreedMod.MODID, "nut_leaves"),
                    2,
                    new TreeGene(4,
                            4,
                            1,
                            "ebony",
                            "spiral",
                            "blob",
                            0.05,
                            0.14)),
            PERSIMMON = new TreeSpecies("persimmon",
                    new ResourceLocation(BreedMod.MODID, "ebony_log"),
                    new ResourceLocation(BreedMod.MODID, "apple_leaves"),
                    2,
                    new TreeGene(1,
                            6,
                            0.3,
                            "persimmon",
                            "straight",
                            "sphere",
                            0.05,
                            0.14));

    public static void registerPair(TreeSpecies a, TreeSpecies b, TreeSpecies child, float chance) {
        child.parent1 = a.id;
        child.parent2 = b.id;
        child.hybridChance = chance;
    }

    static {
        registerPair(SPRUCE, DARK_OAK, PINE, 0.5f);
        registerPair(OAK, BIRCH, ALDER, 0.5f);
        registerPair(ALDER, DARK_OAK, HAZEL, 0.5f);
        registerPair(HAZEL, ALDER, HORNBEAM, 0.5f);
        registerPair(OAK, DARK_OAK, WHITE_OAK, 0.5f);
        registerPair(WHITE_OAK, BIRCH, BEECH, 0.5f);
        registerPair(BEECH, HORNBEAM, WALNUT, 0.5f);
        registerPair(WALNUT, BEECH, HICKORY, 0.5f);
        registerPair(HICKORY, WALNUT, PECAN, 0.5f);
        registerPair(CHERRY, OAK, APPLE, 0.5f);
        registerPair(APPLE, DARK_OAK, PEAR, 0.5f);
        registerPair(PEAR, APPLE, QUINCE, 0.5f);
        registerPair(CHERRY, APPLE, PLUM, 0.5f);
        registerPair(PLUM, PINE, ALMOND, 0.5f);
        registerPair(ALMOND, PLUM, PEACH, 0.5f);
        registerPair(PEACH, PLUM, APRICOT, 0.5f);
        registerPair(MANGROVE, CHERRY, MYRTLE, 0.5f);
        registerPair(MYRTLE, PINE, YEW, 0.5f);
        registerPair(PINE, SPRUCE, HEMLOCK, 0.5f);
        registerPair(HEMLOCK, PINE, FIR, 0.5f);
        registerPair(PEAR, ALMOND, CASHEW, 0.5f);
        registerPair(CASHEW, ALMOND, PISTACHIO, 0.5f);
        registerPair(CASHEW, OAK, SUMAC, 0.5f);
        registerPair(SUMAC, CASHEW, MANGO, 0.5f);
        registerPair(WALNUT, BIRCH, MAPLE, 0.5f);
        registerPair(MAPLE, HICKORY, HORSE_CHESTNUT, 0.5f);
        registerPair(MAPLE, MANGO, LYCHEE, 0.5f);
        registerPair(LYCHEE, HAZEL, RAMBUTAN, 0.5f);
        registerPair(RAMBUTAN, YEW, ACKEE, 0.5f);
        registerPair(MYRTLE, SPRUCE, CLOVE, 0.5f);
        registerPair(CLOVE, MYRTLE, ALLSPICE, 0.5f);
        registerPair(MANGROVE, MIMOSA, POMEGRANATE, 0.5f);
        registerPair(POMEGRANATE, ALLSPICE, EUCALYPTUS, 0.5f);
        registerPair(MANGROVE, ACACIA, MIMOSA, 0.5f);
        registerPair(MIMOSA, CACAO, CAROB, 0.5f);
        registerPair(CAROB, MIMOSA, BLACK_LOCUST, 0.5f);
        registerPair(JUNGLE, ACACIA, CACAO, 0.5f);
        registerPair(CACAO, JUNGLE, DURIAN, 0.5f);
        registerPair(BLACK_LOCUST, EUCALYPTUS, EBONY, 0.5f);
        registerPair(APRICOT, EBONY, PERSIMMON, 0.5f);
    }

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

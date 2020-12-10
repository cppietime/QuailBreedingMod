package com.funguscow.crossbreed.entity;

import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.util.RandomPool;
import com.funguscow.crossbreed.util.UnorderedPair;

import java.util.*;
import java.util.stream.Collectors;

public class QuailType {
    public static Map<String, QuailType> Types = new HashMap<>();
    public static Map<UnorderedPair<String>, RandomPool<String>> Pairings = new HashMap<>();

    public static void registerPair(String mother, String father, String child, float chance){
        RandomPool<String> children = Pairings.computeIfAbsent(new UnorderedPair<>(mother, father), pair -> new RandomPool<>((String)null));
        children.add(child, chance);
    }

    public static void registerPair(QuailType mother, QuailType father, QuailType child, float chance){
        RandomPool<String> children = Pairings.computeIfAbsent(new UnorderedPair<>(mother.name, father.name), pair -> new RandomPool<>((String)null));
        children.add(child.name, chance);
    }

    public String name;
    public String layItem;
    public String deathItem;
    public int deathAmount;
    public int layAmount;
    public int layRandomAmount;
    public int layTime;

    public QuailType(String name, String itemID, int amt, int rAmt, int time, String die, int dieAmt){
        this.name = name;
        layItem = itemID;
        layAmount = amt;
        layRandomAmount = rAmt;
        layTime = time;
        deathItem = die;
        deathAmount = dieAmt;
        Types.put(this.name, this);
    }

    public QuailType(String name, String itemID, int amt, int rAmt, int time){
        this(name, itemID, amt, rAmt, time, "", 0);
    }

    public QuailType getOffspring(QuailType other, Random rand){
        UnorderedPair<String> pair = new UnorderedPair<>(name, other.name);
        RandomPool<String> pool = Pairings.getOrDefault(pair, null);
        QuailType result = pool != null ? Types.get(pool.get(rand.nextFloat())) : null;
        return result != null ? result : rand.nextBoolean() ? this : other;
    }

    public String toString(){
        return name;
    }

    public static void matchConfig(){
        for(Map.Entry<String, QuailType> entry : Types.entrySet()){
            QuailType type = entry.getValue();
            String key = entry.getKey();
            QuailConfig.Common.QuailTypeConfig configType = QuailConfig.COMMON.quailTypes.get(key);
            type.layAmount = configType.amount.get();
            type.layRandomAmount = configType.amountRand.get();
            type.layTime = configType.time.get();
            type.deathAmount = configType.onDieAmount.get();
            type.layItem = configType.dropItem.get();
            type.deathItem = configType.deathItem.get();
        }
        registerPairs();
    }

    public static final QuailType
            // Tier 0 / wild-type
            PAINTED = new QuailType("painted", "breesources:quail_egg", 1, 0, 6000),
            BOBWHITE = new QuailType("bobwhite", "breesources:quail_egg", 1, 0, 6000),
            BROWN = new QuailType("brown", "breesources:quail_egg", 1, 0, 6000),
            ELEGANT = new QuailType("elegant", "breesources:quail_egg", 1, 0, 6000),

            // Tier 1
            GRAVEL = new QuailType("gravel", "minecraft:gravel", 1, 0, 600, "minecraft:flint", 2),
            DIRT = new QuailType("dirt", "minecraft:dirt", 1, 0, 600),
            SAND = new QuailType("sand", "minecraft:sand", 1, 0, 600, "minecraft:kelp", 2),
            NETHERRACK = new QuailType("netherrack", "minecraft:netherrack", 1, 0, 600),
            CLAY = new QuailType("clay", "minecraft:clay", 3, 4, 600),
            COBBLE = new QuailType("cobble", "minecraft:cobblestone", 1, 0, 600, "minecraft:smooth_stone", 2),

            // Tier 2
            OAK = new QuailType("oak", "minecraft:oak_log", 1, 0, 1200, "minecraft:oak_sapling", 1),
            SPRUCE = new QuailType("spruce", "minecraft:spruce_log", 1, 0, 1200, "minecraft:spruce_sapling", 1),
            BIRCH = new QuailType("birch", "minecraft:birch_log", 1, 0, 1200, "minecraft:birch_sapling", 1),
            JUNGLE = new QuailType("jungle", "minecraft:jungle_log", 1, 0, 1200, "minecraft:jungle_sapling", 1),
            ACACIA = new QuailType("acacia", "minecraft:acacia_log", 1, 0, 1200, "minecraft:acacia_sapling", 1),
            DARK_OAK = new QuailType("dark_oak", "minecraft:dark_oak_log", 1, 0, 1200, "minecraft:dark_oak_sapling", 1),
            COAL = new QuailType("coal", "minecraft:coal", 1, 0, 1200),
            QUARTZ = new QuailType("quartz", "minecraft:quartz", 2, 3, 1200),
            APPLE = new QuailType("apple", "minecraft:apple", 1, 0, 1200),
            REEDS = new QuailType("reeds", "minecraft:sugar_cane", 1, 2, 1200, "minecraft:bamboo", 4),
            FEATHER = new QuailType("feather", "minecraft:feather", 2, 3, 1200, "minecraft:chicken", 1),
            STRING = new QuailType("string", "minecraft:string", 2, 3, 1200, "minecraft:mutton", 1),

            // Tier 3
            BONE = new QuailType("bone", "minecraft:bone", 1, 0, 2400),
            COCOA = new QuailType("cocoa", "minecraft:cocoa_beans", 2, 3, 2400),
            INK = new QuailType("ink", "minecraft:ink_sack", 1, 0, 2400),
            BEET = new QuailType("beet", "minecraft:beetroot", 1, 0, 2400, "minecraft:beetroot_seeds", 1),
            CACTUS = new QuailType("cactus", "minecraft:cactus", 1, 0, 2400),
            LAPIS = new QuailType("lapis", "minecraft:lapis_lazuli", 1, 2, 2400),
            FLOWER = new QuailType("flower", "FLOWERS", 1, 0, 2400, "minecraft:rabbit", 1),
            IRON = new QuailType("iron", "minecraft:iron_ingot", 1, 0, 2400, "minecraft:poppy", 1),
            REDSTONE = new QuailType("redstone", "minecraft:redstone", 1, 2, 2400),
            SOULSAND = new QuailType("soulsand", "minecraft:soul_sand", 1, 0, 2400),
            WHEAT = new QuailType("wheat", "minecraft:wheat", 1, 0, 2400, "minecraft:wheat_seeds", 1),
            MELON = new QuailType("melon", "minecraft:melon_slice", 2, 3, 2400),
            PUMPKIN = new QuailType("pumpkin", "minecraft:pumpkin", 1, 0, 3600),
            POTATO = new QuailType("potato", "minecraft:potato", 1, 0, 2400, "minecraft:porkchop", 1),
            CARROT = new QuailType("carrot", "minecraft:carrot", 1, 0, 2400, "minecraft:rabbit", 1),
            WATER = new QuailType("water", "breesources:water_bubble", 1, 0, 2400),
            LEATHER = new QuailType("leather", "minecraft:leather", 1, 0, 2400, "minecraft:beef", 1),
            TERRACOTTA = new QuailType("terracotta", "minecraft:terracotta", 1, 0, 2400),
            SNOWBALL = new QuailType("snowball", "minecraft:snowball", 1, 2, 2000, "minecraft:sweet_berries", 1),

            // Tier 4
            GRASS = new QuailType("grass", "minecraft:grass_block", 1, 0, 6000),
            REDSHROOM = new QuailType("redshroom", "minecraft:red_mushroom", 1, 0, 6000),
            BROWNSHROOM = new QuailType("brownshroom", "minecraft:brown_mushroom", 1, 0, 6000),
            ENDSTONE = new QuailType("endstone", "minecraft:end_stone", 1, 0, 6000, "minecraft:purpur_block", 1),
            GOLD = new QuailType("gold", "minecraft:gold_ingot", 1, 0, 6000),
            LAVA = new QuailType("lava", "breesources:lava_bubble", 1, 0, 6000),
            GUNPOWDER = new QuailType("gunpowder", "minecraft:gunpowder", 1, 0, 6000),
            SPIDEREYE = new QuailType("spidereye", "minecraft:spider_eye", 1, 0, 6000),
            SLIME = new QuailType("slime", "minecraft:slime_balls", 1, 0, 6000),
            WART = new QuailType("wart", "minecraft:nether_wart", 1, 0, 6000),
            GLASS = new QuailType("glass", "minecraft:glass", 1, 0, 6000),
            BASALT = new QuailType("basalt", "minecraft:basalt", 1, 0, 6000),
            ICE = new QuailType("ice", "minecraft:ice", 1, 0, 6000),
            GLOWSTONE = new QuailType("glowstone", "minecraft:glowstone_dust", 3, 4, 6000),
            FISH = new QuailType("fish", "FISH", 1, 2, 6000),
            RABBIT = new QuailType("rabbit", "minecraft:rabbit_hide", 1, 2, 6000, "minecraft:rabbit_foot", 1),
            TURTLE = new QuailType("turtle", "minecraft:scute", 1, 2, 10000, "minecraft:turtle_egg", 1),

            // Tier 5
            EMERALD = new QuailType("emerald", "minecraft:emerald", 1, 0, 24000),
            OBSIDIAN = new QuailType("obsidian", "minecraft:obsidian", 1, 0, 12000),
            BLAZE = new QuailType("blaze", "minecraft:blaze_rod", 1, 0, 12000),
            WARPED_NYL = new QuailType("warped_nyl", "minecraft:warped_nylium", 1, 0, 12000),
            CRIMSON_NYL = new QuailType("crimson_nyl", "minecraft:crimson_nylium", 1, 0, 12000),
            MYCELIUM = new QuailType("mycelium", "minecraft:mycelium", 1, 0, 12000),
            HONEY = new QuailType("honey", "minecraft:honey_bottle", 1, 0, 12000, "minecraft:honeycomb", 1),
            GHAST = new QuailType("ghast", "minecraft:ghast_tear", 1, 0, 16000),
            BLACKSTONE = new QuailType("blackstone", "minecraft:blackstone", 1, 0, 12000, "minecraft:gilded_blackstone", 1),
            CORAL = new QuailType("coral", "CORALS", 1, 0, 12000, "minecraft:sponge", 1),
            PACKED_ICE = new QuailType("packed_ice", "minecraft:packed_ice", 1, 0, 12000),

            // Tier 6
            DIAMOND = new QuailType("diamond", "minecraft:diamond", 1, 0, 48000),
            PEARL = new QuailType("pearl", "minecraft:ender_pearl", 1, 0, 24000),
            SHULKER = new QuailType("shulker", "minecraft:shulker_shell", 1, 0, 48000),
            NAUTILUS = new QuailType("nautilus", "minecraft:nautilus_shell", 1, 0, 24000, "minecraft:trident", 1),
            PRISM = new QuailType("prism", "minecraft:prismarine_shard", 2, 3, 24000, "minecraft:prismarine_crystals", 2),
            MEMBRANE = new QuailType("membrane", "minecraft:phantom_membrane", 1, 0, 24000, "minecraft:elytra", 1),
            WITHER_ROSE = new QuailType("wither_rose", "minecraft:wither_rose", 1, 0, 30000, "minecraft:wither_skeleton_skull", 1),
            CHORUS = new QuailType("chorus", "minecraft:chorus_fruit", 3, 4, 24000, "minecraft:chorus_flower", 1),
            BLUE_ICE = new QuailType("blue_ice", "minecraft:blue_ice", 1, 0, 24000),
            WARPED_STEM = new QuailType("warped_stem", "minecraft:warped_stem", 1, 0, 24000, "minecraft:shroomlight", 1),
            CRIMSON_STEM = new QuailType("crimson_stem", "minecraft:crimson_stem", 1, 0, 24000, "minecraft:shroomlight", 1),

            // Tier 7
            WITHER_STAR = new QuailType("wither_star", "minecraft:wither_star", 1, 0, 144000),
            HEART_OF_SEA = new QuailType("heart_of_sea", "minecraft:heart_of_the_sea", 1, 0, 72000),
            DEBRIS = new QuailType("debris", "minecraft:ancient_debris", 1, 0, 72000),
            DRAGON = new QuailType("dragon", "minecraft:dragon_breath", 1, 0, 48000, "minecraft:dragon_head", 1),
            BOOK = new QuailType("book", "minecraft:book", 1, 0, 48000, "minecraft:experience_bottle", 5),

            // Primary dyes
            WHITE_DYE = new QuailType("white_dye", "minecraft:white_dye", 2, 4, 2400),
            BLACK_DYE = new QuailType("black_dye", "minecraft:black_dye", 2, 4, 2400),
            RED_DYE = new QuailType("red_dye", "minecraft:red_dye", 2, 4, 2400),
            GREEN_DYE = new QuailType("green_dye", "minecraft:green_dye", 2, 4, 2400),
            BLUE_DYE = new QuailType("blue_dye", "minecraft:blue_dye", 2, 4, 2400),
            YELLOW_DYE = new QuailType("yellow_dye", "minecraft:yellow_dye", 2, 4, 2400),
            BROWN_DYE = new QuailType("brown_dye", "minecraft:brown_dye", 2, 4, 2400),

            // Secondary dyes
            GRAY_DYE = new QuailType("gray_dye", "minecraft:gray_dye", 2, 4, 2400),
            PINK_DYE = new QuailType("pink_dye", "minecraft:pink_dye", 2, 4, 2400),
            LIME_DYE = new QuailType("lime_dye", "minecraft:lime_dye", 2, 4, 2400),
            LIGHT_BLUE_DYE = new QuailType("light_blue_dye", "minecraft:light_blue_dye", 2, 4, 2400),
            PURPLE_DYE = new QuailType("purple_dye", "minecraft:purple_dye", 2, 4, 2400),
            CYAN_DYE  = new QuailType("cyan_dye", "minecraft:cyan_dye", 2, 4, 2400),
            ORANGE_DYE = new QuailType("orange_dye", "minecraft:orange_dye", 2, 4, 2400),

            // Tertiary dyes
            LIGHT_GRAY_DYE = new QuailType("light_gray_dye", "minecraft:light_gray_dye", 2, 4, 2400),
            MAGENTA_DYE = new QuailType("magenta_dye", "minecraft:magenta_dye", 2, 4, 2400),
    
            // Concrete powders
            WHITE_CONCRETE_POWDER = new QuailType("white_concrete_powder", "minecraft:white_concrete_powder", 1, 0, 1200),
            BLACK_CONCRETE_POWDER = new QuailType("black_concrete_powder", "minecraft:black_concrete_powder", 1, 0, 1200),
            GRAY_CONCRETE_POWDER = new QuailType("gray_concrete_powder", "minecraft:gray_concrete_powder", 1, 0, 1200),
            LIGHT_GRAY_CONCRETE_POWDER = new QuailType("light_gray_concrete_powder", "minecraft:light_gray_concrete_powder", 1, 0, 1200),
            RED_CONCRETE_POWDER = new QuailType("red_concrete_powder", "minecraft:red_concrete_powder", 1, 0, 1200),
            GREEN_CONCRETE_POWDER = new QuailType("green_concrete_powder", "minecraft:green_concrete_powder", 1, 0, 1200),
            BLUE_CONCRETE_POWDER = new QuailType("blue_concrete_powder", "minecraft:blue_concrete_powder", 1, 0, 1200),
            YELLOW_CONCRETE_POWDER = new QuailType("yellow_concrete_powder", "minecraft:yellow_concrete_powder", 1, 0, 1200),
            BROWN_CONCRETE_POWDER = new QuailType("brown_concrete_powder", "minecraft:brown_concrete_powder", 1, 0, 1200),
            PINK_CONCRETE_POWDER = new QuailType("pink_concrete_powder", "minecraft:pink_concrete_powder", 1, 0, 1200),
            ORANGE_CONCRETE_POWDER = new QuailType("orange_concrete_powder", "minecraft:orange_concrete_powder", 1, 0, 1200),
            PURPLE_CONCRETE_POWDER = new QuailType("purple_concrete_powder", "minecraft:purple_concrete_powder", 1, 0, 1200),
            MAGENTA_CONCRETE_POWDER = new QuailType("magenta_concrete_powder", "minecraft:magenta_concrete_powder", 1, 0, 1200),
            LIME_CONCRETE_POWDER = new QuailType("lime_concrete_powder", "minecraft:lime_concrete_powder", 1, 0, 1200),
            CYAN_CONCRETE_POWDER = new QuailType("cyan_concrete_powder", "minecraft:cyan_concrete_powder", 1, 0, 1200),
            LIGHT_BLUE_CONCRETE_POWDER = new QuailType("light_blue_concrete_powder", "minecraft:light_blue_concrete_powder", 1, 0, 1200),
    
            //Concrete blocks
            WHITE_CONCRETE = new QuailType("white_concrete", "minecraft:white_concrete", 1, 0, 2400),
            BLACK_CONCRETE = new QuailType("black_concrete", "minecraft:black_concrete", 1, 0, 2400),
            GRAY_CONCRETE = new QuailType("gray_concrete", "minecraft:gray_concrete", 1, 0, 2400),
            LIGHT_GRAY_CONCRETE = new QuailType("light_gray_concrete", "minecraft:light_gray_concrete", 1, 0, 2400),
            RED_CONCRETE = new QuailType("red_concrete", "minecraft:red_concrete", 1, 0, 2400),
            GREEN_CONCRETE = new QuailType("green_concrete", "minecraft:green_concrete", 1, 0, 2400),
            BLUE_CONCRETE = new QuailType("blue_concrete", "minecraft:blue_concrete", 1, 0, 2400),
            YELLOW_CONCRETE = new QuailType("yellow_concrete", "minecraft:yellow_concrete", 1, 0, 2400),
            BROWN_CONCRETE = new QuailType("brown_concrete", "minecraft:brown_concrete", 1, 0, 2400),
            PINK_CONCRETE = new QuailType("pink_concrete", "minecraft:pink_concrete", 1, 0, 2400),
            ORANGE_CONCRETE = new QuailType("orange_concrete", "minecraft:orange_concrete", 1, 0, 2400),
            PURPLE_CONCRETE = new QuailType("purple_concrete", "minecraft:purple_concrete", 1, 0, 2400),
            MAGENTA_CONCRETE = new QuailType("magenta_concrete", "minecraft:magenta_concrete", 1, 0, 2400),
            LIME_CONCRETE = new QuailType("lime_concrete", "minecraft:lime_concrete", 1, 0, 2400),
            CYAN_CONCRETE = new QuailType("cyan_concrete", "minecraft:cyan_concrete", 1, 0, 2400),
            LIGHT_BLUE_CONCRETE = new QuailType("light_blue_concrete", "minecraft:light_blue_concrete", 1, 0, 2400),

            //Wool blocks
            WHITE_WOOL = new QuailType("white_wool", "minecraft:white_wool", 1, 0, 2400),
            BLACK_WOOL = new QuailType("black_wool", "minecraft:black_wool", 1, 0, 2400),
            GRAY_WOOL = new QuailType("gray_wool", "minecraft:gray_wool", 1, 0, 2400),
            LIGHT_GRAY_WOOL = new QuailType("light_gray_wool", "minecraft:light_gray_wool", 1, 0, 2400),
            RED_WOOL = new QuailType("red_wool", "minecraft:red_wool", 1, 0, 2400),
            GREEN_WOOL = new QuailType("green_wool", "minecraft:green_wool", 1, 0, 2400),
            BLUE_WOOL = new QuailType("blue_wool", "minecraft:blue_wool", 1, 0, 2400),
            YELLOW_WOOL = new QuailType("yellow_wool", "minecraft:yellow_wool", 1, 0, 2400),
            BROWN_WOOL = new QuailType("brown_wool", "minecraft:brown_wool", 1, 0, 2400),
            PINK_WOOL = new QuailType("pink_wool", "minecraft:pink_wool", 1, 0, 2400),
            ORANGE_WOOL = new QuailType("orange_wool", "minecraft:orange_wool", 1, 0, 2400),
            PURPLE_WOOL = new QuailType("purple_wool", "minecraft:purple_wool", 1, 0, 2400),
            MAGENTA_WOOL = new QuailType("magenta_wool", "minecraft:magenta_wool", 1, 0, 2400),
            LIME_WOOL = new QuailType("lime_wool", "minecraft:lime_wool", 1, 0, 2400),
            CYAN_WOOL = new QuailType("cyan_wool", "minecraft:cyan_wool", 1, 0, 2400),
            LIGHT_BLUE_WOOL = new QuailType("light_blue_wool", "minecraft:light_blue_wool", 1, 0, 2400),

            //Terracotta blocks
            WHITE_TERRACOTTA = new QuailType("white_terracotta", "minecraft:white_terracotta", 1, 0, 4800),
            BLACK_TERRACOTTA = new QuailType("black_terracotta", "minecraft:black_terracotta", 1, 0, 4800),
            GRAY_TERRACOTTA = new QuailType("gray_terracotta", "minecraft:gray_terracotta", 1, 0, 4800),
            LIGHT_GRAY_TERRACOTTA = new QuailType("light_gray_terracotta", "minecraft:light_gray_terracotta", 1, 0, 4800),
            RED_TERRACOTTA = new QuailType("red_terracotta", "minecraft:red_terracotta", 1, 0, 4800),
            GREEN_TERRACOTTA = new QuailType("green_terracotta", "minecraft:green_terracotta", 1, 0, 4800),
            BLUE_TERRACOTTA = new QuailType("blue_terracotta", "minecraft:blue_terracotta", 1, 0, 4800),
            YELLOW_TERRACOTTA = new QuailType("yellow_terracotta", "minecraft:yellow_terracotta", 1, 0, 4800),
            BROWN_TERRACOTTA = new QuailType("brown_terracotta", "minecraft:brown_terracotta", 1, 0, 4800),
            PINK_TERRACOTTA = new QuailType("pink_terracotta", "minecraft:pink_terracotta", 1, 0, 4800),
            ORANGE_TERRACOTTA = new QuailType("orange_terracotta", "minecraft:orange_terracotta", 1, 0, 4800),
            PURPLE_TERRACOTTA = new QuailType("purple_terracotta", "minecraft:purple_terracotta", 1, 0, 4800),
            MAGENTA_TERRACOTTA = new QuailType("magenta_terracotta", "minecraft:magenta_terracotta", 1, 0, 4800),
            LIME_TERRACOTTA = new QuailType("lime_terracotta", "minecraft:lime_terracotta", 1, 0, 4800),
            CYAN_TERRACOTTA = new QuailType("cyan_terracotta", "minecraft:cyan_terracotta", 1, 0, 4800),
            LIGHT_BLUE_TERRACOTTA = new QuailType("light_blue_terracotta", "minecraft:light_blue_terracotta", 1, 0, 4800),

            //Glass blocks
            WHITE_GLASS = new QuailType("white_glass", "minecraft:white_glass", 1, 0, 12000),
            BLACK_GLASS = new QuailType("black_glass", "minecraft:black_glass", 1, 0, 12000),
            GRAY_GLASS = new QuailType("gray_glass", "minecraft:gray_glass", 1, 0, 12000),
            LIGHT_GRAY_GLASS = new QuailType("light_gray_glass", "minecraft:light_gray_glass", 1, 0, 12000),
            RED_GLASS = new QuailType("red_glass", "minecraft:red_glass", 1, 0, 12000),
            GREEN_GLASS = new QuailType("green_glass", "minecraft:green_glass", 1, 0, 12000),
            BLUE_GLASS = new QuailType("blue_glass", "minecraft:blue_glass", 1, 0, 12000),
            YELLOW_GLASS = new QuailType("yellow_glass", "minecraft:yellow_glass", 1, 0, 12000),
            BROWN_GLASS = new QuailType("brown_glass", "minecraft:brown_glass", 1, 0, 12000),
            PINK_GLASS = new QuailType("pink_glass", "minecraft:pink_glass", 1, 0, 12000),
            ORANGE_GLASS = new QuailType("orange_glass", "minecraft:orange_glass", 1, 0, 12000),
            PURPLE_GLASS = new QuailType("purple_glass", "minecraft:purple_glass", 1, 0, 12000),
            MAGENTA_GLASS = new QuailType("magenta_glass", "minecraft:magenta_glass", 1, 0, 12000),
            LIME_GLASS = new QuailType("lime_glass", "minecraft:lime_glass", 1, 0, 12000),
            CYAN_GLASS = new QuailType("cyan_glass", "minecraft:cyan_glass", 1, 0, 12000),
            LIGHT_BLUE_GLASS = new QuailType("light_blue_glass", "minecraft:light_blue_glass", 1, 0, 12000);

    public static void registerPairs() {
        List<Float> tiers = Arrays.stream(QuailConfig.COMMON.tierOdds)
                .map(val -> val.get().floatValue())
                .collect(Collectors.toList());
        registerPair(BROWN, PAINTED, GRAVEL, tiers.get(0));
        registerPair(BROWN, BOBWHITE, DIRT, tiers.get(0));
        registerPair(BROWN, ELEGANT, SAND, tiers.get(0));
        registerPair(PAINTED, BOBWHITE, NETHERRACK, tiers.get(0));
        registerPair(PAINTED, ELEGANT, CLAY, tiers.get(0));
        registerPair(BOBWHITE, ELEGANT, COBBLE, tiers.get(0));

        registerPair(ELEGANT, COBBLE, OAK, tiers.get(1));
        registerPair(ELEGANT, SAND, SPRUCE, tiers.get(1));
        registerPair(ELEGANT, DIRT, BIRCH, tiers.get(1));
        registerPair(ELEGANT, CLAY, JUNGLE, tiers.get(1));
        registerPair(ELEGANT, GRAVEL, ACACIA, tiers.get(1));
        registerPair(ELEGANT, NETHERRACK, DARK_OAK, tiers.get(1));
        registerPair(PAINTED, COBBLE, COAL, tiers.get(1));
        registerPair(BOBWHITE, NETHERRACK, QUARTZ, tiers.get(1));
        registerPair(BROWN, CLAY, APPLE, tiers.get(1));
        registerPair(PAINTED, SAND, REEDS, tiers.get(1));
        registerPair(BOBWHITE, DIRT, FEATHER, tiers.get(1));
        registerPair(BROWN, GRAVEL, STRING, tiers.get(1));

        registerPair(DIRT, COAL, BONE, tiers.get(2));
        registerPair(APPLE, JUNGLE, COCOA, tiers.get(2));
        registerPair(STRING, QUARTZ, LAPIS, tiers.get(2));
        registerPair(APPLE, SPRUCE, BEET, tiers.get(2));
        registerPair(SAND, STRING, CACTUS, tiers.get(2));
        registerPair(DIRT, FEATHER, FLOWER, tiers.get(2));
        registerPair(COAL, DARK_OAK, INK, tiers.get(2));
        registerPair(COAL, QUARTZ, IRON, tiers.get(2));
        registerPair(STRING, NETHERRACK, SOULSAND, tiers.get(2));
        registerPair(REEDS, OAK, WHEAT, tiers.get(2));
        registerPair(REEDS, JUNGLE, MELON, tiers.get(2));
        registerPair(REEDS, SPRUCE, PUMPKIN, tiers.get(2));
        registerPair(REEDS, BIRCH, POTATO, tiers.get(2));
        registerPair(REEDS, ACACIA, CARROT, tiers.get(2));
        registerPair(REEDS, SAND, WATER, tiers.get(2));
        registerPair(FEATHER, STRING, LEATHER, tiers.get(2));
        registerPair(CLAY, COAL, TERRACOTTA, tiers.get(2));
        registerPair(FEATHER, CLAY, SNOWBALL, tiers.get(2));

        registerPair(DIRT, FLOWER, GRASS, tiers.get(3));
        registerPair(CARROT, BEET, REDSHROOM, tiers.get(3));
        registerPair(POTATO, COCOA, BROWNSHROOM, tiers.get(3));
        registerPair(SOULSAND, IRON, ENDSTONE, tiers.get(3));
        registerPair(IRON, FLOWER, GOLD, tiers.get(3));
        registerPair(WATER, SOULSAND, LAVA, tiers.get(3));
        registerPair(COAL, MELON, GUNPOWDER, tiers.get(3));
        registerPair(PUMPKIN, LEATHER, SPIDEREYE, tiers.get(3));
        registerPair(MELON, CACTUS, SLIME, tiers.get(3));
        registerPair(WHEAT, SOULSAND, WART, tiers.get(3));
        registerPair(SAND, IRON, GLASS, tiers.get(3));
        registerPair(COBBLE, SOULSAND, BASALT, tiers.get(3));
        registerPair(WATER, SNOWBALL, ICE, tiers.get(3));
        registerPair(SOULSAND, REDSTONE, GLOWSTONE, tiers.get(3));
        registerPair(WATER, LEATHER, FISH, tiers.get(3));
        registerPair(SAND, GRASS, RABBIT, tiers.get(3));
        registerPair(SAND, FISH, TURTLE, tiers.get(3));

        registerPair(GOLD, SLIME, EMERALD, tiers.get(4));
        registerPair(WATER, LAVA, OBSIDIAN, tiers.get(4));
        registerPair(WATER, SPIDEREYE, BLAZE, tiers.get(4));
        registerPair(BROWNSHROOM, GRASS, WARPED_NYL, tiers.get(4));
        registerPair(REDSHROOM, GRASS, CRIMSON_NYL, tiers.get(4));
        registerPair(REDSHROOM, BROWNSHROOM, MYCELIUM, tiers.get(4));
        registerPair(SLIME, REEDS, HONEY, tiers.get(4));
        registerPair(WART, SPIDEREYE, GHAST, tiers.get(4));
        registerPair(BASALT, COAL, BLACKSTONE, tiers.get(4));
        registerPair(FISH, GLASS, CORAL, tiers.get(4));
        registerPair(ICE, GLASS, PACKED_ICE, tiers.get(4));

        registerPair(EMERALD, OBSIDIAN, DIAMOND, tiers.get(5));
        registerPair(BLAZE, OBSIDIAN, PEARL, tiers.get(5));
        registerPair(GHAST, BLAZE, SHULKER, tiers.get(5));
        registerPair(WATER, EMERALD, NAUTILUS, tiers.get(5));
        registerPair(WATER, OBSIDIAN, PRISM, tiers.get(5));
        registerPair(LEATHER, HONEY, MEMBRANE, tiers.get(5));
        registerPair(FLOWER, GHAST, WITHER_ROSE, tiers.get(5));
        registerPair(ENDSTONE, MYCELIUM, CHORUS, tiers.get(5));
        registerPair(PACKED_ICE, EMERALD, BLUE_ICE, tiers.get(5));
        registerPair(WARPED_NYL, BONE, WARPED_STEM, tiers.get(5));
        registerPair(CRIMSON_NYL, BONE, CRIMSON_STEM, tiers.get(5));

        registerPair(DIAMOND, WITHER_ROSE, WITHER_STAR, tiers.get(6));
        registerPair(NAUTILUS, PEARL, HEART_OF_SEA, tiers.get(6));
        registerPair(DIAMOND, BLACKSTONE, DEBRIS, tiers.get(6));
        registerPair(MEMBRANE, SHULKER, DRAGON, tiers.get(6));
        registerPair(DIAMOND, OBSIDIAN, BOOK, tiers.get(6));

        registerPair(WATER, BONE, WHITE_DYE, tiers.get(1));
        registerPair(WATER, INK, BLACK_DYE, tiers.get(1));
        registerPair(WATER, COCOA, BROWN_DYE, tiers.get(1));
        registerPair(WATER, CACTUS, GREEN_DYE, tiers.get(1));
        registerPair(WATER, LAPIS, BLUE_DYE, tiers.get(1));
        registerPair(WATER, BEET, RED_DYE, tiers.get(1));
        registerPair(WATER, FLOWER, YELLOW_DYE, tiers.get(1));
        registerPair(WHITE_DYE, BLACK_DYE, GRAY_DYE, tiers.get(1));
        registerPair(WHITE_DYE, RED_DYE, PINK_DYE, tiers.get(1));
        registerPair(WHITE_DYE, GREEN_DYE, LIME_DYE, tiers.get(1));
        registerPair(WHITE_DYE, BLUE_DYE, LIGHT_BLUE_DYE, tiers.get(1));
        registerPair(RED_DYE, YELLOW_DYE, ORANGE_DYE, tiers.get(1));
        registerPair(RED_DYE, BLUE_DYE, PURPLE_DYE, tiers.get(1));
        registerPair(BLUE_DYE, GREEN_DYE, CYAN_DYE, tiers.get(1));
        registerPair(PINK_DYE, PURPLE_DYE, MAGENTA_DYE, tiers.get(1));
        registerPair(WHITE_DYE, GRAY_DYE, LIGHT_GRAY_DYE, tiers.get(1));

        registerPair(WHITE_DYE, SAND, WHITE_CONCRETE_POWDER, tiers.get(1));
        registerPair(BLACK_DYE, SAND, BLACK_CONCRETE_POWDER, tiers.get(1));
        registerPair(RED_DYE, SAND, RED_CONCRETE_POWDER, tiers.get(1));
        registerPair(GREEN_DYE, SAND, GREEN_CONCRETE_POWDER, tiers.get(1));
        registerPair(BLUE_DYE, SAND, BLUE_CONCRETE_POWDER, tiers.get(1));
        registerPair(YELLOW_DYE, SAND, YELLOW_CONCRETE_POWDER, tiers.get(1));
        registerPair(BROWN_DYE, SAND, BROWN_CONCRETE_POWDER, tiers.get(1));
        registerPair(GRAY_DYE, SAND, GRAY_CONCRETE_POWDER, tiers.get(1));
        registerPair(PINK_DYE, SAND, PINK_CONCRETE_POWDER, tiers.get(1));
        registerPair(LIME_DYE, SAND, LIME_CONCRETE_POWDER, tiers.get(1));
        registerPair(LIGHT_BLUE_DYE, SAND, LIGHT_BLUE_CONCRETE_POWDER, tiers.get(1));
        registerPair(ORANGE_DYE, SAND, ORANGE_CONCRETE_POWDER, tiers.get(1));
        registerPair(PURPLE_DYE, SAND, PURPLE_CONCRETE_POWDER, tiers.get(1));
        registerPair(CYAN_DYE, SAND, CYAN_CONCRETE_POWDER, tiers.get(1));
        registerPair(MAGENTA_DYE, SAND, MAGENTA_CONCRETE_POWDER, tiers.get(1));
        registerPair(LIGHT_GRAY_DYE, SAND, LIGHT_GRAY_CONCRETE_POWDER, tiers.get(1));

        registerPair(WHITE_DYE, STRING, WHITE_WOOL, tiers.get(1));
        registerPair(BLACK_DYE, STRING, BLACK_WOOL, tiers.get(1));
        registerPair(RED_DYE, STRING, RED_WOOL, tiers.get(1));
        registerPair(GREEN_DYE, STRING, GREEN_WOOL, tiers.get(1));
        registerPair(BLUE_DYE, STRING, BLUE_WOOL, tiers.get(1));
        registerPair(YELLOW_DYE, STRING, YELLOW_WOOL, tiers.get(1));
        registerPair(BROWN_DYE, STRING, BROWN_WOOL, tiers.get(1));
        registerPair(GRAY_DYE, STRING, GRAY_WOOL, tiers.get(1));
        registerPair(PINK_DYE, STRING, PINK_WOOL, tiers.get(1));
        registerPair(LIME_DYE, STRING, LIME_WOOL, tiers.get(1));
        registerPair(LIGHT_BLUE_DYE, STRING, LIGHT_BLUE_WOOL, tiers.get(1));
        registerPair(ORANGE_DYE, STRING, ORANGE_WOOL, tiers.get(1));
        registerPair(PURPLE_DYE, STRING, PURPLE_WOOL, tiers.get(1));
        registerPair(CYAN_DYE, STRING, CYAN_WOOL, tiers.get(1));
        registerPair(MAGENTA_DYE, STRING, MAGENTA_WOOL, tiers.get(1));
        registerPair(LIGHT_GRAY_DYE, STRING, LIGHT_GRAY_WOOL, tiers.get(1));

        registerPair(WHITE_DYE, TERRACOTTA, WHITE_TERRACOTTA, tiers.get(1));
        registerPair(BLACK_DYE, TERRACOTTA, BLACK_TERRACOTTA, tiers.get(1));
        registerPair(RED_DYE, TERRACOTTA, RED_TERRACOTTA, tiers.get(1));
        registerPair(GREEN_DYE, TERRACOTTA, GREEN_TERRACOTTA, tiers.get(1));
        registerPair(BLUE_DYE, TERRACOTTA, BLUE_TERRACOTTA, tiers.get(1));
        registerPair(YELLOW_DYE, TERRACOTTA, YELLOW_TERRACOTTA, tiers.get(1));
        registerPair(BROWN_DYE, TERRACOTTA, BROWN_TERRACOTTA, tiers.get(1));
        registerPair(GRAY_DYE, TERRACOTTA, GRAY_TERRACOTTA, tiers.get(1));
        registerPair(PINK_DYE, TERRACOTTA, PINK_TERRACOTTA, tiers.get(1));
        registerPair(LIME_DYE, TERRACOTTA, LIME_TERRACOTTA, tiers.get(1));
        registerPair(LIGHT_BLUE_DYE, TERRACOTTA, LIGHT_BLUE_TERRACOTTA, tiers.get(1));
        registerPair(ORANGE_DYE, TERRACOTTA, ORANGE_TERRACOTTA, tiers.get(1));
        registerPair(PURPLE_DYE, TERRACOTTA, PURPLE_TERRACOTTA, tiers.get(1));
        registerPair(CYAN_DYE, TERRACOTTA, CYAN_TERRACOTTA, tiers.get(1));
        registerPair(MAGENTA_DYE, TERRACOTTA, MAGENTA_TERRACOTTA, tiers.get(1));
        registerPair(LIGHT_GRAY_DYE, TERRACOTTA, LIGHT_GRAY_TERRACOTTA, tiers.get(1));

        registerPair(WHITE_DYE, GLASS, WHITE_GLASS, tiers.get(1));
        registerPair(BLACK_DYE, GLASS, BLACK_GLASS, tiers.get(1));
        registerPair(RED_DYE, GLASS, RED_GLASS, tiers.get(1));
        registerPair(GREEN_DYE, GLASS, GREEN_GLASS, tiers.get(1));
        registerPair(BLUE_DYE, GLASS, BLUE_GLASS, tiers.get(1));
        registerPair(YELLOW_DYE, GLASS, YELLOW_GLASS, tiers.get(1));
        registerPair(BROWN_DYE, GLASS, BROWN_GLASS, tiers.get(1));
        registerPair(GRAY_DYE, GLASS, GRAY_GLASS, tiers.get(1));
        registerPair(PINK_DYE, GLASS, PINK_GLASS, tiers.get(1));
        registerPair(LIME_DYE, GLASS, LIME_GLASS, tiers.get(1));
        registerPair(LIGHT_BLUE_DYE, GLASS, LIGHT_BLUE_GLASS, tiers.get(1));
        registerPair(ORANGE_DYE, GLASS, ORANGE_GLASS, tiers.get(1));
        registerPair(PURPLE_DYE, GLASS, PURPLE_GLASS, tiers.get(1));
        registerPair(CYAN_DYE, GLASS, CYAN_GLASS, tiers.get(1));
        registerPair(MAGENTA_DYE, GLASS, MAGENTA_GLASS, tiers.get(1));
        registerPair(LIGHT_GRAY_DYE, GLASS, LIGHT_GRAY_GLASS, tiers.get(1));

        registerPair(WHITE_CONCRETE_POWDER, WATER, WHITE_CONCRETE, tiers.get(1));
        registerPair(BLACK_CONCRETE_POWDER, WATER, BLACK_CONCRETE, tiers.get(1));
        registerPair(RED_CONCRETE_POWDER, WATER, RED_CONCRETE, tiers.get(1));
        registerPair(GREEN_CONCRETE_POWDER, WATER, GREEN_CONCRETE, tiers.get(1));
        registerPair(BLUE_CONCRETE_POWDER, WATER, BLUE_CONCRETE, tiers.get(1));
        registerPair(YELLOW_CONCRETE_POWDER, WATER, YELLOW_CONCRETE, tiers.get(1));
        registerPair(BROWN_CONCRETE_POWDER, WATER, BROWN_CONCRETE, tiers.get(1));
        registerPair(GRAY_CONCRETE_POWDER, WATER, GRAY_CONCRETE, tiers.get(1));
        registerPair(PINK_CONCRETE_POWDER, WATER, PINK_CONCRETE, tiers.get(1));
        registerPair(LIME_CONCRETE_POWDER, WATER, LIME_CONCRETE, tiers.get(1));
        registerPair(LIGHT_BLUE_CONCRETE_POWDER, WATER, LIGHT_BLUE_CONCRETE, tiers.get(1));
        registerPair(ORANGE_CONCRETE_POWDER, WATER, ORANGE_CONCRETE, tiers.get(1));
        registerPair(PURPLE_CONCRETE_POWDER, WATER, PURPLE_CONCRETE, tiers.get(1));
        registerPair(CYAN_CONCRETE_POWDER, WATER, CYAN_CONCRETE, tiers.get(1));
        registerPair(MAGENTA_CONCRETE_POWDER, WATER, MAGENTA_CONCRETE, tiers.get(1));
        registerPair(LIGHT_GRAY_CONCRETE_POWDER, WATER, LIGHT_GRAY_CONCRETE, tiers.get(1));
    }

}

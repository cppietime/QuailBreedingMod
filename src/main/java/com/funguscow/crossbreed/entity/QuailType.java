package com.funguscow.crossbreed.entity;

import com.electronwill.nightconfig.core.Config;
import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.init.ModItems;
import com.funguscow.crossbreed.util.RandomPool;
import com.funguscow.crossbreed.util.UnorderedPair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.*;
import java.util.stream.Collectors;

public class QuailType {
    public static Map<String, QuailType> Types = new HashMap<>();
    public static Map<UnorderedPair<String>, RandomPool<String>> Pairings = new HashMap<>();

    public static void preRegisterPair(QuailType mother, QuailType father, QuailType child, int tier) {
        child.parent1 = mother.name;
        child.parent2 = father.name;
        child.tier = tier;
    }

    public String name;
    public String layItem;
    public String deathItem;
    public int deathAmount;
    public int layAmount;
    public int layRandomAmount;
    public int layTime;
    public float fecundity;
    public boolean enabled;
    public String parent1 = "", parent2 = "";
    public int tier = 0;

    public QuailType(String name, String itemID, int amt, int rAmt, float fec, int time, String die, int dieAmt) {
        this.name = name;
        layItem = itemID;
        layAmount = amt;
        layRandomAmount = rAmt;
        fecundity = fec;
        layTime = time;
        deathItem = die;
        deathAmount = dieAmt;
        enabled = true;
        Types.put(this.name, this);
    }

    public QuailType(String name, String itemID, int amt, int rAmt, int time, String die, int dieAmt) {
        this(name, itemID, amt, rAmt, 1, time, die, dieAmt);
    }

    public QuailType(String name, String itemID, int amt, int rAmt, float fec, int time) {
        this(name, itemID, amt, rAmt, fec, time, "", 0);
    }

    public QuailType(String name, String itemID, int amt, int rAmt, int time) {
        this(name, itemID, amt, rAmt, time, "", 0);
    }

    public QuailType disable() {
        enabled = false;
        return this;
    }

    public QuailType getOffspring(QuailType other, RandomSource rand) {
        UnorderedPair<String> pair = new UnorderedPair<>(name, other.name);
        RandomPool<String> pool = Pairings.getOrDefault(pair, null);
        QuailType result = pool != null ? Types.get(pool.get(rand.nextFloat())) : null;
        return result != null ? result : rand.nextBoolean() ? this : other;
    }

    public String toString() {
        return name;
    }

    public static Item getItem(String id, RandomSource rand) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        Item item;
        if ("#@".contains(id.substring(0, 1))) {
            TagKey<Item> tagkey = ItemTags.create(new ResourceLocation(id.substring(1)));
            ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagkey);
            List<Item> items = tag.stream().collect(Collectors.toList());
            if (items.isEmpty())
                return null;
            if (id.charAt(0) == '#') // First item
                item = items.get(0);
            else // Random item
                item = tag.getRandomElement(rand).orElse(null);
        } else
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return item;
    }

    public ItemStack getLoot(RandomSource rand, QuailEntity.Gene gene) {
        float lootThreshold = fecundity * gene.fecundity;
        Item item = getItem(layItem, rand);
        if (item == null || rand.nextFloat() >= lootThreshold) {
            item = ModItems.MANURE.get();
        }
        int amount = layAmount + rand.nextInt(layRandomAmount + 1);
        amount = Math.max(1, (int) (amount * (gene.layAmount + rand.nextFloat() * gene.layAmount)));
        ItemStack itemStack = new ItemStack(item, amount);
        if (item == Items.BOOK) {
            itemStack = EnchantmentHelper.enchantItem(rand, itemStack, 30, true);
        }
        return itemStack;
    }

    public static void matchConfig() {
        List<Float> tiers = Arrays.stream(QuailConfig.COMMON.tierOdds)
                .map(ForgeConfigSpec.DoubleValue::get)
                .map(Double::floatValue)
                .collect(Collectors.toList());
        for (Map.Entry<String, QuailType> entry : Types.entrySet()) {
            QuailType type = entry.getValue();
            String key = entry.getKey();
            QuailConfig.Common.QuailTypeConfig configType = QuailConfig.COMMON.quailTypes.get(key);
            type.layAmount = configType.amount.get();
            type.layRandomAmount = configType.amountRand.get();
            type.layTime = configType.time.get();
            type.deathAmount = configType.onDieAmount.get();
            type.layItem = configType.dropItem.get();
            type.deathItem = configType.deathItem.get();
            type.enabled = configType.enabled.get();
            type.parent1 = configType.parent1.get();
            type.parent2 = configType.parent2.get();
            type.tier = configType.tier.get();
            if (!type.layItem.isEmpty() && getItem(type.layItem.replace("@", "#"), null) == null) {
                BreedMod.LOGGER.warn("Drop item id " + type.layItem + " does not exist");
            }
            if (!type.deathItem.isEmpty() && getItem(type.deathItem.replace("@", "#"), null) == null) {
                BreedMod.LOGGER.warn("Death item id " + type.deathItem + " does not exist");
            }
            if (type.enabled && !type.parent1.equals("") && !type.parent2.equals("")) {
                UnorderedPair<String> pair = new UnorderedPair<>(type.parent1, type.parent2);
                if (Pairings.containsKey(pair)) {
                    throw new IllegalStateException("Pair " + type.parent1 + "+" + type.parent2 + " has already been registered.");
                }
                RandomPool<String> pool = Pairings.computeIfAbsent(pair, keyPair -> new RandomPool<>((String) null));
                pool.add(type.name, tiers.get(type.tier));
            }
        }
        for (Config config : QuailConfig.COMMON.extraQuails.get()) {
            int layAmount = config.getIntOrElse("Amount", 1);
            int layRandomAmount = config.getIntOrElse("RandomAmount", 0);
            int layTime = config.getIntOrElse("LayTime", 6000);
            int deathAmount = config.getIntOrElse("DeathAmount", 0);
            int tier = config.getIntOrElse("Tier", 1);
            String name = config.get("Name");
            String layItem = config.getOrElse("DropItem", BreedMod.MODID + ":quail_egg");
            String deathItem = config.getOrElse("DeathItem", "");
            String parent1 = config.getOrElse("Parent1", "");
            String parent2 = config.getOrElse("Parent2", "");
            QuailType extraType = new QuailType(name, layItem, layAmount, layRandomAmount, layTime, deathItem, deathAmount);
            if (!layItem.isEmpty() && getItem(layItem.replace("@", "#"), null) == null) {
                BreedMod.LOGGER.warn("Drop item id " + layItem + " does not exist");
            }
            if (!deathItem.isEmpty() && getItem(deathItem.replace("@", "#"), null) == null) {
                BreedMod.LOGGER.warn("Death item id " + deathItem + " does not exist");
            }
            if (!config.getOrElse("Enabled", true))
                extraType.disable();
            extraType.tier = tier;
            extraType.parent1 = parent1;
            extraType.parent2 = parent2;
            if (!parent1.equals("") && !parent2.equals("") && extraType.enabled) {
                UnorderedPair<String> pair = new UnorderedPair<>(parent1, parent2);
                if (Pairings.containsKey(pair)) {
                    throw new IllegalStateException("Pair " + extraType.parent1 + "+" + extraType.parent2 + " has already been registered.");
                }
                RandomPool<String> pool = Pairings.computeIfAbsent(pair, keyPair -> new RandomPool<>((String) null));
                pool.add(name, tiers.get(tier));
            }
        }
    }

    public static final QuailType
            // Tier 0 / wild-type
            PAINTED = new QuailType("painted", BreedMod.MODID + ":quail_egg", 1, 0, 0.8f, 6000),
            BOBWHITE = new QuailType("bobwhite", BreedMod.MODID + ":quail_egg", 1, 0, 0.8f, 6000),
            BROWN = new QuailType("brown", BreedMod.MODID + ":quail_egg", 1, 0, 0.8f, 6000),
            ELEGANT = new QuailType("elegant", BreedMod.MODID + ":quail_egg", 1, 0, 0.8f, 6000),

    // Tier 1
    GRAVEL = new QuailType("gravel", "minecraft:gravel", 1, 0, 0.6f, 1200, "minecraft:flint", 2),
            DIRT = new QuailType("dirt", "minecraft:dirt", 1, 0, 0.6f, 1200),
            SAND = new QuailType("sand", "minecraft:sand", 1, 0, 0.6f, 1200, "minecraft:kelp", 2),
            NETHERRACK = new QuailType("netherrack", "minecraft:netherrack", 1, 0, 0.6f, 1200),
            CLAY = new QuailType("clay", "minecraft:clay_ball", 1, 2, 0.6f, 1200),
            COBBLE = new QuailType("cobble", "minecraft:cobblestone", 1, 0, 0.6f, 1200, "minecraft:smooth_stone", 2),

    // Tier 2
    OAK = new QuailType("oak", "minecraft:oak_log", 1, 0, 0.5f, 2400, "minecraft:oak_sapling", 1),
            SPRUCE = new QuailType("spruce", "minecraft:spruce_log", 1, 0, 0.5f, 2400, "minecraft:spruce_sapling", 1),
            BIRCH = new QuailType("birch", "minecraft:birch_log", 1, 0, 0.5f, 2400, "minecraft:birch_sapling", 1),
            JUNGLE = new QuailType("jungle", "minecraft:jungle_log", 1, 0, 0.5f, 2400, "minecraft:jungle_sapling", 1),
            ACACIA = new QuailType("acacia", "minecraft:acacia_log", 1, 0, 0.5f, 2400, "minecraft:acacia_sapling", 1),
            DARK_OAK = new QuailType("dark_oak", "minecraft:dark_oak_log", 1, 0, 0.5f, 2400, "minecraft:dark_oak_sapling", 1),
            MANGROVE = new QuailType("mangrove", "minecraft:mangrove_log", 1, 0, 0.5f, 2400, "minecraft:mangrove_propagule", 1),
            CHERRY = new QuailType("cherry", "minecraft:cherry_log", 1, 0, 0.5f, 2400, "minecraft:cherry_sapling", 1),

    COAL = new QuailType("coal", "minecraft:coal", 1, 0, 0.5f, 2400),
            QUARTZ = new QuailType("quartz", "minecraft:quartz", 1, 0, 0.5f, 2400),
            APPLE = new QuailType("apple", "minecraft:apple", 1, 0, 0.5f, 2400),
            REEDS = new QuailType("reeds", "minecraft:sugar_cane", 1, 2, 0.5f, 2400, "minecraft:bamboo", 4),
            FEATHER = new QuailType("feather", "minecraft:feather", 2, 3, 0.5f, 2400, "minecraft:chicken", 1),
            STRING = new QuailType("string", "minecraft:string", 1, 0, 0.5f, 2400, "minecraft:mutton", 1),

    // Tier 3
    BONE = new QuailType("bone", "minecraft:bone", 1, 0, 0.3f, 4800),
            COCOA = new QuailType("cocoa", "minecraft:cocoa_beans", 1, 2, 0.3f, 4800),
            INK = new QuailType("ink", "minecraft:ink_sack", 1, 0, 0.3f, 4800, "minecraft:glow_ink_sac", 1),
            BEET = new QuailType("beet", "minecraft:beetroot", 1, 0, 0.3f, 4800, "minecraft:beetroot_seeds", 1),
            CACTUS = new QuailType("cactus", "minecraft:cactus", 1, 0, 0.3f, 4800),
            LAPIS = new QuailType("lapis", "minecraft:lapis_lazuli", 1, 0, 0.3f, 4800),
            FLOWER = new QuailType("flower", "@breesources:normal_flowers", 1, 0, 0.3f, 4800, "minecraft:rabbit", 1),
            IRON = new QuailType("iron", "minecraft:raw_iron", 1, 0, 0.3f, 4800, "minecraft:poppy", 1),
            REDSTONE = new QuailType("redstone", "minecraft:redstone", 1, 2, 0.3f, 4800),
            SOULSAND = new QuailType("soulsand", "minecraft:soul_sand", 1, 0, 0.3f, 4800),
            WHEAT = new QuailType("wheat", "minecraft:wheat", 1, 0, 0.3f, 4800, "minecraft:wheat_seeds", 1),
            MELON = new QuailType("melon", "minecraft:melon_slice", 1, 2, 0.3f, 4800),
            PUMPKIN = new QuailType("pumpkin", "minecraft:pumpkin", 1, 0, 0.3f, 3600),
            POTATO = new QuailType("potato", "minecraft:potato", 1, 0, 0.3f, 4800, "minecraft:porkchop", 1),
            CARROT = new QuailType("carrot", "minecraft:carrot", 1, 0, 0.3f, 4800, "minecraft:rabbit", 1),
            WATER = new QuailType("water", BreedMod.MODID + ":water_bubble", 1, 0, 0.3f, 4800),
            LEATHER = new QuailType("leather", "minecraft:leather", 1, 0, 0.3f, 4800, "minecraft:beef", 1),
            TERRACOTTA = new QuailType("terracotta", "minecraft:terracotta", 1, 0, 0.3f, 4800),
            SNOWBALL = new QuailType("snowball", "minecraft:snowball", 1, 2, 0.3f, 2000, "minecraft:sweet_berries", 1),
            COPPER = new QuailType("copper", "minecraft:raw_copper", 1, 0, 0.3f, 4800),
            DEEPSLATE = new QuailType("deepslate", "minecraft:cobbled_deepslate", 1, 0, 0.3f, 4800, "minecraft:tuff", 1),
            SCULK = new QuailType("sculk", "minecraft:sculk", 1, 0, 0.3f, 4800, "minecraft:sculk_vein", 1),
            GLOW = new QuailType("glow", "minecraft:glow_berries", 1, 0, 0.3f, 4800, "minecraft:glow_lichen", 1),
    // Modded
    TIN = new QuailType("tin", "#forge:ingots/tin", 1, 0, 0.3f, 4800).disable(),
            ALUMINUM = new QuailType("aluminum", "#forge:ingots/aluminum", 1, 0, 0.3f, 4800).disable(),
            LEAD = new QuailType("lead", "#forge:ingots/lead", 1, 0, 0.3f, 4800).disable(),
            RUBBER = new QuailType("rubber", "#forge:rubber", 1, 0, 0.3f, 4800).disable(),
            SILICON = new QuailType("silicon", "#forge:silicon", 1, 0, 0.3f, 4800).disable(),

    // Tier 4
    GRASS = new QuailType("grass", "minecraft:grass_block", 1, 0, 0.2f, 6000),
            REDSHROOM = new QuailType("redshroom", "minecraft:red_mushroom", 1, 0, 0.2f, 6000),
            BROWNSHROOM = new QuailType("brownshroom", "minecraft:brown_mushroom", 1, 0, 0.2f, 6000),
            ENDSTONE = new QuailType("endstone", "minecraft:end_stone", 1, 0, 0.2f, 6000, "minecraft:purpur_block", 1),
            GOLD = new QuailType("gold", "minecraft:raw_gold", 1, 0, 0.2f, 6000),
            LAVA = new QuailType("lava", BreedMod.MODID + ":lava_bubble", 1, 0, 0.2f, 6000, "minecraft:pointed_dripstone", 1),
            GUNPOWDER = new QuailType("gunpowder", "minecraft:gunpowder", 1, 0, 0.2f, 6000),
            SPIDEREYE = new QuailType("spidereye", "minecraft:spider_eye", 1, 0, 0.2f, 6000),
            SLIME = new QuailType("slime", "minecraft:slime_ball", 1, 0, 0.2f, 6000),
            WART = new QuailType("wart", "minecraft:nether_wart", 1, 0, 0.2f, 6000),
            GLASS = new QuailType("glass", "minecraft:glass", 1, 0, 0.2f, 6000),
            BASALT = new QuailType("basalt", "minecraft:basalt", 1, 0, 0.2f, 6000),
            ICE = new QuailType("ice", "minecraft:ice", 1, 0, 0.2f, 6000),
            GLOWSTONE = new QuailType("glowstone", "minecraft:glowstone_dust", 1, 3, 0.2f, 6000),
            FISH = new QuailType("fish", "@breesources:raw_fish", 1, 2, 0.2f, 6000, "minecraft:sponge", 1),
            RABBIT = new QuailType("rabbit", "minecraft:rabbit_hide", 1, 2, 0.2f, 6000, "minecraft:rabbit_foot", 1),
            TURTLE = new QuailType("turtle", "minecraft:scute", 1, 0, 0.2f, 10000, "minecraft:turtle_egg", 1),
            AMETHYST = new QuailType("amethyst", "minecraft:amethyst_shard", 1, 0, 0.2f, 6000, "minecraft:calcite", 1),
            MOSS = new QuailType("moss", "minecraft:moss_block", 1, 0, 0.2f, 6000, "minecraft:flowering_azalea", 1),
            MUD = new QuailType("mud", "minecraft:mud", 1, 0, 0.2f, 6000, "minecraft:packed_mud", 1),
    // Modded
    SILVER = new QuailType("silver", "#forge:ingots/silver", 1, 0, 0.2f, 6000).disable(),
            URANIUM = new QuailType("uranium", "#forge:ingots/uranium", 1, 0, 0.2f, 6000).disable(),

    // Tier 5
    EMERALD = new QuailType("emerald", "minecraft:emerald", 1, 0, 0.1f, 24000, "minecraft:saddle", 1),
            OBSIDIAN = new QuailType("obsidian", "minecraft:obsidian", 1, 0, 0.1f, 12000),
            BLAZE = new QuailType("blaze", "minecraft:blaze_rod", 1, 0, 0.1f, 12000),
            WARPED_NYL = new QuailType("warped_nyl", "minecraft:warped_nylium", 1, 0, 0.1f, 12000),
            CRIMSON_NYL = new QuailType("crimson_nyl", "minecraft:crimson_nylium", 1, 0, 0.1f, 12000),
            MYCELIUM = new QuailType("mycelium", "minecraft:mycelium", 1, 0, 0.1f, 12000),
            HONEY = new QuailType("honey", "minecraft:honey_bottle", 1, 0, 0.1f, 12000, "minecraft:honeycomb", 1),
            GHAST = new QuailType("ghast", "minecraft:ghast_tear", 1, 0, 0.1f, 16000),
            BLACKSTONE = new QuailType("blackstone", "minecraft:blackstone", 1, 0, 0.1f, 12000, "minecraft:gilded_blackstone", 1),
            CORAL = new QuailType("coral", "@breesources:coral_items", 1, 0, 0.1f, 12000, "@breesources:coral_blocks", 1),
            PACKED_ICE = new QuailType("packed_ice", "minecraft:packed_ice", 1, 0, 0.1f, 12000),
            SHERD = new QuailType("sherd", "@breesources:sherds", 1, 0, 0.4f, 12000),
            PITCHER = new QuailType("pitcher", "minecraft:pitcher_plant", 1, 0, 0.2f, 12000, "minecraft:pitcher_pod", 1),
            TORCHFLOWER = new QuailType("torchflower", "minecraft:torchflower", 1, 0, 0.2f, 12000, "minecraft:torchflower_seeds", 1),

    // Modded
    RUBY = new QuailType("ruby", "#forge:gems/ruby", 1, 0, 0.1f, 12000).disable(),
            SAPPHIRE = new QuailType("sapphire", "#forge:gems/sapphire", 1, 0, 0.1f, 12000).disable(),

    // Tier 6
    DIAMOND = new QuailType("diamond", "minecraft:diamond", 1, 0, 0.05f, 48000),
            PEARL = new QuailType("pearl", "minecraft:ender_pearl", 1, 0, 0.05f, 24000),
            SHULKER = new QuailType("shulker", "minecraft:shulker_shell", 1, 0, 0.05f, 48000),
            NAUTILUS = new QuailType("nautilus", "minecraft:nautilus_shell", 1, 0, 0.05f, 24000, "minecraft:trident", 1),
            PRISM = new QuailType("prism", "minecraft:prismarine_shard", 1, 3, 0.05f, 24000, "minecraft:prismarine_crystals", 2),
            MEMBRANE = new QuailType("membrane", "minecraft:phantom_membrane", 1, 0, 0.05f, 24000, "minecraft:elytra", 1),
            WITHER_ROSE = new QuailType("wither_rose", "minecraft:wither_rose", 1, 0, 0.05f, 30000, "minecraft:wither_skeleton_skull", 1),
            CHORUS = new QuailType("chorus", "minecraft:chorus_fruit", 1, 3, 0.05f, 24000, "minecraft:chorus_flower", 1),
            BLUE_ICE = new QuailType("blue_ice", "minecraft:blue_ice", 1, 0, 0.05f, 24000),
            WARPED_STEM = new QuailType("warped_stem", "minecraft:warped_stem", 1, 0, 0.05f, 24000, "minecraft:shroomlight", 1),
            CRIMSON_STEM = new QuailType("crimson_stem", "minecraft:crimson_stem", 1, 0, 0.05f, 24000, "minecraft:shroomlight", 1),
            SCULK_SENSOR = new QuailType("sculk_sensor", "minecraft:sculk_sensor", 1, 0, 0.05f, 24000, "minecraft:sculk_shrieker", 1),

    // Tier 7
    WITHER_STAR = new QuailType("wither_star", "minecraft:nether_star", 1, 0, 0.04f, 144000),
            HEART_OF_SEA = new QuailType("heart_of_sea", "minecraft:heart_of_the_sea", 1, 0, 0.04f, 72000),
            DEBRIS = new QuailType("debris", "minecraft:ancient_debris", 1, 0, 0.04f, 72000),
            DRAGON = new QuailType("dragon", "minecraft:dragon_breath", 1, 0, 0.04f, 48000, "minecraft:dragon_head", 1),
            BOOK = new QuailType("book", "minecraft:book", 1, 0, 0.4f, 48000, "minecraft:experience_bottle", 5),
            MUSIC = new QuailType("music", "@minecraft:music_discs", 1, 0, 0.1f, 40000),

    // Primary dyes
    WHITE_DYE = new QuailType("white_dye", "minecraft:white_dye", 2, 2, 0.5f, 4800),
            BLACK_DYE = new QuailType("black_dye", "minecraft:black_dye", 2, 2, 0.5f, 4800),
            RED_DYE = new QuailType("red_dye", "minecraft:red_dye", 2, 2, 0.5f, 4800),
            GREEN_DYE = new QuailType("green_dye", "minecraft:green_dye", 2, 2, 0.5f, 4800),
            BLUE_DYE = new QuailType("blue_dye", "minecraft:blue_dye", 2, 2, 0.5f, 4800),
            YELLOW_DYE = new QuailType("yellow_dye", "minecraft:yellow_dye", 2, 2, 0.5f, 4800),
            BROWN_DYE = new QuailType("brown_dye", "minecraft:brown_dye", 2, 2, 0.5f, 4800),

    // Secondary dyes
    GRAY_DYE = new QuailType("gray_dye", "minecraft:gray_dye", 2, 2, 0.5f, 4800),
            PINK_DYE = new QuailType("pink_dye", "minecraft:pink_dye", 2, 2, 0.5f, 4800),
            LIME_DYE = new QuailType("lime_dye", "minecraft:lime_dye", 2, 2, 0.5f, 4800),
            LIGHT_BLUE_DYE = new QuailType("light_blue_dye", "minecraft:light_blue_dye", 2, 2, 0.5f, 4800),
            PURPLE_DYE = new QuailType("purple_dye", "minecraft:purple_dye", 2, 2, 0.5f, 4800),
            CYAN_DYE = new QuailType("cyan_dye", "minecraft:cyan_dye", 2, 2, 0.5f, 4800),
            ORANGE_DYE = new QuailType("orange_dye", "minecraft:orange_dye", 2, 2, 0.5f, 4800),

    // Tertiary dyes
    LIGHT_GRAY_DYE = new QuailType("light_gray_dye", "minecraft:light_gray_dye", 2, 2, 0.5f, 4800),
            MAGENTA_DYE = new QuailType("magenta_dye", "minecraft:magenta_dye", 2, 2, 0.5f, 4800),

    // Concrete powders
    WHITE_CONCRETE_POWDER = new QuailType("white_concrete_powder", "minecraft:white_concrete_powder", 1, 0, 0.25f, 2400),
            BLACK_CONCRETE_POWDER = new QuailType("black_concrete_powder", "minecraft:black_concrete_powder", 1, 0, 0.25f, 2400),
            GRAY_CONCRETE_POWDER = new QuailType("gray_concrete_powder", "minecraft:gray_concrete_powder", 1, 0, 0.25f, 2400),
            LIGHT_GRAY_CONCRETE_POWDER = new QuailType("light_gray_concrete_powder", "minecraft:light_gray_concrete_powder", 1, 0, 0.25f, 2400),
            RED_CONCRETE_POWDER = new QuailType("red_concrete_powder", "minecraft:red_concrete_powder", 1, 0, 0.25f, 2400),
            GREEN_CONCRETE_POWDER = new QuailType("green_concrete_powder", "minecraft:green_concrete_powder", 1, 0, 0.25f, 2400),
            BLUE_CONCRETE_POWDER = new QuailType("blue_concrete_powder", "minecraft:blue_concrete_powder", 1, 0, 0.25f, 2400),
            YELLOW_CONCRETE_POWDER = new QuailType("yellow_concrete_powder", "minecraft:yellow_concrete_powder", 1, 0, 0.25f, 2400),
            BROWN_CONCRETE_POWDER = new QuailType("brown_concrete_powder", "minecraft:brown_concrete_powder", 1, 0, 0.25f, 2400),
            PINK_CONCRETE_POWDER = new QuailType("pink_concrete_powder", "minecraft:pink_concrete_powder", 1, 0, 0.25f, 2400),
            ORANGE_CONCRETE_POWDER = new QuailType("orange_concrete_powder", "minecraft:orange_concrete_powder", 1, 0, 0.25f, 2400),
            PURPLE_CONCRETE_POWDER = new QuailType("purple_concrete_powder", "minecraft:purple_concrete_powder", 1, 0, 0.25f, 2400),
            MAGENTA_CONCRETE_POWDER = new QuailType("magenta_concrete_powder", "minecraft:magenta_concrete_powder", 1, 0, 0.25f, 2400),
            LIME_CONCRETE_POWDER = new QuailType("lime_concrete_powder", "minecraft:lime_concrete_powder", 1, 0, 0.25f, 2400),
            CYAN_CONCRETE_POWDER = new QuailType("cyan_concrete_powder", "minecraft:cyan_concrete_powder", 1, 0, 0.25f, 2400),
            LIGHT_BLUE_CONCRETE_POWDER = new QuailType("light_blue_concrete_powder", "minecraft:light_blue_concrete_powder", 1, 0, 0.25f, 2400),

    //Concrete blocks
    WHITE_CONCRETE = new QuailType("white_concrete", "minecraft:white_concrete", 1, 0, 0.25f, 4800),
            BLACK_CONCRETE = new QuailType("black_concrete", "minecraft:black_concrete", 1, 0, 0.25f, 4800),
            GRAY_CONCRETE = new QuailType("gray_concrete", "minecraft:gray_concrete", 1, 0, 0.25f, 4800),
            LIGHT_GRAY_CONCRETE = new QuailType("light_gray_concrete", "minecraft:light_gray_concrete", 1, 0, 0.25f, 4800),
            RED_CONCRETE = new QuailType("red_concrete", "minecraft:red_concrete", 1, 0, 0.25f, 4800),
            GREEN_CONCRETE = new QuailType("green_concrete", "minecraft:green_concrete", 1, 0, 0.25f, 4800),
            BLUE_CONCRETE = new QuailType("blue_concrete", "minecraft:blue_concrete", 1, 0, 0.25f, 4800),
            YELLOW_CONCRETE = new QuailType("yellow_concrete", "minecraft:yellow_concrete", 1, 0, 0.25f, 4800),
            BROWN_CONCRETE = new QuailType("brown_concrete", "minecraft:brown_concrete", 1, 0, 0.25f, 4800),
            PINK_CONCRETE = new QuailType("pink_concrete", "minecraft:pink_concrete", 1, 0, 0.25f, 4800),
            ORANGE_CONCRETE = new QuailType("orange_concrete", "minecraft:orange_concrete", 1, 0, 0.25f, 4800),
            PURPLE_CONCRETE = new QuailType("purple_concrete", "minecraft:purple_concrete", 1, 0, 0.25f, 4800),
            MAGENTA_CONCRETE = new QuailType("magenta_concrete", "minecraft:magenta_concrete", 1, 0, 0.25f, 4800),
            LIME_CONCRETE = new QuailType("lime_concrete", "minecraft:lime_concrete", 1, 0, 0.25f, 4800),
            CYAN_CONCRETE = new QuailType("cyan_concrete", "minecraft:cyan_concrete", 1, 0, 0.25f, 4800),
            LIGHT_BLUE_CONCRETE = new QuailType("light_blue_concrete", "minecraft:light_blue_concrete", 1, 0, 0.25f, 4800),

    //Wool blocks
    WHITE_WOOL = new QuailType("white_wool", "minecraft:white_wool", 1, 0, 0.25f, 4800),
            BLACK_WOOL = new QuailType("black_wool", "minecraft:black_wool", 1, 0, 0.25f, 4800),
            GRAY_WOOL = new QuailType("gray_wool", "minecraft:gray_wool", 1, 0, 0.25f, 4800),
            LIGHT_GRAY_WOOL = new QuailType("light_gray_wool", "minecraft:light_gray_wool", 1, 0, 0.25f, 4800),
            RED_WOOL = new QuailType("red_wool", "minecraft:red_wool", 1, 0, 0.25f, 4800),
            GREEN_WOOL = new QuailType("green_wool", "minecraft:green_wool", 1, 0, 0.25f, 4800),
            BLUE_WOOL = new QuailType("blue_wool", "minecraft:blue_wool", 1, 0, 0.25f, 4800),
            YELLOW_WOOL = new QuailType("yellow_wool", "minecraft:yellow_wool", 1, 0, 0.25f, 4800),
            BROWN_WOOL = new QuailType("brown_wool", "minecraft:brown_wool", 1, 0, 0.25f, 4800),
            PINK_WOOL = new QuailType("pink_wool", "minecraft:pink_wool", 1, 0, 0.25f, 4800),
            ORANGE_WOOL = new QuailType("orange_wool", "minecraft:orange_wool", 1, 0, 0.25f, 4800),
            PURPLE_WOOL = new QuailType("purple_wool", "minecraft:purple_wool", 1, 0, 0.25f, 4800),
            MAGENTA_WOOL = new QuailType("magenta_wool", "minecraft:magenta_wool", 1, 0, 0.25f, 4800),
            LIME_WOOL = new QuailType("lime_wool", "minecraft:lime_wool", 1, 0, 0.25f, 4800),
            CYAN_WOOL = new QuailType("cyan_wool", "minecraft:cyan_wool", 1, 0, 0.25f, 4800),
            LIGHT_BLUE_WOOL = new QuailType("light_blue_wool", "minecraft:light_blue_wool", 1, 0, 0.25f, 4800),

    //Terracotta blocks
    WHITE_TERRACOTTA = new QuailType("white_terracotta", "minecraft:white_terracotta", 1, 0, 0.25f, 4800),
            BLACK_TERRACOTTA = new QuailType("black_terracotta", "minecraft:black_terracotta", 1, 0, 0.25f, 4800),
            GRAY_TERRACOTTA = new QuailType("gray_terracotta", "minecraft:gray_terracotta", 1, 0, 0.25f, 4800),
            LIGHT_GRAY_TERRACOTTA = new QuailType("light_gray_terracotta", "minecraft:light_gray_terracotta", 1, 0, 0.25f, 4800),
            RED_TERRACOTTA = new QuailType("red_terracotta", "minecraft:red_terracotta", 1, 0, 0.25f, 4800),
            GREEN_TERRACOTTA = new QuailType("green_terracotta", "minecraft:green_terracotta", 1, 0, 0.25f, 4800),
            BLUE_TERRACOTTA = new QuailType("blue_terracotta", "minecraft:blue_terracotta", 1, 0, 0.25f, 4800),
            YELLOW_TERRACOTTA = new QuailType("yellow_terracotta", "minecraft:yellow_terracotta", 1, 0, 0.25f, 4800),
            BROWN_TERRACOTTA = new QuailType("brown_terracotta", "minecraft:brown_terracotta", 1, 0, 0.25f, 4800),
            PINK_TERRACOTTA = new QuailType("pink_terracotta", "minecraft:pink_terracotta", 1, 0, 0.25f, 4800),
            ORANGE_TERRACOTTA = new QuailType("orange_terracotta", "minecraft:orange_terracotta", 1, 0, 0.25f, 4800),
            PURPLE_TERRACOTTA = new QuailType("purple_terracotta", "minecraft:purple_terracotta", 1, 0, 0.25f, 4800),
            MAGENTA_TERRACOTTA = new QuailType("magenta_terracotta", "minecraft:magenta_terracotta", 1, 0, 0.25f, 4800),
            LIME_TERRACOTTA = new QuailType("lime_terracotta", "minecraft:lime_terracotta", 1, 0, 0.25f, 4800),
            CYAN_TERRACOTTA = new QuailType("cyan_terracotta", "minecraft:cyan_terracotta", 1, 0, 0.25f, 4800),
            LIGHT_BLUE_TERRACOTTA = new QuailType("light_blue_terracotta", "minecraft:light_blue_terracotta", 1, 0, 0.25f, 4800),

    //Glass blocks
    WHITE_GLASS = new QuailType("white_glass", "minecraft:white_stained_glass", 1, 0, 0.25f, 12000),
            BLACK_GLASS = new QuailType("black_glass", "minecraft:black_stained_glass", 1, 0, 0.25f, 12000),
            GRAY_GLASS = new QuailType("gray_glass", "minecraft:gray_stained_glass", 1, 0, 0.25f, 12000),
            LIGHT_GRAY_GLASS = new QuailType("light_gray_glass", "minecraft:light_gray_stained_glass", 1, 0, 0.25f, 12000),
            RED_GLASS = new QuailType("red_glass", "minecraft:red_stained_glass", 1, 0, 0.25f, 12000),
            GREEN_GLASS = new QuailType("green_glass", "minecraft:green_stained_glass", 1, 0, 0.25f, 12000),
            BLUE_GLASS = new QuailType("blue_glass", "minecraft:blue_stained_glass", 1, 0, 0.25f, 12000),
            YELLOW_GLASS = new QuailType("yellow_glass", "minecraft:yellow_stained_glass", 1, 0, 0.25f, 12000),
            BROWN_GLASS = new QuailType("brown_glass", "minecraft:brown_stained_glass", 1, 0, 0.25f, 12000),
            PINK_GLASS = new QuailType("pink_glass", "minecraft:pink_stained_glass", 1, 0, 0.25f, 12000),
            ORANGE_GLASS = new QuailType("orange_glass", "minecraft:orange_stained_glass", 1, 0, 0.25f, 12000),
            PURPLE_GLASS = new QuailType("purple_glass", "minecraft:purple_stained_glass", 1, 0, 0.25f, 12000),
            MAGENTA_GLASS = new QuailType("magenta_glass", "minecraft:magenta_stained_glass", 1, 0, 0.25f, 12000),
            LIME_GLASS = new QuailType("lime_glass", "minecraft:lime_stained_glass", 1, 0, 0.25f, 12000),
            CYAN_GLASS = new QuailType("cyan_glass", "minecraft:cyan_stained_glass", 1, 0, 0.25f, 12000),
            LIGHT_BLUE_GLASS = new QuailType("light_blue_glass", "minecraft:light_blue_stained_glass", 1, 0, 0.25f, 12000);

    static {
        preRegisterPair(BROWN, PAINTED, GRAVEL, (0));
        preRegisterPair(BROWN, BOBWHITE, DIRT, (0));
        preRegisterPair(BROWN, ELEGANT, SAND, (0));
        preRegisterPair(PAINTED, BOBWHITE, NETHERRACK, (0));
        preRegisterPair(PAINTED, ELEGANT, CLAY, (0));
        preRegisterPair(BOBWHITE, ELEGANT, COBBLE, (0));

        preRegisterPair(ELEGANT, COBBLE, OAK, (1));
        preRegisterPair(ELEGANT, SAND, SPRUCE, (1));
        preRegisterPair(ELEGANT, DIRT, BIRCH, (1));
        preRegisterPair(ELEGANT, CLAY, JUNGLE, (1));
        preRegisterPair(ELEGANT, GRAVEL, ACACIA, (1));
        preRegisterPair(ELEGANT, NETHERRACK, DARK_OAK, (1));
        preRegisterPair(PAINTED, CLAY, MANGROVE, (1));
        preRegisterPair(BROWN, COBBLE, CHERRY, (1));
        preRegisterPair(PAINTED, COBBLE, COAL, (1));
        preRegisterPair(BOBWHITE, NETHERRACK, QUARTZ, (1));
        preRegisterPair(BROWN, CLAY, APPLE, (1));
        preRegisterPair(PAINTED, SAND, REEDS, (1));
        preRegisterPair(BOBWHITE, DIRT, FEATHER, (1));
        preRegisterPair(BROWN, GRAVEL, STRING, (1));

        preRegisterPair(DIRT, COAL, BONE, (2));
        preRegisterPair(APPLE, JUNGLE, COCOA, (2));
        preRegisterPair(STRING, QUARTZ, LAPIS, (2));
        preRegisterPair(APPLE, SPRUCE, BEET, (2));
        preRegisterPair(SAND, STRING, CACTUS, (2));
        preRegisterPair(DIRT, FEATHER, FLOWER, (2));
        preRegisterPair(COAL, DARK_OAK, INK, (2));
        preRegisterPair(COAL, QUARTZ, IRON, (2));
        preRegisterPair(STRING, NETHERRACK, REDSTONE, (2));
        preRegisterPair(NETHERRACK, FEATHER, SOULSAND, (2));
        preRegisterPair(REEDS, OAK, WHEAT, (2));
        preRegisterPair(REEDS, JUNGLE, MELON, (2));
        preRegisterPair(REEDS, SPRUCE, PUMPKIN, (2));
        preRegisterPair(REEDS, BIRCH, POTATO, (2));
        preRegisterPair(REEDS, ACACIA, CARROT, (2));
        preRegisterPair(REEDS, SAND, WATER, (2));
        preRegisterPair(FEATHER, STRING, LEATHER, (2));
        preRegisterPair(CLAY, COAL, TERRACOTTA, (2));
        preRegisterPair(FEATHER, CLAY, SNOWBALL, (2));
        preRegisterPair(APPLE, CLAY, COPPER, (2));
        preRegisterPair(REEDS, CLAY, SCULK, (2));
        preRegisterPair(QUARTZ, NETHERRACK, DEEPSLATE, (2));
        preRegisterPair(REEDS, COAL, GLOW, (2));

        preRegisterPair(BONE, IRON, TIN, (2));
        preRegisterPair(WATER, IRON, ALUMINUM, (2));
        preRegisterPair(COAL, IRON, LEAD, (2));
        preRegisterPair(REEDS, CLAY, RUBBER, (2));
        preRegisterPair(CLAY, SAND, SILICON, (2));

        preRegisterPair(DIRT, FLOWER, GRASS, (3));
        preRegisterPair(CARROT, BEET, REDSHROOM, (3));
        preRegisterPair(POTATO, COCOA, BROWNSHROOM, (3));
        preRegisterPair(SOULSAND, IRON, ENDSTONE, (3));
        preRegisterPair(IRON, FLOWER, GOLD, (3));
        preRegisterPair(WATER, SOULSAND, LAVA, (3));
        preRegisterPair(COAL, MELON, GUNPOWDER, (3));
        preRegisterPair(PUMPKIN, LEATHER, SPIDEREYE, (3));
        preRegisterPair(MELON, CACTUS, SLIME, (3));
        preRegisterPair(WHEAT, SOULSAND, WART, (3));
        preRegisterPair(SAND, IRON, GLASS, (3));
        preRegisterPair(COBBLE, SOULSAND, BASALT, (3));
        preRegisterPair(WATER, SNOWBALL, ICE, (3));
        preRegisterPair(SOULSAND, REDSTONE, GLOWSTONE, (3));
        preRegisterPair(WATER, LEATHER, FISH, (3));
        preRegisterPair(SAND, GRASS, RABBIT, (3));
        preRegisterPair(SAND, FISH, TURTLE, (3));
        preRegisterPair(DIRT, WATER, MUD, (3));
        preRegisterPair(WHEAT, CLAY, MOSS, (3));
        preRegisterPair(APPLE, QUARTZ, AMETHYST, (2));

        preRegisterPair(GOLD, BONE, SILVER, (3));
        preRegisterPair(GOLD, WART, URANIUM, (3));

        preRegisterPair(GOLD, SLIME, EMERALD, (4));
        preRegisterPair(WATER, LAVA, OBSIDIAN, (4));
        preRegisterPair(WATER, SPIDEREYE, BLAZE, (4));
        preRegisterPair(BROWNSHROOM, GRASS, WARPED_NYL, (4));
        preRegisterPair(REDSHROOM, GRASS, CRIMSON_NYL, (4));
        preRegisterPair(REDSHROOM, BROWNSHROOM, MYCELIUM, (4));
        preRegisterPair(SLIME, REEDS, HONEY, (4));
        preRegisterPair(WART, SPIDEREYE, GHAST, (4));
        preRegisterPair(BASALT, COAL, BLACKSTONE, (4));
        preRegisterPair(FISH, GLASS, CORAL, (4));
        preRegisterPair(ICE, GLASS, PACKED_ICE, (4));

        preRegisterPair(EMERALD, REDSTONE, RUBY, (4));
        preRegisterPair(EMERALD, LAPIS, SAPPHIRE, (4));

        preRegisterPair(EMERALD, OBSIDIAN, DIAMOND, (5));
        preRegisterPair(BLAZE, OBSIDIAN, PEARL, (5));
        preRegisterPair(GHAST, BLAZE, SHULKER, (5));
        preRegisterPair(WATER, EMERALD, NAUTILUS, (5));
        preRegisterPair(WATER, OBSIDIAN, PRISM, (5));
        preRegisterPair(LEATHER, HONEY, MEMBRANE, (5));
        preRegisterPair(FLOWER, GHAST, WITHER_ROSE, (5));
        preRegisterPair(ENDSTONE, MYCELIUM, CHORUS, (5));
        preRegisterPair(PACKED_ICE, EMERALD, BLUE_ICE, (5));
        preRegisterPair(WARPED_NYL, BONE, WARPED_STEM, (5));
        preRegisterPair(CRIMSON_NYL, BONE, CRIMSON_STEM, (5));
        preRegisterPair(EMERALD, SCULK, SCULK_SENSOR, (5));
        preRegisterPair(AMETHYST, CORAL, SHERD, (5));
        preRegisterPair(REDSHROOM, SAND, PITCHER, (5));
        preRegisterPair(BROWNSHROOM, GRAVEL, TORCHFLOWER, (5));

        preRegisterPair(DIAMOND, WITHER_ROSE, WITHER_STAR, (6));
        preRegisterPair(NAUTILUS, PEARL, HEART_OF_SEA, (6));
        preRegisterPair(DIAMOND, BLACKSTONE, DEBRIS, (6));
        preRegisterPair(MEMBRANE, SHULKER, DRAGON, (6));
        preRegisterPair(DIAMOND, OBSIDIAN, BOOK, (6));
        preRegisterPair(DIAMOND, REDSTONE, MUSIC, 6);

        preRegisterPair(WATER, BONE, WHITE_DYE, (1));
        preRegisterPair(WATER, INK, BLACK_DYE, (1));
        preRegisterPair(WATER, COCOA, BROWN_DYE, (1));
        preRegisterPair(WATER, CACTUS, GREEN_DYE, (1));
        preRegisterPair(WATER, LAPIS, BLUE_DYE, (1));
        preRegisterPair(WATER, BEET, RED_DYE, (1));
        preRegisterPair(WATER, FLOWER, YELLOW_DYE, (1));

        preRegisterPair(WHITE_DYE, BLACK_DYE, GRAY_DYE, (1));
        preRegisterPair(WHITE_DYE, RED_DYE, PINK_DYE, (1));
        preRegisterPair(WHITE_DYE, GREEN_DYE, LIME_DYE, (1));
        preRegisterPair(WHITE_DYE, BLUE_DYE, LIGHT_BLUE_DYE, (1));
        preRegisterPair(RED_DYE, YELLOW_DYE, ORANGE_DYE, (1));
        preRegisterPair(RED_DYE, BLUE_DYE, PURPLE_DYE, (1));
        preRegisterPair(BLUE_DYE, GREEN_DYE, CYAN_DYE, (1));
        preRegisterPair(PINK_DYE, PURPLE_DYE, MAGENTA_DYE, (1));
        preRegisterPair(WHITE_DYE, GRAY_DYE, LIGHT_GRAY_DYE, (1));

        preRegisterPair(WHITE_DYE, SAND, WHITE_CONCRETE_POWDER, (1));
        preRegisterPair(BLACK_DYE, SAND, BLACK_CONCRETE_POWDER, (1));
        preRegisterPair(RED_DYE, SAND, RED_CONCRETE_POWDER, (1));
        preRegisterPair(GREEN_DYE, SAND, GREEN_CONCRETE_POWDER, (1));
        preRegisterPair(BLUE_DYE, SAND, BLUE_CONCRETE_POWDER, (1));
        preRegisterPair(YELLOW_DYE, SAND, YELLOW_CONCRETE_POWDER, (1));
        preRegisterPair(BROWN_DYE, SAND, BROWN_CONCRETE_POWDER, (1));
        preRegisterPair(GRAY_DYE, SAND, GRAY_CONCRETE_POWDER, (1));
        preRegisterPair(PINK_DYE, SAND, PINK_CONCRETE_POWDER, (1));
        preRegisterPair(LIME_DYE, SAND, LIME_CONCRETE_POWDER, (1));
        preRegisterPair(LIGHT_BLUE_DYE, SAND, LIGHT_BLUE_CONCRETE_POWDER, (1));
        preRegisterPair(ORANGE_DYE, SAND, ORANGE_CONCRETE_POWDER, (1));
        preRegisterPair(PURPLE_DYE, SAND, PURPLE_CONCRETE_POWDER, (1));
        preRegisterPair(CYAN_DYE, SAND, CYAN_CONCRETE_POWDER, (1));
        preRegisterPair(MAGENTA_DYE, SAND, MAGENTA_CONCRETE_POWDER, (1));
        preRegisterPair(LIGHT_GRAY_DYE, SAND, LIGHT_GRAY_CONCRETE_POWDER, (1));

        preRegisterPair(WHITE_DYE, STRING, WHITE_WOOL, (1));
        preRegisterPair(BLACK_DYE, STRING, BLACK_WOOL, (1));
        preRegisterPair(RED_DYE, STRING, RED_WOOL, (1));
        preRegisterPair(GREEN_DYE, STRING, GREEN_WOOL, (1));
        preRegisterPair(BLUE_DYE, STRING, BLUE_WOOL, (1));
        preRegisterPair(YELLOW_DYE, STRING, YELLOW_WOOL, (1));
        preRegisterPair(BROWN_DYE, STRING, BROWN_WOOL, (1));
        preRegisterPair(GRAY_DYE, STRING, GRAY_WOOL, (1));
        preRegisterPair(PINK_DYE, STRING, PINK_WOOL, (1));
        preRegisterPair(LIME_DYE, STRING, LIME_WOOL, (1));
        preRegisterPair(LIGHT_BLUE_DYE, STRING, LIGHT_BLUE_WOOL, (1));
        preRegisterPair(ORANGE_DYE, STRING, ORANGE_WOOL, (1));
        preRegisterPair(PURPLE_DYE, STRING, PURPLE_WOOL, (1));
        preRegisterPair(CYAN_DYE, STRING, CYAN_WOOL, (1));
        preRegisterPair(MAGENTA_DYE, STRING, MAGENTA_WOOL, (1));
        preRegisterPair(LIGHT_GRAY_DYE, STRING, LIGHT_GRAY_WOOL, (1));

        preRegisterPair(WHITE_DYE, TERRACOTTA, WHITE_TERRACOTTA, (1));
        preRegisterPair(BLACK_DYE, TERRACOTTA, BLACK_TERRACOTTA, (1));
        preRegisterPair(RED_DYE, TERRACOTTA, RED_TERRACOTTA, (1));
        preRegisterPair(GREEN_DYE, TERRACOTTA, GREEN_TERRACOTTA, (1));
        preRegisterPair(BLUE_DYE, TERRACOTTA, BLUE_TERRACOTTA, (1));
        preRegisterPair(YELLOW_DYE, TERRACOTTA, YELLOW_TERRACOTTA, (1));
        preRegisterPair(BROWN_DYE, TERRACOTTA, BROWN_TERRACOTTA, (1));
        preRegisterPair(GRAY_DYE, TERRACOTTA, GRAY_TERRACOTTA, (1));
        preRegisterPair(PINK_DYE, TERRACOTTA, PINK_TERRACOTTA, (1));
        preRegisterPair(LIME_DYE, TERRACOTTA, LIME_TERRACOTTA, (1));
        preRegisterPair(LIGHT_BLUE_DYE, TERRACOTTA, LIGHT_BLUE_TERRACOTTA, (1));
        preRegisterPair(ORANGE_DYE, TERRACOTTA, ORANGE_TERRACOTTA, (1));
        preRegisterPair(PURPLE_DYE, TERRACOTTA, PURPLE_TERRACOTTA, (1));
        preRegisterPair(CYAN_DYE, TERRACOTTA, CYAN_TERRACOTTA, (1));
        preRegisterPair(MAGENTA_DYE, TERRACOTTA, MAGENTA_TERRACOTTA, (1));
        preRegisterPair(LIGHT_GRAY_DYE, TERRACOTTA, LIGHT_GRAY_TERRACOTTA, (1));

        preRegisterPair(WHITE_DYE, GLASS, WHITE_GLASS, (1));
        preRegisterPair(BLACK_DYE, GLASS, BLACK_GLASS, (1));
        preRegisterPair(RED_DYE, GLASS, RED_GLASS, (1));
        preRegisterPair(GREEN_DYE, GLASS, GREEN_GLASS, (1));
        preRegisterPair(BLUE_DYE, GLASS, BLUE_GLASS, (1));
        preRegisterPair(YELLOW_DYE, GLASS, YELLOW_GLASS, (1));
        preRegisterPair(BROWN_DYE, GLASS, BROWN_GLASS, (1));
        preRegisterPair(GRAY_DYE, GLASS, GRAY_GLASS, (1));
        preRegisterPair(PINK_DYE, GLASS, PINK_GLASS, (1));
        preRegisterPair(LIME_DYE, GLASS, LIME_GLASS, (1));
        preRegisterPair(LIGHT_BLUE_DYE, GLASS, LIGHT_BLUE_GLASS, (1));
        preRegisterPair(ORANGE_DYE, GLASS, ORANGE_GLASS, (1));
        preRegisterPair(PURPLE_DYE, GLASS, PURPLE_GLASS, (1));
        preRegisterPair(CYAN_DYE, GLASS, CYAN_GLASS, (1));
        preRegisterPair(MAGENTA_DYE, GLASS, MAGENTA_GLASS, (1));
        preRegisterPair(LIGHT_GRAY_DYE, GLASS, LIGHT_GRAY_GLASS, (1));

        preRegisterPair(WHITE_CONCRETE_POWDER, WATER, WHITE_CONCRETE, (1));
        preRegisterPair(BLACK_CONCRETE_POWDER, WATER, BLACK_CONCRETE, (1));
        preRegisterPair(RED_CONCRETE_POWDER, WATER, RED_CONCRETE, (1));
        preRegisterPair(GREEN_CONCRETE_POWDER, WATER, GREEN_CONCRETE, (1));
        preRegisterPair(BLUE_CONCRETE_POWDER, WATER, BLUE_CONCRETE, (1));
        preRegisterPair(YELLOW_CONCRETE_POWDER, WATER, YELLOW_CONCRETE, (1));
        preRegisterPair(BROWN_CONCRETE_POWDER, WATER, BROWN_CONCRETE, (1));
        preRegisterPair(GRAY_CONCRETE_POWDER, WATER, GRAY_CONCRETE, (1));
        preRegisterPair(PINK_CONCRETE_POWDER, WATER, PINK_CONCRETE, (1));
        preRegisterPair(LIME_CONCRETE_POWDER, WATER, LIME_CONCRETE, (1));
        preRegisterPair(LIGHT_BLUE_CONCRETE_POWDER, WATER, LIGHT_BLUE_CONCRETE, (1));
        preRegisterPair(ORANGE_CONCRETE_POWDER, WATER, ORANGE_CONCRETE, (1));
        preRegisterPair(PURPLE_CONCRETE_POWDER, WATER, PURPLE_CONCRETE, (1));
        preRegisterPair(CYAN_CONCRETE_POWDER, WATER, CYAN_CONCRETE, (1));
        preRegisterPair(MAGENTA_CONCRETE_POWDER, WATER, MAGENTA_CONCRETE, (1));
        preRegisterPair(LIGHT_GRAY_CONCRETE_POWDER, WATER, LIGHT_GRAY_CONCRETE, (1));
    }

}

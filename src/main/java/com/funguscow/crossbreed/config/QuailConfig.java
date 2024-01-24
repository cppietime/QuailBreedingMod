package com.funguscow.crossbreed.config;

import com.electronwill.nightconfig.core.Config;
import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.QuailType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BreedMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class QuailConfig {

    public static class Common {

        private static final double[] TIER_DEFAULTS = {0.5, 0.9, 0.75, 0.5, 0.25, 0.125, 0.0625};

        public static class QuailTypeConfig {
            public ForgeConfigSpec.IntValue amount, amountRand, time, onDieAmount, tier;
            public ForgeConfigSpec.DoubleValue fecundity;
            public ForgeConfigSpec.ConfigValue<String> dropItem, deathItem, parent1, parent2;
            public ForgeConfigSpec.BooleanValue enabled;
        }

        public Map<String, Common.QuailTypeConfig> quailTypes;
        public ForgeConfigSpec.DoubleValue[] tierOdds;

        public ForgeConfigSpec.IntValue quailEggChance, quailEggMultiChance,
                quailWeight, quailMin, quailMax, quailBreedingTime,
                maxSeedsInNest, maxQuailsInNest, seedsToBreed;
        public ForgeConfigSpec.DoubleValue nestTickRate, manureCompostValue;

        public ForgeConfigSpec.ConfigValue<List<Config>> extraQuails;

        public Common(ForgeConfigSpec.Builder builder) {
            quailTypes = new HashMap<>();
            tierOdds = new ForgeConfigSpec.DoubleValue[7];

            builder.comment("Nest-related config.").push("Nest");
            nestTickRate = builder
                    .comment("Rate at which to tick nests relative to overworld quails.")
                    .worldRestart()
                    .defineInRange("NestTickRate", 1f, 0f, 1_000_000f);
            maxSeedsInNest = builder
                    .comment("Maximum number of seeds a nest can hold for breeding.")
                    .worldRestart()
                    .defineInRange("MaxSeedsInNest", 64, 2, 1000);
            maxQuailsInNest = builder
                    .comment("Maximum number of quails a nest can hold.")
                    .worldRestart()
                    .defineInRange("MaxQuailsInNest", 64, 2, 1000);
            seedsToBreed = builder
                    .comment("Number of seed items to breed a new quail.")
                    .worldRestart()
                    .defineInRange("SeedsToBreed", 32, 1, 1000);
            builder.pop();

            builder.comment("Odds of successful cross-breeding to ascend one tier.").push("Tiers");
            for (int i = 0; i < 7; i++) {
                tierOdds[i] = builder.worldRestart().defineInRange("tier" + i, TIER_DEFAULTS[i], 0, 1);
            }
            builder.pop();

            builder.comment("World gen options.").push("World");
            quailWeight = builder
                    .comment("Quail spawning weight (higher = more common).")
                    .worldRestart()
                    .defineInRange("SpawnWeight", 10, 0, 100);
            quailMin = builder
                    .comment("Minimum number of quails to spawn at once.")
                    .worldRestart()
                    .defineInRange("SpawnMin", 2, 0, 20);
            quailMax = builder
                    .comment("Maximum number of quails to spawn at once.")
                    .worldRestart()
                    .defineInRange("SpawnMax", 5, 0, 20);
            builder.pop();

            builder.comment("Options for quail breeding.").push("QuailBreeding");
            quailEggChance = builder
                    .comment("Chance for egg to spawn a quail when thrown (higher = rarer).")
                    .worldRestart()
                    .defineInRange("QuailEggChance", 4, 1, 1000);
            quailEggMultiChance = builder
                    .comment("Chance for egg to spawn 4 quails instead of 1.")
                    .worldRestart()
                    .defineInRange("QuailEggMultiChance", 32, 1, 1000);
            quailBreedingTime = builder
                    .comment("Delay in ticks before quails can mate again.")
                    .worldRestart()
                    .defineInRange("QuailBreedingTime", 6000, 1, 144_000);
            builder.pop();

            manureCompostValue = builder
                    .comment("Odds of manure filling a composter.")
                    .worldRestart()
                    .defineInRange("ManureCompostValue", 0.5, 0, 1);

            builder.comment("Settings for each type of quail.").push("QuailTypes");
            for (Map.Entry<String, QuailType> type : QuailType.Types.entrySet()) {
                builder.comment("Config values for quail type " + type.getKey() + ".").push(type.getKey());
                QuailTypeConfig config = new QuailTypeConfig();
                config.amount = builder
                        .comment("Base amount of loot laid.")
                        .worldRestart()
                        .defineInRange("Amount", type.getValue().layAmount, 0, 64);
                config.amountRand = builder
                        .comment("Size of range of loot variance.")
                        .worldRestart()
                        .defineInRange("RandomAmount", type.getValue().layRandomAmount, 0, 64);
                config.time = builder
                        .comment("Minimum ticks between laying loot.")
                        .worldRestart()
                        .defineInRange("LayTime", type.getValue().layTime, 0, 1_000_000);
                config.onDieAmount = builder
                        .comment("Amount of extra death loot dropped, if any.")
                        .worldRestart()
                        .defineInRange("DeathAmount", type.getValue().deathAmount, 0, 64);
                config.fecundity = builder
                        .comment("Chance of laying an egg each time.")
                        .worldRestart()
                        .defineInRange("Fecundity", 1.0, 0, 1);
                config.dropItem = builder
                        .comment("ID of item dropped as egg.")
                        .worldRestart()
                        .define("DropItem", type.getValue().layItem);
                config.deathItem = builder
                        .comment("Extra item dropped on death, empty string for nothing.")
                        .worldRestart()
                        .define("DeathItem", type.getValue().deathItem);
                config.enabled = builder
                        .comment("Whether the quail type is enabled.")
                        .worldRestart()
                        .define("Enabled", type.getValue().enabled);
                config.parent1 = builder
                        .comment("One parent of breeding pair, empty for non-breedable.")
                        .worldRestart()
                        .define("Parent1", type.getValue().parent1);
                config.parent2 = builder
                        .comment("One parent of breeding pair, empty for non-breedable.")
                        .worldRestart()
                        .define("Parent2", type.getValue().parent2);
                config.tier = builder
                        .comment("Tier for odds of successful breed.")
                        .worldRestart()
                        .defineInRange("Tier", type.getValue().tier, 0, 6);
                builder.pop();
                quailTypes.put(type.getKey(), config);
            }
            extraQuails = builder
                    .comment("Additional quail types, copy the format from default quails, but add a Name property.")
                    .worldRestart()
                    .define("Extras", new ArrayList<>());
            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec CONFIG_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {

    }

    @SubscribeEvent
    public static void onFileChanged(ModConfigEvent.Reloading event) {

    }

}

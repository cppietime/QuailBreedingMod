package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.item.*;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BreedMod.MODID);

    public static final RegistryObject<Item> QUAIL_EGG = ITEMS.register("quail_egg",
            () -> new GenericEggItem(new Item.Properties(),
                    1,
                    1000,
                    "breesources:quail",
                    "breesources:quail_egg")),
            WATER_BUBBLE = ITEMS.register("water_bubble",
                    () -> new BubbleItem(new Item.Properties(),
                            Fluids.WATER)),
            LAVA_BUBBLE = ITEMS.register("lava_bubble",
                    () -> new BubbleItem(new Item.Properties(),
                            Fluids.LAVA)),
            QUAIL_JAIL = ITEMS.register("quail_jail",
                    () -> new JailItem(new Item.Properties().stacksTo(1), false)),
            STRONG_QUAIL_JAIL = ITEMS.register("strong_quail_jail",
                    () -> new JailItem(new Item.Properties().stacksTo(1), true)),
            PICKLED_EGG = ITEMS.register("pickled_egg",
                    () -> new Item(new Item.Properties().food(
                            new FoodProperties.Builder().nutrition(2).saturationMod(0.8f).build()))),
            FRIED_EGG = ITEMS.register("fried_egg",
                    () -> new Item(new Item.Properties().food(
                            new FoodProperties.Builder().nutrition(3).saturationMod(0.4f).build()))),
            RAW_QUAIL = ITEMS.register("raw_quail",
                    () -> new Item(new Item.Properties().food(Foods.CHICKEN))),
            COOKED_QUAIL = ITEMS.register("cooked_quail",
                    () -> new Item(new Item.Properties().food(Foods.COOKED_CHICKEN))),
            QUAIL_METER = ITEMS.register("quail_meter",
                    () -> new MeterItem(new Item.Properties().stacksTo(1))),
            QUAIL_SPAWN_EGG = ITEMS.register("quail_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.QUAIL, 0x734011, 0xa4b5bd, new Item.Properties().stacksTo(16)));

    public static void matchConfig() {
        GenericEggItem quailEgg = (GenericEggItem) QUAIL_EGG.get();
        quailEgg.updateOdds(QuailConfig.COMMON.quailEggChance.get(), QuailConfig.COMMON.quailEggMultiChance.get());
    }

}

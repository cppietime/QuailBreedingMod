package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.item.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Food;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BreedMod.MODID);

    public static final RegistryObject<Item> QUAIL_EGG = ITEMS.register("quail_egg",
            () -> new GenericEggItem(new Item.Properties().group(BreedMod.GROUP),
                    1,
                    1000,
                    "breesources:quail",
                    "breesources:quail_egg")),
            WATER_BUBBLE = ITEMS.register("water_bubble",
                    () -> new BubbleItem(new Item.Properties().group(BreedMod.GROUP),
                            Fluids.WATER)),
            LAVA_BUBBLE = ITEMS.register("lava_bubble",
                    () -> new BubbleItem(new Item.Properties().group(BreedMod.GROUP),
                            Fluids.LAVA)),
            QUAIL_JAIL = ITEMS.register("quail_jail",
                    () -> new JailItem(new Item.Properties().maxStackSize(1).group(BreedMod.GROUP), false)),
            STRONG_QUAIL_JAIL = ITEMS.register("strong_quail_jail",
                    () -> new JailItem(new Item.Properties().maxStackSize(1).group(BreedMod.GROUP), true)),
            PICKLED_EGG = ITEMS.register("pickled_egg",
                    () -> new Item(new Item.Properties().group(BreedMod.GROUP).food(
                            new Food.Builder().hunger(2).saturation(0.8f).build()))),
            FRIED_EGG = ITEMS.register("fried_egg",
                    () -> new Item(new Item.Properties().group(BreedMod.GROUP).food(
                            new Food.Builder().hunger(3).saturation(0.4f).build()))),
            RAW_QUAIL = ITEMS.register("raw_quail",
                    () -> new Item(new Item.Properties().group(BreedMod.GROUP).food(Foods.CHICKEN))),
            COOKED_QUAIL = ITEMS.register("cooked_quail",
                    () -> new Item(new Item.Properties().group(BreedMod.GROUP).food(Foods.COOKED_CHICKEN))),
            QUAIL_METER = ITEMS.register("quail_meter",
                    () -> new MeterItem(new Item.Properties().group(BreedMod.GROUP).maxStackSize(1))),
            QUAIL_SPAWN_EGG = ITEMS.register("quail_spawn_egg",
                    () -> new ModSpawnEgg(ModEntities.QUAIL, 0x734011, 0xa4b5bd, new Item.Properties().group(BreedMod.GROUP)));

    public static void matchConfig(){
        GenericEggItem quailEgg = (GenericEggItem)QUAIL_EGG.get();
        quailEgg.updateOdds(QuailConfig.COMMON.quailEggChance.get(), QuailConfig.COMMON.quailEggMultiChance.get());
    }

}

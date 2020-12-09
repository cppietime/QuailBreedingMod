package com.funguscow.crossbreed.bus;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.item.GenericEggItem;
import com.funguscow.crossbreed.item.ModSpawnEgg;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BreedMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusSubscriber {

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event){
        ModSpawnEgg.registerMobs();
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event){
        GenericEggItem.registerDispenser();
    }

}

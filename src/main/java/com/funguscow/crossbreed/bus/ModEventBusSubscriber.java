package com.funguscow.crossbreed.bus;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.item.GenericEggItem;
import com.funguscow.crossbreed.item.ModSpawnEgg;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = BreedMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusSubscriber {

//    @SubscribeEvent
//    public static void onRegisterItems(Event event){
//        GenericEggItem.registerDispenser();
//    }

    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        BreedMod.LOGGER.warn("DEFINING ENTITY ATTRIBUTES");
        event.put(ModEntities.QUAIL.get(), QuailEntity.createAttributes().build());
    }

}

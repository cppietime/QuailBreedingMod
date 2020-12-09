package com.funguscow.crossbreed.bus;


import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.init.ModEntities;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = BreedMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBusSubscriber {

    private static final Logger logger = LogManager.getLogger();

    @SubscribeEvent
    public static void biomeGeneration(BiomeLoadingEvent event){
        Biome.Category category = event.getCategory();
        int min = QuailConfig.COMMON.quailMin.get();
        int max = QuailConfig.COMMON.quailMax.get();
        if(min > max){
            int tmp = min;
            min = max;
            max = tmp;
        }
        if(category != Biome.Category.NETHER && category != Biome.Category.THEEND && category != Biome.Category.OCEAN){
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(ModEntities.QUAIL.get(),
                    QuailConfig.COMMON.quailWeight.get(),
                    min,
                    max));
        }
    }

}

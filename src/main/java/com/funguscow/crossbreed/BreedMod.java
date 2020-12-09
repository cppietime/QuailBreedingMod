package com.funguscow.crossbreed;

import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.entity.QuailType;
import com.funguscow.crossbreed.init.ModBlocks;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.init.ModItems;
import com.funguscow.crossbreed.init.ModSounds;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BreedMod.MODID)
public class BreedMod
{

    public static final ItemGroup GROUP = new ItemGroup("brees_group") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.QUAIL_JAIL.get());
        }
    };

    public static final String MODID = "breesources";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public BreedMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, QuailConfig.CONFIG_SPEC, "quails.toml");

        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntities.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModSounds.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(ModEntities.QUAIL.get(), QuailEntity.setAttributes().create());
            QuailType.matchConfig();
            ModItems.matchConfig();
            ModEntities.registerPlacements();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }
}

package com.funguscow.crossbreed;

import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.entity.QuailType;
import com.funguscow.crossbreed.init.*;
import com.funguscow.crossbreed.item.GenericEggItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BreedMod.MODID)
public class BreedMod
{

    public static final CreativeModeTab GROUP = new CreativeModeTab("brees_group") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.QUAIL_JAIL.get());
        }
    };

    public static final String MODID = "breesources";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public BreedMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        bus.addListener(this::onCommonSetup);
        // Register the doClientStuff method for modloading
        bus.addListener(this::doClientStuff);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, QuailConfig.CONFIG_SPEC, "quails.toml");

        ModTileEntities.registerEntities();
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModEntities.ENTITY_TYPES.register(bus);
        ModSounds.SOUNDS.register(bus);
        ModContainers.CONTAINERS.register(bus);
        ModTileEntities.TILE_ENTITIES.register(bus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            //GlobalEntityTypeAttributes.put(ModEntities.QUAIL.get(), QuailEntity.createAttributes().build());
            QuailType.matchConfig();
            ModItems.matchConfig();
            ModEntities.registerPlacements();
            ModContainers.registerScreens();
            GenericEggItem.registerDispenser();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.log(Level.INFO, "Doing client stuff");
    }
}

package com.funguscow.crossbreed;

import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.entity.QuailType;
import com.funguscow.crossbreed.init.*;
import com.funguscow.crossbreed.item.GenericEggItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BreedMod.MODID)
public class BreedMod {

    public static final String MODID = "breesources";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public BreedMod() {
        LOGGER.debug("Initializing BreedMod");
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        bus.addListener(this::onCommonSetup);

        // Register the doClientStuff method for modloading
        // bus.addListener(this::doClientStuff);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, QuailConfig.CONFIG_SPEC, "quails.toml");

        ModItems.register(bus);
        ModBlocks.register(bus);
        ModEntities.register(bus);
        ModSounds.register(bus);
        ModContainers.register(bus);
        ModTileEntities.register(bus);
        ModCreativeTabs.register(bus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.debug("Performing common setup");
        event.enqueueWork(() -> {
            QuailType.matchConfig();
            ModItems.matchConfig();
            ModEntities.registerPlacements();
            ModContainers.registerScreens();
            GenericEggItem.registerDispenser();
        });
    }
}

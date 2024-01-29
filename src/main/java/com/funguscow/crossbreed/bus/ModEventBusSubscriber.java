package com.funguscow.crossbreed.bus;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.init.ModCreativeTabs;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = BreedMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusSubscriber {

    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.QUAIL.get(), QuailEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        ModCreativeTabs.buildContents(event);
    }

    // Some test code for experimenting with datagen. Not using for now.
    /*@SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<? extends DataProvider>) (PackOutput packOutput) -> new RecipeProvider(packOutput) {

                    @Override
                    protected void buildRecipes(RecipeOutput output) {
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.EGG).requires(ModItems.QUAIL_EGG.get()).unlockedBy("criteria", has(ModItems.QUAIL_EGG.get())).save(output, new ResourceLocation(BreedMod.MODID, "egg"));
                    }
                }
        );
    }*/

}

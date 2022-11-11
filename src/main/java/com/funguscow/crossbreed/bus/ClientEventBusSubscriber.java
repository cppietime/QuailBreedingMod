package com.funguscow.crossbreed.bus;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.client.model.QuailModel;
import com.funguscow.crossbreed.client.renderer.QuailRenderer;
import com.funguscow.crossbreed.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BreedMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(ModEntities.QUAIL.get(), QuailRenderer::new);
        event.registerEntityRenderer(ModEntities.GENERIC_EGG.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(QuailModel.LAYER_LOCATION, QuailModel::createBodyLayer);
    }

}

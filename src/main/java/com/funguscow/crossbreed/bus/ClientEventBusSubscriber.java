package com.funguscow.crossbreed.bus;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.block.GeneticLeafBlock;
import com.funguscow.crossbreed.client.model.QuailModel;
import com.funguscow.crossbreed.client.renderer.QuailRenderer;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.init.ModTileEntities;
import com.funguscow.crossbreed.worldgen.botany.wood.ModWoodType;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BreedMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.QUAIL.get(), QuailRenderer::new);
        event.registerEntityRenderer(ModEntities.GENERIC_EGG.get(), ThrownItemRenderer::new);

        event.registerBlockEntityRenderer(ModTileEntities.SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(QuailModel.LAYER_LOCATION, QuailModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerClientEvent(FMLClientSetupEvent event) {
        for (ModWoodType modWoodType : ModWoodType.WoodTypes.values()) {
            Sheets.addWoodType(modWoodType.woodType);
        }
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BlockColor colorizer = (state, level, pos, index) -> level.getBlockTint(pos, BiomeColors.FOLIAGE_COLOR_RESOLVER);
        event.register(colorizer,
                GeneticLeafBlock.LEAF_TYPES.stream()
                        .filter(spec -> spec.foliageColored)
                        .map(spec -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BreedMod.MODID, spec.name)))
                        .toArray(Block[]::new));
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, index) -> FoliageColor.getDefaultColor(),
                GeneticLeafBlock.LEAF_TYPES.stream()
                        .filter(spec -> spec.foliageColored)
                        .map(spec -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BreedMod.MODID, spec.name)))
                        .toArray(Block[]::new));
    }

}

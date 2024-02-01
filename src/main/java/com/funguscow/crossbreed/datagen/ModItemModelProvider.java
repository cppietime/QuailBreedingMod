package com.funguscow.crossbreed.datagen;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import com.funguscow.crossbreed.worldgen.botany.wood.ModWoodType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BreedMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (ModWoodType woodType : ModWoodType.WoodTypes.values()) {
            defaultBlockItem(woodType.getStairsBlock());
            defaultBlockItem(woodType.getSlabBlock());
            defaultBlockItem(woodType.getPressurePlateBlock());
            defaultBlockItem(woodType.getFenceGateBlock());

            fenceItem(woodType.getFenceBlock(), woodType.getPlanksBlock());
            buttonItem(woodType.getButtonBlock(), woodType.getPlanksBlock());
            trapdoorItem(woodType.getTrapdoorBlock());

            defaultItem(woodType.getSignItem());
            defaultItem(woodType.getHangingSignItem());
            defaultItem(woodType.getDoorItem());
        }
        for (RegistryObject<Block> sapling : TreeSpecies.Saplings) {
            //generatedBlockItem(sapling);
            defaultBlockItem(sapling);
        }
    }

    private void defaultBlockItem(RegistryObject<Block> block) {
        withExistingParent(block.getId().getPath(), block.getId().withPrefix("block/"));
    }

    private void defaultItem(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(), new ResourceLocation("item/generated"))
                .texture("layer0", item.getId().withPrefix("item/"));
    }

    private void generatedBlockItem(RegistryObject<Block> item) {
        withExistingParent(item.getId().getPath(), new ResourceLocation("item/generated"))
                .texture("layer0", item.getId().withPrefix("block/"));
    }

    private void fenceItem(RegistryObject<Block> block, RegistryObject<Block> textureBlock) {
        withExistingParent(block.getId().getPath(), new ResourceLocation("block/fence_inventory"))
                .texture("texture", textureBlock.getId().withPrefix("block/"));
    }

    private void buttonItem(RegistryObject<Block> block, RegistryObject<Block> textureBlock) {
        withExistingParent(block.getId().getPath(), new ResourceLocation("block/button_inventory"))
                .texture("texture", textureBlock.getId().withPrefix("block/"));
    }

    private void trapdoorItem(RegistryObject<Block> block) {
        withExistingParent(block.getId().getPath(), block.getId().withSuffix("_bottom").withPrefix("block/"));
    }
}

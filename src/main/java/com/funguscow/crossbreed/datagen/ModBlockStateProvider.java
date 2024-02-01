package com.funguscow.crossbreed.datagen;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.block.GeneticLeafBlock;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import com.funguscow.crossbreed.worldgen.botany.wood.ModWoodType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, BreedMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (ModWoodType woodType : ModWoodType.WoodTypes.values()) {
            logBlock(((RotatedPillarBlock) woodType.getLogBlock().get()));
            defaultBlockItem(woodType.getLogBlock());
            ResourceLocation logTexture = blockTexture(woodType.getLogBlock().get());

            axisBlock(((RotatedPillarBlock) woodType.getWoodBlock().get()), logTexture, logTexture);
            defaultBlockItem(woodType.getWoodBlock());

            ResourceLocation strippedTexture = blockTexture(woodType.getStrippedLogBlock().get());
            axisBlock(((RotatedPillarBlock) woodType.getStrippedLogBlock().get()), strippedTexture, logTexture.withSuffix("_top"));
            defaultBlockItem(woodType.getStrippedLogBlock());

            axisBlock(((RotatedPillarBlock) woodType.getStrippedWoodBlock().get()), strippedTexture, strippedTexture);
            defaultBlockItem(woodType.getStrippedWoodBlock());

            cubeBlockWithItem(woodType.getPlanksBlock());
            ResourceLocation plankTexture = blockTexture(woodType.getPlanksBlock().get());

            stairsBlock(((StairBlock) woodType.getStairsBlock().get()), plankTexture);

            slabBlock(((SlabBlock) woodType.getSlabBlock().get()), plankTexture, plankTexture);

            fenceBlock(((FenceBlock) woodType.getFenceBlock().get()), plankTexture);

            fenceGateBlock(((FenceGateBlock) woodType.getFenceGateBlock().get()), plankTexture);

            buttonBlock(((ButtonBlock) woodType.getButtonBlock().get()), plankTexture);

            pressurePlateBlock(((PressurePlateBlock) woodType.getPressurePlateBlock().get()), plankTexture);

            ResourceLocation doorTexture = blockTexture(woodType.getDoorBlock().get());
            doorBlockWithRenderType(((DoorBlock) woodType.getDoorBlock().get()), doorTexture.withSuffix("_bottom"), doorTexture.withSuffix("_top"), "cutout");

            trapdoorBlockWithRenderType(((TrapDoorBlock) woodType.getTrapdoorBlock().get()), blockTexture(woodType.getTrapdoorBlock().get()), true, "cutout");

            signBlock(((StandingSignBlock) woodType.getStandingSignBlock().get()), ((WallSignBlock) woodType.getWallSignBlock().get()), plankTexture);

            ModelFile hangingSignModel = models().sign(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(woodType.getCeilingHangingSignBlock().get())).getPath(), plankTexture);
            simpleBlock(woodType.getCeilingHangingSignBlock().get(), hangingSignModel);
            simpleBlock(woodType.getWallHangingSignBlock().get(), hangingSignModel);
        }
        for (RegistryObject<Block> sapling : TreeSpecies.Saplings) {
            ResourceLocation vanilla = new ResourceLocation(sapling.getId().getPath());
            ResourceLocation texture = ForgeRegistries.BLOCKS.containsKey(vanilla) ? blockTexture(ForgeRegistries.BLOCKS.getValue(vanilla)) : blockTexture(sapling.get());
            simpleBlock(sapling.get(), models().cross(sapling.getId().getPath(), texture).renderType("cutout"));
        }
        for (GeneticLeafBlock.LeafSpec leafSpec : GeneticLeafBlock.LEAF_TYPES) {
            String leafName = leafSpec.name;
            ResourceLocation location = new ResourceLocation(BreedMod.MODID, leafName);
            Block block = ForgeRegistries.BLOCKS.getValue(location);
            ResourceLocation texture = leafName.startsWith("g_") ? blockTexture(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(leafName.substring(2)))) : blockTexture(block);
            simpleBlockWithItem(block, models().singleTexture(leafName, new ResourceLocation("block/leaves"), "all", texture).renderType("cutout"));
        }
    }

    private void defaultBlockItem(RegistryObject<Block> block) {
        simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block.get())).withPrefix("block/")));
    }

    private void cubeBlockWithItem(RegistryObject<Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }
}

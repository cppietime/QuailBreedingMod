package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.block.VarChestBlock;
import com.funguscow.crossbreed.block.VoiderBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BreedMod.MODID);

    private static VarChestBlock newChestBlock(int rows){
        return new VarChestBlock(AbstractBlock.Properties.from(Blocks.CHEST), rows);
    }
    public static final VarChestBlock CHEST4_BLOCK = newChestBlock(4),
        CHEST5_BLOCK = newChestBlock(5),
        CHEST6_BLOCK = newChestBlock(6),
        CHEST7_BLOCK = newChestBlock(7),
        CHEST8_BLOCK = newChestBlock(8);

    public static final RegistryObject<Block> VOIDER = registerBlockAndItem("voider",
            new VoiderBlock(AbstractBlock.Properties
                    .create(Material.ROCK)
                    .hardnessAndResistance(3.0f, 4.0f)
                    .sound(SoundType.STONE)
                    .setRequiresTool()),
            block -> new BlockItem(block, new Item.Properties().group(BreedMod.GROUP))),
            CHEST4 = registerBlockAndItem("chest4",
                  CHEST4_BLOCK,
                  block -> new BlockItem(block, new Item.Properties().group(BreedMod.GROUP))),
            CHEST5 = registerBlockAndItem("chest5",
                  CHEST5_BLOCK,
                  block -> new BlockItem(block, new Item.Properties().group(BreedMod.GROUP))),
            CHEST6 = registerBlockAndItem("chest6",
                  CHEST6_BLOCK,
                  block -> new BlockItem(block, new Item.Properties().group(BreedMod.GROUP))),
            CHEST7 = registerBlockAndItem("chest7",
                  CHEST7_BLOCK,
                  block -> new BlockItem(block, new Item.Properties().group(BreedMod.GROUP))),
            CHEST8 = registerBlockAndItem("chest8",
                  CHEST8_BLOCK,
                  block -> new BlockItem(block, new Item.Properties().group(BreedMod.GROUP)));

    private static RegistryObject<Block> registerBlockAndItem(String name, Block block, Function<Block, Item> function){
        if(function != null){
            ModItems.ITEMS.register(name, () -> function.apply(block));
        }
        return BLOCKS.register(name, () -> block);
    }

}

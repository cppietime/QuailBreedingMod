package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.block.VoiderBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
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

    public static final RegistryObject<Block> VOIDER = registerBlockAndItem("voider",
            new VoiderBlock(AbstractBlock.Properties
                    .create(Material.WOOD)
                    .hardnessAndResistance(3.0f, 4.0f)
                    .sound(SoundType.WOOD)),
            block -> new BlockItem(block, new Item.Properties().group(BreedMod.GROUP)));

    private static RegistryObject<Block> registerBlockAndItem(String name, Block block, Function<Block, Item> function){
        if(function != null){
            ModItems.ITEMS.register(name, () -> function.apply(block));
        }
        return BLOCKS.register(name, () -> block);
    }

}

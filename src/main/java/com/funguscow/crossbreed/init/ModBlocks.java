package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.block.GeneratorBlock;
import com.funguscow.crossbreed.block.NestBlock;
import com.funguscow.crossbreed.block.VarChestBlock;
import com.funguscow.crossbreed.block.VoiderBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BreedMod.MODID);

    private static VarChestBlock newChestBlock(int rows) {
        return new VarChestBlock(BlockBehaviour.Properties.copy(Blocks.CHEST), rows);
    }

    public static final Supplier<Block> CHEST4_BLOCK = () -> newChestBlock(4),
            CHEST5_BLOCK = () -> newChestBlock(5),
            CHEST6_BLOCK = () -> newChestBlock(6),
            CHEST7_BLOCK = () -> newChestBlock(7),
            CHEST8_BLOCK = () -> newChestBlock(8),
            QUAIL_NEST_BLOCK = () -> new NestBlock(BlockBehaviour.Properties.copy(Blocks.HAY_BLOCK)),
            GENERATOR_BLOCK = () -> new GeneratorBlock(BlockBehaviour.Properties.copy(Blocks.STONE));

    public static final RegistryObject<Block> VOIDER = registerSimpleBlockAndItem("voider",
            () -> new VoiderBlock(BlockBehaviour.Properties
                    .copy(Blocks.STONE)
                    .destroyTime(3.0f)
                    .explosionResistance(4.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())),
            CHEST4 = registerSimpleBlockAndItem("chest4",
                    CHEST4_BLOCK),
            CHEST5 = registerSimpleBlockAndItem("chest5",
                    CHEST5_BLOCK),
            CHEST6 = registerSimpleBlockAndItem("chest6",
                    CHEST6_BLOCK),
            CHEST7 = registerSimpleBlockAndItem("chest7",
                    CHEST7_BLOCK),
            CHEST8 = registerSimpleBlockAndItem("chest8",
                    CHEST8_BLOCK),
            QUAIL_NEST = registerSimpleBlockAndItem("quail_nest", QUAIL_NEST_BLOCK),
            GENERATOR = registerBlockAndItem("quail_generator", GENERATOR_BLOCK,
                    block -> new BlockItem(block, new Item.Properties()),
                    Optional.empty());

    public static RegistryObject<Block> registerBlockAndItem(String name, Supplier<Block> block, Function<Block, Item> function, Optional<ModCreativeTabs.ModCreativeTab> creativeTab) {
        RegistryObject<Block> registry = BLOCKS.register(name, block);
        if (function != null) {
            RegistryObject<Item> item = ModItems.ITEMS.register(name, () -> function.apply(registry.get()));
            creativeTab.ifPresent(modCreativeTab -> modCreativeTab.add(item));
        }
        return registry;
    }

    public static RegistryObject<Block> registerSimpleBlockAndItem(String name, Supplier<Block> block) {
        return registerBlockAndItem(name, block, b -> new BlockItem(b, new Item.Properties()), Optional.of(ModCreativeTabs.QUAIL_MOD_TAB));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

}

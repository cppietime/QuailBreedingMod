package com.funguscow.crossbreed.worldgen.botany.wood;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.block.*;
import com.funguscow.crossbreed.init.ModBlocks;
import com.funguscow.crossbreed.init.ModCreativeTabs;
import com.funguscow.crossbreed.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Each wood type will need the following:
 * <p>
 *     Blocks:
 *     Log
 *     Stripped Log
 *     Wood
 *     Stripped Wood
 *     Planks
 *     Slab
 *     Stairs
 *     Fence
 *     Fence Gate
 *     Door
 *     Trapdoor
 *     Button
 *     Pressure Plate
 *     Standing Sign
 *     Wall Sign
 *     Hanging Sign
 *     Wall Hanging Sign
 * </p>
 * <p>
 *     Items (Non-trivial):
 *     Sign
 *     Hanging Sign
 *     Door
 * </p>
 * <p>
 *     Block textures:
 *     Bark
 *     Stripped Bark
 *     Xylem
 *     Planks
 *     Door Bottom
 *     Door Top
 *     Trapdoor
 *     Sign
 *     Hanging Sign
 * </p>
 */
public class ModWoodType {

    public static final Map<String, ModWoodType> WoodTypes = new HashMap<>();
    public static final Map<ResourceLocation, RegistryObject<Block>> Strippables = new HashMap<>();

    public final String name;
    public final WoodType woodType;

    public ModWoodType(String name) {
        this.name = name;
        woodType = WoodType.register(new WoodType(BreedMod.MODID + ":" + name, BlockSetType.OAK));
        WoodTypes.put(name, this);
    }

    public void register() {
        // Register log block
        ModBlocks.registerSimpleBlockAndItem(name + "_log",
                () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        // Register stripped log block
        RegistryObject<Block> strippedLog = ModBlocks.registerSimpleBlockAndItem(name + "_stripped_log",
                () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        Strippables.put(new ResourceLocation(BreedMod.MODID, name + "_log"), strippedLog);
        // Register wood block
        ModBlocks.registerSimpleBlockAndItem(name + "_wood",
                () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        // Register stripped wood block
        RegistryObject<Block> strippedWood = ModBlocks.registerSimpleBlockAndItem(name + "_stripped_wood",
                () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        Strippables.put(new ResourceLocation(BreedMod.MODID, name + "_wood"), strippedWood);
        // Register planks
        RegistryObject<Block> planks = ModBlocks.registerSimpleBlockAndItem(name + "_planks",
                () -> new FlammableBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
        // Register slab
        ModBlocks.registerSimpleBlockAndItem(name + "_slab",
                () -> new FlammableSlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
        // Register stairs
        ModBlocks.registerSimpleBlockAndItem(name + "_stairs",
                () -> new FlammableStairBlock(() -> planks.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
        // Register fence
        ModBlocks.registerSimpleBlockAndItem(name + "_fence",
                () -> new FlammableFenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));
        // Register fence gate
        ModBlocks.registerSimpleBlockAndItem(name + "_fence_gate",
                () -> new FlammableFenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE)));
        // Register button
        ModBlocks.registerSimpleBlockAndItem(name + "_button",
                () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 30, true));
        // Register pressure plate
        ModBlocks.registerSimpleBlockAndItem(name + "_pressure_plate",
                () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE), BlockSetType.OAK));
        // Register door
        ModBlocks.registerBlockAndItem(name + "_door",
                () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR), BlockSetType.OAK),
                block -> new DoubleHighBlockItem(block, new Item.Properties()),
                Optional.of(ModCreativeTabs.QUAIL_MOD_TAB));
        // Register trapdoor
        ModBlocks.registerSimpleBlockAndItem(name + "_trapdoor",
                () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR), BlockSetType.OAK));
        // Register wall sign block
        RegistryObject<Block> wallSign = ModBlocks.BLOCKS.register(name + "_wall_sign",
                () -> new ModWallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN), woodType));
        // Register standing sign block
        RegistryObject<Block> standingSign = ModBlocks.BLOCKS.register(name + "_standing_sign",
                () -> new ModStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN), woodType));
        // Register sign item
        RegistryObject<Item> signItem = ModItems.ITEMS.register(name + "_sign",
                () -> new SignItem(new Item.Properties().stacksTo(16), standingSign.get(), wallSign.get()));
        ModCreativeTabs.QUAIL_MOD_TAB.add(signItem);
        // Register wall-hanging sign block
        RegistryObject<Block> wallHangingSign = ModBlocks.BLOCKS.register(name + "_wall_hanging_sign",
                () -> new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), woodType));
        // Register ceiling-hanging sign block
        RegistryObject<Block> ceilingHangingSign = ModBlocks.BLOCKS.register(name + "_ceiling_hanging_sign",
                () -> new ModCeilingHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), woodType));
        // Register hanging sign item
        RegistryObject<Item> hangingSignItem = ModItems.ITEMS.register(name + "_hanging_sign",
                () -> new HangingSignItem(ceilingHangingSign.get(), wallHangingSign.get(), new Item.Properties().stacksTo(16)));
        ModCreativeTabs.QUAIL_MOD_TAB.add(hangingSignItem);
    }

    public static final ModWoodType TESTWOOD = new ModWoodType("testwood");

    public static void registerAll() {
        for (ModWoodType woodType : WoodTypes.values()) {
            woodType.register();
        }
    }

}

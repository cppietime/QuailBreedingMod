package com.funguscow.crossbreed.worldgen.botany.wood;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.block.*;
import com.funguscow.crossbreed.init.ModBlocks;
import com.funguscow.crossbreed.init.ModCreativeTabs;
import com.funguscow.crossbreed.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
import java.util.List;
import java.util.Map;

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

    private RegistryObject<Block> logBlock,
        strippedLogBlock,
        woodBlock,
        strippedWoodBlock,
        planksBlock,
        slabBlock,
        stairsBlock,
        fenceBlock,
        fenceGateBlock,
        buttonBlock,
        pressurePlateBlock,
        doorBlock,
        trapdoorBlock,
        standingSignBlock,
        wallSignBlock,
        ceilingHangingSignBlock,
        wallHangingSignBlock;

    private RegistryObject<Item> doorItem,
        signItem,
        hangingSignItem;

    public final TagKey<Item> craftsToPlanks;

    public ModWoodType(String name) {
        this.name = name;
        woodType = WoodType.register(new WoodType(BreedMod.MODID + ":" + name, BlockSetType.OAK));
        WoodTypes.put(name, this);

        craftsToPlanks = ItemTags.create(new ResourceLocation(BreedMod.MODID, name + "_crafts_to_planks"));
    }

    public void register() {
        // Register log block
        logBlock = ModBlocks.registerSimpleBlockAndItem(name + "_log",
                () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        // Register stripped log block
        strippedLogBlock = ModBlocks.registerSimpleBlockAndItem(name + "_stripped_log",
                () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        Strippables.put(new ResourceLocation(BreedMod.MODID, name + "_log"), strippedLogBlock);
        // Register wood block
        woodBlock = ModBlocks.registerSimpleBlockAndItem(name + "_wood",
                () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        // Register stripped wood block
        strippedWoodBlock = ModBlocks.registerSimpleBlockAndItem(name + "_stripped_wood",
                () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        Strippables.put(new ResourceLocation(BreedMod.MODID, name + "_wood"), strippedWoodBlock);
        // Register planks
        planksBlock = ModBlocks.registerSimpleBlockAndItem(name + "_planks",
                () -> new FlammableBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
        // Register slab
        slabBlock = ModBlocks.registerSimpleBlockAndItem(name + "_slab",
                () -> new FlammableSlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
        // Register stairs
        stairsBlock = ModBlocks.registerSimpleBlockAndItem(name + "_stairs",
                () -> new FlammableStairBlock(() -> planksBlock.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
        // Register fence
        fenceBlock = ModBlocks.registerSimpleBlockAndItem(name + "_fence",
                () -> new FlammableFenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));
        // Register fence gate
        fenceGateBlock = ModBlocks.registerSimpleBlockAndItem(name + "_fence_gate",
                () -> new FlammableFenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE)));
        // Register button
        buttonBlock = ModBlocks.registerSimpleBlockAndItem(name + "_button",
                () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 30, true));
        // Register pressure plate
        pressurePlateBlock = ModBlocks.registerSimpleBlockAndItem(name + "_pressure_plate",
                () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE), BlockSetType.OAK));
        // Register door
        doorBlock = ModBlocks.BLOCKS.register(name + "_door",
                () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR), BlockSetType.OAK));
        doorItem = ModItems.ITEMS.register(name + "_door",
                () -> new DoubleHighBlockItem(doorBlock.get(), new Item.Properties()));
        ModCreativeTabs.QUAIL_MOD_TAB.add(doorItem);
        // Register trapdoor
        trapdoorBlock = ModBlocks.registerSimpleBlockAndItem(name + "_trapdoor",
                () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR), BlockSetType.OAK));
        // Register wall sign block
        wallSignBlock = ModBlocks.BLOCKS.register(name + "_wall_sign",
                () -> new ModWallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN), woodType));
        // Register standing sign block
        standingSignBlock = ModBlocks.BLOCKS.register(name + "_standing_sign",
                () -> new ModStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN), woodType));
        // Register sign item
        signItem = ModItems.ITEMS.register(name + "_sign",
                () -> new SignItem(new Item.Properties().stacksTo(16), standingSignBlock.get(), wallSignBlock.get()));
        ModCreativeTabs.QUAIL_MOD_TAB.add(signItem);
        // Register wall-hanging sign block
        wallHangingSignBlock = ModBlocks.BLOCKS.register(name + "_wall_hanging_sign",
                () -> new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), woodType));
        // Register ceiling-hanging sign block
        ceilingHangingSignBlock = ModBlocks.BLOCKS.register(name + "_ceiling_hanging_sign",
                () -> new ModCeilingHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), woodType));
        // Register hanging sign item
        hangingSignItem = ModItems.ITEMS.register(name + "_hanging_sign",
                () -> new HangingSignItem(ceilingHangingSignBlock.get(), wallHangingSignBlock.get(), new Item.Properties().stacksTo(16)));
        ModCreativeTabs.QUAIL_MOD_TAB.add(hangingSignItem);
    }

    public RegistryObject<Block> getLogBlock() {
        return logBlock;
    }

    public RegistryObject<Block> getStrippedLogBlock() {
        return strippedLogBlock;
    }

    public RegistryObject<Block> getWoodBlock() {
        return woodBlock;
    }

    public RegistryObject<Block> getStrippedWoodBlock() {
        return strippedWoodBlock;
    }

    public RegistryObject<Block> getPlanksBlock() {
        return planksBlock;
    }

    public RegistryObject<Block> getSlabBlock() {
        return slabBlock;
    }

    public RegistryObject<Block> getStairsBlock() {
        return stairsBlock;
    }

    public RegistryObject<Block> getFenceBlock() {
        return fenceBlock;
    }

    public RegistryObject<Block> getFenceGateBlock() {
        return fenceGateBlock;
    }

    public RegistryObject<Block> getButtonBlock() {
        return buttonBlock;
    }

    public RegistryObject<Block> getPressurePlateBlock() {
        return pressurePlateBlock;
    }

    public RegistryObject<Block> getDoorBlock() {
        return doorBlock;
    }

    public RegistryObject<Block> getTrapdoorBlock() {
        return trapdoorBlock;
    }

    public RegistryObject<Block> getStandingSignBlock() {
        return standingSignBlock;
    }

    public RegistryObject<Block> getWallSignBlock() {
        return wallSignBlock;
    }

    public RegistryObject<Block> getCeilingHangingSignBlock() {
        return ceilingHangingSignBlock;
    }

    public RegistryObject<Block> getWallHangingSignBlock() {
        return wallHangingSignBlock;
    }

    public RegistryObject<Item> getDoorItem() {
        return doorItem;
    }

    public RegistryObject<Item> getSignItem() {
        return signItem;
    }

    public RegistryObject<Item> getHangingSignItem() {
        return hangingSignItem;
    }

    public List<RegistryObject<Block>> getAllBlocks() {
        return List.of(
                logBlock, strippedLogBlock, woodBlock, strippedWoodBlock,
                planksBlock, stairsBlock, slabBlock,
                buttonBlock, pressurePlateBlock,
                fenceBlock, fenceGateBlock,
                doorBlock, trapdoorBlock,
                standingSignBlock, wallSignBlock, ceilingHangingSignBlock, wallHangingSignBlock
        );
    }

    public static final ModWoodType PINE = new ModWoodType("pine");
    public static final ModWoodType ALDER = new ModWoodType("alder");
    public static final ModWoodType MAPLE = new ModWoodType("maple");
    public static final ModWoodType CITRUS = new ModWoodType("citrus");
    public static final ModWoodType WALNUT = new ModWoodType("walnut");
    public static final ModWoodType EUCALYPTUS = new ModWoodType("eucalyptus");
    public static final ModWoodType EBONY = new ModWoodType("ebony");
    public static final ModWoodType MAHOGANY = new ModWoodType("mahogany");
    public static final ModWoodType SUMAC = new ModWoodType("sumac");
    public static final ModWoodType CAROB = new ModWoodType("carob");
    public static final ModWoodType APPLE = new ModWoodType("apple");
    public static final ModWoodType PLUM = new ModWoodType("plum");

    public static void registerAll() {
        for (ModWoodType woodType : WoodTypes.values()) {
            woodType.register();
        }
    }

}

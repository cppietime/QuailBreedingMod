package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.tileentities.GeneratorTileEntity;
import com.funguscow.crossbreed.tileentities.NestTileEntity;
import com.funguscow.crossbreed.tileentities.VarChestTileEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModTileEntities {

    private static Type<?> fetchBlockEntityType(String name) {
        return (Util.fetchChoiceType(References.BLOCK_ENTITY, name));
    }

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BreedMod.MODID);

    public static final Map<Integer, RegistryObject<BlockEntityType<VarChestTileEntity>>> CHEST_TYPES = new HashMap<>();

    public static final RegistryObject<BlockEntityType<?>> QUAIL_NEST = TILE_ENTITIES.register("quail_nest",
            () -> BlockEntityType.Builder.of(NestTileEntity::new, ModBlocks.QUAIL_NEST.get()).build(fetchBlockEntityType("quail_nest"))),
        GENERATOR = TILE_ENTITIES.register("quail_generator",
                () -> BlockEntityType.Builder.of(GeneratorTileEntity::new, ModBlocks.GENERATOR.get()).build(fetchBlockEntityType("quail_generator")));

    public static VarChestTileEntity newChestTileEntity(BlockPos pos, BlockState state, int rows) {
        return new VarChestTileEntity(CHEST_TYPES.get(rows).get(), pos, state, rows);
    }

    @SafeVarargs
    private static void registerChestTileEntity(int rows, Supplier<Block>... blocks) {
        String name = String.format("chest%d", rows);
        CHEST_TYPES.put(rows, TILE_ENTITIES.register(name,
                () -> BlockEntityType.Builder.of((BlockPos pos, BlockState state) -> newChestTileEntity(pos, state, rows), Arrays.stream(blocks).map(Supplier::get).toArray(Block[]::new)).build(fetchBlockEntityType(name))));
    }

    public static void register(IEventBus bus) {
        registerChestTileEntity(4, ModBlocks.CHEST4);
        registerChestTileEntity(5, ModBlocks.CHEST5);
        registerChestTileEntity(6, ModBlocks.CHEST6);
        registerChestTileEntity(7, ModBlocks.CHEST7);
        registerChestTileEntity(8, ModBlocks.CHEST8);
        TILE_ENTITIES.register(bus);
    }

}

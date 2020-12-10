package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.tileentities.VarChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModTileEntities {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BreedMod.MODID);

    public static final Map<Integer, RegistryObject<TileEntityType<VarChestTileEntity>>> CHEST_TYPES = new HashMap<>();

    public static VarChestTileEntity newChestTileEntity(int rows){
        return new VarChestTileEntity(CHEST_TYPES.get(rows).get(), rows);
    }

    private static void registerChestTileEntity(int rows, Block... blocks){
        String name = String.format("chest%d", rows);
        CHEST_TYPES.put(rows, TILE_ENTITIES.register(name,
                () -> TileEntityType.Builder.create(() -> newChestTileEntity(rows), blocks).build(Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, name))));
    }

    static {
        registerChestTileEntity(4, ModBlocks.CHEST4_BLOCK);
        registerChestTileEntity(5, ModBlocks.CHEST5_BLOCK);
        registerChestTileEntity(6, ModBlocks.CHEST6_BLOCK);
        registerChestTileEntity(7, ModBlocks.CHEST7_BLOCK);
        registerChestTileEntity(8, ModBlocks.CHEST8_BLOCK);
    }

}

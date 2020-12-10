package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.gui.ResizableChestScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BreedMod.MODID);

    public static final Map<Integer, RegistryObject<ContainerType<ChestContainer>>> CHEST_TYPES = new HashMap<>();

    public static ChestContainer newChestContainer(int id, PlayerInventory inventory, int rows){
        return new ChestContainer(CHEST_TYPES.get(rows).get(), id, inventory, new Inventory(rows * 9), rows);
    }

    public static void registerScreens(){
        for(RegistryObject<ContainerType<ChestContainer>> reg : CHEST_TYPES.values()){
            ScreenManager.registerFactory(reg.get(), ResizableChestScreen::new);
        }
    }

    private static void registerChest(int rows){
        CHEST_TYPES.put(rows, CONTAINERS.register(String.format("chest%d", rows),
                () -> new ContainerType<>((id, player) -> newChestContainer(id, player, rows))));
    }

    static{
        registerChest(4);
        registerChest(5);
        registerChest(6);
        registerChest(7);
        registerChest(8);
    }

}

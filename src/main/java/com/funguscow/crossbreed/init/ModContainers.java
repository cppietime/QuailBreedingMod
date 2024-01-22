package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.gui.ResizableChestScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModContainers {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BreedMod.MODID);

    public static final Map<Integer, RegistryObject<MenuType<ChestMenu>>> CHEST_TYPES = new HashMap<>();

    public static ChestMenu newChestContainer(int id, Inventory inventory, int rows) {
        return new ChestMenu(CHEST_TYPES.get(rows).get(), id, inventory, new SimpleContainer(rows * 9), rows);
    }

    public static void registerScreens() {
        for (RegistryObject<MenuType<ChestMenu>> reg : CHEST_TYPES.values()) {
            MenuScreens.register(reg.get(), ResizableChestScreen::new);
        }
    }

    private static void registerChest(int rows) {
        CHEST_TYPES.put(rows, CONTAINERS.register(String.format("chest%d", rows),
                () -> new MenuType<>((id, player) -> newChestContainer(id, player, rows), FeatureFlags.VANILLA_SET)));
    }

    static {
        registerChest(4);
        registerChest(5);
        registerChest(6);
        registerChest(7);
        registerChest(8);
    }

}

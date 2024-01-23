package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Wrapper for
 */
@Mod.EventBusSubscriber(modid = BreedMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB_DEFERRED_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BreedMod.MODID);

    private static final List<ModCreativeTab> tabs = new ArrayList<>();
    private static final Map<ResourceKey<CreativeModeTab>, List<Supplier<Item>>> vanillaTabItems = new TreeMap<>();

    public static class ModCreativeTab {
        private final String id;
        private final Component displayName;
        private final Supplier<ItemStack> icon;
        private final List<Supplier<Item>> items = new ArrayList<>();

        public ModCreativeTab(String id, Component displayName, Supplier<ItemStack> icon) {
            this.id = id;
            this.displayName = displayName;
            this.icon = icon;

            tabs.add(this);
        }

        public void add(Supplier<Item> item) {
            items.add(item);
        }

        public void accept(CreativeModeTab.Output output) {
            items.forEach(item -> output.accept(item.get()));
        }
    }

    public static ModCreativeTab QUAIL_MOD_TAB = new ModCreativeTab("brees_group",
            Component.translatable("itemGroup.brees_group"),
            () -> new ItemStack(ModItems.QUAIL_SPAWN_EGG.get()));

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TAB_DEFERRED_REGISTER.register(bus);
        for (final ModCreativeTab tab : tabs) {
            RegistryObject<CreativeModeTab> tabRegistryObject = CREATIVE_MODE_TAB_DEFERRED_REGISTER.register(tab.id,
                    () -> CreativeModeTab.builder()
                            .title(tab.displayName)
                            .icon(tab.icon)
                            .displayItems((params, output) -> tab.accept(output))
                            .build());
        }
    }

    public static void addItemToVanillaTab(ResourceKey<CreativeModeTab> tab, Supplier<Item> item) {
        vanillaTabItems.computeIfAbsent(tab, key -> new ArrayList<>()).add(item);
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (vanillaTabItems.containsKey(event.getTabKey())) {
            vanillaTabItems.get(event.getTabKey()).forEach(item -> event.accept(item.get()));
        }
    }

}

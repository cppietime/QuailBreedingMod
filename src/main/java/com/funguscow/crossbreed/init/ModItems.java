package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.item.BubbleItem;
import com.funguscow.crossbreed.item.GenericEggItem;
import com.funguscow.crossbreed.item.JailItem;
import com.funguscow.crossbreed.item.MeterItem;
import com.funguscow.crossbreed.tileentities.NestTileEntity;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import com.funguscow.crossbreed.worldgen.botany.leaves.GeneticLeafBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BreedMod.MODID);

    public static final RegistryObject<Item> QUAIL_EGG = ITEMS.register("quail_egg",
            () -> new GenericEggItem(new Item.Properties(),
                    1,
                    1000,
                    BreedMod.MODID + ":quail",
                    BreedMod.MODID + ":quail_egg")),
            WATER_BUBBLE = ITEMS.register("water_bubble",
                    () -> new BubbleItem(new Item.Properties(),
                            Fluids.WATER)),
            LAVA_BUBBLE = ITEMS.register("lava_bubble",
                    () -> new BubbleItem(new Item.Properties(),
                            Fluids.LAVA)),
            QUAIL_JAIL = ITEMS.register("quail_jail",
                    () -> new JailItem(new Item.Properties().stacksTo(1), false)),
            STRONG_QUAIL_JAIL = ITEMS.register("strong_quail_jail",
                    () -> new JailItem(new Item.Properties().stacksTo(1), true)),
            PICKLED_EGG = ITEMS.register("pickled_egg",
                    () -> new Item(new Item.Properties().food(
                            new FoodProperties.Builder().nutrition(2).saturationMod(0.8f).build()))),
            FRIED_EGG = ITEMS.register("fried_egg",
                    () -> new Item(new Item.Properties().food(
                            new FoodProperties.Builder().nutrition(3).saturationMod(0.4f).build()))),
            RAW_QUAIL = ITEMS.register("raw_quail",
                    () -> new Item(new Item.Properties().food(Foods.CHICKEN))),
            COOKED_QUAIL = ITEMS.register("cooked_quail",
                    () -> new Item(new Item.Properties().food(Foods.COOKED_CHICKEN))),
            QUAIL_METER = ITEMS.register("quail_meter",
                    () -> new MeterItem(new Item.Properties().stacksTo(1))),
            QUAIL_SPAWN_EGG = ITEMS.register("quail_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.QUAIL, 0x734011, 0xa4b5bd, new Item.Properties().stacksTo(16))),
            MANURE = ITEMS.register("manure",
                    () -> new Item(new Item.Properties()));

    public static void matchConfig() {
        GenericEggItem quailEgg = (GenericEggItem) QUAIL_EGG.get();
        quailEgg.updateOdds(QuailConfig.COMMON.quailEggChance.get(), QuailConfig.COMMON.quailEggMultiChance.get());
    }

    public static void register(IEventBus bus) {
        // Invoke procedural item registration from other classes.
        TreeSpecies.registerItems();
        GeneticLeafBlock.registerItems();

        ITEMS.register(bus);
        ModCreativeTabs.QUAIL_MOD_TAB.add(QUAIL_EGG);
        ModCreativeTabs.QUAIL_MOD_TAB.add(WATER_BUBBLE);
        ModCreativeTabs.QUAIL_MOD_TAB.add(LAVA_BUBBLE);
        ModCreativeTabs.QUAIL_MOD_TAB.add(QUAIL_JAIL);
        ModCreativeTabs.QUAIL_MOD_TAB.add(STRONG_QUAIL_JAIL);
        ModCreativeTabs.QUAIL_MOD_TAB.add(RAW_QUAIL);
        ModCreativeTabs.QUAIL_MOD_TAB.add(COOKED_QUAIL);
        ModCreativeTabs.QUAIL_MOD_TAB.add(PICKLED_EGG);
        ModCreativeTabs.QUAIL_MOD_TAB.add(FRIED_EGG);
        ModCreativeTabs.QUAIL_MOD_TAB.add(QUAIL_METER);
        ModCreativeTabs.QUAIL_MOD_TAB.add(QUAIL_SPAWN_EGG);
        ModCreativeTabs.QUAIL_MOD_TAB.add(MANURE);

        ModCreativeTabs.addItemToVanillaTab(CreativeModeTabs.SPAWN_EGGS, QUAIL_SPAWN_EGG);
    }

    public static void registerDispenser() {
        DefaultDispenseItemBehavior bubbleBehavior = new DefaultDispenseItemBehavior() {
            @Override
            protected @NotNull ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BubbleItem bubble = (BubbleItem)itemStack.getItem();
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                if (bubble.tryPlaceContainedLiquid(null, blockSource.level(), pos, null)) {
                    itemStack.shrink(1);
                    return itemStack;
                }
                return super.execute(blockSource, itemStack);
            }
        };
        DispenserBlock.registerBehavior(WATER_BUBBLE.get(), bubbleBehavior);
        DispenserBlock.registerBehavior(LAVA_BUBBLE.get(), bubbleBehavior);

        OptionalDispenseItemBehavior jailBehavior = new OptionalDispenseItemBehavior() {
            @Override
            protected @NotNull ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                JailItem item = (JailItem)itemStack.getItem();
                CompoundTag jailedTag = itemStack.getTagElement(JailItem.JAILED_TAG_KEY);
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                Level level = blockSource.level();
                BlockEntity entity = level.getBlockEntity(pos);
                if (!(entity instanceof NestTileEntity nest)) {
                    if (jailedTag == null) {
                        // Attempt to capture a quail
                        for (QuailEntity quail : level.getEntitiesOfClass(QuailEntity.class, new AABB(pos), QuailEntity::isAlive)) {
                            setSuccess(true);
                            return item.captureQuail(itemStack, quail);
                        }
                    } else {
                        // Release a quail
                        if (item.releaseQuail(level, pos, itemStack, null)) {
                            setSuccess(true);
                            if (item.isReusable()) {
                                ItemStack empty = new ItemStack(item, 1);
                                empty.removeTagKey(JailItem.JAILED_TAG_KEY);
                                return empty;
                            }
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    // Nest is in front of dispenser
                    if (jailedTag == null) {
                        // Attempt to withdraw a quail
                        CompoundTag quail = nest.getQuail();
                        if (quail != null) {
                            setSuccess(true);
                            ItemStack jailed = new ItemStack(item, 1);
                            jailed.addTagElement(JailItem.JAILED_TAG_KEY, quail);
                            return jailed;
                        }
                    } else {
                        // Deposit the quail
                        if (item.depositQuail(itemStack, nest)) {
                            setSuccess(true);
                            if (item.isReusable()) {
                                ItemStack empty = new ItemStack(item, 1);
                                empty.removeTagKey(JailItem.JAILED_TAG_KEY);
                                return empty;
                            }
                            return ItemStack.EMPTY;
                        }
                    }
                }
                return super.execute(blockSource, itemStack);
            }
        };
        DispenserBlock.registerBehavior(QUAIL_JAIL.get(), jailBehavior);
        DispenserBlock.registerBehavior(STRONG_QUAIL_JAIL.get(), jailBehavior);
    }

    public static void registerCompost() {
        ComposterBlock.COMPOSTABLES.put(MANURE.get(), QuailConfig.COMMON.manureCompostValue.get().floatValue());
    }

}

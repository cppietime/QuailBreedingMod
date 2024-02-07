package com.funguscow.crossbreed.item;

import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.init.ModCreativeTabs;
import com.funguscow.crossbreed.init.ModItems;
import com.funguscow.crossbreed.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FruitItem extends Item {

    public FruitItem(Properties pProperties) {
        super(pProperties);
    }

    private QuailConfig.Common.FruitItemConfig getConfig() {
        return QuailConfig.COMMON.fruitItems.get(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this)).getPath());
    }

    @Override
    public boolean isEdible() {
        return getConfig().enabled.get();
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        QuailConfig.Common.FruitItemConfig config = getConfig();
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(config.nutrition.get()).saturationMod(config.saturationMod.get().floatValue());
        if (config.alwaysEat.get()) {
            builder.alwaysEat();
        }
        if (config.fast.get()) {
            builder.fast();
        }
        return builder.build();
    }

    public static class FruitDef {
        public final String name;
        public final int nutrition;
        public final float saturation;
        public final boolean enabled, alwaysEdible, fast;
        public final List<Pair<MobEffectInstance, Float>> effects = new ArrayList<>();

        private FruitDef(String name, int nutrition, float saturation, boolean enabled, boolean alwaysEdible, boolean fast) {
            this.name = name;
            this.nutrition = nutrition;
            this.saturation = saturation;
            this.enabled = enabled;
            this.alwaysEdible = alwaysEdible;
            this.fast = fast;
        }

        private FruitDef(String name, int nutrition, float saturation) {
            this(name, nutrition, saturation, true, false, false);
        }

        private FruitDef(String name) {
            this(name, 1, 0.5f);
        }

        private FruitDef(String name, boolean edible) {
            this(name, 1, 0.5f, edible, false, false);
        }

        public FruitDef addEffect(MobEffectInstance effect, float probability) {
            this.effects.add(new Pair<>(effect, probability));
            return this;
        }
    }

    public static final List<FruitDef> FruitDefs = List.of(
            new FruitDef("cherry", 2, .3f),
            new FruitDef("acorn", 1, .5f),
            new FruitDef("black_walnut", 2, .3f),
            new FruitDef("pear", 4, .3f),
            new FruitDef("orange", 4, .3f),
            new FruitDef("bitter_orange", 2, .6f),
            new FruitDef("mandarin", 4, .3f),
            new FruitDef("pomelo", 4, .3f),
            new FruitDef("citron", 4, .3f),
            new FruitDef("papeda", 4, .3f),
            new FruitDef("kumquat", 2, .3f),
            new FruitDef("lemon", 2, .3f),
            new FruitDef("grapefruit", 4, .3f),
            new FruitDef("lime", 2, .3f),
            new FruitDef("persimmon", 4, .3f),
            new FruitDef("pomegranate", 4, .3f),
            new FruitDef("plum", 4, .3f),
            new FruitDef("almond", 2, .6f),
            new FruitDef("apricot", 4, .3f),
            new FruitDef("peach", 4, .3f),
            new FruitDef("mango", 4, .3f),
            new FruitDef("pistachio", 2, .6f),
            new FruitDef("cashew", 2, .6f),
            new FruitDef("lychee", 2, .3f),
            new FruitDef("rambutan", 2, .3f),
            new FruitDef("ackee", 4, .6f).addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f),
            new FruitDef("sumac_berry", 1, .3f),
            new FruitDef("hazelnut", 2, .6f),
            new FruitDef("hickory_nut", 2, .6f),
            new FruitDef("pecan", 4, .6f),
            new FruitDef("quince", 4, .3f),
            new FruitDef("yew_berry", 2, .3f).addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f),
            new FruitDef("clove", 1, .3f),
            new FruitDef("allspice", 1, .3f),
            new FruitDef("carob_bean", 1, .3f),
            new FruitDef("durian", 6, .3f),
            new FruitDef("horse_chestnut", 0, 0f).addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0), 1f),
            new FruitDef("beech_nut", 2, .3f)
    );

    public static final List<RegistryObject<Item>> Fruits = FruitDefs.stream().map(
            fruitDef -> {
                RegistryObject<Item> registryObject = ModItems.ITEMS.register(fruitDef.name, () -> new FruitItem(new Item.Properties()));
                ModCreativeTabs.QUAIL_MOD_TAB.add(registryObject);
                return registryObject;
            }
    ).toList();
}

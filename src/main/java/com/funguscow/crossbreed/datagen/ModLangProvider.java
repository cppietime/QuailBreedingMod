package com.funguscow.crossbreed.datagen;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.init.ModItems;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import com.funguscow.crossbreed.worldgen.botany.wood.ModWoodType;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output, String locale) {
        super(output, BreedMod.MODID, locale);
    }

    public static String toProperCase(String name) {
        return Arrays.stream(
                        name.replace("_", " ").split("\\s+"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" "));
    }

    @Override
    protected void addTranslations() {
        JsonReader old = Json.createReader(new StringReader(OriginalLang));
        JsonObject obj = old.readObject();
        obj.forEach((key, value) -> add(key, ((JsonString)value).getString()));

        add("text.breesources.recipe_category.quail_breeding_recipe_type", "Quail Breeding");
        add("text.breesources.recipe_category.tree_breeding_recipe_type", "Tree Breeding");

        add(ModItems.SWAB.get(), "Pollen Swab");

        add("block.breesources.apple_leaves", "Apple Leaves");
        add("block.breesources.nut_leaves", "Nut Leaves");
        add("block.breesources.red_maple_leaves", "Red Maple Leaves");

        for (ModWoodType woodType : ModWoodType.WoodTypes.values()) {
            String properName = woodType.displayName.isEmpty() ? toProperCase(woodType.name) : woodType.displayName;
            add(woodType.getLogBlock().get(), properName + " Log");
            add(woodType.getStrippedLogBlock().get(), "Stripped " + properName + " Log");
            add(woodType.getWoodBlock().get(), properName + " Wood");
            add(woodType.getStrippedWoodBlock().get(), "Stripped " + properName + " Wood");
            add(woodType.getPlanksBlock().get(), properName + " Planks");
            add(woodType.getStairsBlock().get(), properName + " Stairs");
            add(woodType.getSlabBlock().get(), properName + " Slab");
            add(woodType.getFenceBlock().get(), properName + " Fence");
            add(woodType.getFenceGateBlock().get(), properName + " Fence Gate");
            add(woodType.getButtonBlock().get(), properName + " Button");
            add(woodType.getPressurePlateBlock().get(), properName + " Pressure Plate");
            add(woodType.getDoorBlock().get(), properName + " Door");
            add(woodType.getTrapdoorBlock().get(), properName + " Trapdoor");
            add(woodType.getSignItem().get(), properName + " Sign");
            add(woodType.getHangingSignItem().get(), properName + " Hanging Sign");
        }
        for (TreeSpecies species : TreeSpecies.Species.values()) {
            String display = species.lang.isEmpty() ? toProperCase(species.id) : species.lang;
            add(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(species.sapling)), display + " Sapling");
            add("text.breesources.species." + species.id, display);
        }
    }

    public static final String OriginalLang = """
            {  "entity.breesources.quail": "Quail",
              "entity.breesources.generic_egg": "Modded Egg",
              "item.breesources.quail_egg": "Quail Egg",
              "item.breesources.water_bubble": "Water Bubble",
              "item.breesources.lava_bubble": "Lava Bubble",
              "item.breesources.quail_spawn_egg": "Quail Spawn Egg",
              "item.breesources.quail_jail": "Quail Jail",
              "item.breesources.strong_quail_jail": "Reusable Quail Jail",
              "item.breesources.pickled_egg": "Pickled Egg",
              "item.breesources.fried_egg": "Fried Egg",
              "item.breesources.raw_quail": "Raw Quail",
              "item.breesources.cooked_quail": "Cooked Quail",
              "item.breesources.quail_meter": "Quail Meter",
              "item.breesources.manure": "Manure",
              "block.breesources.voider": "Voider",
              "block.breesources.chest4": "4-Shelf Rack",
              "block.breesources.chest5": "5-Shelf Rack",
              "block.breesources.chest6": "6-Shelf Rack",
              "block.breesources.chest7": "7-Shelf Rack",
              "block.breesources.chest8": "8-Shelf Rack",
              "block.breesources.quail_nest": "Quail Nest",
              "block.breesources.g_oak_leaves": "Oak Leaves",
              "block.breesources.g_birch_leaves": "Birch Leaves",
              "block.breesources.g_spruce_leaves": "Spruce Leaves",
              "block.breesources.g_jungle_leaves": "Jungle Leaves",
              "block.breesources.g_dark_oak_leaves": "Dark Oak Leaves",
              "block.breesources.g_acacia_leaves": "Acacia Leaves",
              "block.breesources.g_mangrove_leaves": "Mangrove Leaves",
              "block.breesources.g_cherry_leaves": "Cherry Leaves",
              "item.breesources.voider": "Voider",
              "item.breesources.chest4": "4-Shelf Rack",
              "item.breesources.chest5": "5-Shelf Rack",
              "item.breesources.chest6": "6-Shelf Rack",
              "item.breesources.chest7": "7-Shelf Rack",
              "item.breesources.chest8": "8-Shelf Rack",
              "item.breesources.quail_nest": "Quail Nest",
              "itemGroup.brees_group": "Quail Breeding Items",
              "breesources:container.var_chest": "Storage Shelves",
              "breesources:container.quail_nest": "Quail Nest",
              "breesources.subtitle.quail_ambient": "Quail chirps",
              "breesources.subtitle.quail_hurt": "Quail hurts",
              "breesources.subtitle.quail_die": "Quail dies",
              "breesources.subtitle.quail_step": "Quail steps",
              "breesources.subtitle.quail_plop": "Quail plops",
              "breesources.subtitle.quail_milk": "Quail milked",
              "text.breesources.multiplier": "%s x%d",
              "text.breesources.stat.amount": "Egg Amount Modifier: x%s",
              "text.breesources.stat.amountRandom": "Egg Amount Modifier Variation: x%s",
              "text.breesources.stat.time": "Egg Cooldown Modifier: x%s",
              "text.breesources.stat.timeRandom": "Egg Cooldown Modifier Variation: x%s",
              "text.breesources.stat.fecundity": "Fecundity: x%s",
              "text.breesources.stat.eggTimer": "Time Until Next Egg: %s",
              "text.breesources.stat.species": "Species: %s",
              "text.breesources.stat.trunkWidth": "Trunk Width: %1$sx%1$s",
              "text.breesources.empty": "Empty",
              "text.breesources.onetime": "One-time use",
              "text.breesources.breed.painted": "Painted Quail",
              "text.breesources.breed.brown": "Brown Quail",
              "text.breesources.breed.elegant": "Elegant Quail",
              "text.breesources.breed.bobwhite": "Northern Bobwhite",
              "text.breesources.breed.cobble": "Cobblestone Quail",
              "text.breesources.breed.dirt": "Dirt Quail",
              "text.breesources.breed.sand": "Sand Quail",
              "text.breesources.breed.gravel": "Gravel Quail",
              "text.breesources.breed.netherrack": "Netherrack Quail",
              "text.breesources.breed.clay": "Clay Quail",
              "text.breesources.breed.oak": "Oak Quail",
              "text.breesources.breed.spruce": "Spruce Quail",
              "text.breesources.breed.birch": "Birch Quail",
              "text.breesources.breed.jungle": "Jungle Quail",
              "text.breesources.breed.acacia": "Acacia Quail",
              "text.breesources.breed.dark_oak": "Dark Oak Quail",
              "text.breesources.breed.coal": "Coal Quail",
              "text.breesources.breed.quartz": "Nether Quarts Quail",
              "text.breesources.breed.apple": "Apple Quail",
              "text.breesources.breed.reeds": "Sugar Cane Quail",
              "text.breesources.breed.feather": "Feather Quail",
              "text.breesources.breed.string": "String Quail",
              "text.breesources.breed.ink": "Ink Quail",
              "text.breesources.breed.bone": "Bone Quail",
              "text.breesources.breed.beet": "Beetroot Quail",
              "text.breesources.breed.flower": "Flower Quail",
              "text.breesources.breed.lapis": "Lapis Lazuli Quail",
              "text.breesources.breed.cactus": "Cactus Quail",
              "text.breesources.breed.cocoa": "Cocoa Bean Quail",
              "text.breesources.breed.iron": "Iron Quail",
              "text.breesources.breed.redstone": "Redstone Quail",
              "text.breesources.breed.soulsand": "Soul Sand Quail",
              "text.breesources.breed.wheat": "Wheat Quail",
              "text.breesources.breed.melon": "Melon Quail",
              "text.breesources.breed.pumpkin": "Pumpkin Quail",
              "text.breesources.breed.carrot": "Carrot Quail",
              "text.breesources.breed.potato": "Potato Quail",
              "text.breesources.breed.water": "Water Quail",
              "text.breesources.breed.leather": "Leather Quail",
              "text.breesources.breed.terracotta": "Terracotta Quail",
              "text.breesources.breed.snowball": "Snowball Quail",
              "text.breesources.breed.fish": "Fish Quail",
              "text.breesources.breed.rabbit": "Rabbit Quail",
              "text.breesources.breed.turtle": "Turtle Quail",
              "text.breesources.breed.grass": "Grass Quail",
              "text.breesources.breed.redshroom": "Red Mushroom Quail",
              "text.breesources.breed.brownshroom": "Brown Mushroom Quail",
              "text.breesources.breed.endstone": "End Stone Quail",
              "text.breesources.breed.gold": "Gold Quail",
              "text.breesources.breed.lava": "Lava Quail",
              "text.breesources.breed.gunpowder": "Gunpowder Quail",
              "text.breesources.breed.spidereye": "Spider Eye Quail",
              "text.breesources.breed.slime": "Slime Quail",
              "text.breesources.breed.wart": "Wart Quail",
              "text.breesources.breed.glass": "Glass Quail",
              "text.breesources.breed.basalt": "Basalt Quail",
              "text.breesources.breed.ice": "Ice Quail",
              "text.breesources.breed.glowstone": "Glowstone Quail",
              "text.breesources.breed.emerald": "Emerald Quail",
              "text.breesources.breed.obsidian": "Obsidian Quail",
              "text.breesources.breed.blaze": "Blaze Quail",
              "text.breesources.breed.warped_nyl": "Warped Nylium Quail",
              "text.breesources.breed.crimson_nyl": "Crimsono Nylium Quail",
              "text.breesources.breed.mycelium": "Mycelium Quail",
              "text.breesources.breed.honey": "Honey Quail",
              "text.breesources.breed.ghast": "Ghast Quail",
              "text.breesources.breed.blackstone": "Blackstone Quail",
              "text.breesources.breed.coral": "Coral Quail",
              "text.breesources.breed.packed_ice": "Packed Ice Quail",
              "text.breesources.breed.diamond": "Diamond Quail",
              "text.breesources.breed.pearl": "Ender Pearl Quail",
              "text.breesources.breed.shulker": "Shulker Shell Quail",
              "text.breesources.breed.nautilus": "Nautilus Shell Quail",
              "text.breesources.breed.prism": "Prismarine Quail",
              "text.breesources.breed.membrane": "Phantom Membrane Quail",
              "text.breesources.breed.wither_rose": "Wither Rose Quail",
              "text.breesources.breed.chorus": "Chorus Quail",
              "text.breesources.breed.blue_ice": "Blue Ice Quail",
              "text.breesources.breed.warped_stem": "Warped Stem Quail",
              "text.breesources.breed.crimson_stem": "Crimson Stem Quail",
              "text.breesources.breed.wither_star": "Wither Quail",
              "text.breesources.breed.heart_of_sea": "Ocean Quail",
              "text.breesources.breed.debris": "Netherite Quail",
              "text.breesources.breed.dragon": "Ender Dragon Quail",
              "text.breesources.breed.book": "Enchanted Quail",
              "text.breesources.breed.white_dye": "White Dye Quail",
              "text.breesources.breed.white_concrete_powder": "White Concrete Powder Quail",
              "text.breesources.breed.white_concrete": "White Concrete Quail",
              "text.breesources.breed.white_terracotta": "White Terracotta Quail",
              "text.breesources.breed.white_glass": "White Glass Quail",
              "text.breesources.breed.white_wool": "White Wool Quail",
              "text.breesources.breed.black_dye": "Black Dye Quail",
              "text.breesources.breed.black_concrete_powder": "Black Concrete Powder Quail",
              "text.breesources.breed.black_concrete": "Black Concrete Quail",
              "text.breesources.breed.black_terracotta": "Black Terracotta Quail",
              "text.breesources.breed.black_glass": "Black Glass Quail",
              "text.breesources.breed.black_wool": "Black Wool Quail",
              "text.breesources.breed.red_dye": "Red Dye Quail",
              "text.breesources.breed.red_concrete_powder": "Red Concrete Powder Quail",
              "text.breesources.breed.red_concrete": "Red Concrete Quail",
              "text.breesources.breed.red_terracotta": "Red Terracotta Quail",
              "text.breesources.breed.red_glass": "Red Glass Quail",
              "text.breesources.breed.red_wool": "Red Wool Quail",
              "text.breesources.breed.green_dye": "Green Dye Quail",
              "text.breesources.breed.green_concrete_powder": "Green Concrete Powder Quail",
              "text.breesources.breed.green_concrete": "Green Concrete Quail",
              "text.breesources.breed.green_terracotta": "Green Terracotta Quail",
              "text.breesources.breed.green_glass": "Green Glass Quail",
              "text.breesources.breed.green_wool": "Green Wool Quail",
              "text.breesources.breed.blue_dye": "Blue Dye Quail",
              "text.breesources.breed.blue_concrete_powder": "Blue Concrete Powder Quail",
              "text.breesources.breed.blue_concrete": "Blue Concrete Quail",
              "text.breesources.breed.blue_terracotta": "Blue Terracotta Quail",
              "text.breesources.breed.blue_glass": "Blue Glass Quail",
              "text.breesources.breed.blue_wool": "Blue Wool Quail",
              "text.breesources.breed.yellow_dye": "Yellow Dye Quail",
              "text.breesources.breed.yellow_concrete_powder": "Yellow Concrete Powder Quail",
              "text.breesources.breed.yellow_concrete": "Yellow Concrete Quail",
              "text.breesources.breed.yellow_terracotta": "Yellow Terracotta Quail",
              "text.breesources.breed.yellow_glass": "Yellow Glass Quail",
              "text.breesources.breed.yellow_wool": "Yellow Wool Quail",
              "text.breesources.breed.brown_dye": "Brown Dye Quail",
              "text.breesources.breed.brown_concrete_powder": "Brown Concrete Powder Quail",
              "text.breesources.breed.brown_concrete": "Brown Concrete Quail",
              "text.breesources.breed.brown_terracotta": "Brown Terracotta Quail",
              "text.breesources.breed.brown_glass": "Brown Glass Quail",
              "text.breesources.breed.brown_wool": "Brown Wool Quail",
              "text.breesources.breed.gray_dye": "Gray Dye Quail",
              "text.breesources.breed.gray_concrete_powder": "Gray Concrete Powder Quail",
              "text.breesources.breed.gray_concrete": "Gray Concrete Quail",
              "text.breesources.breed.gray_terracotta": "Gray Terracotta Quail",
              "text.breesources.breed.gray_glass": "Gray Glass Quail",
              "text.breesources.breed.gray_wool": "Gray Wool Quail",
              "text.breesources.breed.pink_dye": "Pink Dye Quail",
              "text.breesources.breed.pink_concrete_powder": "Pink Concrete Powder Quail",
              "text.breesources.breed.pink_concrete": "Pink Concrete Quail",
              "text.breesources.breed.pink_terracotta": "Pink Terracotta Quail",
              "text.breesources.breed.pink_glass": "Pink Glass Quail",
              "text.breesources.breed.pink_wool": "Pink Wool Quail",
              "text.breesources.breed.lime_dye": "Lime Dye Quail",
              "text.breesources.breed.lime_concrete_powder": "Lime Concrete Powder Quail",
              "text.breesources.breed.lime_concrete": "Lime Concrete Quail",
              "text.breesources.breed.lime_terracotta": "Lime Terracotta Quail",
              "text.breesources.breed.lime_glass": "Lime Glass Quail",
              "text.breesources.breed.lime_wool": "Lime Wool Quail",
              "text.breesources.breed.light_blue_dye": "Light_blue Dye Quail",
              "text.breesources.breed.light_blue_concrete_powder": "Light_blue Concrete Powder Quail",
              "text.breesources.breed.light_blue_concrete": "Light_blue Concrete Quail",
              "text.breesources.breed.light_blue_terracotta": "Light_blue Terracotta Quail",
              "text.breesources.breed.light_blue_glass": "Light_blue Glass Quail",
              "text.breesources.breed.light_blue_wool": "Light_blue Wool Quail",
              "text.breesources.breed.orange_dye": "Orange Dye Quail",
              "text.breesources.breed.orange_concrete_powder": "Orange Concrete Powder Quail",
              "text.breesources.breed.orange_concrete": "Orange Concrete Quail",
              "text.breesources.breed.orange_terracotta": "Orange Terracotta Quail",
              "text.breesources.breed.orange_glass": "Orange Glass Quail",
              "text.breesources.breed.orange_wool": "Orange Wool Quail",
              "text.breesources.breed.purple_dye": "Purple Dye Quail",
              "text.breesources.breed.purple_concrete_powder": "Purple Concrete Powder Quail",
              "text.breesources.breed.purple_concrete": "Purple Concrete Quail",
              "text.breesources.breed.purple_terracotta": "Purple Terracotta Quail",
              "text.breesources.breed.purple_glass": "Purple Glass Quail",
              "text.breesources.breed.purple_wool": "Purple Wool Quail",
              "text.breesources.breed.cyan_dye": "Cyan Dye Quail",
              "text.breesources.breed.cyan_concrete_powder": "Cyan Concrete Powder Quail",
              "text.breesources.breed.cyan_concrete": "Cyan Concrete Quail",
              "text.breesources.breed.cyan_terracotta": "Cyan Terracotta Quail",
              "text.breesources.breed.cyan_glass": "Cyan Glass Quail",
              "text.breesources.breed.cyan_wool": "Cyan Wool Quail",
              "text.breesources.breed.magenta_dye": "Magenta Dye Quail",
              "text.breesources.breed.magenta_concrete_powder": "Magenta Concrete Powder Quail",
              "text.breesources.breed.magenta_concrete": "Magenta Concrete Quail",
              "text.breesources.breed.magenta_terracotta": "Magenta Terracotta Quail",
              "text.breesources.breed.magenta_glass": "Magenta Glass Quail",
              "text.breesources.breed.magenta_wool": "Magenta Wool Quail",
              "text.breesources.breed.light_gray_dye": "Light_gray Dye Quail",
              "text.breesources.breed.light_gray_concrete_powder": "Light_gray Concrete Powder Quail",
              "text.breesources.breed.light_gray_concrete": "Light_gray Concrete Quail",
              "text.breesources.breed.light_gray_terracotta": "Light_gray Terracotta Quail",
              "text.breesources.breed.light_gray_glass": "Light_gray Glass Quail",
              "text.breesources.breed.light_gray_wool": "Light_gray Wool Quail",
              "text.breesources.breed.tin": "Tin Quail",
              "text.breesources.breed.copper": "Copper Quail",
              "text.breesources.breed.lead": "Lead Quail",
              "text.breesources.breed.aluminum": "Aluminum Quail",
              "text.breesources.breed.rubber": "Rubber Quail",
              "text.breesources.breed.silicon": "Silicon Quail",
              "text.breesources.breed.silver": "Silver Quail",
              "text.breesources.breed.uranium": "Uranium Quail",
              "text.breesources.breed.ruby": "Ruby Quail",
              "text.breesources.breed.sapphire": "Sapphire Quail",
              "text.breesources.breed.music": "Music Quail",
              "text.breesources.breed.mangrove": "Mangrove Quail",
              "text.breesources.breed.mud": "Mud Quail",
              "text.breesources.breed.amethyst": "Amethyst Quail",
              "text.breesources.breed.deepslate": "Deepslate Quail",
              "text.breesources.breed.moss": "Moss Quail",
              "text.breesources.breed.glow": "Glowing Quail",
              "text.breesources.breed.sculk": "Sculk Quail",
              "text.breesources.breed.sculk_sensor": "Deep Dark Quail",
              "text.breesources.breed.cherry": "Cherry Quail",
              "text.breesources.breed.sherd": "Pottery Quail",
              "text.breesources.breed.torchflower": "Torchflower Quail",
              "text.breesources.breed.pitcher": "Pitcher Plant Quail"}""";
}
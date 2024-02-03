package com.funguscow.crossbreed.datagen;

import com.funguscow.crossbreed.init.ModItems;
import com.funguscow.crossbreed.worldgen.botany.wood.ModWoodType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SWAB.get())
                .pattern("W")
                .pattern("I")
                .pattern("I")
                .define('W', ItemTags.WOOL)
                .define('I', Items.STICK)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(ItemTags.WOOL),
                        ItemPredicate.Builder.item().of(Items.STICK)
                ))
                .save(output);

        for (ModWoodType woodType : ModWoodType.WoodTypes.values()) {
            // Planks
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, woodType.getPlanksBlock().get(), 4)
                    .requires(woodType.craftsToPlanks)
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(
                            ItemPredicate.Builder.item().of(woodType.craftsToPlanks)
                    ))
                    .save(output);
            // Wood
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, woodType.getWoodBlock().get(), 3)
                    .pattern("XX")
                    .pattern("XX")
                    .define('X', woodType.getLogBlock().get())
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(woodType.getLogBlock().get()))
                    .save(output);
            // Stairs
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, woodType.getStairsBlock().get(), 4)
                    .pattern("X  ")
                    .pattern("XX ")
                    .pattern("XXX")
                    .define('X', woodType.getPlanksBlock().get())
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(woodType.getPlanksBlock().get()))
                    .save(output);
            // Slab
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, woodType.getSlabBlock().get(), 6)
                    .pattern("XXX")
                    .define('X', woodType.getPlanksBlock().get())
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(woodType.getPlanksBlock().get()))
                    .save(output);
            // Pressure Plate
            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, woodType.getPressurePlateBlock().get(), 1)
                    .pattern("XX")
                    .define('X', woodType.getPlanksBlock().get())
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(woodType.getPlanksBlock().get()))
                    .save(output);
            // Button
            ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, woodType.getButtonBlock().get(), 1)
                    .requires(woodType.getPlanksBlock().get())
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(woodType.getPlanksBlock().get()))
                    .save(output);
            // Door
            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, woodType.getDoorItem().get(), 3)
                    .pattern("XX")
                    .pattern("XX")
                    .pattern("XX")
                    .define('X', woodType.getPlanksBlock().get())
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(woodType.getPlanksBlock().get()))
                    .save(output);
            // Trapdoor
            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, woodType.getTrapdoorBlock().get(), 2)
                    .pattern("XXX")
                    .pattern("XXX")
                    .define('X', woodType.getPlanksBlock().get())
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(woodType.getPlanksBlock().get()))
                    .save(output);
            // Fence
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, woodType.getFenceBlock().get(), 3)
                    .pattern("XIX")
                    .pattern("XIX")
                    .define('X', woodType.getPlanksBlock().get())
                    .define('I', Items.STICK)
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(
                            woodType.getPlanksBlock().get(),
                            Items.STICK
                    ))
                    .save(output);
            // Fence Gate
            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, woodType.getFenceGateBlock().get(), 1)
                    .pattern("IXI")
                    .pattern("IXI")
                    .define('X', woodType.getPlanksBlock().get())
                    .define('I', Items.STICK)
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(
                            woodType.getPlanksBlock().get(),
                            Items.STICK
                    ))
                    .save(output);
            // Sign
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, woodType.getSignItem().get(), 3)
                    .pattern("XXX")
                    .pattern("XXX")
                    .pattern(" I ")
                    .define('X', woodType.getPlanksBlock().get())
                    .define('I', Items.STICK)
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(
                            woodType.getPlanksBlock().get(),
                            Items.STICK
                    ))
                    .save(output);
            // Hanging Sign
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, woodType.getHangingSignItem().get(), 6)
                    .pattern("I I")
                    .pattern("XXX")
                    .pattern("XXX")
                    .define('X', woodType.getStrippedLogBlock().get())
                    .define('I', Items.CHAIN)
                    .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(
                            woodType.getStrippedLogBlock().get(),
                            Items.CHAIN
                    ))
                    .save(output);
        }
    }
}

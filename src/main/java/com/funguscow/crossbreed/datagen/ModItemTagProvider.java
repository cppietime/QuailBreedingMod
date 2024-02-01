package com.funguscow.crossbreed.datagen;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import com.funguscow.crossbreed.worldgen.botany.wood.ModWoodType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, BreedMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        for (ModWoodType woodType : ModWoodType.WoodTypes.values()) {
            tag(ItemTags.LOGS_THAT_BURN).add(
                    woodType.getLogBlock().get().asItem(),
                    woodType.getWoodBlock().get().asItem(),
                    woodType.getStrippedLogBlock().get().asItem(),
                    woodType.getStrippedWoodBlock().get().asItem()
            );
            tag(ItemTags.PLANKS).add(woodType.getPlanksBlock().get().asItem());
        }
        tag(ItemTags.SAPLINGS).add(TreeSpecies.Saplings.stream().map(ro -> ro.get().asItem()).toArray(Item[]::new));
    }
}

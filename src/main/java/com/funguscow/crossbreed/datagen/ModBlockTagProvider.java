package com.funguscow.crossbreed.datagen;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import com.funguscow.crossbreed.worldgen.botany.wood.ModWoodType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, BreedMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        for (ModWoodType woodType : ModWoodType.WoodTypes.values()) {
            tag(BlockTags.LOGS_THAT_BURN).add(
                    woodType.getLogBlock().get(),
                    woodType.getWoodBlock().get(),
                    woodType.getStrippedLogBlock().get(),
                    woodType.getStrippedWoodBlock().get()
            );

            tag(BlockTags.PLANKS).add(woodType.getPlanksBlock().get());

            tag(BlockTags.FENCES).add(woodType.getFenceBlock().get());
            tag(BlockTags.FENCE_GATES).add(woodType.getFenceGateBlock().get());
        }
        tag(BlockTags.SAPLINGS).add(TreeSpecies.Saplings.stream().map(RegistryObject::get).toArray(Block[]::new));
    }
}
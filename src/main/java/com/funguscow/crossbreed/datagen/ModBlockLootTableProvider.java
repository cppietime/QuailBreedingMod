package com.funguscow.crossbreed.datagen;

import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import com.funguscow.crossbreed.worldgen.botany.wood.ModWoodType;
import com.google.common.collect.Iterables;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    public ModBlockLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        for (ModWoodType woodType : ModWoodType.WoodTypes.values()) {
            dropSelf(woodType.getLogBlock().get());
            dropSelf(woodType.getStrippedLogBlock().get());
            dropSelf(woodType.getWoodBlock().get());
            dropSelf(woodType.getStrippedWoodBlock().get());
            dropSelf(woodType.getPlanksBlock().get());
            dropSelf(woodType.getStairsBlock().get());
            dropSelf(woodType.getSlabBlock().get());
            dropSelf(woodType.getTrapdoorBlock().get());
            dropSelf(woodType.getFenceBlock().get());
            dropSelf(woodType.getFenceGateBlock().get());
            dropSelf(woodType.getButtonBlock().get());
            dropSelf(woodType.getPressurePlateBlock().get());

            add(woodType.getDoorBlock().get(), createDoorTable(woodType.getDoorBlock().get()));
            add(woodType.getStandingSignBlock().get(), createSingleItemTable(woodType.getSignItem().get()));
            add(woodType.getWallSignBlock().get(), createSingleItemTable(woodType.getSignItem().get()));
            add(woodType.getCeilingHangingSignBlock().get(), createSingleItemTable(woodType.getHangingSignItem().get()));
            add(woodType.getWallHangingSignBlock().get(), createSingleItemTable(woodType.getHangingSignItem().get()));
        }

        for (RegistryObject<Block> registryObject : TreeSpecies.Saplings) {
            Block block = registryObject.get();
            add(block, createSingleItemTable(block.asItem())
                    .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                            .copy("AlleleA", "BlockEntityTag.AlleleA", CopyNbtFunction.MergeStrategy.REPLACE)
                            .copy("AlleleB", "BlockEntityTag.AlleleB", CopyNbtFunction.MergeStrategy.REPLACE)));
        }
    }

    @Override
    @NotNull
    protected Iterable<Block> getKnownBlocks() {
        return Iterables.concat(
                ModWoodType.WoodTypes.values().stream()
                        .map(ModWoodType::getAllBlocks)
                        .flatMap(Collection::stream)
                        .map(RegistryObject::get)
                        .toList(),
                TreeSpecies.Saplings.stream().map(RegistryObject::get).toList());
    }
}

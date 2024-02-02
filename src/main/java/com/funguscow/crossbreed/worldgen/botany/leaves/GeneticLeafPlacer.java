package com.funguscow.crossbreed.worldgen.botany.leaves;

import com.funguscow.crossbreed.block.GeneticLeafBlock;
import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class GeneticLeafPlacer {

    public static final Map<String, GeneticLeafPlacer> LeafPlacers = new HashMap<>();

    public final String name;

    public GeneticLeafPlacer(String name) {
        this.name = name;
        LeafPlacers.put(name, this);
    }

    public abstract Set<BlockPos> placeLeaves(WorldGenLevel level, GeneticFoliageAttachment attachment, GeneticTreeTileEntity geneEntity);

    public boolean canPlaceLeaf(WorldGenLevel level, GeneticFoliageAttachment attachment, BlockPos leafPos, TreeGene gene) {
        return level.isStateAtPosition(leafPos, state -> state.isAir() || state.is(BlockTags.REPLACEABLE_BY_TREES));
    }

    public void putLeaf(WorldGenLevel level, BlockPos pos, GeneticTreeTileEntity geneEntity) {
        ResourceLocation leafLocation = geneEntity.getGene().species().leafBlock;
        BlockState leafState = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(leafLocation)).defaultBlockState();
        if (leafState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            leafState = leafState.setValue(BlockStateProperties.WATERLOGGED, level.isFluidAtPosition(pos, fluid -> fluid.isSourceOfType(Fluids.WATER)));
        }
        if (leafState.hasProperty(GeneticLeafBlock.POLLINATED)) {
            leafState = leafState.setValue(GeneticLeafBlock.POLLINATED, Boolean.FALSE);
        }
        level.setBlock(pos, leafState, Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        if (leafState.getBlock() instanceof GeneticLeafBlock) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof GeneticTreeTileEntity newTreeEntity) {
                CompoundTag nbt = new CompoundTag();
                geneEntity.saveAdditional(nbt);
                newTreeEntity.load(nbt);

                // I'm disabling this because it makes too many sapling item stacks.
                // The player can recombine by pollinating.
                // newTreeEntity.recombine(level.getRandom());
            }
        }
    }

    public boolean tryPutLeaf(WorldGenLevel level, GeneticFoliageAttachment attachment, BlockPos pos, GeneticTreeTileEntity geneEntity) {
        if (!canPlaceLeaf(level, attachment, pos, geneEntity.getGene())) {
            return false;
        }
        putLeaf(level, pos, geneEntity);
        return true;
    }

    public static void register() {
        new CubeLeafPlacer();
        new ConeLeafPlacer();
        new SphereLeafPlacer();
        new BlobLeafPlacer();
        new SnakeLeafPlacer();
        new FlatLeafPlacer();
    }

}

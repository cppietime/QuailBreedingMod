package com.funguscow.crossbreed.worldgen.botany;

import com.funguscow.crossbreed.block.GeneticSaplingBlock;
import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.trunks.GeneticTrunkPlacer;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

/**
 * This is mostly a copy and paste of TreeFeature, but allowing for genetic placers that check the sapling block entity.
 */
public class GeneticTreeFeature extends Feature<NoneFeatureConfiguration> {

    public static final GeneticTreeFeature Instance = new GeneticTreeFeature(NoneFeatureConfiguration.CODEC);

    public GeneticTreeFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    /**
     * Update the distance block states of all leaves.
     * This was taken from TreeFeature. See that class to see how to use it (later) to update the shapes of adjacent blocks.
     */
    private static DiscreteVoxelShape updateLeaves(LevelAccessor pLevel, BoundingBox pBox, Set<BlockPos> pRootPositions, Set<BlockPos> pTrunkPositions, Set<BlockPos> pFoliagePositions) {
        DiscreteVoxelShape discretevoxelshape = new BitSetDiscreteVoxelShape(pBox.getXSpan(), pBox.getYSpan(), pBox.getZSpan());
        List<Set<BlockPos>> list = Lists.newArrayList();

        for(int j = 0; j < 7; ++j) {
            list.add(Sets.newHashSet());
        }

        for(BlockPos blockpos : Lists.newArrayList(Sets.union(pTrunkPositions, pFoliagePositions))) {
            if (pBox.isInside(blockpos)) {
                discretevoxelshape.fill(blockpos.getX() - pBox.minX(), blockpos.getY() - pBox.minY(), blockpos.getZ() - pBox.minZ());
            }
        }

        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int distance = 0;
        list.get(0).addAll(pRootPositions);

        while(true) {
            while(distance >= 7 || !list.get(distance).isEmpty()) {
                if (distance >= 7) {
                    return discretevoxelshape;
                }

                Iterator<BlockPos> iterator = list.get(distance).iterator();
                BlockPos position = iterator.next();
                iterator.remove();
                if (pBox.isInside(position)) {
                    if (distance != 0) {
                        BlockState blockstate = pLevel.getBlockState(position);
                        setBlockKnownShape(pLevel, position, blockstate.setValue(BlockStateProperties.DISTANCE, distance));
                    }

                    discretevoxelshape.fill(position.getX() - pBox.minX(), position.getY() - pBox.minY(), position.getZ() - pBox.minZ());

                    for(Direction direction : Direction.values()) {
                        mutableBlockPos.setWithOffset(position, direction);
                        if (pBox.isInside(mutableBlockPos)) {
                            int x = mutableBlockPos.getX() - pBox.minX();
                            int y = mutableBlockPos.getY() - pBox.minY();
                            int z = mutableBlockPos.getZ() - pBox.minZ();
                            if (!discretevoxelshape.isFull(x, y, z)) {
                                BlockState blockState = pLevel.getBlockState(mutableBlockPos);
                                OptionalInt optionalint = LeavesBlock.getOptionalDistanceAt(blockState);
                                if (optionalint.isPresent()) {
                                    int leafDistance = Math.min(optionalint.getAsInt(), distance + 1);
                                    if (leafDistance < 7) {
                                        list.get(leafDistance).add(mutableBlockPos.immutable());
                                        distance = Math.min(distance, leafDistance);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ++distance;
        }
    }

    /**
     * Updates a block position to a provided state, updating the block and clients, but not reshaping neighbors.
     * @param pLevel
     * @param pPos
     * @param pState
     */
    private static void setBlockKnownShape(LevelWriter pLevel, BlockPos pPos, BlockState pState) {
        pLevel.setBlock(pPos, pState, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
    }

    private static boolean validSaplings(WorldGenLevel level, BlockPos startPos, String species, int width) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                BlockPos pos = startPos.offset(x, 0, z);
                Block block = level.getBlockState(pos).getBlock();
                if (!(block instanceof GeneticSaplingBlock)) {
                    return false;
                }
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (!(blockEntity instanceof GeneticTreeTileEntity tree)) {
                    return false;
                }
                if (!tree.getGene().species.equals(species)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        WorldGenLevel level = pContext.level();
        BlockPos origin = pContext.origin();
        RandomSource random = pContext.random();

        // Get tile entity of sapling
        BlockEntity blockEntity = level.getBlockEntity(origin);
        if (!(blockEntity instanceof GeneticTreeTileEntity geneEntity)) {
            return false;
        }

        // Retrieve the tree gene
        TreeGene gene = geneEntity.getGene();

        int width = gene.trunkWidth;
        outerLoop:
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                BlockPos minPos = origin.offset(-x, 0, -z);
                if (validSaplings(level, minPos, gene.species, width)) {
                    origin = minPos;
                    break outerLoop;
                }
            }
            if (x + 1 == width) {
                return false;
            }
        }

        GeneticTrunkPlacer trunkPlacer = GeneticTrunkPlacer.TrunkPlacers.get(gene.trunkType);
        OptionalInt height = trunkPlacer.heightAt(level, origin, random, gene);
        if (height.isEmpty()) {
            return false;
        }
        int treeHeight = height.getAsInt();
        if (treeHeight == 0) {
            return false;
        }

        // From here on, we will grow the actual tree.
        for (int x = 0; x < gene.trunkWidth; x++) {
            for (int z = 0; z < gene.trunkWidth; z++) {
                BlockPos saplingPos = origin.offset(x, 0, z);
                BlockPos dirtPos = saplingPos.below();

                // Get the sapling out of the way.
                level.setBlock(saplingPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_INVISIBLE);

                // Set the base block to dirt.
                level.setBlock(dirtPos, Blocks.DIRT.defaultBlockState(), Block.UPDATE_INVISIBLE);
            }
        }

        // Grow the trunk.
        trunkPlacer.placeTrunk(level, origin, random, treeHeight, gene);
        return true;
    }

    /**
     * Register singleton instances of all interface implementations used by tree growing logic.
     */
    public static void register() {
            GeneticTrunkPlacer.register();
    }
}

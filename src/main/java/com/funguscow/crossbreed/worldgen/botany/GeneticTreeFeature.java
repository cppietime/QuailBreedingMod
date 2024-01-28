package com.funguscow.crossbreed.worldgen.botany;

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
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
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
                        setBlockKnownShape(pLevel, position, blockstate.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(distance)));
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

    private static void setBlockKnownShape(LevelWriter pLevel, BlockPos pPos, BlockState pState) {
        pLevel.setBlock(pPos, pState, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        WorldGenLevel level = pContext.level();
        BlockPos origin = pContext.origin();
        RandomSource random = pContext.random();

        BlockEntity blockEntity = level.getBlockEntity(origin);
        if (!(blockEntity instanceof GeneticTreeTileEntity geneEntity)) {
            return false;
        }

        TreeGene gene = geneEntity.getGene();
        GeneticTrunkPlacer trunkPlacer = GeneticTrunkPlacer.TrunkPlacers.get(gene.trunkType);
        OptionalInt height = trunkPlacer.heightAt(level, origin, random, gene);
        if (height.isEmpty()) {
            return false;
        }
        int treeHeight = height.getAsInt();
        if (treeHeight == 0) {
            return false;
        }

        trunkPlacer.placeTrunk(level, origin, random, treeHeight, gene);
        return true;
    }
}

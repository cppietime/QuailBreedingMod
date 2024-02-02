package com.funguscow.crossbreed.worldgen.botany.trunks;

import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public abstract class GeneticTrunkPlacer {

    public static final Map<String, GeneticTrunkPlacer> TrunkPlacers = new HashMap<>();

    public final String name;

    protected GeneticTrunkPlacer(String name) {
        this.name = name;
        TrunkPlacers.put(name, this);
    }

    /**
     *
     * @param level
     * @param startPos The minimum block position of all saplings growing into this tree
     * @param random
     * @param gene
     * @return The height of the trunk of a tree generated at the provided position. or EMPTY if it cannot generate.
     */
    public OptionalInt heightAt(WorldGenLevel level, BlockPos startPos, RandomSource random, TreeGene gene) {
        TreeSpecies species = gene.species();
        int baseHeight = gene.minHeight;
        for (int i = 1; i < baseHeight; i++) {
            BlockPos centerPos = startPos.above(i);
            for (int x = -species.minWidth; x < gene.trunkWidth + species.minWidth; x++) {
                for (int z = -species.minWidth; z < gene.trunkWidth + species.minWidth; z++) {
                    BlockPos pos = centerPos.offset(x, 0, z);
                    if (!canPlaceLog(level, startPos, pos, gene)) {
                        return OptionalInt.empty();
                    }
                }
            }
        }
        int attemptHeight = random.nextInt((int)(gene.heightRange * baseHeight) + 1);
        for (int i = 0; i < attemptHeight; i++) {
            BlockPos centerPos = startPos.above(baseHeight + i);
            for (int x = -species.minWidth; x < gene.trunkWidth + species.minWidth; x++) {
                for (int z = -species.minWidth; z < gene.trunkWidth + species.minWidth; z++) {
                    BlockPos pos = centerPos.offset(x, 0, z);
                    if (!canPlaceLog(level, startPos, pos, gene)) {
                        return OptionalInt.empty();
                    }
                }
            }
        }
        return OptionalInt.of(baseHeight + attemptHeight);
    }

    /**
     * This function shall handle all logic of placing the trunk of the tree.
     * @param level
     * @param startPos Minimum blockpos of saplings.
     * @param random
     * @param height Height the trunk shall generate.
     * @param gene
     * @return Returned result must contain all foliage attachment points and the set of all new log block positions.
     */
    public abstract TrunkPlacementResult placeTrunk(WorldGenLevel level, BlockPos startPos, RandomSource random, int height, TreeGene gene);

    /**
     *
     * @param level
     * @param startPos Minimum block position of saplings.
     * @param logPos Position to check whether the log can generate.
     * @param gene
     * @return True iff a log can be placed at logPos.
     */
    public boolean canPlaceLog(WorldGenLevel level, BlockPos startPos, BlockPos logPos, TreeGene gene) {
        BlockState block = level.getBlockState(logPos);
        return block.isAir() || block.is(BlockTags.REPLACEABLE_BY_TREES) || block.is(BlockTags.LOGS);
    }

    public void putLogBlock(WorldGenLevel level, BlockPos pos, BlockState logBlock, Direction.Axis axis, TrunkPlacementResult result) {
        level.setBlock(pos, logBlock.trySetValue(RotatedPillarBlock.AXIS, axis), Block.UPDATE_ALL);
        result.trunkBlocks.add(pos);
    }

    public boolean tryPutLogBlock(WorldGenLevel level, BlockPos startPos, BlockPos logPos, BlockState logBlock, TreeGene gene, Direction.Axis axis, TrunkPlacementResult result) {
        if (!canPlaceLog(level, startPos, logPos, gene)) {
            return false;
        }
        putLogBlock(level, logPos, logBlock, axis, result);
        return true;
    }

    public static class TrunkPlacementResult {
        public final List<GeneticFoliageAttachment> attachments;
        public final Set<BlockPos> trunkBlocks;

        public TrunkPlacementResult(List<GeneticFoliageAttachment> attachments, Set<BlockPos> trunkBlocks) {
            this.attachments = attachments;
            this.trunkBlocks = trunkBlocks;
        }

        public TrunkPlacementResult() {
            this(new ArrayList<>(), new HashSet<>());
        }
    }

    /**
     * Apparently I need to put this logic in a function instead of a static block, or else it gets optimized away :(
     */
    public static void register() {
        new StraightTrunkPlacer();
        new HollowTrunkPlacer();
        new SpiralTrunkPlacer();
        new RottenTrunkPlacer();
        new HourglassTrunkPlacer();
        new BulgeTrunkPlacer();
        new CrossTrunkPlacer();
        new BranchTrunkPlacer();
        new RunnerTrunkPlacer();
    }

}

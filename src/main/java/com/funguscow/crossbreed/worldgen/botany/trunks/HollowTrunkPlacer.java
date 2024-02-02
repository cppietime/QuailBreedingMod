package com.funguscow.crossbreed.worldgen.botany.trunks;

import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class HollowTrunkPlacer extends GeneticTrunkPlacer {

    public HollowTrunkPlacer() {
        super("hollow");
    }

    @Override
    public TrunkPlacementResult placeTrunk(WorldGenLevel level, BlockPos startPos, RandomSource random, int height, TreeGene gene) {
        TrunkPlacementResult result = new TrunkPlacementResult();
        BlockState logBlock = gene.species().logBlock();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < gene.trunkWidth; x++) {
                for (int z = 0; z < gene.trunkWidth; z++) {
                    if (x == 0 || x == gene.trunkWidth - 1 || z == 0 || z == gene.trunkWidth - 1) {
                        BlockPos logPos = startPos.offset(x, y, z);
                        tryPutLogBlock(level, startPos, logPos, logBlock, gene, Direction.Axis.Y, result);
                    }
                }
            }
        }
        GeneticFoliageAttachment attachment = new GeneticFoliageAttachment(startPos.above(height - 1), Direction.UP, gene.trunkWidth);
        result.attachments.add(attachment);
        return result;
    }
}

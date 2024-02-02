package com.funguscow.crossbreed.worldgen.botany.trunks;

import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class RunnerTrunkPlacer extends GeneticTrunkPlacer {

    public RunnerTrunkPlacer() {
        super("runner");
    }

    @Override
    public TrunkPlacementResult placeTrunk(WorldGenLevel level, BlockPos startPos, RandomSource random, int height, TreeGene gene) {
        TrunkPlacementResult result = new TrunkPlacementResult();
        BlockState logBlock = gene.species().logBlock();
        int offset = random.nextInt(1 + gene.trunkWidth / 2, gene.trunkWidth + gene.trunkWidth / 2 + 1);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < gene.trunkWidth; x++) {
                for (int z = 0; z < gene.trunkWidth; z++) {
                    BlockPos logPos = startPos.offset(x, y, z);
                    tryPutLogBlock(level, startPos, logPos, logBlock, gene, Direction.Axis.Y, result);
                }
            }
            if ((y - offset) % (gene.trunkWidth * 2) == gene.trunkWidth && y >= offset) {
                int dir = random.nextInt(4);
                int side = random.nextInt(gene.trunkWidth);
                switch (dir) {
                    case 0:
                    case 1:
                        // North/south
                        for (int dist = 0; dist < gene.trunkWidth + 3; dist++) {
                                BlockPos logPos;
                                if (dir == 0) {
                                    logPos = startPos.offset(side, y, dist + gene.trunkWidth);
                                } else {
                                    logPos = startPos.offset(side, y, -dist - 1);
                                }
                                tryPutLogBlock(level, startPos, logPos, logBlock, gene, Direction.Axis.Z, result);
                        }
                        int z = dir == 0 ? gene.trunkWidth * 2 + 2 : -gene.trunkWidth - 3;
                        for (int h = 0; h < gene.trunkWidth + 1; h++) {
                            tryPutLogBlock(level, startPos, startPos.offset(side, y + h, z), logBlock, gene, Direction.Axis.Y, result);
                        }
                        result.attachments.add(
                                new GeneticFoliageAttachment(startPos.offset(side, y + gene.trunkWidth, z), Direction.UP, gene.trunkWidth)
                        );
                        break;

                    case 2:
                    case 3:
                        // East/West
                        for (int dist = 0; dist < gene.trunkWidth * 2; dist++) {
                                BlockPos logPos;
                                if (dir == 2) {
                                    logPos = startPos.offset(dist + gene.trunkWidth, y, side);
                                } else {
                                    logPos = startPos.offset(-dist - 1, y, side);
                                }
                                tryPutLogBlock(level, startPos, logPos, logBlock, gene, Direction.Axis.X, result);
                        }
                        int x = dir == 2 ? gene.trunkWidth * 2 + 2 : -gene.trunkWidth - 3;
                        for (int h = 0; h < gene.trunkWidth + 1; h++) {
                            tryPutLogBlock(level, startPos, startPos.offset(x, y + h, side), logBlock, gene, Direction.Axis.Y, result);
                        }
                        result.attachments.add(
                                new GeneticFoliageAttachment(startPos.offset(x, y + gene.trunkWidth, side), Direction.EAST, gene.trunkWidth)
                        );
                }
            }
        }
        GeneticFoliageAttachment attachment = new GeneticFoliageAttachment(startPos.above(height - 1), Direction.UP, gene.trunkWidth);
        result.attachments.add(attachment);
        return result;
    }
}

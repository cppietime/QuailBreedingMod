package com.funguscow.crossbreed.worldgen.botany.trunks;

import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class BranchTrunkPlacer extends GeneticTrunkPlacer {

    public BranchTrunkPlacer() {
        super("branch");
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
                switch (dir) {
                    case 0:
                    case 1:
                        // North/south
                        for (int dist = 0; dist < gene.trunkWidth * 2; dist++) {
                            for (int side = 0; side < gene.trunkWidth; side++) {
                                for (int h = 0; h < gene.trunkWidth; h++) {
                                    BlockPos logPos;
                                    if (dir == 0) {
                                        logPos = startPos.offset(side, y + h, dist + gene.trunkWidth);
                                    } else {
                                        logPos = startPos.offset(side, y + h, -dist - 1);
                                    }
                                    tryPutLogBlock(level, startPos, logPos, logBlock, gene, Direction.Axis.Z, result);
                                }
                            }
                        }
                        if (dir == 0) {
                            result.attachments.add(
                                    new GeneticFoliageAttachment(startPos.offset(0, y, gene.trunkWidth * 3), Direction.SOUTH, gene.trunkWidth)
                            );
                        } else {
                            result.attachments.add(
                                    new GeneticFoliageAttachment(startPos.offset(0, y, -gene.trunkWidth * 2), Direction.NORTH, gene.trunkWidth)
                            );
                        }
                        break;

                    case 2:
                    case 3:
                        // East/West
                        for (int dist = 0; dist < gene.trunkWidth * 2; dist++) {
                            for (int side = 0; side < gene.trunkWidth; side++) {
                                for (int h = 0; h < gene.trunkWidth; h++) {
                                    BlockPos logPos;
                                    if (dir == 2) {
                                        logPos = startPos.offset(dist + gene.trunkWidth, y + h, side);
                                    } else {
                                        logPos = startPos.offset(-dist - 1, y + h, side);
                                    }
                                    tryPutLogBlock(level, startPos, logPos, logBlock, gene, Direction.Axis.X, result);
                                }
                            }
                        }
                        if (dir == 2) {
                            result.attachments.add(
                                    new GeneticFoliageAttachment(startPos.offset(gene.trunkWidth * 3, y, 0), Direction.EAST, gene.trunkWidth)
                            );
                        } else {
                            result.attachments.add(
                                    new GeneticFoliageAttachment(startPos.offset(-gene.trunkWidth * 2, y, 0), Direction.WEST, gene.trunkWidth)
                            );
                        }
                }
            }
        }
        GeneticFoliageAttachment attachment = new GeneticFoliageAttachment(startPos.above(height - 1), Direction.UP, gene.trunkWidth);
        result.attachments.add(attachment);
        return result;
    }
}

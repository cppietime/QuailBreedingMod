package com.funguscow.crossbreed.worldgen.botany.trunks;

import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class SpiralTrunkPlacer extends GeneticTrunkPlacer {

    public SpiralTrunkPlacer() {
        super("spiral");
    }

    @Override
    public TrunkPlacementResult placeTrunk(WorldGenLevel level, BlockPos startPos, RandomSource random, int height, TreeGene gene) {
        TrunkPlacementResult result = new TrunkPlacementResult();
        BlockState logBlock = gene.species().logBlock();
        BlockPos logPos = startPos;
        int state = 0;
        int x = 0, z = 0;
        for (int y = 0; y < height; y++) {
            logPos = startPos.offset(x, y, z);
            tryPutLogBlock(level, startPos, logPos, logBlock, gene, Direction.Axis.Y, result);
            if (gene.trunkWidth > 1) {
                switch (state) {
                    case 0: // +X
                        if (++x == gene.trunkWidth - 1) {
                            state++;
                        }
                        break;
                    case 1: // +Z
                        if (++z == gene.trunkWidth - 1) {
                            state++;
                        }
                        break;
                    case 2: // -X
                        if (--x == 0) {
                            state++;
                        }
                        break;
                    case 3: // -Z
                        if (--z == 0) {
                            state = 0;
                        }
                        break;
                }
            }
        }
        GeneticFoliageAttachment attachment = new GeneticFoliageAttachment(logPos, Direction.UP, 1);
        result.attachments.add(attachment);
        return result;
    }
}

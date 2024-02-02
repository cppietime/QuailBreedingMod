package com.funguscow.crossbreed.worldgen.botany.trunks;

import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class BulgeTrunkPlacer extends GeneticTrunkPlacer {

    public BulgeTrunkPlacer() {
        super("bulge");
    }

    @Override
    public TrunkPlacementResult placeTrunk(WorldGenLevel level, BlockPos startPos, RandomSource random, int height, TreeGene gene) {
        TrunkPlacementResult result = new TrunkPlacementResult();
        BlockState logBlock = gene.species().logBlock();
        for (int y = 0; y < height; y++) {
            int scale = Math.min(y, height - 1 - y);
            int range = 1 + (int)((gene.trunkWidth - 1) * ((float)scale * 2 / height));
            for (int x = 0; x < gene.trunkWidth; x++) {
                for (int z = 0; z < gene.trunkWidth; z++) {
                    float dx = x - (gene.trunkWidth - 1) / 2f;
                    float dz = z - (gene.trunkWidth - 1) / 2f;
                    float radius = dx * dx + dz * dz;
                    if (radius > range * range) {
                        continue;
                    }
                    BlockPos logPos = startPos.offset(x, y, z);
                    tryPutLogBlock(level, startPos, logPos, logBlock, gene, Direction.Axis.Y, result);
                }
            }
        }
        GeneticFoliageAttachment attachment = new GeneticFoliageAttachment(startPos.above(height - 1), Direction.UP, gene.trunkWidth);
        result.attachments.add(attachment);
        return result;
    }
}

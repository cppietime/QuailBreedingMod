package com.funguscow.crossbreed.worldgen.botany.trunks;

import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class SlantTrunkPlacer extends GeneticTrunkPlacer {

    public SlantTrunkPlacer() {
        super("slant");
    }

    @Override
    public TrunkPlacementResult placeTrunk(WorldGenLevel level, BlockPos startPos, RandomSource random, int height, TreeGene gene) {
        TrunkPlacementResult result = new TrunkPlacementResult();
        BlockState logBlock = gene.species().logBlock();
        Vec3i offset = switch (random.nextInt(4)) {
            case 0 -> new Vec3i(1, 0, 0);
            case 1 -> new Vec3i(-1, 0, 0);
            case 2 -> new Vec3i(0, 0, 1);
            default -> new Vec3i(0, 0, -1);
        };
        int threshold = (int)(height * (0.5 + random.nextFloat() * 0.5));
        BlockPos logPos = startPos;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < gene.trunkWidth; x++) {
                for (int z = 0; z < gene.trunkWidth; z++) {
                    tryPutLogBlock(level, startPos, logPos, logBlock, gene, Direction.Axis.Y, result);
                    logPos = logPos.above();
                    if (y >= threshold && y + 1 < height) {
                        logPos = logPos.offset(offset);
                    }
                }
            }
        }
        GeneticFoliageAttachment attachment = new GeneticFoliageAttachment(logPos, Direction.UP, gene.trunkWidth);
        result.attachments.add(attachment);
        return result;
    }
}

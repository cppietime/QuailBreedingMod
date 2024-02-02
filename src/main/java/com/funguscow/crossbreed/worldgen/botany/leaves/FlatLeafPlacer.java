package com.funguscow.crossbreed.worldgen.botany.leaves;

import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;

import java.util.HashSet;
import java.util.Set;

public class FlatLeafPlacer extends GeneticLeafPlacer {
    public FlatLeafPlacer() {
        super("flat");
    }

    @Override
    public Set<BlockPos> placeLeaves(WorldGenLevel level, GeneticFoliageAttachment attachment, GeneticTreeTileEntity geneEntity) {
        Set<BlockPos> leaves = new HashSet<>();

        int width = geneEntity.getGene().trunkWidth;
        // Iterate through layers
        Vec3i growth = attachment.growthDirection.getNormal();
        Vec3i tan1 = new Vec3i(Mth.abs(growth.getY()), Mth.abs(growth.getZ()), Mth.abs(growth.getX()));
        Vec3i tan2 = new Vec3i(tan1.getY(), tan1.getZ(), tan1.getX());
        for (int axial = 0; axial < 2; axial++) {
            BlockPos centerPos = attachment.minPos.offset(growth.multiply(axial));
            for (int i = -3; i < 3 + width; i++) {
                for (int j = -3; j < 3 + width; j++) {
                    int distI = Math.min(Mth.abs(i), Mth.abs(width - 1 - i));
                    int distJ = Math.min(Mth.abs(j), Mth.abs(width - 1 - j));
                    if (distI * distI + distJ * distJ > 3 * 3) {
                        continue;
                    }
                    BlockPos pos = centerPos.offset(tan1.multiply(i)).offset(tan2.multiply(j));
                    if (tryPutLeaf(level, attachment, pos, geneEntity)) {
                        leaves.add(pos);
                    }
                }
            }
        }

        return leaves;
    }
}

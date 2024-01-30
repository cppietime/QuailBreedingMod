package com.funguscow.crossbreed.worldgen.botany.leaves;

import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;

import java.util.HashSet;
import java.util.Set;

public class ConeLeafPlacer extends GeneticLeafPlacer {
    public ConeLeafPlacer() {
        super("cone");
    }

    @Override
    public Set<BlockPos> placeLeaves(WorldGenLevel level, GeneticFoliageAttachment attachment, GeneticTreeTileEntity geneEntity) {
        Set<BlockPos> leaves = new HashSet<>();

        int width = geneEntity.getGene().trunkWidth;
        // Iterate through layers
        Vec3i growth = attachment.growthDirection.getNormal();
        Vec3i tan1 = new Vec3i(Mth.abs(growth.getY()), Mth.abs(growth.getZ()), Mth.abs(growth.getX()));
        Vec3i tan2 = new Vec3i(tan1.getY(), tan1.getZ(), tan1.getX());
        for (int layer = 0; layer < 5; layer++) {
            BlockPos centerPos = attachment.minPos.offset(growth.multiply(layer - 2));
            int reach = Math.min(4 - layer, 3);
            for (int i = -reach; i < reach + width; i++) {
                for (int j = -reach; j < reach + width; j++) {
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

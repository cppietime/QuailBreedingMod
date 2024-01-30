package com.funguscow.crossbreed.worldgen.botany.leaves;

import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;

import java.util.HashSet;
import java.util.Set;

public class CubeLeafPlacer extends GeneticLeafPlacer {
    public CubeLeafPlacer() {
        super("cube");
    }

    @Override
    public Set<BlockPos> placeLeaves(WorldGenLevel level, GeneticFoliageAttachment attachment, GeneticTreeTileEntity geneEntity) {
        Set<BlockPos> leaves = new HashSet<>();

        int width = geneEntity.getGene().trunkWidth;
        // Iterate through layers
        Vec3i growth = attachment.growthDirection.getNormal();
        Vec3i tan1 = new Vec3i(Mth.abs(growth.getY()), Mth.abs(growth.getZ()), Mth.abs(growth.getX()));
        Vec3i tan2 = new Vec3i(tan1.getY(), tan1.getZ(), tan1.getX());
        for (int axial = -2; axial < 3; axial++) {
            BlockPos centerPos = attachment.minPos.offset(growth.multiply(axial));
            for (int i = -2; i < 2 + width; i++) {
                for (int j = -2; j < 2 + width; j++) {
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

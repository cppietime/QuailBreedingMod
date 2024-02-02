package com.funguscow.crossbreed.worldgen.botany.leaves;

import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.GeneticFoliageAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;

import java.util.HashSet;
import java.util.Set;

public class SnakeLeafPlacer extends GeneticLeafPlacer {
    public SnakeLeafPlacer() {
        super("snake");
    }

    @Override
    public Set<BlockPos> placeLeaves(WorldGenLevel level, GeneticFoliageAttachment attachment, GeneticTreeTileEntity geneEntity) {
        Set<BlockPos> leaves = new HashSet<>();

        int width = geneEntity.getGene().trunkWidth;
        Vec3i growth = attachment.growthDirection.getNormal();
        Vec3i tan1 = new Vec3i(Mth.abs(growth.getY()), Mth.abs(growth.getZ()), Mth.abs(growth.getX()));
        Vec3i tan2 = new Vec3i(tan1.getY(), tan1.getZ(), tan1.getX());

        // Iterate through snakes
        for (int i = 0; i < 6; i++) {
            int x = level.getRandom().nextInt(width);
            int y = level.getRandom().nextInt(width);
            BlockPos leafPos = attachment.minPos.offset(growth).offset(tan1.multiply(x)).offset(tan2.multiply(y));
            // Iterate through blocks
            for (int j = 0; j < 7; j++) {
                if (tryPutLeaf(level, attachment, leafPos, geneEntity)) {
                    leaves.add(leafPos);
                }
                Direction dir = Direction.getRandom(level.getRandom());
                BlockPos newPos = leafPos.offset(dir.getNormal());
                if (canPlaceLeaf(level, attachment, newPos, geneEntity.getGene())) {
                    leafPos = newPos;
                }
            }
        }

        return leaves;
    }
}

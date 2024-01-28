package com.funguscow.crossbreed.worldgen.botany;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class GeneticFoliageAttachment {

    public BlockPos minPos;
    public Direction growthDirection;
    public int trunkWidth;

    public GeneticFoliageAttachment(BlockPos minPos, Direction growthDirection, int trunkWidth) {
        this.minPos = minPos;
        this.growthDirection = growthDirection;
        this.trunkWidth = trunkWidth;
    }

}

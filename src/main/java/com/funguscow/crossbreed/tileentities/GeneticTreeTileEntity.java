package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GeneticTreeTileEntity extends BlockEntity {
    public GeneticTreeTileEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        // TODO everything
        super(pType, pPos, pBlockState);
    }

    public TreeGene getGene() {
        // TODO
        return null;
    }
}

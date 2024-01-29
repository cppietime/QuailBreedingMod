package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.init.ModTileEntities;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GeneticTreeTileEntity extends BlockEntity {
    public GeneticTreeTileEntity(BlockPos pPos, BlockState pBlockState) {
        // TODO everything
        super(ModTileEntities.GENETIC_TREE.get(), pPos, pBlockState);
    }

    public TreeGene getGene() {
        // TODO
        return TreeSpecies.TEST_TREE.defaultGene;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("AlleleA", getGene().save());
    }
}

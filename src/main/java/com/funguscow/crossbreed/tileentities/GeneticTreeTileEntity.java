package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.init.ModTileEntities;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GeneticTreeTileEntity extends BlockEntity {

    private TreeGene alleleA, alleleB;

    public GeneticTreeTileEntity(BlockPos pPos, BlockState pBlockState) {
        // TODO everything
        super(ModTileEntities.GENETIC_TREE.get(), pPos, pBlockState);
        alleleA = alleleB = TreeSpecies.TEST_TREE.defaultGene;
    }

    public TreeGene getGene() {
        return alleleA.dominance >= alleleB.dominance ? alleleA : alleleB;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("AlleleA", alleleA.save());
        pTag.put("AlleleB", alleleB.save());
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        alleleA.readFromTag(pTag.getCompound("AlleleA"));
        alleleB.readFromTag(pTag.getCompound("AlleleB"));
    }
}

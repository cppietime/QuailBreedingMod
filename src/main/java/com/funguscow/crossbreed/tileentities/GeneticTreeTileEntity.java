package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.genetics.Gene;
import com.funguscow.crossbreed.init.ModTileEntities;
import com.funguscow.crossbreed.util.Pair;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneticTreeTileEntity extends BlockEntity {

    private TreeGene alleleA, alleleB;

    public GeneticTreeTileEntity(BlockPos pPos, BlockState pBlockState) {
        // TODO everything
        super(ModTileEntities.GENETIC_TREE.get(), pPos, pBlockState);
        alleleA = TreeSpecies.TEST_TREE.defaultGene.copy();
        alleleB = TreeSpecies.TEST_TREE.defaultGene.copy();
    }

    public TreeGene getGene() {
        return alleleA.dominance >= alleleB.dominance ? alleleA : alleleB;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("AlleleA", alleleA.save());
        pTag.put("AlleleB", alleleB.save());
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("AlleleA")) {
            alleleA.readFromTag(pTag.getCompound("AlleleA"));
        }
        if (pTag.contains("AlleleB")) {
            alleleB.readFromTag(pTag.getCompound("AlleleB"));
        }
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void recombine(RandomSource random) {
        Pair<TreeGene, TreeGene> pair = Gene.diploid(alleleA, alleleB, alleleA, alleleB, random);
        alleleA = pair.first;
        alleleB = pair.second;
    }
}

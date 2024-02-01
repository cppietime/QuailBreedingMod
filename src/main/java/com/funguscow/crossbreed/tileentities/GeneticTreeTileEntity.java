package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.block.GeneticSaplingBlock;
import com.funguscow.crossbreed.genetics.Gene;
import com.funguscow.crossbreed.init.ModTileEntities;
import com.funguscow.crossbreed.util.Pair;
import com.funguscow.crossbreed.util.RandomPool;
import com.funguscow.crossbreed.util.UnorderedPair;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GeneticTreeTileEntity extends BlockEntity {

    private TreeGene alleleA, alleleB;

    public GeneticTreeTileEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModTileEntities.GENETIC_TREE.get(), pPos, pBlockState);
        Block block = pBlockState.getBlock();
        if (block instanceof GeneticSaplingBlock) {
            String saplingName = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();
            String speciesName = saplingName.substring(0, saplingName.length() - "_sapling".length());
            TreeSpecies species = TreeSpecies.Species.get(speciesName);
            alleleA = species.defaultGene.copy();
            alleleB = species.defaultGene.copy();
        } else {
            alleleA = TreeSpecies.TEST_TREE.defaultGene.copy();
            alleleB = TreeSpecies.TEST_TREE.defaultGene.copy();
        }
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

    public TreeGene getAlleleA() {
        return alleleA;
    }

    public TreeGene getAlleleB() {
        return alleleB;
    }

    public void sendUpdate() {
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void pollinate(TreeGene pollen, RandomSource random) {
        UnorderedPair<String> pair = new UnorderedPair<>(pollen.species, getGene().species);
        RandomPool<String> pool = TreeSpecies.Pairings.get(pair);
        if (pool != null) {
            float p = random.nextFloat();
            String hybrid = pool.get(p);
            if (hybrid != null) {
                TreeSpecies species = Objects.requireNonNull(TreeSpecies.Species.get(hybrid));
                alleleA = species.defaultGene.copy();
                alleleB = species.defaultGene.copy();
                sendUpdate();
                return;
            }
        }

        Pair<TreeGene, TreeGene> alleles = Gene.diploid(alleleA, alleleB, pollen, pollen, random);
        alleleA = alleles.first;
        alleleB = alleles.second;
        sendUpdate();
    }
}

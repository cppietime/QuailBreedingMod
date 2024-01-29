package com.funguscow.crossbreed.block;

import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.GeneticTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneticSaplingBlock extends SaplingBlock implements EntityBlock {
    public GeneticSaplingBlock(Properties pProperties) {
        super(new OakTreeGrower(), pProperties);
    }

    @Override
    public void advanceTree(@NotNull ServerLevel pLevel, @NotNull BlockPos pPos, BlockState pState, @NotNull RandomSource pRandom) {
        if (pState.getValue(STAGE) == 0) {
            pLevel.setBlock(pPos, pState.cycle(STAGE), 4);
        } else {
            GeneticTreeFeature.Instance.place(NoneFeatureConfiguration.INSTANCE, pLevel, pLevel.getChunkSource().getGenerator(), pRandom, pPos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new GeneticTreeTileEntity(pPos, pState);
    }
}

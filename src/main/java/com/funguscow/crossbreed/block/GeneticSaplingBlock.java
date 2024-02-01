package com.funguscow.crossbreed.block;

import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.GeneticTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    public void randomTick(@NotNull BlockState pState, ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        if (!pLevel.isAreaLoaded(pPos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof GeneticTreeTileEntity treeEntity)) {
            return;
        }
        if (pLevel.getMaxLocalRawBrightness(pPos.above()) >= 9 && pRandom.nextDouble() <= treeEntity.getGene().growthRate) {
            this.advanceTree(pLevel, pPos, pState, pRandom);
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        CompoundTag blockEntityTag = pStack.getTagElement("BlockEntityTag");
        if (blockEntityTag != null) {
            CompoundTag alleleA = blockEntityTag.getCompound("AlleleA");
            CompoundTag alleleB = blockEntityTag.getCompound("AlleleB");
            CompoundTag gene = alleleA.getFloat("Dominance") >= alleleB.getFloat("Dominance") ? alleleA : alleleB;
            int width = gene.getInt("TrunkWidth");
            pTooltip.add(Component.translatable("text.breesources.stat.trunkWidth", width));
        }
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }
}

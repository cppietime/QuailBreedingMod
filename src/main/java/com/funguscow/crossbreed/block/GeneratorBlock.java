package com.funguscow.crossbreed.block;

import com.funguscow.crossbreed.init.ModTileEntities;
import com.funguscow.crossbreed.tileentities.GeneratorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlock extends BaseEntityBlock {

    public GeneratorBlock(Properties builder) {
        super(builder);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GeneratorTileEntity(pPos, pState);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (pBlockEntityType == ModTileEntities.GENERATOR.get()) ? (level, pos, state, entity) -> {
            ((GeneratorTileEntity) entity).tick();
        } : null;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof GeneratorTileEntity)) {
            return InteractionResult.PASS;
        }
        GeneratorTileEntity gen = (GeneratorTileEntity) be;
        Component msg = Component.literal(gen.getEnergy() + "");
        pPlayer.displayClientMessage(msg, false);
        return InteractionResult.SUCCESS;
    }
}

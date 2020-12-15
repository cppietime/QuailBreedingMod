package com.funguscow.crossbreed.block;

import com.funguscow.crossbreed.tileentities.NestTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class NestBlock extends ContainerBlock {

    public NestBlock(Properties builder) {
        super(builder);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new NestTileEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof NestTileEntity){
            NestTileEntity nest = (NestTileEntity)tileEntity;
            return Math.min(15, nest.numQuails());
        }
        return 0;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(!state.isIn(newState.getBlock())) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof NestTileEntity) {
                ((NestTileEntity) tileEntity).spawnQuails(worldIn);
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}

package com.funguscow.crossbreed.block;

import com.funguscow.crossbreed.tileentities.ModSignTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModCeilingHangingSignBlock extends CeilingHangingSignBlock {
    public ModCeilingHangingSignBlock(Properties pProperties, WoodType pType) {
        super(pProperties, pType);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ModSignTileEntity(pPos, pState);
    }
}

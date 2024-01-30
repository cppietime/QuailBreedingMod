package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModSignTileEntity extends SignBlockEntity {
    public ModSignTileEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModTileEntities.SIGN.get(), pPos, pBlockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModTileEntities.SIGN.get();
    }
}

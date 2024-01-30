package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModHangingSignTileEntity extends SignBlockEntity {
    public ModHangingSignTileEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModTileEntities.HANGING_SIGN.get(), pPos, pBlockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModTileEntities.HANGING_SIGN.get();
    }
}

package com.funguscow.crossbreed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VoiderBlock extends Block implements WorldlyContainerHolder {

    public VoiderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public WorldlyContainer getContainer(@NotNull BlockState state, @NotNull LevelAccessor world, @NotNull BlockPos pos) {
        return new VoidInventory();
    }

    public static class VoidInventory extends SimpleContainer implements WorldlyContainer {

        public VoidInventory() {
            super(1);
        }

        @Override
        public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
            return new int[]{0};
        }

        @Override
        public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, Direction direction) {
            return true;
        }

        @Override
        public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
            return false;
        }

    }
}

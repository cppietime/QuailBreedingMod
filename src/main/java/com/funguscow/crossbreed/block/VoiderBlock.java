package com.funguscow.crossbreed.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class VoiderBlock extends Block implements ISidedInventoryProvider {

    public VoiderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos) {
        return new VoidInventory();
    }

    public static class VoidInventory extends Inventory implements ISidedInventory {

        public VoidInventory(){
            super(1);
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            return new int[]{0};
        }

        @Override
        public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
            return true;
        }

        @Override
        public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
            return false;
        }
    }
}

package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.init.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.network.chat.Component.translatable;

public class VarChestTileEntity extends RandomizableContainerBlockEntity {

    protected NonNullList<ItemStack> contents;
    protected int rows;

    public VarChestTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state, int rows) {
        super(typeIn, pos, state);
        contents = NonNullList.withSize(rows * 9, ItemStack.EMPTY);
        this.rows = rows;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return contents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        contents = itemsIn;
    }

    @Override
    protected Component getDefaultName() {
        return translatable(BreedMod.MODID + ":container.var_chest");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ChestMenu(ModContainers.CHEST_TYPES.get(rows).get(), id,  inventory, this, rows);
    }

    @Override
    public int getContainerSize() {
        return contents.size();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        contents = NonNullList.withSize(rows * 9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, contents);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, contents);
    }
}

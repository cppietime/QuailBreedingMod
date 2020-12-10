package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.init.ModContainers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class VarChestTileEntity extends LockableLootTileEntity {

    protected NonNullList<ItemStack> contents;
    protected int rows;

    public VarChestTileEntity(TileEntityType<?> typeIn, int rows) {
        super(typeIn);
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
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent(BreedMod.MODID + ":container.var_chest");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new ChestContainer(ModContainers.CHEST_TYPES.get(rows).get(), id,  player, this, rows);
    }

    @Override
    public int getSizeInventory() {
        return contents.size();
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        contents = NonNullList.withSize(rows * 9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, contents);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        ItemStackHelper.saveAllItems(compound, contents);
        return compound;
    }
}

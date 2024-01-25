package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.init.ModBlocks;
import com.funguscow.crossbreed.init.ModItems;
import com.funguscow.crossbreed.init.ModTileEntities;
import com.funguscow.crossbreed.util.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratorTileEntity extends BlockEntity {

    private static class EnergyGenerator extends EnergyStorage {
        public EnergyGenerator(int max, int in, int out, int energy) {
            super(max, in, out, energy);
        }

        public void setEnergy(int energy) {
            if (energy < 0) energy = 0;
            if (energy > capacity) energy = capacity;
            this.energy = energy;
        }

        public void charge(int charge) {
            setEnergy(energy + charge);
        }
    }

    private static class QuailEater implements IItemHandler {
        int fullness = 0;

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() == ModItems.RAW_QUAIL.get();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.getItem() == ModItems.RAW_QUAIL.get()) {
                int inserts = Math.min(64 - fullness, stack.getCount());
                fullness += inserts;
                if (inserts == stack.getCount()) {
                    return ItemStack.EMPTY;
                }
                return stack.copyWithCount(stack.getCount() - inserts);
            }
            return stack;
        }

        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("Fullness", fullness);
            return nbt;
        }

        public void deserializeNBT(Tag nbt) {
            if (!(nbt instanceof CompoundTag)) {
                throw new IllegalArgumentException("Tag must be a compound tag");
            }
            fullness = TagUtils.getOrDefault((CompoundTag)nbt, "Fullness", 0);
        }
    }

    private final QuailEater inventory = new QuailEater();
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(() -> inventory);

    private final EnergyGenerator energy = new EnergyGenerator(1000, 0, 100, 0);
    private final LazyOptional<EnergyStorage> energyOptional = LazyOptional.of(() -> energy);

    public GeneratorTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.GENERATOR.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.UP) {
                return inventoryOptional.cast();
            }
        }
        else if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    public void tick() {
        if (level == null || level.isClientSide()) {
            return;
        }

        if (inventory.fullness > 0 && energy.getEnergyStored() < energy.getMaxEnergyStored()) {
            inventory.fullness--;
            energy.charge(100);
            sendUpdate();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("Inventory", inventory.serializeNBT());
        nbt.put("Energy", energy.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Inventory")) {
            inventory.deserializeNBT(nbt.get("Inventory"));
        }
        if (nbt.contains("Energy")) {
            energy.deserializeNBT(nbt.get("Energy"));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryOptional.invalidate();
        energyOptional.invalidate();
    }

    private void sendUpdate() {
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public int getEnergy() {
        return energy.getEnergyStored();
    }
}

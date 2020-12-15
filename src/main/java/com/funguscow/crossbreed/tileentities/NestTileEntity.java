package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.entity.QuailType;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class NestTileEntity extends TileEntity implements ITickableTileEntity, ISidedInventory {

    public static class VirtualQuail{
        private final QuailType breed;
        private final QuailEntity.Gene gene, alleleA, alleleB;
        private final CompoundNBT extraNBT;
        private float layTimer;
        public VirtualQuail(CompoundNBT nbt){
            extraNBT = nbt.copy();
            breed = QuailType.Types.get(extraNBT.getString("Breed"));
            alleleA = new QuailEntity.Gene().readFromTag(extraNBT.getCompound("AlleleA"));
            alleleB = new QuailEntity.Gene().readFromTag(extraNBT.getCompound("AlleleB"));
            layTimer = extraNBT.getInt("EggLayTime");
            extraNBT.remove("EggLayTime");
            extraNBT.remove("Breed");
            extraNBT.remove("AlleleA");
            extraNBT.remove("AlleleB");
            gene = alleleA.dominance >= alleleB.dominance ? alleleA : alleleB;
        }

        public CompoundNBT writeToTag(){
            CompoundNBT nbt = extraNBT.copy();
            nbt.putString("Breed", breed.name);
            nbt.putInt("EggLayTime", (int)layTimer);
            nbt.put("AlleleA", alleleA.writeToTag());
            nbt.put("AlleleB", alleleB.writeToTag());
            return nbt;
        }

        public void resetTimer(Random rand){
            layTimer = breed.layTime + rand.nextInt(breed.layTime + 1);
            layTimer *=  gene.layTime + rand.nextFloat() * gene.layRandomTime;
            layTimer = Math.max(600, layTimer);
        }
    }

    private final Stack<VirtualQuail> quails;
    private final Queue<ItemStack> inventory;
    private final Random rand;

    public NestTileEntity() {
        super(ModTileEntities.QUAIL_NEST.get());
        quails = new Stack<>();
        inventory = new ArrayDeque<>();
        rand = new Random();
    }

    public void putQuail(CompoundNBT nbt){
        quails.add(new VirtualQuail(nbt));
    }

    public CompoundNBT getQuail(){
        if(quails.isEmpty())
            return null;
        VirtualQuail head = quails.pop();
        return head.writeToTag();
    }

    @Override
    public void tick() {
        for(VirtualQuail quail : quails){
            quail.layTimer -= QuailConfig.COMMON.nestTickRate.get();
            if(quail.layTimer <= 0){
                quail.resetTimer(rand);
                ItemStack nextThing = quail.breed.getLoot(rand, quail.gene);
                inventory.add(nextThing);
            }
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return !inventory.isEmpty();
    }

    @Override
    public int getSizeInventory() {
        return inventory.isEmpty() ? 0 : 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if(inventory.isEmpty())
            return ItemStack.EMPTY;
        return inventory.peek();
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack head = inventory.peek();
        if(head != null) {
            ItemStack taken = head.split(count);
            if (head.isEmpty())
                inventory.poll();
            return taken;
        }
        else
            return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if(inventory.isEmpty())
            return ItemStack.EMPTY;
        return inventory.poll();
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        ListNBT listNBT = new ListNBT();
        for(VirtualQuail quail : quails){
            listNBT.add(quail.writeToTag());
        }
        compound.put("Quails", listNBT);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        ListNBT listNBT = nbt.getList("Quails", 10);
        for(int i = 0; i < listNBT.size(); i++){
            CompoundNBT quailTag = listNBT.getCompound(i);
            VirtualQuail quail = new VirtualQuail(quailTag);
            quails.add(quail);
        }
    }

    public void spawnQuails(World world){
        for(VirtualQuail quail : quails){
            CompoundNBT nbt = quail.writeToTag();
            QuailEntity entity = (QuailEntity)ModEntities.QUAIL.get().spawn((ServerWorld)world, null, null, getPos(), SpawnReason.TRIGGERED, true, false);
            if(entity != null)
                entity.readAdditional(nbt);
        }
    }

    public void printQuails(PlayerEntity player){
        Map<String, Integer> breeds = new HashMap<>();
        for(VirtualQuail quail : quails){
            breeds.put(quail.breed.name, breeds.getOrDefault(quail.breed.name, 0) + 1);
        }
        for(Map.Entry<String, Integer> breed : breeds.entrySet()){
            player.sendMessage(new TranslationTextComponent("text." + BreedMod.MODID + ".multiplier",
                    new TranslationTextComponent("text." + BreedMod.MODID + ".breed." + breed.getKey()),
                    breed.getValue()),
                    Util.DUMMY_UUID);
        }
    }

    public int numQuails(){
        return quails.size();
    }
}

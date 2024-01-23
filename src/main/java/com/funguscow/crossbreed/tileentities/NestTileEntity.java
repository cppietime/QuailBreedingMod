package com.funguscow.crossbreed.tileentities;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.entity.QuailType;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

public class NestTileEntity extends BlockEntity implements WorldlyContainer {

    public static class VirtualQuail {
        private final QuailType breed;
        private final QuailEntity.Gene gene, alleleA, alleleB;
        private final CompoundTag extraNBT;
        private float layTimer;

        public VirtualQuail(CompoundTag nbt) {
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

        public CompoundTag writeToTag() {
            CompoundTag nbt = extraNBT.copy();
            nbt.putString("Breed", breed.name);
            nbt.putInt("EggLayTime", (int) layTimer);
            nbt.put("AlleleA", alleleA.writeToTag());
            nbt.put("AlleleB", alleleB.writeToTag());
            return nbt;
        }

        public void resetTimer(RandomSource rand) {
            layTimer = breed.layTime + rand.nextInt(breed.layTime + 1);
            layTimer *= gene.layTime + rand.nextFloat() * gene.layRandomTime;
            layTimer = Math.max(600, layTimer);
        }
    }

    private final Stack<VirtualQuail> quails;
    private final Queue<ItemStack> inventory;
    private int breedCooldown;
    private int seeds;

    public NestTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.QUAIL_NEST.get(), pos, state);
        quails = new Stack<>();
        inventory = new ArrayDeque<>();
        breedCooldown = QuailConfig.COMMON.quailBreedingTime.get();
        seeds = 0;
    }

    public void putQuail(CompoundTag nbt) {
        quails.add(new VirtualQuail(nbt));
        setChanged();
    }

    public CompoundTag getQuail() {
        if (quails.isEmpty())
            return null;
        VirtualQuail head = quails.pop();
        setChanged();
        return head.writeToTag();
    }

    public ListTag getQuails() {
        ListTag nbt = new ListTag();
        for (VirtualQuail quail : quails) {
            nbt.add(quail.writeToTag());
        }
        return nbt;
    }

    public static <T extends BlockEntity> void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull T self) {
        if (!(self instanceof NestTileEntity)) {
            return;
        }
        NestTileEntity me = (NestTileEntity) self;
        me.selfTick(level, pos, state);
    }

    private void selfTick(Level level, BlockPos pos, BlockState state) {
        for (VirtualQuail quail : quails) {
            quail.layTimer -= QuailConfig.COMMON.nestTickRate.get();
            if (quail.layTimer <= 0) {
                quail.resetTimer(level.random);
                ItemStack nextThing = quail.breed.getLoot(level.random, quail.gene);
                inventory.add(nextThing);
            }
        }
        if (numQuails() >= 2 && !level.isClientSide()) {
            if (breedCooldown > 0) {
                breedCooldown--;
            }
            int neededSeeds = QuailConfig.COMMON.seedsToBreed.get();
            if (breedCooldown <= 0 && seeds >= neededSeeds && numQuails() < QuailConfig.COMMON.maxQuailsInNest.get()) {
                breedOne(level.random);
                breedCooldown = QuailConfig.COMMON.quailBreedingTime.get();
                seeds -= neededSeeds;
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        ListTag listNBT = new ListTag();
        for (VirtualQuail quail : quails) {
            listNBT.add(quail.writeToTag());
        }
        compound.put("Quails", listNBT);
        compound.putInt("Cooldown", breedCooldown);
        compound.putInt("Seeds", seeds);
        ListTag inventoryTag = new ListTag();
        for (ItemStack itemStack : inventory) {
            CompoundTag itemTag = new CompoundTag();
            itemStack.save(itemTag);
            inventoryTag.add(itemTag);
        }
        compound.put("Inventory", inventoryTag);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        ListTag listNBT = nbt.getList("Quails", 10);
        quails.clear();
        for (int i = 0; i < listNBT.size(); i++) {
            CompoundTag quailTag = listNBT.getCompound(i);
            VirtualQuail quail = new VirtualQuail(quailTag);
            quails.add(quail);
        }
        inventory.clear();
        breedCooldown = nbt.getInt("Cooldown");
        seeds = nbt.getInt("Seeds");
        ListTag inventoryTag = nbt.getList("Inventory", 10);
        for (int i = 0; i < inventoryTag.size(); i++) {
            CompoundTag itemTag = inventoryTag.getCompound(i);
            ItemStack itemStack = ItemStack.of(itemTag);
            inventory.add(itemStack);
        }
    }

    public void spawnQuails(Level world) {
        for (VirtualQuail quail : quails) {
            CompoundTag nbt = quail.writeToTag();
            QuailEntity entity = (QuailEntity) ModEntities.QUAIL.get().spawn((ServerLevel) world, getBlockPos(), MobSpawnType.TRIGGERED);
            if (entity != null)
                entity.readAdditionalSaveData(nbt);
        }
    }

    public void printQuails(Player player) {
        final MutableComponent component;
        if (quails.empty()) {
            component = Component.translatable("text." + BreedMod.MODID + ".empty");
        } else {
            component = Component.empty();
            Map<String, Integer> breeds = new HashMap<>();
            for (VirtualQuail quail : quails) {
                breeds.put(quail.breed.name, breeds.getOrDefault(quail.breed.name, 0) + 1);
            }
            for (Map.Entry<String, Integer> breed : breeds.entrySet()) {
                if (!component.getSiblings().isEmpty()) {
                    component.append(literal("\n"));
                }
                component.append(translatable("text." + BreedMod.MODID + ".multiplier",
                        translatable("text." + BreedMod.MODID + ".breed." + breed.getKey()),
                        breed.getValue()));
            }
        }
        player.displayClientMessage(component, false);
    }

    public void killOne(Level level) {
        if (quails.empty() || level.isClientSide()) {
            return;
        }
        MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }
        VirtualQuail quail = quails.remove(level.random.nextInt(numQuails()));
        String deathItemId = quail.breed.deathItem;
        if (deathItemId != null && !deathItemId.isEmpty()) {
            int amount = quail.breed.deathAmount;
            Item deathItem = QuailType.getItem(deathItemId, level.random);
            if (deathItem != null) {
                ItemStack itemStack = new ItemStack(deathItem, amount);
                inventory.add(itemStack);
            }
        }
        ResourceLocation lootLocation = new ResourceLocation(BreedMod.MODID, "entities/" + QuailEntity.ID);
        LootTable lootTable = server.getLootData().getLootTable(lootLocation);
        if (lootTable != LootTable.EMPTY) {
            QuailEntity quailEntity = ModEntities.QUAIL.get().create(level);
            if (quailEntity != null) {
                LootParams params = new LootParams.Builder((ServerLevel) level).withParameter(LootContextParams.THIS_ENTITY, quailEntity).withParameter(LootContextParams.ORIGIN, Vec3.ZERO).withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().fellOutOfWorld()).withOptionalParameter(LootContextParams.KILLER_ENTITY, quailEntity).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, quailEntity).create(LootContextParamSets.ENTITY);
                inventory.addAll(lootTable.getRandomItems(params));
                quailEntity.discard();
            }
        }
    }

    public void breedOne(RandomSource random) {
        int index = random.nextInt(numQuails());
        VirtualQuail parentA = quails.remove(index);
        index = random.nextInt(numQuails());
        VirtualQuail parentB = quails.remove(index);
        CompoundTag nbt = parentA.extraNBT.copy();
        QuailType breed = parentA.breed.getOffspring(parentB.breed, random);
        nbt.putInt("EggLayTime", breed.layTime * 2); // Maximum, is this good tho?
        nbt.putString("Breed", breed.name);
        QuailEntity.Gene alleleA = parentA.alleleA.crossover(parentA.alleleB, random);
        QuailEntity.Gene alleleB = parentB.alleleA.crossover(parentB.alleleB, random);
        nbt.put("AlleleA", alleleA.writeToTag());
        nbt.put("AlleleB", alleleB.writeToTag());
        VirtualQuail child = new VirtualQuail(nbt);
        quails.add(parentA);
        quails.add(parentB);
        quails.add(child);
    }

    public int numQuails() {
        return quails.size();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        if (inventory.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return inventory.peek();
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack head = inventory.peek();
        if (head == null) {
            return ItemStack.EMPTY;
        }
        ItemStack remaining = head.split(amount);
        if (head.isEmpty()) {
            inventory.poll();
        }
        return remaining;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        if (inventory.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return inventory.poll();
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack item) {
        if (QuailEntity.BREED_MATERIAL.test(item)) {
            seeds += item.getCount();
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return false;
    }

    @Override
    public void clearContent() {
        inventory.clear();
        seeds = 0;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction direction) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return direction != Direction.DOWN && QuailEntity.BREED_MATERIAL.test(itemStack) && seeds < QuailConfig.COMMON.maxSeedsInNest.get();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return direction == Direction.DOWN;
    }
}

package com.industria.blockentity;

import com.industria.menu.MiningDrillMenu;
import com.industria.registry.ModBlockEntities;
import com.industria.util.ModEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Foreuse automatique.
 * Mine une zone (3x3 a 9x9) sous elle, couche par couche, jusqu'au fond du monde.
 * Les objets vont dans son inventaire interne (27 emplacements) et sont aussi
 * envoyes automatiquement dans les inventaires adjacents (coffre, entonnoir...).
 */
public class MiningDrillBE extends MenuMachineBE {
    public static final int ENERGY_PER_TICK = 150;
    public static final int ENERGY_PER_BLOCK = 3000; // = 20 ticks par bloc
    public static final int CAPACITY = 100000;
    public static final int BUFFER_SLOTS = 27;
    public static final int DATA_COUNT = 9;

    private int curY = Integer.MIN_VALUE;
    private int idx = 0;
    private int progress = 0;
    private int radius = 4;
    private boolean enabled = true;
    private boolean finished = false;
    @Nullable
    private BlockPos target = null;

    private final ItemStackHandler buffer = new ItemStackHandler(BUFFER_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> new OutputOnlyHandler(buffer));

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored() & 0xFFFF;
                case 1 -> energy.getEnergyStored() >>> 16;
                case 2 -> curY == Integer.MIN_VALUE ? worldPosition.getY() - 1 : curY;
                case 3 -> enabled ? 1 : 0;
                case 4 -> finished ? 1 : 0;
                case 5 -> radius;
                case 6 -> hasRoom() ? 0 : 1;
                case 7 -> progress * 100 / ENERGY_PER_BLOCK;
                case 8 -> level != null && level.hasNeighborSignal(worldPosition) ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public MiningDrillBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MINING_DRILL.get(), pos, state, new ModEnergy(CAPACITY, 2000, 0));
    }

    public ItemStackHandler getBuffer() {
        return buffer;
    }

    private int size() {
        return radius * 2 + 1;
    }

    private boolean hasRoom() {
        for (int i = 0; i < buffer.getSlots(); i++) {
            ItemStack s = buffer.getStackInSlot(i);
            if (s.isEmpty() || s.getCount() < s.getMaxStackSize()) return true;
        }
        return false;
    }

    @Override
    public void serverTick() {
        if (level.getGameTime() % 5 == 0) flushBuffer();
        if (!enabled || finished) return;
        if (!hasRoom()) return;
        if (level.hasNeighborSignal(worldPosition)) return;
        if (curY == Integer.MIN_VALUE) curY = worldPosition.getY() - 1;

        if (target == null) {
            target = findNext();
            if (target == null) return;
        }
        if (energy.getEnergyStored() < ENERGY_PER_TICK) return;

        energy.removeEnergyInternal(ENERGY_PER_TICK);
        progress += ENERGY_PER_TICK;
        if (progress < ENERGY_PER_BLOCK) return;
        progress = 0;

        mine((ServerLevel) level, target);
        target = null;
        setChanged();
    }

    /** Cherche le prochain bloc a miner. Retourne null si termine ou en attente (chunk non charge). */
    @Nullable
    private BlockPos findNext() {
        int size = size();
        int y = curY;
        int i = idx;
        int steps = 0;
        while (y >= level.getMinBuildHeight()) {
            if (steps++ > 600) {
                curY = y;
                idx = i;
                return null;
            }
            BlockPos p = new BlockPos(worldPosition.getX() - radius + (i % size), y,
                    worldPosition.getZ() - radius + (i / size));
            if (!level.hasChunkAt(p)) {
                curY = y;
                idx = i;
                return null;
            }
            BlockState st = level.getBlockState(p);
            boolean skip = st.isAir()
                    || st.getBlock() instanceof LiquidBlock
                    || st.getDestroySpeed(level, p) < 0
                    || level.getBlockEntity(p) != null;
            if (skip) {
                i++;
                if (i >= size * size) {
                    i = 0;
                    y--;
                }
                continue;
            }
            curY = y;
            idx = i;
            return p;
        }
        curY = y;
        finished = true;
        setChanged();
        return null;
    }

    private void mine(ServerLevel sl, BlockPos p) {
        BlockState st = sl.getBlockState(p);
        if (st.isAir()) return;
        List<ItemStack> drops = Block.getDrops(st, sl, p, null, null, new ItemStack(Items.DIAMOND_PICKAXE));
        sl.levelEvent(2001, p, Block.getId(st));
        sl.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
        for (ItemStack drop : drops) {
            ItemStack rest = ItemHandlerHelper.insertItem(buffer, drop, false);
            if (!rest.isEmpty()) Block.popResource(sl, worldPosition.above(), rest);
        }
    }

    private void flushBuffer() {
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));
            if (be == null) continue;
            IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).orElse(null);
            if (handler == null) continue;
            for (int i = 0; i < buffer.getSlots(); i++) {
                ItemStack stack = buffer.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                ItemStack rest = ItemHandlerHelper.insertItem(handler, stack.copy(), false);
                int moved = stack.getCount() - rest.getCount();
                if (moved > 0) buffer.extractItem(i, moved, false);
            }
        }
    }

    private void restartScan() {
        curY = Integer.MIN_VALUE;
        idx = 0;
        progress = 0;
        finished = false;
        target = null;
        setChanged();
    }

    @Override
    public boolean handleButton(Player player, int id) {
        switch (id) {
            case 0 -> {
                enabled = !enabled;
                setChanged();
            }
            case 1 -> {
                radius = radius <= 1 ? 4 : radius - 1;
                restartScan();
            }
            case 2 -> restartScan();
            default -> {
                return false;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new MiningDrillMenu(id, inv, this, data);
    }

    @Override
    public void dropContents() {
        if (level == null) return;
        for (int i = 0; i < buffer.getSlots(); i++) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(),
                    worldPosition.getZ(), buffer.getStackInSlot(i));
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("CurY", curY);
        tag.putInt("Idx", idx);
        tag.putInt("Progress", progress);
        tag.putInt("Radius", radius);
        tag.putBoolean("Enabled", enabled);
        tag.putBoolean("Finished", finished);
        tag.put("Buffer", buffer.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        curY = tag.contains("CurY") ? tag.getInt("CurY") : Integer.MIN_VALUE;
        idx = tag.getInt("Idx");
        progress = tag.getInt("Progress");
        radius = tag.contains("Radius") ? tag.getInt("Radius") : 4;
        enabled = !tag.contains("Enabled") || tag.getBoolean("Enabled");
        finished = tag.getBoolean("Finished");
        buffer.deserializeNBT(tag.getCompound("Buffer"));
    }
}

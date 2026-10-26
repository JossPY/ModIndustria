package com.industria.blockentity;

import com.industria.menu.GeneratorMenu;
import com.industria.registry.ModBlockEntities;
import com.industria.util.EnergyUtil;
import com.industria.util.ModEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Generateur a combustible : brule charbon, bois, etc. et produit du FE. */
public class GeneratorBE extends MenuMachineBE {
    public static final int PRODUCTION = 40; // FE/tick
    public static final int CAPACITY = 20000;
    public static final int DATA_COUNT = 5;

    private final ItemStackHandler fuel = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> fuel);
    private int burnTime = 0;
    private int burnTotal = 0;
    private boolean enabled = true;

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored() & 0xFFFF;
                case 1 -> energy.getEnergyStored() >>> 16;
                case 2 -> burnTime;
                case 3 -> burnTotal;
                case 4 -> enabled ? 1 : 0;
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

    public GeneratorBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR.get(), pos, state, new ModEnergy(CAPACITY, 0, 200));
    }

    public ItemStackHandler getFuel() {
        return fuel;
    }

    @Override
    public void serverTick() {
        boolean room = energy.getEnergyStored() + PRODUCTION <= energy.getMaxEnergyStored();

        if (enabled) {
            if (burnTime <= 0 && room) {
                ItemStack stack = fuel.getStackInSlot(0);
                int time = stack.isEmpty() ? 0 : ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
                if (time > 0) {
                    burnTime = time;
                    burnTotal = time;
                    if (stack.hasCraftingRemainingItem()) {
                        fuel.setStackInSlot(0, stack.getCraftingRemainingItem());
                    } else {
                        fuel.extractItem(0, 1, false);
                    }
                }
            }
            if (burnTime > 0 && room) {
                burnTime--;
                energy.addEnergyInternal(PRODUCTION);
            }
        }

        EnergyUtil.push(level, worldPosition, energy, 200, false);
        if (level.getGameTime() % 20 == 0) setChanged();
    }

    @Override
    public boolean handleButton(Player player, int id) {
        if (id == 0) {
            enabled = !enabled;
            setChanged();
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new GeneratorMenu(id, inv, this, data);
    }

    @Override
    public void dropContents() {
        if (level != null) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(),
                    worldPosition.getZ(), fuel.getStackInSlot(0));
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
        tag.put("Fuel", fuel.serializeNBT());
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnTotal", burnTotal);
        tag.putBoolean("Enabled", enabled);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fuel.deserializeNBT(tag.getCompound("Fuel"));
        burnTime = tag.getInt("BurnTime");
        burnTotal = tag.getInt("BurnTotal");
        enabled = !tag.contains("Enabled") || tag.getBoolean("Enabled");
    }
}

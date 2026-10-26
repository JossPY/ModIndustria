package com.industria.blockentity;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/** Vue en lecture/extraction seule d'un inventaire (pour entonnoirs et tuyaux). */
public class OutputOnlyHandler implements IItemHandler {
    private final IItemHandler inner;

    public OutputOnlyHandler(IItemHandler inner) {
        this.inner = inner;
    }

    @Override
    public int getSlots() {
        return inner.getSlots();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inner.getStackInSlot(slot);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return inner.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inner.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }
}

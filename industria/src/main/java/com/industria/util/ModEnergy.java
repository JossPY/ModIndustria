package com.industria.util;

import net.minecraft.util.Mth;
import net.minecraftforge.energy.EnergyStorage;

/** EnergyStorage avec acces interne (sans limite de transfert). */
public class ModEnergy extends EnergyStorage {
    public ModEnergy(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public int addEnergyInternal(int amount) {
        int added = Math.max(0, Math.min(amount, capacity - energy));
        energy += added;
        return added;
    }

    public int removeEnergyInternal(int amount) {
        int removed = Math.max(0, Math.min(amount, energy));
        energy -= removed;
        return removed;
    }

    public void setEnergy(int value) {
        this.energy = Mth.clamp(value, 0, capacity);
    }
}

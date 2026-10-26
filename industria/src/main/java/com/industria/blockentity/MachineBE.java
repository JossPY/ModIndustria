package com.industria.blockentity;

import com.industria.util.ModEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MachineBE extends BlockEntity {
    protected final ModEnergy energy;
    private final LazyOptional<IEnergyStorage> energyCap;

    protected MachineBE(BlockEntityType<?> type, BlockPos pos, BlockState state, ModEnergy energy) {
        super(type, pos, state);
        this.energy = energy;
        this.energyCap = LazyOptional.of(() -> this.energy);
    }

    public abstract void serverTick();

    public InteractionResult onUse(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public void dropContents() {
    }

    public ModEnergy getEnergy() {
        return energy;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return energyCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Energy", energy.getEnergyStored());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energy.setEnergy(tag.getInt("Energy"));
    }
}

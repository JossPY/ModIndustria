package com.industria.blockentity;

import com.industria.registry.ModBlockEntities;
import com.industria.util.EnergyUtil;
import com.industria.util.ModEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CableBE extends MachineBE {
    public CableBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE.get(), pos, state, new ModEnergy(4000, 1000, 0));
    }

    @Override
    public void serverTick() {
        EnergyUtil.push(level, worldPosition, energy, 1000, true);
    }
}

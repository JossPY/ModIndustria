package com.industria.block;

import com.industria.registry.ModBlockEntities;

public class MiningDrillBlock extends MachineBlock {
    public MiningDrillBlock(Properties properties) {
        super(properties, ModBlockEntities.MINING_DRILL::get);
    }
}

package com.industria.block;

import com.industria.registry.ModBlockEntities;

public class ReactorBlock extends MachineBlock {
    public ReactorBlock(Properties properties) {
        super(properties, ModBlockEntities.REACTOR::get);
    }
}

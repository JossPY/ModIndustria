package com.industria.block;

import com.industria.registry.ModBlockEntities;

public class GeneratorBlock extends MachineBlock {
    public GeneratorBlock(Properties properties) {
        super(properties, ModBlockEntities.GENERATOR::get);
    }
}

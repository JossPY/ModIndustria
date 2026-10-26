package com.industria.block;

import com.industria.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CableBlock extends MachineBlock {
    private static final VoxelShape SHAPE = Block.box(5, 5, 5, 11, 11, 11);

    public CableBlock(Properties properties) {
        super(properties, ModBlockEntities.CABLE::get);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
}

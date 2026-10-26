package com.industria.registry;

import com.industria.Industria;
import com.industria.blockentity.CableBE;
import com.industria.blockentity.GeneratorBE;
import com.industria.blockentity.MiningDrillBE;
import com.industria.blockentity.ReactorBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Industria.MODID);

    public static final RegistryObject<BlockEntityType<CableBE>> CABLE = BLOCK_ENTITIES.register("cable",
            () -> BlockEntityType.Builder.of(CableBE::new, ModBlocks.CABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<GeneratorBE>> GENERATOR = BLOCK_ENTITIES.register("generator",
            () -> BlockEntityType.Builder.of(GeneratorBE::new, ModBlocks.GENERATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<MiningDrillBE>> MINING_DRILL = BLOCK_ENTITIES.register("mining_drill",
            () -> BlockEntityType.Builder.of(MiningDrillBE::new, ModBlocks.MINING_DRILL.get()).build(null));

    public static final RegistryObject<BlockEntityType<ReactorBE>> REACTOR = BLOCK_ENTITIES.register("nuclear_reactor",
            () -> BlockEntityType.Builder.of(ReactorBE::new, ModBlocks.NUCLEAR_REACTOR.get()).build(null));
}

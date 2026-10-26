package com.industria.registry;

import com.industria.Industria;
import com.industria.block.CableBlock;
import com.industria.block.GeneratorBlock;
import com.industria.block.MiningDrillBlock;
import com.industria.block.ReactorBlock;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Industria.MODID);

    // ---- Minerais ----
    public static final RegistryObject<Block> URANIUM_ORE = ore("uranium_ore", false, 4.0f);
    public static final RegistryObject<Block> DEEPSLATE_URANIUM_ORE = ore("deepslate_uranium_ore", true, 5.0f);
    public static final RegistryObject<Block> LEAD_ORE = ore("lead_ore", false, 3.0f);
    public static final RegistryObject<Block> DEEPSLATE_LEAD_ORE = ore("deepslate_lead_ore", true, 4.0f);
    public static final RegistryObject<Block> TITANIUM_ORE = ore("titanium_ore", false, 4.5f);
    public static final RegistryObject<Block> DEEPSLATE_TITANIUM_ORE = ore("deepslate_titanium_ore", true, 5.5f);

    // ---- Machines ----
    public static final RegistryObject<Block> CABLE = register("cable",
            () -> new CableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE).strength(1.0f).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> GENERATOR = register("generator",
            () -> new GeneratorBlock(machineProps()));

    public static final RegistryObject<Block> MINING_DRILL = register("mining_drill",
            () -> new MiningDrillBlock(machineProps()));

    public static final RegistryObject<Block> NUCLEAR_REACTOR = register("nuclear_reactor",
            () -> new ReactorBlock(machineProps().strength(5.0f, 12.0f)));

    private static BlockBehaviour.Properties machineProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL).strength(3.5f).sound(SoundType.METAL).requiresCorrectToolForDrops();
    }

    private static RegistryObject<Block> ore(String name, boolean deepslate, float hardness) {
        return register(name, () -> new DropExperienceBlock(
                BlockBehaviour.Properties.copy(deepslate ? Blocks.DEEPSLATE_IRON_ORE : Blocks.IRON_ORE)
                        .strength(hardness, 3.0f),
                UniformInt.of(1, 4)));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier) {
        RegistryObject<T> obj = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(obj.get(), new Item.Properties()));
        return obj;
    }
}

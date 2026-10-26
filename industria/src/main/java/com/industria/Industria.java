package com.industria;

import com.industria.registry.ModBlockEntities;
import com.industria.registry.ModBlocks;
import com.industria.registry.ModCreativeTabs;
import com.industria.registry.ModMenus;
import com.industria.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Industria.MODID)
public class Industria {
    public static final String MODID = "industria";

    public Industria() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModCreativeTabs.TABS.register(bus);
        ModMenus.MENUS.register(bus);
    }
}

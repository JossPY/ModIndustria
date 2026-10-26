package com.industria.client;

import com.industria.Industria;
import com.industria.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Industria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.GENERATOR.get(), GeneratorScreen::new);
            MenuScreens.register(ModMenus.MINING_DRILL.get(), MiningDrillScreen::new);
            MenuScreens.register(ModMenus.REACTOR.get(), ReactorScreen::new);
        });
    }
}

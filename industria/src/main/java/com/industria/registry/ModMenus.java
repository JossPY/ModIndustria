package com.industria.registry;

import com.industria.Industria;
import com.industria.menu.GeneratorMenu;
import com.industria.menu.MiningDrillMenu;
import com.industria.menu.ReactorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Industria.MODID);

    public static final RegistryObject<MenuType<GeneratorMenu>> GENERATOR =
            MENUS.register("generator", () -> IForgeMenuType.create(GeneratorMenu::new));

    public static final RegistryObject<MenuType<MiningDrillMenu>> MINING_DRILL =
            MENUS.register("mining_drill", () -> IForgeMenuType.create(MiningDrillMenu::new));

    public static final RegistryObject<MenuType<ReactorMenu>> REACTOR =
            MENUS.register("nuclear_reactor", () -> IForgeMenuType.create(ReactorMenu::new));
}

package com.industria.registry;

import com.industria.Industria;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Industria.MODID);

    public static final RegistryObject<Item> RAW_URANIUM = simple("raw_uranium");
    public static final RegistryObject<Item> RAW_LEAD = simple("raw_lead");
    public static final RegistryObject<Item> RAW_TITANIUM = simple("raw_titanium");

    public static final RegistryObject<Item> URANIUM_INGOT = simple("uranium_ingot");
    public static final RegistryObject<Item> LEAD_INGOT = simple("lead_ingot");
    public static final RegistryObject<Item> TITANIUM_INGOT = simple("titanium_ingot");

    // Barre de combustible : 24000 ticks = 20 minutes de fonctionnement
    public static final RegistryObject<Item> URANIUM_FUEL_ROD = ITEMS.register("uranium_fuel_rod",
            () -> new Item(new Item.Properties().durability(24000)));
    public static final RegistryObject<Item> DEPLETED_FUEL_ROD = simple("depleted_fuel_rod");

    private static RegistryObject<Item> simple(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }
}

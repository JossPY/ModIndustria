package com.industria.menu;

import com.industria.blockentity.GeneratorBE;
import com.industria.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class GeneratorMenu extends MachineMenu {
    public GeneratorMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, inv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(GeneratorBE.DATA_COUNT));
    }

    public GeneratorMenu(int id, Inventory inv, BlockEntity be, ContainerData data) {
        super(ModMenus.GENERATOR.get(), id, be, data, 1);
        IItemHandler fuel = be instanceof GeneratorBE gen ? gen.getFuel() : new ItemStackHandler(1);
        addSlot(new SlotItemHandler(fuel, 0, 44, 36));
        addPlayerSlots(inv, 8, 84);
    }

    public int burnTime() {
        return data.get(2);
    }

    public int burnTotal() {
        return data.get(3);
    }

    public boolean isEnabled() {
        return data.get(4) != 0;
    }
}
